package bill.com.mybills.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import bill.com.mybills.R
import bill.com.mybills.model.Item
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.mytransaction_item.view.*

internal class MyTransactionViewHolder(var parent: ViewGroup?) : RecyclerView.ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.mytransaction_item, parent, false)) {


    fun setBillItem(billItem: Item) {
        itemView.transactionIdTextView.text = billItem.customerName
        itemView.vouchernameTextView.text = billItem.particulars
        val total = billItem.amtGold + billItem.makingCharge
        itemView.voucherAmount.text = ("₹ $total").toString()
        itemView.activityDateTimeTextView.text = billItem.date
        Picasso.with(itemView.context).load(billItem.itemUri).into(itemView.voucherImage)
    }

}