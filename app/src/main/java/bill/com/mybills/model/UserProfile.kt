package bill.com.mybills.model

import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser

internal data class UserProfile(var phone: String, var orgName: String, var gstIN: String, var address: String) {


}