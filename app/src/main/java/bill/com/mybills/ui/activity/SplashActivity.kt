package bill.com.mybills.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import bill.com.mybills.R
import bill.com.mybills.config.AppDAL
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash.*




class SplashActivity : AppCompatActivity() {
	private var countdownTimer: CountDownTimer? = null
	private val appDAL get() = AppDAL(applicationContext)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_splash)
		loading.playAnimation()
		countdownTimer = object : CountDownTimer(3000, 1000) {

			override fun onTick(millisUntilFinished: Long) {
			}

			override fun onFinish() {
				val user = FirebaseAuth.getInstance().currentUser
				if (user!=null) {
					val intent = Intent(applicationContext, MainActivity::class.java)
					startActivity(intent)
					finish()
				} else {
					val intent = Intent(applicationContext, LoginActivity::class.java)
					startActivity(intent)
					finish()
				}
			}
		}.start()
	}

	override fun onDestroy() {
		super.onDestroy()
		if (countdownTimer != null) {
			countdownTimer?.cancel()
		}
	}
}