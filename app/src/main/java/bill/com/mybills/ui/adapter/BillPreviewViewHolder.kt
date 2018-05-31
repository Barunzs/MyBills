package bill.com.mybills.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import bill.com.mybills.R
import bill.com.mybills.model.Item
import kotlinx.android.synthetic.main.listview_bill_item.view.*

internal class BillPreviewViewHolder(val parent: ViewGroup?) : RecyclerView.ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.listview_bill_item, parent, false))  {

	fun setBillItem(billItem: Item) {
		itemView.itemName?.text = billItem.particulars
		itemView.itemWeight?.text = billItem.weight.toString()
		itemView.goldAmt?.text = billItem.amtGold.toString()
		itemView.makingAmt?.text = billItem.makingCharge.toString()
	}
}