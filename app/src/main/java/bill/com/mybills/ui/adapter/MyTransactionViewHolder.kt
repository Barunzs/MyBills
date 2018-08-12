package bill.com.mybills.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import bill.com.mybills.R
import bill.com.mybills.model.BillItem
import bill.com.mybills.model.Item
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.mytransaction_item.view.*
import java.text.SimpleDateFormat
import java.util.*

internal class MyTransactionViewHolder(var parent: ViewGroup?) : RecyclerView.ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.mytransaction_item, parent, false)) {


    fun setBillItem(billItem: BillItem) {
        itemView.transactionIdTextView.text = billItem.customerName
        itemView.vouchernameTextView.text = billItem.particulars
        val total = billItem.amtGold + billItem.makingCharge
        itemView.voucherAmount.text = ("₹ $total").toString()
        val d = Date(billItem.date.toLong())
        val f = SimpleDateFormat("dd-MM-yyyy",Locale.US)
        itemView.activityDateTimeTextView.text = f.format(d)
        Picasso.with(itemView.context).load(billItem.itemUri).into(itemView.voucherImage)
    }

}