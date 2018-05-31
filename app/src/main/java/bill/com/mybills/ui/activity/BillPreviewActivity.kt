package bill.com.mybills.ui.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import bill.com.mybills.R
import bill.com.mybills.config.AppDAL
import bill.com.mybills.model.Item
import bill.com.mybills.ui.adapter.BillPreviewAdapter
import kotlinx.android.synthetic.main.activity_preview.*
import kotlinx.android.synthetic.main.fragment_bill.*


class BillPreviewActivity : AppCompatActivity() {

    private lateinit var billPreviewAdapter: BillPreviewAdapter
    private var appDAL: AppDAL? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        appDAL = applicationContext?.let { AppDAL(it) }
        billRecyclerView?.layoutManager = LinearLayoutManager(applicationContext)
        try {
            var billItemList: ArrayList<Item> = this.intent.extras.getParcelableArrayList("billItemList")
            //customer.text = billItemList[1].customerName
            billPreviewAdapter = BillPreviewAdapter()
            billPreviewAdapter.billItemArray = billItemList
            billRecyclerView?.adapter = billPreviewAdapter
        } catch (e: IllegalStateException) {
            Snackbar.make(billRecyclerView, "Please Generate Bill before Preview", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }


    }
}