package bill.com.mybills.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import bill.com.mybills.R
import bill.com.mybills.config.AppDAL
import bill.com.mybills.model.Item
import bill.com.mybills.ui.adapter.BillPreviewAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_preview.*



class BillPreviewActivity  : AppCompatActivity() {

	private lateinit var billPreviewAdapter: BillPreviewAdapter
	private var appDAL: AppDAL? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
		appDAL = applicationContext?.let { AppDAL(it) }
		billRecyclerView?.layoutManager = LinearLayoutManager(applicationContext)
		val itemJsonDB = appDAL?.billItemJson
		var billItemList : ArrayList<Item> = ArrayList()
		val gson = Gson()
		val billItem = gson.fromJson(itemJsonDB, Item::class.java)
		billItemList.add(billItem)
		billPreviewAdapter = BillPreviewAdapter()
		billPreviewAdapter.billItemArray = billItemList
		billRecyclerView?.adapter = billPreviewAdapter

    }
}