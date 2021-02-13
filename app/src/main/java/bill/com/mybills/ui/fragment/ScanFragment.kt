package bill.com.mybills.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import bill.com.mybills.R
import bill.com.mybills.barcodevision.BarcodeCaptureActivity
import bill.com.mybills.ui.activity.WelcomeActivity
import com.google.android.gms.vision.barcode.Barcode
import kotlinx.android.synthetic.main.fragment_scan.*


internal class ScanFragment : Fragment() {


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_scan, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initEventListeners()
	}


	private fun initEventListeners() {
		scan?.setOnClickListener { onClickScanButton(it) }
	}

	private fun onClickScanButton(view: View) {
		val intent = Intent(activity, BarcodeCaptureActivity::class.java)
		intent.putExtra(BarcodeCaptureActivity.AutoFocus, true)
		startActivityForResult(intent, 4)


	}

}