package bill.com.mybills.config

import android.content.Context

internal class AppDAL(val context: Context)  {

	private val billingSharedPref get() = context.getSharedPreferences("BILLS", Context.MODE_PRIVATE)

	var isLoginSucess: Boolean
		get() = billingSharedPref.getBoolean("IS_LOGIN_SUCCESS", false)
		set(value) = billingSharedPref.edit().putBoolean("IS_LOGIN_SUCCESS", value).apply()
}