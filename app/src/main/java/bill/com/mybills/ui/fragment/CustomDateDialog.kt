package bill.com.mybills.ui.fragment

import android.support.v4.app.DialogFragment
import android.view.WindowManager
import android.widget.EditText
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import bill.com.mybills.R
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.util.Log
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.custom_date.*


class CustomDateDialog: DialogFragment() {

    val myCalendar = Calendar.getInstance()

    companion object {
        fun newInstance(title: String): CustomDateDialog {
            val frag = CustomDateDialog()
            val args = Bundle()
            args.putString("title", title)
            frag.arguments = args
            return frag
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_Dark)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.custom_date, container)
    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        start_date.setOnClickListener {
            DatePickerDialog(context, startDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        end_date.setOnClickListener {
            DatePickerDialog(context, endDate, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    var endDate: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        end_date.setText("$dayOfMonth-$monthOfYear-$year")
    }
    var startDate: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        start_date.setText("$dayOfMonth-$monthOfYear-$year")
    }


}