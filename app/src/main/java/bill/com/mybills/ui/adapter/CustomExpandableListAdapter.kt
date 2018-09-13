package bill.com.mybills.ui.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import bill.com.mybills.R
import bill.com.mybills.model.BillItem
import bill.com.mybills.ui.activity.WebviewActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class CustomExpandableListAdapter(val context: Context?, val expandableListTitle: List<String>,
								  val expandableListDetail: HashMap<String, ArrayList<BillItem>?>, val headerTitle: List<String>, val user: FirebaseUser?) : BaseExpandableListAdapter() {


	override fun hasStableIds(): Boolean {
		return false
	}


	override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
		return true
	}

	override fun getGroupView(listPosition: Int, isExpanded: Boolean,
							  convertView: View?, parent: ViewGroup): View {
		val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
		var convertView = convertView
		val listTitle = getGroup(listPosition) as String
		val billDate = expandableListTitle[listPosition]
		val formattedDate = Date(billDate.toLong())
		val dateString = dateFormat.format(formattedDate)
		if (convertView == null) {
			val layoutInflater = this.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
			convertView = layoutInflater.inflate(R.layout.list_group, null)
		}
		val listTitleTextView = convertView!!
				.findViewById<View>(R.id.listTitle) as TextView
		val dateTextView = convertView
				.findViewById<View>(R.id.billDate) as TextView
		val pdfDownload = convertView.findViewById<View>(R.id.pdf) as ImageView
		pdfDownload.setOnClickListener { v: View? ->
			val localFile = File(context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "bill.pdf")
			val storageReference = FirebaseStorage.getInstance().reference
			/*storageReference.child(user?.uid + "/" + billDate + "/bills").getFile(localFile).addOnFailureListener {
				Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
				val intent = Intent(context, WebviewActivity::class.java)
				intent.putExtra("URL", Uri.parse("file://" + localFile.absolutePath))
				context?.startActivity(intent)
			}.addOnSuccessListener {
				Toast.makeText(context, "success", Toast.LENGTH_LONG).show()
			}*/
			storageReference.child(user?.uid + "/" + billDate + "/" + "/bills" + "/bill.pdf").getDownloadUrl().addOnSuccessListener {
				Toast.makeText(context, "success", Toast.LENGTH_LONG).show()
				val intent = Intent(context, WebviewActivity::class.java)
				intent.putExtra("URL", it.toString())
				context?.startActivity(intent)
			}.addOnFailureListener {
				Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
			}
		}
		listTitleTextView.setTypeface(null, Typeface.BOLD)
		dateTextView.setTypeface(null, Typeface.BOLD)
		listTitleTextView.text = listTitle
		dateTextView.text = dateString
		return convertView
	}

	override fun getGroupCount(): Int {
		return this.expandableListTitle.size
	}


	override fun getChildrenCount(listPosition: Int): Int {
		return this.expandableListDetail[this.expandableListTitle[listPosition]]
				?.size!!
	}

	override fun getGroup(listPosition: Int): Any {
		return this.headerTitle[listPosition]
	}

	override fun getGroupId(listPosition: Int): Long {
		return listPosition.toLong()
	}

	override fun getChildView(listPosition: Int, expandedListPosition: Int,
							  isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
		var convertView = convertView
		val billItem = getChild(listPosition, expandedListPosition) as BillItem
		if (convertView == null) {
			val layoutInflater = this.context
					?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
			convertView = layoutInflater.inflate(R.layout.list_item, null)
		}
		val df = DecimalFormat("#.##")
		val expandedListTextView = convertView
				?.findViewById<View>(R.id.particulars) as TextView
		val weightTextView = convertView
				.findViewById<View>(R.id.weight) as TextView
		val amountTextView = convertView
				.findViewById<View>(R.id.amount) as TextView
		val imageView = convertView
				.findViewById<View>(R.id.ornamentImage) as ImageView
		expandedListTextView.text = billItem.particulars
		weightTextView.text = "${billItem.weight}g"
		amountTextView.text = "₹ " + df.format(billItem.amtGold)
		Picasso.with(context).load(billItem.itemUri).into(imageView)
		return convertView
	}


	override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
		return expandedListPosition.toLong()
	}


	override fun getChild(listPosition: Int, expandedListPosition: Int): BillItem? {
		return this.expandableListDetail[this.expandableListTitle[listPosition]]?.get(expandedListPosition)
	}
}