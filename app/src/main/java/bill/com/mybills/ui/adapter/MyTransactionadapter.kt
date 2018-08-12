package bill.com.mybills.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import bill.com.mybills.model.BillItem
import bill.com.mybills.model.Item
import java.util.ArrayList

internal class MyTransactionadapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var billItemList: ArrayList<BillItem> =  ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyTransactionViewHolder(parent)
    }

    override fun getItemCount(): Int = billItemList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyTransactionViewHolder -> {
                holder.setIsRecyclable(false)
                holder.setBillItem(billItemList[position])
            }
        }
    }
}