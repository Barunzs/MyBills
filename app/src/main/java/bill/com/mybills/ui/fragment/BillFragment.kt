package bill.com.mybills.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v4.content.PermissionChecker
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import bill.com.mybills.BuildConfig
import bill.com.mybills.R
import bill.com.mybills.config.AppDAL
import bill.com.mybills.model.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_bill.*
import java.io.File
import java.io.FileOutputStream
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*


internal class BillFragment : Fragment() {

    private var appDAL: AppDAL? = null
    private var gst = 0.0
    private var amountOfGold = 0.0
    private val REQUEST_PERMISSION_CAMERA = 0
    private val REQUEST_CAMERA = 2
    private val REQUEST_IMAGE_BROWSER = 1
    private lateinit var capturedImageFile: File
    //Firebase
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var auth: FirebaseAuth? = null
    private lateinit var billItemList: ArrayList<String>
    private var uriFirebase: Uri? = null
    private var db: FirebaseFirestore? = null


    companion object {
        val TAG = BillFragment::class.java.simpleName

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        storageReference = storage?.reference
        billItemList = ArrayList()
        db = FirebaseFirestore.getInstance()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bill, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //creating new file path
        appDAL = context?.let { AppDAL(it) }
        initEventsListeners()
    }

    private fun initEventsListeners() {
        makingCharge.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(makingCharge: Editable) {

                try {
                    gst = ((amtofgold.text.toString().toDouble() + makingCharge.toString().toDouble()) * 1.5) / 100
                    sgstrate.setText(gst.toString())
                    cgstrate.setText(gst.toString())
                } catch (e: NumberFormatException) {
                    Toast.makeText(context, "Please enter all fields", Toast.LENGTH_LONG).show()
                    sgstrate.setText(gst.toString())
                    cgstrate.setText(gst.toString())
                }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })

