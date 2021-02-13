package bill.com.mybills.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import bill.com.mybills.model.BillItem
import java.util.ArrayList
import java.util.HashMap

internal class MyTransactionadapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var billItemList: ArrayList<BillItem> =  ArrayList()
    var billitemsMap = HashMap<String, ArrayList<BillItem>?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyTransactionViewHolder(parent)
    }

    override fun getItemCount(): Int = billitemsMap.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyTransactionViewHolder -> {
                holder.setIsRecyclable(false)
                holder.setBillItem(billItemList[position])
            }
        }
    }
}