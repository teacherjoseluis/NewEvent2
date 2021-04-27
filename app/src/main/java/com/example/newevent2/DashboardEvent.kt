package com.example.newevent2

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Functions.Loginfo
import com.example.newevent2.MVP.LogPresenter
import com.example.newevent2.MVP.PaymentPresenter
import com.example.newevent2.MVP.TaskPresenter
import com.example.newevent2.Model.Task
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.dashboardactivity.*
import kotlinx.android.synthetic.main.dashboardactivity.view.*
import kotlinx.android.synthetic.main.dashboardcharts.*
import kotlinx.android.synthetic.main.welcome.recentactivityrv
import java.text.DecimalFormat

class DashboardEvent(private val view: DashboardView) : Fragment(),
    TaskPresenter.ViewTaskWelcomeActivity,
    PaymentPresenter.ViewPaymentWelcomeActivity {

    private lateinit var presentertask: TaskPresenter
    private lateinit var presenterpayment: PaymentPresenter

    private var tfRegular: Typeface? = null
    private var tfLight: Typeface? = null
    private lateinit var charttask: PieChart
    private lateinit var chartpayment: PieChart

    var userid = ""
    var eventid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userid = arguments!!.get("userid").toString()
        eventid = arguments!!.get("eventid").toString()
    }

    @SuppressLint("ResourceType")
    var BandG_Colors = arrayListOf<Int>(
        Color.parseColor("#F8BBD0"),
        Color.parseColor("#C2185B")
    )

    var BandG_Colors2 = arrayListOf<Int>(
        Color.parseColor("#C2185B"),
        Color.parseColor("#9E9E9E")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.dashboardcharts, container, false)

        //---------------------------------------------------------------------------------------------------------------
        tfRegular = ResourcesCompat.getFont(context!!, R.font.raleway_thin)
        tfLight = ResourcesCompat.getFont(context!!, R.font.raleway_thin)

        charttask = inf.findViewById(R.id.charttask)
        charttask.apply {
            setUsePercentValues(false)
            description.isEnabled = false
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f
            setCenterTextTypeface(tfLight)
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
        chartpayment = inf.findViewById(R.id.chartpayment)
        chartpayment.apply {
            setUsePercentValues(false)
            description.isEnabled = false
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f
            setCenterTextTypeface(tfLight)
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
        //----------------------------------------------------------------------------------------------------//
        presentertask = TaskPresenter(view, userid, eventid)
        presentertask.getTaskStats()

        presenterpayment = PaymentPresenter(this, userid, eventid)
        presenterpayment.getPaymentStats()
        return inf
    }

    override fun onViewTaskStatsSuccess(taskpending: Int, taskcompleted: Int, sumbudget: Float) {
        emptycharts.visibility = View.GONE
        val taskentries = ArrayList<PieEntry>()
        taskentries.add(PieEntry(taskpending.toFloat(), " To Do "))
        taskentries.add(PieEntry(taskcompleted.toFloat(), " Done "))

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

    override fun onViewNextTaskSuccess(task: Task) {
        TODO("Not yet implemented")
    }

    override fun onViewTaskError(errcode: String) {
        chartlayout.visibility = ConstraintLayout.GONE
    }

    override fun onViewPaymentStatsSuccess(countpayment: Int, sumpayment: Float, sumbudget: Float) {
        emptycharts.visibility = View.GONE
        val paymententries = ArrayList<PieEntry>()
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
            setValueFormatter(Welcome.CurrencyFormatter())
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

    override fun onViewPaymentError(errcode: String) {
        chartlayout.visibility = ConstraintLayout.GONE
    }
}