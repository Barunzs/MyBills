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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_mytransaction.*
import java.util.ArrayList
import android.util.Log
import bill.com.mybills.model.BillItem
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.*
import com.google.firebase.firestore.QuerySnapshot
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull




class MyBillTransactionFragment : Fragment() {

    private var appDAL: AppDAL? = null
    private var billItemList: ArrayList<BillItem> = ArrayList()
    private lateinit var transactionAdapter: MyTransactionadapter
    private var docRef: DocumentReference? = null
    private var user: FirebaseUser? = null
    private var db: FirebaseFirestore? = null

    companion object {
        val TAG = MyBillTransactionFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDAL = context?.let { AppDAL(it) }
        db = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance().currentUser
        docRef = user?.uid?.let { db?.collection(it)?.document("Bill") }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mytransaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //billItemList = getBillItemList() as ArrayList<BillItem>
        getBillItemList()
        goalVoucherRecyclerView?.layoutManager = LinearLayoutManager(context)
        transactionAdapter = MyTransactionadapter()
    }


    private fun getBillItemList() {
        /*val itemListJsonDB = appDAL?.billItemJson
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
        }*/
        /*  docRef?.get()?.addOnSuccessListener { documentSnapshot ->
              if (documentSnapshot.exists()) {
                  //val businessProfile = documentSnapshot.toObject(Item::class.java)
                  Toast.makeText(context, " Data Found${documentSnapshot.data.get("goldRate")}", Toast.LENGTH_LONG).show()

              } else {
                  Toast.makeText(context, "No Profile Data Found", Toast.LENGTH_LONG).show()
              }
          }*/
        /*docRef?.get()?.addOnCompleteListener { documentSnapshot ->
            if (documentSnapshot.isSuccessful) {

            } else {
                Toast.makeText(context, "No Profile Data Found", Toast.LENGTH_LONG).show()
            }
        }*/
       docRef
                ?.get()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in arrayOf(task.result)) {
                            if (document.exists())
                                Log.d(TAG, document.id + " => " + document.data)
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.exception)
                    }
                }

       /* val capitalCities = db?.collection("Bill")?.document()?.get()

        for (doc in arrayOf(capitalCities)) {
            *//*Log.d(TAG, "HHHHH: ${doc?.document()?.get()?.addOnCompleteListener(OnCompleteListener {
                if (it.result.exists())
                    Log.w(TAG, "Hello:" + it.result.data)
            })}")*//*
            Log.w(TAG, "HHH:"+doc?.addOnCompleteListener(OnCompleteListener {
               // Log.w(TAG, "helllo"+it.result.data)
            }))
        }
*/
        val list = ArrayList<String>()
        db?.collection("Wban5NWAP8aQegqpVt1avLrY5at1")?.document("9090909090")?.parent?.get()?.addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    list.add(document.id)
                }
                Log.d(TAG, list.toString())
            } else {
                Log.d(TAG, "Error getting documents: ", task.exception)
            }
        })
        //docRef?.addSnapshotListener( EventListener())

        val notesList = mutableListOf<BillItem>()

        user?.uid?.let {
            db?.collection(it)?.document("9090909090")?.collection("Bill Items")
                    ?.addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                        completedGoalsUpdateProgressBar.visibility = View.GONE
                        if (e != null) {
                            Log.w(TAG, "listen:error", e)
                            return@EventListener
                        }

                        for (doc in snapshots) {
                            val note = doc.toObject(BillItem::class.java)
                            //Log.w(TAG, "doc:"+doc.data)
                            //note.id = doc.id
                            notesList.add(note)
                        }
                        transactionAdapter.billItemList = notesList as ArrayList<BillItem>
                        goalVoucherRecyclerView?.adapter = transactionAdapter
                        // instead of simply using the entire query snapshot
                        // see the actual changes to query results between query snapshots (added, removed, and modified)
                        for (dc in snapshots.documentChanges) {
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> Log.d(TAG, "New city: " + dc.document.data)
                                DocumentChange.Type.MODIFIED -> Log.d(TAG, "Modified city: " + dc.document.data)
                                DocumentChange.Type.REMOVED -> Log.d(TAG, "Removed city: " + dc.document.data)
                            }
                        }
                    })
        }

    }
}