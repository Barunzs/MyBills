package bill.com.mybills.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bill.com.mybills.R

internal class EditProfileFragment: Fragment() {
	companion object {
		val TAG = EditProfileFragment.javaClass.name
	}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_editprofile, container, false)
    }
}