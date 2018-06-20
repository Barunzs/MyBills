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
import android.util.Log
import android.view.*
import android.widget.Toast
import bill.com.mybills.BuildConfig
import bill.com.mybills.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
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

	companion object {
		val TAG = EditProfileFragment.javaClass.name
	}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
		storage = FirebaseStorage.getInstance()
		auth = FirebaseAuth.getInstance()
		storageReference = storage?.reference
		db = FirebaseFirestore.getInstance()

	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_editprofile, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initEventListeners()
	}


	override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
		super.onCreateOptionsMenu(menu, inflater);
		menu?.clear();
	}

	private fun initEventListeners() {
		updateprofileButton?.setOnClickListener { onClickScanButton(it) }
		changeProfilePictureButton.setOnClickListener { takeProductImage(it) }
	}

	private fun onClickScanButton(view: View) {

		val profileUpdates = UserProfileChangeRequest.Builder()
				.setDisplayName(firstNameEditText.text.toString())
				.setPhotoUri(uriFirebase)
				.build()

		user?.updateProfile(profileUpdates)
				?.addOnCompleteListener { task ->
					if (task.isSuccessful) {
						Log.d(TAG, "User profile updated.")
					}
				}

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
		capturedImageFile = File.createTempFile("user_image", ".jpg", context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
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
						profilePictureUpdateProgressBar.visibility = View.VISIBLE
						val optimizeGoalImage = optimizeGoalImage(it)
						it.delete()
						it.createNewFile()
						val byteArrayOutputStream = FileOutputStream(it)
						optimizeGoalImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
						byteArrayOutputStream.flush()
						byteArrayOutputStream.close()
						val uri = Uri.fromFile(capturedImageFile)
						user = FirebaseAuth.getInstance().currentUser
						val filePath = uri?.lastPathSegment?.let { it1 -> storageReference?.child(user?.uid + "/" + System.currentTimeMillis())?.child(it1) }
						filePath?.putFile(uri)?.addOnFailureListener {
							Toast.makeText(context, "Error" + it.localizedMessage, Toast.LENGTH_LONG).show()
							profilePictureUpdateProgressBar.visibility = View.GONE
						}?.addOnSuccessListener {
							Toast.makeText(context, "Upload Successfully", Toast.LENGTH_LONG).show()
							profilePictureUpdateProgressBar.visibility = View.GONE
							uriFirebase = it.downloadUrl
							Picasso.with(context).load(uriFirebase).into(editProfileImage)
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