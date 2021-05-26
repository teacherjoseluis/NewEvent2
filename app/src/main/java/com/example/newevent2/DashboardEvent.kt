package com.example.newevent2

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.newevent2.MVP.PaymentPresenter
import com.example.newevent2.MVP.TaskPresenter
import com.example.newevent2.Model.Task
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.dashboardcharts.*
import kotlinx.android.synthetic.main.dashboardcharts.view.*
import java.text.DecimalFormat

class DashboardEvent() : Fragment(), TaskPresenter.TaskStats, TaskPresenter.TaskItem,
    PaymentPresenter.PaymentStats {

    private lateinit var presentertask: TaskPresenter
    private lateinit var presenterpayment: PaymentPresenter

    private lateinit var BandG_Colors: ArrayList<Int>
    private lateinit var BandG_Colors2: ArrayList<Int>
    private var tfLarge: Typeface? = null
    private var tfRegular: Typeface? = null
    private var tfLight: Typeface? = null

    //private lateinit var mShimmerViewContainer: ShimmerFrameLayout

    var userid = ""
    var eventid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userid = arguments!!.get("userid").toString()
        eventid = arguments!!.get("eventid").toString()

        val rosaPalido = context!!.resources.getColor(R.color.rosapalido)
        val azulmasClaro = context!!.resources.getColor(R.color.azulmasClaro)
        val palodeRosa = context!!.resources.getColor(R.color.paloderosa)

        @SuppressLint("ResourceType")
        BandG_Colors = arrayListOf(
            rosaPalido,
            azulmasClaro
        )

        BandG_Colors2 = arrayListOf(
            azulmasClaro,
            palodeRosa
        )
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.dashboardcharts, container, false)

        // mShimmerViewContainer = ShimmerFrameLayout(context)
