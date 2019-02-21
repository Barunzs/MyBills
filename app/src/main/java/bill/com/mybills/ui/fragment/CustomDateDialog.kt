package bill.com.mybills.ui.fragment

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bill.com.mybills.R
import bill.com.mybills.ui.activity.BarChartActivity
import kotlinx.android.synthetic.main.custom_date.*


class CustomDateDialog : DialogFragment() {

    val myCalendar = Calendar.getInstance()
    var endDateStr = ""
    var startDatestr = ""

    companion object {
        fun newInstance(title: String): CustomDateDialog {
            val frag = CustomDateDialog()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
        searchBtn.setOnClickListener {
            //Toast.makeText(context,"startDatestr:"+startDatestr,Toast.LENGTH_LONG).show()

            val decorView = dialog
                    ?.window
                    ?.decorView

            val scaleDown = ObjectAnimator.ofPropertyValuesHolder(decorView,
                    PropertyValuesHolder.ofFloat("scaleX", 1.0f, 15.0f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.0f, 15.0f),
                    PropertyValuesHolder.ofFloat("alpha", 1.0f, 15.0f))
            scaleDown.addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator) {
                    val reportIntent = Intent(activity, BarChartActivity::class.java)
                    startActivity(reportIntent)
                }

                override fun onAnimationStart(animation: Animator) {

                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            scaleDown.duration = 1500
            scaleDown.start()
        }
    }

    var endDate: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        var month = monthOfYear
        month++
        endDateStr = "$dayOfMonth-$month-$year"
        end_date.setText(endDateStr)
    }
    var startDate: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        var month = monthOfYear
        month++
        startDatestr = "$dayOfMonth-$month-$year"
        start_date.setText(startDatestr)
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog as AlertDialog

        val decorView = getDialog()
                ?.window
                ?.decorView

        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(decorView,
                PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f))
        scaleDown.duration = 2000
        scaleDown.start()
    }




}