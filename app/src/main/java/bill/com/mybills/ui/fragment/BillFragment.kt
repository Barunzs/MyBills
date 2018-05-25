package bill.com.mybills.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import bill.com.mybills.R
import bill.com.mybills.config.AppDAL
import bill.com.mybills.model.Item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_bill.*
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat


internal class BillFragment : Fragment() {

    private var appDAL: AppDAL? = null
    private var gst = 0.0
    private var amountOfGold = 0.0

    companion object {
        val TAG = BillFragment.javaClass.name

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bill, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //creating new file path
        appDAL = context?.let { AppDAL(it) }
        initEventsListeners()


    }

    private fun initEventsListeners() {
        makingCharge.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(makingCharge: Editable) {

                try {
                    gst = ((amtofgold.text.toString().toDouble() + makingCharge.toString().toDouble()) * 1.5) / 100
                    sgstrate.setText(gst.toString())
                    cgstrate.setText(gst.toString())
                } catch (e: NumberFormatException) {
                    Toast.makeText(context, "Please enter all fields", Toast.LENGTH_LONG).show()
                    sgstrate.setText(gst.toString())
                    cgstrate.setText(gst.toString())
                }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })

        rateofgold.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(rateOfGold: Editable) {

                try {
                    amountOfGold = weight.text.toString().toDouble() * (rateOfGold.toString().toDouble() / 10)
                    amtofgold.setText(amountOfGold.toString())
                } catch (e: NumberFormatException) {
                    Toast.makeText(context, "Please enter all fields", Toast.LENGTH_LONG).show()
                    amtofgold.setText(amountOfGold.toString())
                }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })

        generatebill.setOnClickListener { generateBill(it) }

    }

    private fun generateBill(view: View) {
        try {
            val item = Item(particular?.text.toString(), weight.text.toString().toDouble(), rateofgold.text.toString().toDouble(), weight.text.toString().toDouble() *(rateofgold.text.toString().toDouble()/10), makingCharge.text.toString().toDouble())
            val totalAmt = amountOfGold + makingCharge.text.toString().toDouble() + gst + gst
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            total.text = "₹ " + df.format(totalAmt)
            val gson = Gson()
            val type = object : TypeToken<Item>() {
            }.type
            val itemJson = gson.toJson(item, type)
            Log.d(TAG, "itemJson::$itemJson")
            appDAL?.billItemJson = itemJson
        } catch (e: NumberFormatException) {
            Snackbar.make(view, "Please Enter all Fields", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }


    private fun viewPrevious() {
        val fm = activity?.supportFragmentManager
        fm?.popBackStackImmediate()
    }


    /*private fun addNewItem() {
        val ft = fragmentManager?.beginTransaction()?.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        val fragment = BillFragment()
        ft?.add(R.id.frameLayout, fragment)
        ft?.addToBackStack(null)
        ft?.commit()
    }*/



}