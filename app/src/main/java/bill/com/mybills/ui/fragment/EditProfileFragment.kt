package bill.com.mybills.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v4.content.PermissionChecker
import android.view.*
import android.widget.Toast
import bill.com.mybills.BuildConfig
import bill.com.mybills.R
import bill.com.mybills.model.BusinessProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_editprofile.*
import java.io.File
import java.io.FileOutputStream


internal class EditProfileFragment : Fragment() {
    //Firebase
    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var auth: FirebaseAuth? = null
    private var uriFirebase: Uri? = null
    private var db: FirebaseFirestore? = null
    private val REQUEST_PERMISSION_CAMERA = 2
    private lateinit var capturedImageFile: File
    private val REQUEST_CAMERA = 4
    private val REQUEST_IMAGE_BROWSER = 1
    private var user: FirebaseUser? = null
    private var docRef: DocumentReference? = null
    private var businessLogoURI: Uri? = null


    companion object {
        val TAG = EditProfileFragment::class.java.simpleName
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        storageReference = storage?.reference
        db = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance().currentUser
        docRef = user?.uid?.let { db?.collection(it)?.document("Business Profile") }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_editprofile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (user != null) {
            Picasso.with(context).load(user?.photoUrl).into(editProfileImage)
            firstNameEditText.setText(user?.displayName)
            profilePictureUpdateProgressBar.visibility = View.GONE
        }

        docRef?.get()?.addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val businessProfile = documentSnapshot.toObject(BusinessProfile::class.java)
                mobileNo.setText(businessProfile.phone)
                shopName.setText(businessProfile.orgName)
                gstin.setText(businessProfile.gstIN)
                address.setText(businessProfile.address)
                pincode.setText(businessProfile.pincode)
                storageReference?.child(user?.uid + "/businessLogo/businessLogo_image")?.downloadUrl?.addOnFailureListener {
                    Toast.makeText(context, "Error" + it.localizedMessage, Toast.LENGTH_LONG).show()
                    businessPictureUpdateProgressBar.visibility = View.GONE
                }?.addOnSuccessListener {
                    Picasso.with(context).load(it).into(editLogo)
                    businessPictureUpdateProgressBar.visibility = View.GONE
                }

            } else {
                Toast.makeText(context, "No Profile Data Found", Toast.LENGTH_LONG).show()
            }
        }
        initEventListeners()
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater);
        menu?.clear();
    }

    private fun initEventListeners() {
        updateprofileButton?.setOnClickListener { onClickScanButton(it) }
        changeProfilePictureButton.setOnClickListener { uploadProfileImage(it) }
        changeBusinesslogoPictureButton.setOnClickListener { uploadBusinessLogo(it) }
    }

    private fun uploadBusinessLogo(view: View) {
        if (context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.CAMERA) } == PermissionChecker.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
            }
        } else {
            takeImage(true)
        }
    }

    private fun onClickScanButton(view: View) {

        if (firstNameEditText.text.isEmpty()) {
            firstNameEditText.requestFocus()
            firstNameEditText.error = "Name cannot be empty"
            return
        }
        if (mobileNo.text.toString() == "+91") {
            mobileNo.requestFocus()
            mobileNo.error = "Phone No cannot be empty"
            return
        }
        if (mobileNo.text.isEmpty()) {
            mobileNo.requestFocus()
            mobileNo.error = "Phone No cannot be empty"
            return
        }
        if (shopName.text.isEmpty()) {
            shopName.requestFocus()
            shopName.error = "Shop Name cannot be empty"
            return
        }
        if (gstin.text.isEmpty()) {
            gstin.requestFocus()
            gstin.error = "GST IN cannot be empty"
            return
        }
        if (address.text.isEmpty()) {
            address.requestFocus()
            address.error = "Shop Address cannot be empty"
            return
        }
        progressbarUpdateprofile.visibility = View.VISIBLE
        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(firstNameEditText.text.toString())
                .build()
        updateUserProfile(profileUpdates)
    }

    private fun updateUserProfile(profileUpdates: UserProfileChangeRequest) {
        user?.updateProfile(profileUpdates)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val businessProfile = BusinessProfile(mobileNo.text.toString(), shopName.text.toString(), gstin.text.toString(), address.text.toString(), null, pincode.text.toString(), true)
                        user?.uid?.let {
                            db?.collection(it)?.document("Business Profile")?.set(businessProfile)?.addOnSuccessListener { void: Void? ->
                                Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
                                val fragmentManager = fragmentManager
                                val fragment = MyProfileFragment()
                                fragmentManager?.beginTransaction()?.replace(R.id.main_fragment_container, fragment,
                                        MyProfileFragment.TAG)?.commit()
                            }?.addOnFailureListener { exception: java.lang.Exception ->
                                Toast.makeText(context, "Failure", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Error Try Again", Toast.LENGTH_LONG).show()
                    }
                    progressbarUpdateprofile.visibility = View.GONE
                }
    }

    private fun uploadProfileImage(view: View) {
        if (context?.let { ActivityCompat.checkSelfPermission(it, Manifest.permission.CAMERA) } == PermissionChecker.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
            }
        } else {
            takeImage(false)
        }
    }

    private fun takeImage(isBusinessLogo: Boolean) {
        if (isBusinessLogo) {
            capturedImageFile = File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "businessLogo_image")
        } else {
            capturedImageFile = File.createTempFile("user_image", ".jpg", context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
        }
        if (capturedImageFile.exists()) {
            capturedImageFile.delete()
        }
        if (isBusinessLogo) {
            businessPictureUpdateProgressBar.visibility = View.VISIBLE
            val i = Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(i, REQUEST_IMAGE_BROWSER)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(context?.packageManager) != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, context?.let { FileProvider.getUriForFile(it, BuildConfig.APPLICATION_ID + ".fileprovider", capturedImageFile) })
                startActivityForResult(intent, REQUEST_CAMERA)
            }
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
                        val uri = Uri.fromFile(capturedImageFile)
                        var filePath: StorageReference?
                        filePath = uri?.lastPathSegment?.let { it1 -> storageReference?.child(user?.uid + "/" + "businessLogo")?.child(it1) }
                        filePath?.putFile(uri)?.addOnFailureListener {
                            Toast.makeText(context, "Error" + it.localizedMessage, Toast.LENGTH_LONG).show()
                            businessPictureUpdateProgressBar.visibility = View.GONE
                        }?.addOnSuccessListener {
                            businessLogoURI = Uri.EMPTY
                            businessLogoURI = it.downloadUrl
                            businessPictureUpdateProgressBar.visibility = View.GONE
                            Picasso.with(context).load(it.downloadUrl).into(editLogo)
                            val localFile = File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image.jpg")
                            storageReference?.child(user?.uid + "/businessLogo/businessLogo_image")?.getFile(localFile)?.addOnFailureListener {
                                Toast.makeText(context, "Error" + it.localizedMessage, Toast.LENGTH_LONG).show()
                            }?.addOnSuccessListener {
                                Toast.makeText(context, "Profile iamge saved", Toast.LENGTH_LONG).show()
                            }
                        }

                    }
                }
            }
            REQUEST_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    capturedImageFile.let {
                        profilePictureUpdateProgressBar.visibility = View.VISIBLE
                        val optimizeGoalImage = optimizeGoalImage(it)
                        it.delete()
                        it.createNewFile()
                        val byteArrayOutputStream = FileOutputStream(it)
                        optimizeGoalImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                        byteArrayOutputStream.flush()
                        byteArrayOutputStream.close()
                        val uri = Uri.fromFile(capturedImageFile)
                        val filePath: StorageReference?
                        filePath = uri?.lastPathSegment?.let { it1 -> storageReference?.child(user?.uid + "/" + "profileImage")?.child(it1) }
                        filePath?.putFile(uri)?.addOnFailureListener {
                            Toast.makeText(context, "Error" + it.localizedMessage, Toast.LENGTH_LONG).show()
                            profilePictureUpdateProgressBar.visibility = View.GONE
                        }?.addOnSuccessListener {
                            uriFirebase = Uri.EMPTY
                            uriFirebase = it.downloadUrl
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(firstNameEditText.text.toString())
                                    .setPhotoUri(uriFirebase)
                                    .build()
                            user?.updateProfile(profileUpdates)
                                    ?.addOnCompleteListener { task ->
                                        profilePictureUpdateProgressBar.visibility = View.GONE
                                        if (task.isSuccessful) {
                                            Toast.makeText(context, "Profile Picture Uploaded Successfully", Toast.LENGTH_LONG).show()
                                            Picasso.with(context).load(uriFirebase).into(editProfileImage)
                                        } else {
                                            Toast.makeText(context, "Error Try Again", Toast.LENGTH_LONG).show()
                                        }
                                    }
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
}