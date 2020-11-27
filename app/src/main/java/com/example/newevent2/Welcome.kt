package com.example.newevent2

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import java.text.DecimalFormat

class Welcome : AppCompatActivity() {

    private lateinit var charttask: PieChart
    private lateinit var chartpayment: PieChart

    var tfRegular: Typeface? = null
    var tfLight: Typeface? = null

    var sharedPreference: SharedPreferences? = null

    @SuppressLint("ResourceType")
    var BandG_Colors = arrayListOf<Int>(
        Color.parseColor("#F8BBD0"),
        Color.parseColor("#C2185B")
    )

    var BandG_Colors2 = arrayListOf<Int>(
        Color.parseColor("#C2185B"),
        Color.parseColor("#9E9E9E")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        // Pie charts
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        tfRegular = ResourcesCompat.getFont(this.applicationContext, R.font.robotoregular)
        //Typeface.createFromAsset(assets, "fonts/robotoregular.ttf")
        tfLight = ResourcesCompat.getFont(this.applicationContext, R.font.robotolight)
        //Typeface.createFromAsset(assets, "fonts/robotolight.ttf")

        setContentView(R.layout.welcome)

        charttask = findViewById(R.id.charttask)
        charttask.apply {
            setUsePercentValues(false)
            description.isEnabled = false
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f
            setCenterTextTypeface(tfLight)
            //centerText = generateCenterSpannableTextTask()
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            animateY(1400, Easing.EaseInOutQuad)
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTypeface(tfRegular)
            setEntryLabelTextSize(12f)
        }

        val l: Legend = charttask.legend
        l.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(false)
            xEntrySpace = 0f
            yEntrySpace = 0f
            yOffset = 0f
        }

//----------------------------------------------------------------------------------------------------//

        chartpayment = findViewById(R.id.chartpayment)
        chartpayment.apply {
            setUsePercentValues(false)
            description.isEnabled = false
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f
            setCenterTextTypeface(tfLight)
            //centerText = generateCenterSpannableTextPayment()
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            animateY(1400, Easing.EaseInOutQuad)
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTypeface(tfRegular)
            setEntryLabelTextSize(12f)
        }

        val g: Legend = charttask.legend
        g.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(false)
            xEntrySpace = 0f
            yEntrySpace = 0f
            yOffset = 0f
        }

        setData()
    }

    private fun setData() {
        val taskentries = ArrayList<PieEntry>()
        val taskentity = TaskEntity()
        taskentity.eventid =
            "-MLy-LKwd8RnRb-Bwesn" //HARDCODE********************************************************

        taskentity.getTasksEvent(object : FirebaseSuccessListenerTask {
            override fun onTasksEvent(taskpending: Int, taskcompleted: Int, sumbudget: Float) {
                taskentries.add(PieEntry(taskpending.toFloat(), "Pending"))
                taskentries.add(PieEntry(taskcompleted.toFloat(), "Completed"))

                //------------------------------------------------------------------
                var editor = sharedPreference!!.edit()
                editor.putFloat("sumbudget", sumbudget)
                editor.commit()
                //------------------------------------------------------------------

                val dataSettask = PieDataSet(taskentries, "").apply {
                    setDrawIcons(false)
                    sliceSpace = 3f
                    iconsOffset = MPPointF(0f, 40f)
                    selectionShift = 5f
                }

                val colors = ArrayList<Int>()
                for (c in BandG_Colors) colors.add(c)
                dataSettask.colors = colors

                val datatask = PieData(dataSettask).apply {
                    setValueFormatter(DefaultValueFormatter(0))
                    setValueTextSize(11f)
                    setValueTextColor(Color.BLACK)
                    setValueTypeface(tfLight)
                }
                charttask.centerText = "${taskcompleted + taskpending}\ntasks"
                charttask.data = datatask
                charttask.highlightValues(null)
                charttask.invalidate()
            }

            override fun onTasksList(list: ArrayList<Task>) {
                TODO("Not yet implemented")
            }
        })

        val paymententries = ArrayList<PieEntry>()
        val paymententity = PaymentEntity()
        paymententity.eventid =
            "-MLy-LKwd8RnRb-Bwesn" //HARDCODE********************************************************

        paymententity.getPaymentEvent(object : FirebaseSuccessListenerPayment {
            override fun onPaymentEvent(sumpayment: Float) {
                //--------------------------------------------------------------------------------
                val sumbudget = sharedPreference!!.getFloat("sumbudget", 0.0f)
                //--------------------------------------------------------------------------------

                paymententries.add(PieEntry(sumpayment, "Spent"))
                paymententries.add(PieEntry(sumbudget - sumpayment, "Available"))

                val dataSetpayment = PieDataSet(paymententries, "").apply {
                    setDrawIcons(false)
                    sliceSpace = 3f
                    iconsOffset = MPPointF(0f, 40f)
                    selectionShift = 5f
                }

                val colors = ArrayList<Int>()
                for (c in BandG_Colors2) colors.add(c)
                dataSetpayment.colors = colors

                val datapayment = PieData(dataSetpayment).apply {
                    //setValueFormatter(PercentFormatter(chartpayment))
                    setValueFormatter(CurrencyFormatter())
                    setValueTextSize(11f)
                    setValueTextColor(Color.BLACK)
                    setValueTypeface(tfLight)
                }
                val formatter = DecimalFormat("$#,###.00")
                chartpayment.centerText = "Budget\n${formatter.format(sumbudget)}"
                chartpayment.data = datapayment
                chartpayment.highlightValues(null)
                chartpayment.invalidate()
            }

            override fun onPaymentList(list: ArrayList<Payment>) {
                TODO("Not yet implemented")
            }

            override fun onPaymentStats(countpayment: Int, sumpayment: Float) {
                TODO("Not yet implemented")
            }

        })
    }

    class CurrencyFormatter : ValueFormatter {
        var mFormat: DecimalFormat? = null

        constructor() {
            mFormat = DecimalFormat("$###,###.00")
        }

        override fun getFormattedValue(value: Float): String? {
            return mFormat!!.format(value.toDouble())
        }

    }
}