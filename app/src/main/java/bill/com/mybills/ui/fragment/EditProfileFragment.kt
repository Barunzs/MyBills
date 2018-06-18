package bill.com.mybills.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import bill.com.mybills.R
import kotlinx.android.synthetic.main.fragment_editprofile.*



internal class EditProfileFragment: Fragment() {
	companion object {
		val TAG = EditProfileFragment.javaClass.name
	}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_editprofile, container, false)
    }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		//creating new file path
		initEventListeners()
	}




	override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater);
        menu?.clear();
    }

	private fun initEventListeners() {
		updateprofileButton?.setOnClickListener { onClickScanButton(it) }
	}

	private fun onClickScanButton(view: View) {
		val permissionCheck = context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CALL_PHONE) }
		if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
			activity?.let { ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.CALL_PHONE), 2) }
		} else {
			val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "333,333"))
			startActivity(intent)
		}
	}
}