        rateofgold.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(rateOfGold: Editable) {

                try {
                    amountOfGold = weight.text.toString().toDouble() * (rateOfGold.toString().toDouble() / 10)
                    amtofgold.setText(amountOfGold.toString())
                } catch (e: NumberFormatException) {
                    Toast.makeText(context, "Please enter all fields", Toast.LENGTH_LONG).show()
                    amtofgold.setText(amountOfGold.toString())
                }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })

        generatebill.setOnClickListener { generateBill(it) }
        image.setOnClickListener { takeProductImage(it) }
    }


    private fun takeProductImage(view: View) {
        if (context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.CAMERA) } == PermissionChecker.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
            }
        } else {
            startImageCapture()
        }
    }

    private fun startImageCapture() {
        if (customerField.text.toString().isEmpty()) {
            Toast.makeText(context, "Please enter Customer name before taking image", Toast.LENGTH_LONG).show()
            return
        }
        capturedImageFile = File.createTempFile("product_image", ".jpg", context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
        if (capturedImageFile.exists()) {
            capturedImageFile.delete()
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, context?.let { FileProvider.getUriForFile(it, BuildConfig.APPLICATION_ID + ".fileprovider", capturedImageFile) })
        if (intent.resolveActivity(context?.packageManager) != null) {
            startActivityForResult(intent, REQUEST_CAMERA)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_BROWSER -> {
                if (resultCode == Activity.RESULT_OK) {
                    capturedImageFile.let {
                        val openInputStream = context?.contentResolver?.openInputStream(data?.data)
                        val imageByteArray = openInputStream?.available()?.let { ByteArray(it) }
                        openInputStream?.read(imageByteArray)
                        val imageFileOutputStream = FileOutputStream(it)
                        imageFileOutputStream.write(imageByteArray)
                        imageFileOutputStream.flush()
                        imageFileOutputStream.close()
                        val optimizeGoalImage = optimizeGoalImage(it)
                        it.delete()
                        it.createNewFile()
                        val byteArrayOutputStream = FileOutputStream(it)
                        optimizeGoalImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                        byteArrayOutputStream.flush()
                        byteArrayOutputStream.close()
                    }
                }
            }
            REQUEST_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    capturedImageFile.let {
                        productIconProgressBar.visibility = View.VISIBLE
                        val optimizeGoalImage = optimizeGoalImage(it)
                        it.delete()
                        it.createNewFile()
                        val byteArrayOutputStream = FileOutputStream(it)
                        optimizeGoalImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                        byteArrayOutputStream.flush()
                        byteArrayOutputStream.close()
                        val uri = Uri.fromFile(capturedImageFile)
                        val customerName = customerField.text.toString()
                        val filePath = uri?.lastPathSegment?.let { it1 -> storageReference?.child(customerName + "/" + System.currentTimeMillis())?.child(it1) }
                        filePath?.putFile(uri)?.addOnFailureListener {
                            Toast.makeText(context, "Error" + it.localizedMessage, Toast.LENGTH_LONG).show()
                            productIconProgressBar.visibility = View.GONE
                        }?.addOnSuccessListener {
                            Toast.makeText(context, "Upload Successfully", Toast.LENGTH_LONG).show()
                            productIconProgressBar.visibility = View.GONE
                            uriFirebase = it.downloadUrl
                            Picasso.with(context).load(uriFirebase).into(image)
                        }
                    }
                }
            }
        }
    }

    private fun optimizeGoalImage(imageFile: File): Bitmap {
        val bmpOptions = BitmapFactory.Options()
        bmpOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imageFile.absolutePath, bmpOptions)
        val scaleFactor = Math.min((bmpOptions.outWidth / 100), (bmpOptions.outHeight / 100))
        bmpOptions.inJustDecodeBounds = false
        bmpOptions.inSampleSize = scaleFactor
        bmpOptions.inPurgeable = true
        return BitmapFactory.decodeFile(imageFile.absolutePath, bmpOptions)
    }


    private fun generateBill(view: View) {
        addBillItem()
    }

    private fun addBillItem() {
        try {
            val item = Item(particular?.text.toString(), weight.text.toString().toDouble(), rateofgold.text.toString().toDouble(), weight.text.toString().toDouble() * (rateofgold.text.toString().toDouble() / 10), makingCharge.text.toString().toDouble(), gst, gst, customerField.text.toString(), uriFirebase.toString())
            val totalAmt = amountOfGold + makingCharge.text.toString().toDouble() + gst + gst
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            total.text = "₹ " + df.format(totalAmt)
            val gson = Gson()
            val type = object : TypeToken<Item>() {
            }.type
            val itemJson = gson.toJson(item, type)
            Log.d(TAG, "itemJson::$itemJson")
            // Get List from SharedPref before adding
            val arrayListJson = appDAL?.billItemJson
            if (arrayListJson.isNullOrEmpty()) {
                billItemList.add(itemJson)
            } else {
                val arrayListType = object : TypeToken<ArrayList<String>>() {
                }.type
                val ArrayLisgson = Gson()
                billItemList = ArrayLisgson.fromJson<java.util.ArrayList<String>>(arrayListJson, arrayListType)
                billItemList.add(itemJson)
            }
            val jsonItemArraylist = gson.toJson(billItemList)
            appDAL?.billItemJson = jsonItemArraylist
            timer(2000, 1000,item).start()
        } catch (e: NumberFormatException) {
            view?.let {
                Snackbar.make(it, "Please Enter all Fields", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            }
        }
    }

    private fun addNewItem() {
        val ft = fragmentManager?.beginTransaction()?.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        var fragment = BillFragment()
        ft?.add(R.id.main_fragment_container, fragment)
        ft?.commit()

    }

    private fun showalert(item:Item) {
        val builder = context?.let { context?.let { it1 -> AlertDialog.Builder(it1, R.style.MyDialogTheme) } }
        builder?.setTitle("Generate Bill")
        builder?.setMessage("Do you want add ${particular.text} to Bill?")
        builder?.setPositiveButton("YES") { dialog, which ->
            /*val items = HashMap<String, Any>()
            items.put(item.customerName,"Hello")
            db?.collection("Akash")?.document(item.customerName)?.set(items)?.addOnSuccessListener {
                void: Void? ->
                Toast.makeText(context,"Sucess",Toast.LENGTH_LONG).show()

            }?.addOnFailureListener {
                exception: java.lang.Exception ->
                Toast.makeText(context,"Failure",Toast.LENGTH_LONG).show()
            }*/
            addNewItem()
        }
        builder?.setNegativeButton("No") { dialog, which ->
        }
        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
    }

    private fun timer(millisInFuture: Long, countDownInterval: Long,item:Item): CountDownTimer {
        return object : CountDownTimer(millisInFuture, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                showalert(item)
            }
        }
    }

}