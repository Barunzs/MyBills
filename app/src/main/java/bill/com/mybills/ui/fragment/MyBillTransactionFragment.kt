package bill.com.mybills.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import bill.com.mybills.R
import bill.com.mybills.config.AppDAL
import bill.com.mybills.model.BillItem
import bill.com.mybills.ui.adapter.MyTransactionadapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.fragment_mytransaction.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.Toast
import bill.com.mybills.ui.adapter.CustomExpandableListAdapter
import bill.com.mybills.R.id.expandableListView
import bill.com.mybills.model.BusinessProfile
import com.google.firebase.firestore.CollectionReference


class MyBillTransactionFragment : Fragment() {

    private var appDAL: AppDAL? = null
    private lateinit var transactionAdapter: MyTransactionadapter
    private var docRef: DocumentReference? = null
    private var user: FirebaseUser? = null
    private var db: FirebaseFirestore? = null
    private var registration: ListenerRegistration? = null;
    var expandableListAdapter: ExpandableListAdapter? = null
    private var businessprofileDocument: DocumentReference? = null
    private var businessprofile: BusinessProfile? = null

    companion object {
        val TAG = MyBillTransactionFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDAL = context?.let { AppDAL(it) }
        db = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance().currentUser
        docRef = user?.uid?.let { db?.collection(it)?.document("Bill") }
        businessprofileDocument = user?.uid?.let { db?.collection(it)?.document("Business Profile") }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mytransaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionAdapter = MyTransactionadapter()
        businessprofileDocument?.get()?.addOnSuccessListener { documentSnapshot ->
            if (this.isVisible) {
                if (documentSnapshot.exists()) {
                    businessprofile = documentSnapshot.toObject(BusinessProfile::class.java)
                } else {
                    Toast.makeText(context, "No Profile Data Found", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showalert()
    }


    private fun getBillItemList(phoneNo: String) {
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
        /* docRef
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
                  }*/

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
        /*val list = ArrayList<String>()
        db?.collection("Wban5NWAP8aQegqpVt1avLrY5at1")?.document("9090909090")?.parent?.get()?.addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    list.add(document.id)
                }
                Log.d(TAG, list.toString())
            } else {
                Log.d(TAG, "Error getting documents: ", task.exception)
            }
        })*/
        //docRef?.addSnapshotListener( EventListener())

        /*val citiesRef = user?.uid?.let { db?.collection(it)?.document(phoneNo)?.collection("Bill Items")?.whereEqualTo("customerName", "Barun")}

        citiesRef?.addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
            for (doc in snapshots) {
                val note = doc.toObject(BillItem::class.java)
                Log.d(TAG, "note::$note")
            }

        })*/

        val billitemList = mutableListOf<BillItem>()
        val expandableListTitle = mutableListOf<String>()
        val billNoList = mutableListOf<String>()
        user?.uid?.let { it ->
            registration = db?.collection(it)?.document(phoneNo)?.collection("Bill Items")?.orderBy("date", Query.Direction.DESCENDING)
                    ?.addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                        completedGoalsUpdateProgressBar.visibility = View.GONE
                        if (e != null) {
                            Log.w(TAG, "listen:error", e)
                            return@EventListener
                        }
                        val billitemsMap = HashMap<String, ArrayList<BillItem>?>()
                        if (snapshots != null) {
                            for (doc in snapshots) {
                                val billItem = doc.toObject(BillItem::class.java)
                                billitemList.add(billItem)
                                if (!billNoList.contains(billItem.billNo)) {
                                    expandableListTitle.add(billItem.customerName)
                                    billNoList.add(billItem.billNo)
                                }
                                if (billitemsMap.containsKey(billItem.billNo)) {
                                    val billItemsList = billitemsMap[billItem.billNo]
                                    billItemsList?.add(billItem)
                                    billitemsMap[billItem.billNo] = billItemsList
                                } else {
                                    val billItemsList = ArrayList<BillItem>()
                                    billItemsList.add(billItem)
                                    billitemsMap[billItem.billNo] = billItemsList
                                }
                            }
                        }
                        expandableListAdapter = CustomExpandableListAdapter(context, billNoList, billitemsMap,expandableListTitle,user,businessprofile,requireActivity())
                        expandableListView.setAdapter(expandableListAdapter)
                        expandableListView.setOnGroupCollapseListener { groupPosition ->
                        }
                        expandableListView.setOnGroupExpandListener { groupPosition ->

                        }
                        for (dc in snapshots?.documentChanges!!) {
                            when (dc.type) {
                                DocumentChange.Type.ADDED ->
                                    Log.d(TAG, "New city: " + dc.document.data)
                                DocumentChange.Type.MODIFIED ->
                                    Log.d(TAG, "Modified city: " + dc.document.data)
                                DocumentChange.Type.REMOVED ->
                                    Log.d(TAG, "Removed city: " + dc.document.data)
                            }
                        }
                    })
        }

    }

    private fun showalert() {
        val builder = context?.let { context?.let { it1 -> AlertDialog.Builder(it1, R.style.MyDialogTheme) } }
        val inputPhone = EditText(context)
        inputPhone.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_CLASS_PHONE
        inputPhone.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(10))
        builder?.setView(inputPhone)
        builder?.setTitle("SEARCH BILL")
        builder?.setMessage("Please enter Customer Phone No")
        builder?.setPositiveButton("Search") { dialog, which ->
            Log.d(TAG, "YES: ")
            val customerPhone = inputPhone.text.toString()
            if (customerPhone.isNotEmpty())
                getBillItemList(customerPhone)
        }
        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
    }

    override fun onStop() {
        super.onStop()
        registration?.remove()
    }
}