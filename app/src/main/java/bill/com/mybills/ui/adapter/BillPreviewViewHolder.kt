package bill.com.mybills.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import bill.com.mybills.R
import bill.com.mybills.model.Item
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.bill_preview_item.view.*
import java.math.RoundingMode
import java.text.DecimalFormat

internal class BillPreviewViewHolder(val parent: ViewGroup?) : RecyclerView.ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.bill_preview_item, parent, false)) {

    fun setBillItem(billItem: Item) {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        itemView.itemName?.text = billItem.particulars
        itemView.itemWeight?.text = df.format(billItem.weight) + "g"
        itemView.goldAmt?.text = "₹ " + df.format(billItem.amtGold)
        itemView.makingAmt?.text = "₹ " + df.format(billItem.makingCharge)
        itemView.othersAmt?.text = "₹ " + df.format(billItem.otherItemPrice)
        itemView.otherItemName?.text = billItem.otherItemDesc
        Picasso.get().load(billItem.itemUri).into(itemView.productUploadIcon)
    }
}