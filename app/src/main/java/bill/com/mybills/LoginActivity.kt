package bill.com.mybills

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import bill.com.mybills.config.AppDAL
import bill.com.mybills.ui.activity.MainActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
	private val appDAL get() = AppDAL(applicationContext)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)
		initEventsListeners()
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
		if (input_email.text.toString() == "akash.205@gmail.com" && input_password.text.toString() == "akash123") {
			appDAL.isLoginSucess = true
			val intent = Intent(applicationContext, MainActivity::class.java)
			startActivity(intent)
			finish()
		} else {
			Snackbar.make(view, "Invalid email or password", Snackbar.LENGTH_LONG)
					.setAction("Action", null).show()
		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return when (item.itemId) {
			R.id.action_share -> true
			R.id.action_preview -> true
			else -> super.onOptionsItemSelected(item)
		}
	}
}
