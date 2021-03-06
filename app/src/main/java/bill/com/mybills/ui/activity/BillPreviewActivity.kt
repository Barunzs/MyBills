package bill.com.mybills.ui.activity

import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import bill.com.mybills.R
import bill.com.mybills.config.AppDAL
import bill.com.mybills.model.BusinessProfile
import bill.com.mybills.model.Item
import bill.com.mybills.task.CreatePDFTask
import bill.com.mybills.ui.adapter.BillPreviewAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_preview.*
import java.io.File
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat


class BillPreviewActivity : AppCompatActivity() {

	private lateinit var billPreviewAdapter: BillPreviewAdapter
	private var appDAL: AppDAL? = null
	private var docRef: DocumentReference? = null
	private var billItemList: ArrayList<Item> = ArrayList()
	private var user: FirebaseUser? = null
	private var db: FirebaseFirestore? = null
	private var businessProfile: BusinessProfile? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_preview)
		appDAL = applicationContext?.let { AppDAL(it) }
		billRecyclerView?.layoutManager = LinearLayoutManager(applicationContext)
		db = FirebaseFirestore.getInstance()
		user = FirebaseAuth.getInstance().currentUser
		docRef = user?.uid?.let { db?.collection(it)?.document("Business Profile") }
		try {
			billItemList = this.intent.extras.getParcelableArrayList("billItemList")
			if (billItemList.size > 0) {
				customer.text = billItemList[0].customerName
				billPreviewAdapter = BillPreviewAdapter()
				billPreviewAdapter.billItemArray = billItemList
				billRecyclerView?.adapter = billPreviewAdapter
				billRecyclerView.scrollToPosition(billItemList.size - 1);
			} else {
				tax.visibility = View.GONE
				sendBill.visibility = View.GONE
				Snackbar.make(billRecyclerView, "Please Generate Bill before Preview", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show()
				return
			}
		} catch (e: Throwable) {
			Snackbar.make(billRecyclerView, "Please Generate Bill before Preview", Snackbar.LENGTH_LONG)
					.setAction("Action", null).show()
		}
		val df = DecimalFormat("#.##")
		df.roundingMode = RoundingMode.CEILING
		var totalAmt = 0.0
		var cgst = 0.0
		var sgst = 0.0
		for (item in billItemList) {
			totalAmt += (item.cgst + item.sgst + item.amtGold + item.makingCharge)
			cgst += item.cgst
			sgst += item.sgst
		}
		totalAmount.text = "₹ " + df.format(totalAmt)
		sgstAmt.text = "₹ " + df.format(sgst)
		cgstAmt.text = "₹ " + df.format(cgst)
		sendBill.setOnClickListener { sendPDF(billItemList) }
		docRef?.get()?.addOnSuccessListener { documentSnapshot ->
			if (documentSnapshot.exists()) {
				businessProfile = documentSnapshot.toObject(BusinessProfile::class.java)
			} else {
				Toast.makeText(applicationContext, "No Profile Data Found", Toast.LENGTH_LONG).show()
			}
		}
	}

	private fun sendPDF(itemList: ArrayList<Item>) {
		/*val path = Environment.getExternalStorageDirectory().absolutePath + "/Trinity/PDF Files"
		val dir = File(path)
		if (dir.exists()) {
			dir.mkdirs()
		}*/
		try {
			val file = File(applicationContext?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "bill.pdf")
			businessProfile?.let { CreatePDFTask(this@BillPreviewActivity, file, itemList, progressPdf, it).execute() }
			sendBill.visibility = View.GONE
		} catch (e: IOException) {
			e.printStackTrace()
		}

	}

	override fun onResume() {
		super.onResume()
		if (billItemList.size > 0)
			sendBill.visibility = View.VISIBLE
	}


}