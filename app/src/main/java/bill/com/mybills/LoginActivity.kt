package bill.com.mybills

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import bill.com.mybills.config.AppDAL
import bill.com.mybills.ui.activity.MainActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit


class LoginActivity : AppCompatActivity() {

	private val TAG = LoginActivity::javaClass.name
	private val appDAL get() = AppDAL(applicationContext)
	private var mAuth: FirebaseAuth? = null
	private var mVerificationInProgress = false
	private var mVerificationId: String? = null
	private var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
	private lateinit var mCallbacks: OnVerificationStateChangedCallbacks


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)
		initEventsListeners()
		mAuth = FirebaseAuth.getInstance()
		mCallbacks = object : OnVerificationStateChangedCallbacks() {

			override fun onVerificationCompleted(credential: PhoneAuthCredential) {
				// This callback will be invoked in two situations:
				// 1 - Instant verification. In some cases the phone number can be instantly
				//     verified without needing to send or enter a verification code.
				// 2 - Auto-retrieval. On some devices Google Play services can automatically
				//     detect the incoming verification SMS and perform verificaiton without
				//     user action.
				Log.d(TAG, "onVerificationCompleted:$credential")
				// [START_EXCLUDE silent]
				mVerificationInProgress = false
				// [END_EXCLUDE]

				// [START_EXCLUDE silent]
				// Update the UI and attempt sign in with the phone credential
				//updateUI(STATE_VERIFY_SUCCESS, credential);
				// [END_EXCLUDE]
				signInWithPhoneAuthCredential(credential);
			}

			override fun onVerificationFailed(e: FirebaseException) {
				// This callback is invoked in an invalid request for verification is made,
				// for instance if the the phone number format is not valid.
				Log.w(TAG, "onVerificationFailed", e)
				// [START_EXCLUDE silent]
				mVerificationInProgress = false
				// [END_EXCLUDE]

				if (e is FirebaseAuthInvalidCredentialsException) {
					// Invalid request
					// [START_EXCLUDE]
					input_email.error = "Invalid phone number."
					// [END_EXCLUDE]
				} else if (e is FirebaseTooManyRequestsException) {
					// The SMS quota for the project has been exceeded
					// [START_EXCLUDE]
					Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
							Snackbar.LENGTH_SHORT).show()
					// [END_EXCLUDE]
				}

				// Show a message and update the UI
				// [START_EXCLUDE]
				//updateUI(STATE_VERIFY_FAILED)
				// [END_EXCLUDE]
			}

			override fun onCodeSent(verificationId: String?,
									token: PhoneAuthProvider.ForceResendingToken?) {
				// The SMS verification code has been sent to the provided phone number, we
				// now need to ask the user to enter the code and then construct a credential
				// by combining the code with a verification ID.
				Log.d(TAG, "onCodeSent:" + verificationId!!)

				// Save verification ID and resending token so we can use them later
				mVerificationId = verificationId
				mResendToken = token


				// [START_EXCLUDE]
				// Update UI
				//updateUI(STATE_CODE_SENT);
				// [END_EXCLUDE]
			}
		}
	}


	private fun startPhoneNumberVerification(phoneNumber: String) {

		PhoneAuthProvider.getInstance().verifyPhoneNumber(
				phoneNumber, // Phone number to verify
				60,             // Timeout duration
				TimeUnit.SECONDS,   // Unit of timeout
				this,           // Activity (for callback binding)
				mCallbacks)      // OnVerificationStateChangedCallbacks
	}

	private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
		mAuth?.signInWithCredential(credential)
				?.addOnCompleteListener(this) { task ->
					if (task.isSuccessful) {
						// Sign in success, update UI with the signed-in user's information
						Log.d(TAG, "signInWithCredential:success")
						input_password.setText(credential.smsCode)
						val user = task.result.user
						Log.d(TAG, "signInWithCrede")
						val intent = Intent(applicationContext, MainActivity::class.java)
						startActivity(intent)
						finish()
					} else {
						// Sign in failed, display a message and update the UI
						Log.w(TAG, "signInWithCredential:failure", task.exception)
						if (task.exception is FirebaseAuthInvalidCredentialsException) {
							// The verification code entered was invalid
							// [START_EXCLUDE silent]
							//mVerificationField.setError("Invalid code.")
							// [END_EXCLUDE]
						}
						// [START_EXCLUDE silent]
						// Update UI
						//updateUI(STATE_SIGNIN_FAILED);
						// [END_EXCLUDE]
					}
				}
	}

	private fun initEventsListeners() {
		btn_login?.setOnClickListener { onClickLoginButton(it) }
	}

	private fun onClickLoginButton(view: View) {

		if (input_email.text.isEmpty()) {
			input_email.requestFocus()
			input_email.error = "Email may not be empty"
			return
		}
		if (input_password.text.isEmpty()) {
			input_password.requestFocus()
			input_password.error = "Password may not be empty"
			return
		}
		//startPhoneNumberVerification(input_phone.text.toString())
		signin.visibility = View.VISIBLE
		performLoginOrAccountCreation(input_email.text.toString(), input_password.text.toString())
	}

	private fun performLoginOrAccountCreation(email: String, password: String) {

		mAuth?.fetchProvidersForEmail(email)?.addOnCompleteListener(
				this) { task ->
			if (task.isSuccessful) {
				Log.d(TAG, "checking to see if user exists in firebase or not")
				val result = task.result
				if ((result != null && result.providers != null
								&& result.providers!!.size > 0)) {
					Log.d(TAG, "User exists, trying to login using entered credentials")
					performLogin(email, password);
				} else {
					Toast.makeText(this@LoginActivity,
							"User doesn't exist",
							Toast.LENGTH_SHORT).show()
				}
			} else {
				Log.e(TAG, "User check failed", task.exception)
				Toast.makeText(this@LoginActivity,
						"There is a problem, please try again later.",
						Toast.LENGTH_SHORT).show()

			}
		}

	}

	private fun performLogin(email: String, password: String) {
		mAuth?.signInWithEmailAndPassword(email, password)
				?.addOnCompleteListener(this, { task ->
					if (task.isSuccessful) {
						val user = task.result.user
                        Log.d(TAG,"name::"+user.displayName)
						val intent = Intent(applicationContext, MainActivity::class.java)
						startActivity(intent)
						finish()
					} else {
						Toast.makeText(this@LoginActivity,
								"Authentication failed.",
								Toast.LENGTH_SHORT).show()
					}
					signin.visibility = View.GONE
				})
	}

}

