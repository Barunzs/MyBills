package bill.com.mybills.util

import android.media.MediaPlayer
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import bill.com.mybills.R
import bill.com.mybills.ui.fragment.LoadingDialogFragment
import bill.com.mybills.ui.fragment.TaskCompleteDialogFragment
import java.util.*

object Util {

    private val dialogs = Stack<DialogFragment>()

    fun showloading(activity: FragmentActivity): DialogFragment {
        val ft: FragmentTransaction = activity.supportFragmentManager.beginTransaction()
        val prev: Fragment? = activity.supportFragmentManager.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val dialogFragment: DialogFragment = LoadingDialogFragment()
        dialogs.push(dialogFragment)
        dialogFragment.show(ft, "dialog")
        return dialogFragment
    }


    fun showTaskComplete(activity: FragmentActivity): DialogFragment {
        val ft: FragmentTransaction = activity.supportFragmentManager.beginTransaction()
        val prev: Fragment? = activity.supportFragmentManager.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val dialogFragment: DialogFragment = TaskCompleteDialogFragment()
        dialogFragment.show(ft, "TaskCompleteDialogFragment")
        dialogs.push(dialogFragment)
        return dialogFragment
    }
}