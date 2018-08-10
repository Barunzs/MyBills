package bill.com.mybills.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bill.com.mybills.R
import bill.com.mybills.config.AppDAL
import bill.com.mybills.model.Item
import bill.com.mybills.ui.adapter.MyTransactionadapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_mytransaction.*
import java.util.ArrayList

class MyBillTransactionFragment: Fragment() {

    private var appDAL: AppDAL? = null
    private var billItemList: ArrayList<Item> = ArrayList()
    private lateinit var transactionAdapter: MyTransactionadapter

    companion object {
        val TAG = MyBillTransactionFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDAL = context?.let { AppDAL(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mytransaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        billItemList = getBillItemList() as ArrayList<Item>
        goalVoucherRecyclerView?.layoutManager = LinearLayoutManager(context)
        transactionAdapter = MyTransactionadapter()
        goalVoucherRecyclerView?.adapter = transactionAdapter
        transactionAdapter.billItemList = billItemList
        completedGoalsUpdateProgressBar.visibility = View.GONE
    }


    private fun getBillItemList(): java.util.ArrayList<*> {
        val itemListJsonDB = appDAL?.billItemJson
        val type = object : TypeToken<ArrayList<String>>() {
        }.type
        val gson = Gson()
        val billItemListJson = gson.fromJson<java.util.ArrayList<String>>(itemListJsonDB, type)
        val billItemListObj = java.util.ArrayList<Item>()
        if (billItemListJson != null && billItemListJson.size > 0) {
            val itemType = object : TypeToken<Item>() {
            }.type
            val itemGson = Gson()
            for (billItemObj in billItemListJson) {
                val billItem = itemGson.fromJson<Item>(billItemObj, itemType)
                billItemListObj.add(billItem)
            }
        }
        return billItemListObj
    }
}