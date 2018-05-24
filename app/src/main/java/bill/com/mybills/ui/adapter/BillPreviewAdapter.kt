package bill.com.mybills.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import bill.com.mybills.model.Item

internal class BillPreviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


	var billItemArray: ArrayList<Item> = ArrayList()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return BillPreviewViewHolder(parent)
	}

	override fun getItemCount(): Int {
		return billItemArray.size
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		when (holder) {
			is BillPreviewViewHolder -> {
				holder.setIsRecyclable(false)
				holder.setBillItem(billItemArray.get(position))
			}
		}
	}
}