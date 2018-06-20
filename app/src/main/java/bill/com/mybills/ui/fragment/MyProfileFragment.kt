package bill.com.mybills.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import bill.com.mybills.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_myprofile.*


internal class MyProfileFragment : Fragment() {

	private var storage: FirebaseStorage? = null
	private var storageReference: StorageReference? = null
	private var auth: FirebaseAuth? = null

	companion object {
		val TAG = MyProfileFragment::class.java.simpleName
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
		storage = FirebaseStorage.getInstance()
		storageReference = storage?.reference
		auth = FirebaseAuth.getInstance()

	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_myprofile, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		/*storageReference?.child("photos/akash.jpg")?.downloadUrl?.addOnFailureListener {
			Log.d(MyProfileFragment.TAG, "OnFailureListener")
			if (isVisible) {
				Toast.makeText(context, "Error::" + it.localizedMessage, Toast.LENGTH_LONG).show()
				profilePictureUpdateProgressBar.visibility = View.GONE
			}
		}?.addOnSuccessListener {
			Log.d(MyProfileFragment.TAG, "OnSuccessListener")
			if (isVisible) {
				profilePictureUpdateProgressBar.visibility = View.GONE
				Picasso.with(context).load(it).into(profilePictureImageView)
			}

		}*/
		val user = FirebaseAuth.getInstance().currentUser
		if (user != null) {
			// Name, email address, and profile photo Url
			val name = user.displayName
			val email = user.email
			val photoUrl = user.photoUrl
			profilePictureUpdateProgressBar.visibility = View.GONE
			Picasso.with(context).load(photoUrl).into(profilePictureImageView)
			fullNameTextView.setText(name)

		}
	}

	override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
		super.onCreateOptionsMenu(menu, inflater);
		menu?.clear();
	}


	override fun onDestroyView() {
		super.onDestroyView()

	}


}