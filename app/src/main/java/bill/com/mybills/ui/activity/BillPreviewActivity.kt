package bill.com.mybills.ui.activity

import android.app.ActivityOptions
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import bill.com.mybills.R
import bill.com.mybills.config.AppDAL
import bill.com.mybills.model.BusinessProfile
import bill.com.mybills.model.Item
import bill.com.mybills.task.CreatePDFTask
import bill.com.mybills.ui.adapter.BillPreviewAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_preview.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat


class BillPreviewActivity : AppCompatActivity() {

	private lateinit var billPreviewAdapter: BillPreviewAdapter
	private var appDAL: AppDAL? = null
	private var docRef: DocumentReference? = null
	private var billItemList: ArrayList<Item> = ArrayList()
	private var user: FirebaseUser? = null
	private var db: FirebaseFirestore? = null
	private var businessProfile: BusinessProfile? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_preview)
		val builder = StrictMode.VmPolicy.Builder()
		StrictMode.setVmPolicy(builder.build())
		appDAL = applicationContext?.let { AppDAL(it) }
		val topToolBar = findViewById<Toolbar>(R.id.toolbar2)
		setSupportActionBar(topToolBar)
		billRecyclerView?.layoutManager = LinearLayoutManager(applicationContext)
		db = FirebaseFirestore.getInstance()
		user = FirebaseAuth.getInstance().currentUser
		docRef = user?.uid?.let { db?.collection(it)?.document("Business Profile") }
		try {
            billItemList = getBillItemList() as ArrayList<Item>
			if (billItemList.size > 0) {
				customer.text = billItemList[0].customerName
				billPreviewAdapter = BillPreviewAdapter()
				billPreviewAdapter.billItemArray = billItemList
				billRecyclerView?.adapter = billPreviewAdapter
				billRecyclerView.scrollToPosition(billItemList.size - 1);
			} else {
				tax.visibility = View.GONE
				sendBill.visibility = View.GONE
				Snackbar.make(billRecyclerView, "Please Generate Bill before Preview", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show()
				return
			}
		} catch (e: Throwable) {
			Snackbar.make(billRecyclerView, "Please Generate Bill before Preview", Snackbar.LENGTH_LONG)
					.setAction("Action", null).show()
		}
		val df = DecimalFormat("#.##")
		df.roundingMode = RoundingMode.CEILING
		var totalAmt = 0.0
		var cgst = 0.0
		var sgst = 0.0
		for (item in billItemList) {
			totalAmt += (item.cgst + item.sgst + item.amtGold + item.makingCharge)
			cgst += item.cgst
			sgst += item.sgst
		}
		totalAmount.text = "₹ " + df.format(totalAmt)
		sgstAmt.text = "₹ " + df.format(sgst)
		cgstAmt.text = "₹ " + df.format(cgst)
		sendBill.setOnClickListener { sendPDF(billItemList) }
		docRef?.get()?.addOnSuccessListener { documentSnapshot ->
			if (documentSnapshot.exists()) {
				businessProfile = documentSnapshot.toObject(BusinessProfile::class.java)
			} else {
				Toast.makeText(applicationContext, "No Profile Data Found", Toast.LENGTH_LONG).show()
			}
		}
	}

	private fun sendPDF(itemList: ArrayList<Item>) {
		try {
			val fileimage = File(applicationContext?.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image.jpg")
            val bitmapLoga = BitmapFactory.decodeStream(FileInputStream(fileimage))
			val filepdf = File(applicationContext?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "bill.pdf")
			businessProfile?.let { CreatePDFTask(this@BillPreviewActivity, filepdf, itemList, progressPdf, it,bitmapLoga,db,user).execute() }
			sendBill.visibility = View.GONE
		} catch (e: IOException) {
			Toast.makeText(applicationContext,"Please update your Logo before generating Bill",Toast.LENGTH_LONG).show()
		}

	}

	override fun onResume() {
		super.onResume()
		if (billItemList.size > 0)
			sendBill.visibility = View.VISIBLE
	}

    private fun getBillItemList(): java.util.ArrayList<*> {
        val itemListJsonDB = appDAL?.billItemJson
        val type = object : TypeToken<java.util.ArrayList<String>>() {
        }.type
        val gson = Gson()
        val billItemListJson = gson.fromJson<java.util.ArrayList<String>>(itemListJsonDB, type)
        val billItemListObj = java.util.ArrayList<Item>()
        if (billItemListJson != null && billItemListJson.size > 0) {
            val itemType = object : TypeToken<Item>() {
            }.type
            val itemGson = Gson()
            for (billItemObj in billItemListJson) {
                val billItem = itemGson.fromJson<Item>(billItemObj, itemType)
                billItemListObj.add(billItem)
            }
        }
        return billItemListObj
    }

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.preview_menu, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		val id = item.itemId
		if (id == R.id.clear) {
			appDAL?.billItemJson = String()
			finish()
			startActivity(intent,
					ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
			return true
		}
		return super.onOptionsItemSelected(item)
	}

}