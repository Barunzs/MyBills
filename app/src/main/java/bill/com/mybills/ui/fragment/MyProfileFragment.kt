package bill.com.mybills.ui.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.Toast
import bill.com.mybills.R
import bill.com.mybills.model.BusinessProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_myprofile.*


internal class MyProfileFragment : Fragment() {

    private var storage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var auth: FirebaseAuth? = null
    private var docRef: DocumentReference? = null
    private var user: FirebaseUser? = null
    private var db: FirebaseFirestore? = null


    companion object {
        val TAG = MyProfileFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        storage = FirebaseStorage.getInstance()
        storageReference = storage?.reference
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        user = FirebaseAuth.getInstance().currentUser
        docRef = user?.uid?.let { db?.collection(it)?.document("Business Profile") }
        val tabletSize = resources.getBoolean(R.bool.isTablet)
        if (tabletSize) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_myprofile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val name = user?.displayName
            val photoUrl = user?.photoUrl
            profilePictureUpdateProgressBar.visibility = View.GONE
            Picasso.get().load(photoUrl).into(profilePictureImageView)
            fullNameTextView.text = name
        }
        docRef?.get()?.addOnSuccessListener { documentSnapshot ->
            if (this.isVisible) {
                if (documentSnapshot.exists()) {
                    val businessProfile = documentSnapshot.toObject(BusinessProfile::class.java)
                    BusinessNameTextView.text = businessProfile?.orgName
                    businessNumberTextView.text = businessProfile?.address + " Pin :" + businessProfile?.pincode
                    businessGst.text = businessProfile?.gstIN
                    phoneNumberTextView.text = businessProfile?.phone
                    emailAddressTextView.text = user?.email

                } else {
                    Toast.makeText(context, "No Profile Data Found", Toast.LENGTH_LONG).show()
                }
                businessViewProgressBar.visibility = View.GONE
            }
        }
        storageReference?.child(user?.uid + "/businessLogo/businessLogo_image")?.downloadUrl?.addOnFailureListener {
            Toast.makeText(context, "Please Upload Your Business Logo", Toast.LENGTH_LONG).show()
        }?.addOnSuccessListener {
            if (this.isVisible)
                Picasso.get().load(it).into(businessPictureImageView)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater);
        menu?.clear();
    }

}