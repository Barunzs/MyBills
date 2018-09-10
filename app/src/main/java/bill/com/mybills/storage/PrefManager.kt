package bill.com.mybills.storage

import android.content.Context
import android.content.SharedPreferences

 class PrefManager {

     lateinit var pref: SharedPreferences
     lateinit var editor: SharedPreferences.Editor
     lateinit var _context: Context

     // shared pref mode
     internal var PRIVATE_MODE = 0

     // Shared preferences file name
     private val PREF_NAME = "MYBILL_WELCOME"

     private val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"

     companion object {

     }

      fun setup(context: Context) {
         this._context = context
         pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
         editor = pref.edit()
     }

     fun setFirstTimeLaunch(isFirstTime: Boolean) {
         editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
         editor.commit()
     }

     fun isFirstTimeLaunch(): Boolean {
         return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
     }
}