//        mShimmerViewContainer = inf.findViewById(R.id.shimmer_view_container)
//        mShimmerViewContainer.startShimmerAnimation()
//        mShimmerViewContainer.duration = 1000

        //---------------------------------------------------------------------------------------------------------------
        tfLight = ResourcesCompat.getFont(context!!, R.font.raleway_thin)
        tfRegular = ResourcesCompat.getFont(context!!, R.font.raleway_medium)
        tfLarge = ResourcesCompat.getFont(context!!, R.font.raleway_medium)

        presentertask = TaskPresenter(context!!, this, inf)
        presentertask.userid = userid
        presentertask.eventid = eventid
        presentertask.getDueNextTask()
        presentertask.getTaskStats()

        presenterpayment = PaymentPresenter(context!!, this, inf)
        presenterpayment.getPaymentStats()
        return inf
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onTask(inflatedView: View, task: Task) {
        //Thread.sleep(300)
//        mShimmerViewContainer.stopShimmerAnimation()
//        mShimmerViewContainer.visibility = View.GONE
        //splashlayout.visibility = ConstraintLayout.GONE
        inflatedView.withdata.visibility = ConstraintLayout.VISIBLE

        val cardlayout = inflatedView.findViewById<View>(R.id.duenextcard)
        val cardtitle = cardlayout.findViewById<TextView>(R.id.cardtitle)
        val cardsecondarytext = cardlayout.findViewById<TextView>(R.id.secondarytext)
        val action1Button = cardlayout.findViewById<Button>(R.id.action1)
        val action2Button = cardlayout.findViewById<Button>(R.id.action2)

        cardtitle.setText("Due next task")
        cardsecondarytext.setText("${task.name} due by ${task.date}")
        action1Button.setText("mark complete")
        action2Button.visibility = View.INVISIBLE
    }

    override fun onTaskError(inflatedView: View, errcode: String) {
//        Thread.sleep(300)
//        splashlayout.visibility = ConstraintLayout.GONE
        withdata.visibility = ConstraintLayout.GONE
        withnodata.visibility = ConstraintLayout.VISIBLE

        newtaskbutton.setOnClickListener {
            val newtask = Intent(activity, NewTask_TaskDetail::class.java)
            newtask.putExtra("userid", userid)
            newtask.putExtra("eventid", eventid)
            startActivity(newtask)
        }
    }

    override fun onTasksStats(
        inflatedView: View,
        taskpending: Int,
        taskcompleted: Int,
        sumbudget: Float
    ) {
        val cardlayout = inflatedView.findViewById<View>(R.id.taskchart)
        val cardtitle = cardlayout.findViewById<TextView>(R.id.cardtitle)
        val cardsecondarytext = cardlayout.findViewById<TextView>(R.id.secondarytext)
        val charttask = cardlayout.findViewById<PieChart>(R.id.charttask)
        val action1Button = cardlayout.findViewById<Button>(R.id.action1)
        val action2Button = cardlayout.findViewById<Button>(R.id.action2)

        val chartcolors = ArrayList<Int>()
        for (c in BandG_Colors) chartcolors.add(c)

        cardtitle.setText("Tasks")
        cardsecondarytext.setText("Summary of pending and done tasks")
        action1Button.setText("add more")
        action2Button.visibility = View.INVISIBLE

        val taskentries = ArrayList<PieEntry>()
        taskentries.add(PieEntry(taskpending.toFloat(), "Pending"))
        taskentries.add(PieEntry(taskcompleted.toFloat(), "Done"))

        val dataSettask = PieDataSet(taskentries, "").apply {
            setDrawIcons(false)
            sliceSpace = 3f //separation between slices
            iconsOffset = MPPointF(0f, 20f) //position of labels to slices. 40f seems too much
            //selectionShift = 5f // Think is the padding
            colors = chartcolors
        }

        val datatask = PieData(dataSettask).apply {
            setValueFormatter(DefaultValueFormatter(0))
            setValueTextSize(14f)
            setValueTextColor(Color.WHITE)
            setValueTypeface(tfRegular)
        }

        charttask.legend.apply {
            //formSize = 12.0f
            form = Legend.LegendForm.CIRCLE
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            textSize = 12.0f
            textColor = (context!!.resources.getColor(R.color.secondaryText))
            typeface = tfRegular
            setDrawInside(false)
            xEntrySpace = 10f
            yEntrySpace = 4f
            yOffset = 5f
        }

        charttask.apply {
            setUsePercentValues(false)
            description.isEnabled = false
            setExtraOffsets(5f, 5f, 5f, 5f) //apparently this is padding
            dragDecelerationFrictionCoef = 0.95f
            setCenterTextTypeface(tfRegular)
            setCenterTextColor(context!!.resources.getColor(R.color.rosaChillon))
            setCenterTextSize(30f)
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
//            Don't really care too much about having a transparent circle
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 50f
//            transparentCircleRadius = 61f
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = false
            isHighlightPerTapEnabled = true
            animateY(1400, Easing.EaseInOutQuad)
            setEntryLabelColor(context!!.resources.getColor(R.color.Cream))
            setEntryLabelTypeface(tfRegular)
            setEntryLabelTextSize(14f)
            centerText = "${taskcompleted + taskpending}"
            data = datatask
            highlightValues(null)
            invalidate()
        }
    }

    override fun onTaskStatsError(inflatedView: View, errcode: String) {
        TODO("Not yet implemented")
    }

    override fun onPaymentStats(
        inflatedView: View,
        countpayment: Int,
        sumpayment: Float,
        sumbudget: Float
    ) {
        val cardlayout = inflatedView.findViewById<View>(R.id.paymentchart)
        val cardtitle = cardlayout.findViewById<TextView>(R.id.cardtitle)
        val cardsecondarytext = cardlayout.findViewById<TextView>(R.id.secondarytext)
        val chartpayment = cardlayout.findViewById<PieChart>(R.id.chartpayment)
        val action1Button = cardlayout.findViewById<Button>(R.id.action1)
        val action2Button = cardlayout.findViewById<Button>(R.id.action2)

        val chartcolors = ArrayList<Int>()
        for (c in BandG_Colors2) chartcolors.add(c)
        val formatter = DecimalFormat("$#,###.00")

        cardtitle.setText("Payments")
        cardsecondarytext.setText("Summary of payments")
        action1Button.setText("add more")
        action2Button.visibility = View.INVISIBLE

        val paymententries = ArrayList<PieEntry>()
        paymententries.add(PieEntry(sumpayment, "Spent"))
        paymententries.add(PieEntry(sumbudget - sumpayment, "Available"))

        val dataSetpayment = PieDataSet(paymententries, "").apply {
            setDrawIcons(false)
            sliceSpace = 3f //separation between slices
            iconsOffset = MPPointF(0f, 20f) //position of labels to slices. 40f seems too much
            //selectionShift = 5f // Think is the padding
            colors = chartcolors
        }

        val datapayment = PieData(dataSetpayment).apply {
            setValueFormatter(Welcome.CurrencyFormatter())
            setValueTextSize(11f)
            setValueTextColor(Color.BLACK)
            setValueTypeface(tfLight)
        }

        chartpayment.apply {
            setUsePercentValues(false)
            description.isEnabled = false
            setExtraOffsets(5f, 5f, 5f, 5f) //apparently this is padding
            dragDecelerationFrictionCoef = 0.95f
            setCenterTextTypeface(tfRegular)
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
//            Don't really care too much about having a transparent circle
//            setTransparentCircleColor(Color.WHITE)
//            setTransparentCircleAlpha(110)
            holeRadius = 40f
//            transparentCircleRadius = 61f
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = false
            isHighlightPerTapEnabled = true
            animateY(1400, Easing.EaseInOutQuad)
            setEntryLabelColor(context!!.resources.getColor(R.color.secondaryText))
            setEntryLabelTypeface(tfRegular)
            setEntryLabelTextSize(14f)
            centerText = "Budget\n${formatter.format(sumbudget)}"
            data = datapayment
            highlightValues(null)
            invalidate()
        }
        chartpayment.legend.apply {
            Legend.LegendForm.SQUARE
            formSize = 12.0f
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(false)
            xEntrySpace = 4f
            yEntrySpace = 4f
            yOffset = 0f
        }
    }

    override fun onPaymentStatsError(inflatedView: View, errcode: String) {
        //withdata.visibility = ConstraintLayout.GONE
        //withnodata.visibility = ConstraintLayout.VISIBLE
        val paymentlayout = inflatedView.findViewById<View>(R.id.paymentchart)
        paymentlayout.findViewById<View>(R.id.paymentchartcardlayout).visibility = View.INVISIBLE

        val emptypayments = paymentlayout.findViewById<View>(R.id.emptypayments)
        emptypayments.visibility = View.VISIBLE

        val cardtitle = emptypayments.findViewById<TextView>(R.id.cardtitleempty)
        val cardsecondarytext = emptypayments.findViewById<TextView>(R.id.secondarytextempty)
        val emptycardmessage = emptypayments.findViewById<TextView>(R.id.emptycardmessage)
        val action1Button = emptypayments.findViewById<Button>(R.id.newpayment)

        cardtitle.setText("Payments")
        cardsecondarytext.setText("There are currently no payments made")
        emptycardmessage.setText("You also can keep track of the payments you made so you can control how much you are spending")
        action1Button.setText("add payment")

        action1Button.setOnClickListener {
            val newpayment = Intent(activity, NewTask_PaymentDetail::class.java)
            newpayment.putExtra("userid", userid)
            newpayment.putExtra("eventid", eventid)
            startActivity(newpayment)
        }
    }
}
