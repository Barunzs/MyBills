package bill.com.mybills.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import bill.com.mybills.model.BillItem
import android.widget.TextView
import android.view.LayoutInflater
import bill.com.mybills.R

import android.graphics.Typeface
import bill.com.mybills.R.id.weight
import java.text.DecimalFormat


class CustomExpandableListAdapter(val context: Context?, val expandableListTitle: List<String>,
                                  val expandableListDetail: HashMap<String, ArrayList<BillItem>?>) : BaseExpandableListAdapter() {


    override fun hasStableIds(): Boolean {
        return false
    }


    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }

    override fun getGroupView(listPosition: Int, isExpanded: Boolean,
                              convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val listTitle = getGroup(listPosition) as String
        if (convertView == null) {
            val layoutInflater = this.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.list_group, null)
        }
        val listTitleTextView = convertView!!
                .findViewById<View>(R.id.listTitle) as TextView
        listTitleTextView.setTypeface(null, Typeface.BOLD)
        listTitleTextView.text = listTitle
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
        return this.expandableListTitle[listPosition]
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
        expandedListTextView.text = billItem.particulars
        weightTextView.text = "${billItem.weight}g"
        amountTextView.text =  "₹ " + df.format(billItem.amtGold)
        return convertView
    }


    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }


    override fun getChild(listPosition: Int, expandedListPosition: Int): BillItem? {
        return this.expandableListDetail[this.expandableListTitle[listPosition]]?.get(expandedListPosition)
    }
}