package bill.com.mybills.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import bill.com.mybills.R
import bill.com.mybills.barcodevision.BarcodeCaptureActivity
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

	companion object {
		val TAG = ScanFragment.javaClass.name

	}

	private fun initEventListeners() {
		scan?.setOnClickListener { onClickScanButton(it) }
	}

	private fun onClickScanButton(view: View) {
		val intent = Intent(activity, BarcodeCaptureActivity::class.java)
		intent.putExtra(BarcodeCaptureActivity.AutoFocus, true)
		startActivityForResult(intent, 4)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (data != null) {
			val barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject) as Barcode
			Toast.makeText(context, barcode.displayValue, Toast.LENGTH_LONG).show()
		}

	}

}