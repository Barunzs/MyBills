package bill.com.mybills.ui.activity

import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import bill.com.mybills.R
import bill.com.mybills.model.BillItem
import bill.com.mybills.reportdata.*
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.model.GradientColor
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.fragment_mytransaction.*
import java.util.*
import java.util.function.Consumer


class BarChartActivity : DemoBase(), SeekBar.OnSeekBarChangeListener, OnChartValueSelectedListener {

    private var chart: BarChart? = null

    //private var seekBarX: SeekBar? = null
    //private var seekBarY: SeekBar? = null
    private var tvX: TextView? = null
    private var tvY: TextView? = null
    private val onValueSelectedRectF = RectF()
    private val TAG = "BarChartActivity"
    private var user: FirebaseUser? = null
    private var db: FirebaseFirestore? = null
    private val values = ArrayList<BarEntry>()
    private val billitemsMap = HashMap<String, Double?>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_report)

        title = "BarChartActivity"

        user = FirebaseAuth.getInstance().currentUser
        db = FirebaseFirestore.getInstance()

        tvX = findViewById(R.id.tvXMax)
        tvY = findViewById(R.id.tvYMax)


        //seekBarX = findViewById(R.id.seekBar1)
        //seekBarY = findViewById(R.id.seekBar2)

        //seekBarY?.setOnSeekBarChangeListener(this)
        //seekBarX?.setOnSeekBarChangeListener(this)

        chart = findViewById(R.id.chart1)
        chart?.setOnChartValueSelectedListener(this)

        chart?.setDrawBarShadow(false)
        chart?.setDrawValueAboveBar(true)

        chart?.description?.isEnabled = false

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart?.setMaxVisibleValueCount(60)

        // scaling can now only be done on x- and y-axis separately
        chart?.setPinchZoom(false)

        chart?.setDrawGridBackground(false)
        // chart.setDrawYLabels(false);

        val xAxisFormatter = DayAxisValueFormatter(chart!!)

        val xAxis = chart?.xAxis
        xAxis?.position = XAxis.XAxisPosition.BOTTOM
        xAxis?.typeface = tfLight
        xAxis?.setDrawGridLines(false)
        xAxis?.granularity = 1f // only intervals of 1 day
        xAxis?.labelCount = 7
        xAxis?.valueFormatter = xAxisFormatter

        val custom = MyValueFormatter("$")

        val leftAxis = chart?.axisLeft
        leftAxis?.typeface = tfLight
        leftAxis?.setLabelCount(8, false)
        leftAxis?.valueFormatter = custom
        leftAxis?.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis?.spaceTop = 15f
        leftAxis?.axisMinimum = 0f // this replaces setStartAtZero(true)

        val rightAxis = chart?.axisRight
        rightAxis?.setDrawGridLines(false)
        rightAxis?.typeface = tfLight
        rightAxis?.setLabelCount(8, false)
        rightAxis?.valueFormatter = custom
        rightAxis?.spaceTop = 15f
        rightAxis?.axisMinimum = 0f // this replaces setStartAtZero(true)

        val l = chart?.legend
        l?.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l?.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l?.orientation = Legend.LegendOrientation.HORIZONTAL
        l?.setDrawInside(false)
        l?.form = Legend.LegendForm.SQUARE
        l?.formSize = 12f
        l?.textSize = 12f
        l?.xEntrySpace = 4f

        val mv = XYMarkerView(this, xAxisFormatter)
        mv.chartView = chart // For bounds control
        chart?.marker = mv // Set the marker to the chart

        // setting data


        // chart.setDrawLegend(false);
    }

    override fun onStart() {
        super.onStart()
        billitemsMap.clear()
        user?.uid?.let {
            val startDate = intent.getStringExtra("start_date")
            val endDate = intent.getStringExtra("end_date")
            Log.d(TAG, "startDate::$startDate")
            Log.d(TAG, "endDate::$endDate")
            db?.collection("Barun")?.get()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null) {
                        val documentIdList = mutableListOf<String>()
                        document.forEach { queryDocumentSnapshot: QueryDocumentSnapshot? ->
                            queryDocumentSnapshot?.id?.let { docmentStr ->
                                documentIdList.add(docmentStr)
                            }
                        }
                        documentIdList.forEach(Consumer { t ->
                            Log.d(TAG, "inside outer loop::$t")
                            startDate?.let { it1 ->
                                endDate?.let { it2 ->
                                    db?.collection(it)?.document(t)?.collection("Bill Items")?.whereGreaterThanOrEqualTo("date", it1)?.whereLessThanOrEqualTo("date", it2)
                                            ?.addSnapshotListener(EventListener { snapshots, e ->
                                                if (e != null) {
                                                    Log.e(TAG, "listen:error", e)
                                                    return@EventListener
                                                }
                                                if (snapshots != null) {
                                                    for (doc in snapshots) {
                                                        val billItem = doc.toObject(BillItem::class.java)
                                                        if (billitemsMap.containsKey(billItem.billNo)) {
                                                            val billItemweight = billitemsMap[billItem.billNo]
                                                            billitemsMap[billItem.billNo] = billItemweight?.plus(billItem.weight)
                                                        } else {
                                                            billitemsMap[billItem.billNo] = billItem.weight
                                                        }
                                                        Log.d(TAG, "inside inner loop::" + billItem.billNo)
                                                    }
                                                    setData(billitemsMap)
                                                    chart?.invalidate()

                                                }
                                            })
                                }
                            }
                        })
                    } else {
                        Log.d(TAG, "No such document")
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.exception)
                }
            }
        }
    }


    private fun setData(billitemsMap: HashMap<String, Double?>) {
        var start = 1f
        billitemsMap.forEach { (key, value) ->
            println("$key = $value")
            values.add(BarEntry(start, value!!.toFloat()))
            start++
        }

        val set1: BarDataSet
        if (chart?.data != null && chart?.data?.dataSetCount!! > 0) {
            set1 = chart?.data?.getDataSetByIndex(0) as BarDataSet
            set1.values = values
            chart?.data?.notifyDataChanged()
            chart?.notifyDataSetChanged()

        } else {
            set1 = BarDataSet(values, "The year 2019")
            set1.setDrawIcons(false)
            val startColor1 = ContextCompat.getColor(this, android.R.color.holo_orange_light)
            val startColor2 = ContextCompat.getColor(this, android.R.color.holo_blue_light)
            val startColor3 = ContextCompat.getColor(this, android.R.color.holo_orange_light)
            val startColor4 = ContextCompat.getColor(this, android.R.color.holo_green_light)
            val startColor5 = ContextCompat.getColor(this, android.R.color.holo_red_light)
            val endColor1 = ContextCompat.getColor(this, android.R.color.holo_blue_dark)
            val endColor2 = ContextCompat.getColor(this, android.R.color.holo_purple)
            val endColor3 = ContextCompat.getColor(this, android.R.color.holo_green_dark)
            val endColor4 = ContextCompat.getColor(this, android.R.color.holo_red_dark)
            val endColor5 = ContextCompat.getColor(this, android.R.color.holo_orange_dark)

            val gradientColors = ArrayList<GradientColor>()
            gradientColors.add(GradientColor(startColor1, endColor1))
            gradientColors.add(GradientColor(startColor2, endColor2))
            gradientColors.add(GradientColor(startColor3, endColor3))
            gradientColors.add(GradientColor(startColor4, endColor4))
            gradientColors.add(GradientColor(startColor5, endColor5))

            set1.gradientColors = gradientColors

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)

            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            data.setValueTypeface(tfLight)
            data.barWidth = 1f
            chart?.data = data

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }

    override fun saveToGallery() {
        saveToGallery(chart, "BarChartActivity")
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }


    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }


    override fun onValueSelected(e: Entry?, h: Highlight) {

        if (e == null)
            return

        val bounds = onValueSelectedRectF
        chart?.getBarBounds(e as BarEntry?, bounds)
        val position = chart?.getPosition(e, YAxis.AxisDependency.LEFT)

        MPPointF.recycleInstance(position)
    }

    override fun onNothingSelected() {

    }


}