package bill.com.mybills.ui.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import bill.com.mybills.R
import bill.com.mybills.model.BillItem
import bill.com.mybills.model.BusinessProfile
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class CustomExpandableListAdapter(val context: Context?, val expandableListTitle: List<String>,
                                  val expandableListDetail: HashMap<String, ArrayList<BillItem>?>, val headerTitle: List<String>, val user: FirebaseUser?, val businessProfile: BusinessProfile?) : BaseExpandableListAdapter() {


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
        val loadingData: LottieAnimationView = convertView.findViewById(R.id.loadingdata) as LottieAnimationView
        pdfDownload.setOnClickListener { v: View? ->
            loadingData.visibility = View.VISIBLE
            val storageReference = FirebaseStorage.getInstance().reference
            var billPdfFilePath = user?.uid + "/" + billDate + "/" + "/bills/" + businessProfile?.orgName?.trim() + "_" + listTitle.trim()
            storageReference.child("$billPdfFilePath.pdf").downloadUrl.addOnSuccessListener {
                Toast.makeText(context, "success", Toast.LENGTH_LONG).show()
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(it.toString()), "application/pdf")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                val newIntent = Intent.createChooser(intent, "Open File")
                loadingData.visibility = View.GONE
                try {
                    context?.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // Instruct the user to install a PDF reader here, or something
                }

            }.addOnFailureListener {
                loadingData.visibility = View.GONE
                billPdfFilePath = user?.uid + "/" + billDate + "/" + "/bills/bill"
                storageReference.child("$billPdfFilePath.pdf").downloadUrl.addOnSuccessListener {
                    Toast.makeText(context, "success", Toast.LENGTH_LONG).show()
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(Uri.parse(it.toString()), "application/pdf")
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    val newIntent = Intent.createChooser(intent, "Open File")
                    loadingData.visibility = View.GONE
                    try {
                        context?.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        // Instruct the user to install a PDF reader here, or something
                    }

                }.addOnFailureListener {
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
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
        Picasso.get().load(billItem.itemUri).into(imageView)
        return convertView
    }


    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }


    override fun getChild(listPosition: Int, expandedListPosition: Int): BillItem? {
        return this.expandableListDetail[this.expandableListTitle[listPosition]]?.get(expandedListPosition)
    }
}