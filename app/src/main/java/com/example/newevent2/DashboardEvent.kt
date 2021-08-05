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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.newevent2.MVP.DashboardEventPresenter
import com.example.newevent2.Model.Task
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.dashboardcharts.view.*
import kotlinx.android.synthetic.main.dashboardcharts.view.withnodata
import kotlinx.android.synthetic.main.empty_state.view.*
import kotlinx.android.synthetic.main.summary_weddingguests.view.*
import java.text.DecimalFormat

class DashboardEvent() : Fragment(), DashboardEventPresenter.TaskStats,
    DashboardEventPresenter.PaymentStats, DashboardEventPresenter.GuestStats {

    private lateinit var presentertask: DashboardEventPresenter
    //private lateinit var presenterpayment: PaymentPresenter

    private lateinit var BandG_Colors: ArrayList<Int>
    private lateinit var BandG_Colors2: ArrayList<Int>
    private var tfLarge: Typeface? = null
    private var tfRegular: Typeface? = null
    private var tfLight: Typeface? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        val rosaPalido = context!!.resources.getColor(R.color.rosapalido)
        val azulmasClaro = context!!.resources.getColor(R.color.azulmasClaro)
        val palodeRosa = context!!.resources.getColor(R.color.paloderosa)

        tfLight = ResourcesCompat.getFont(context!!, R.font.raleway_thin)
        tfRegular = ResourcesCompat.getFont(context!!, R.font.raleway_medium)
        tfLarge = ResourcesCompat.getFont(context!!, R.font.raleway_medium)

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
        //---------------------------------------------------------------------------------------------------------------
        presentertask = DashboardEventPresenter(context!!, this, inf)
        return inf
    }

    override fun onTasksStats(
        inflatedView: View,
        taskpending: Int,
        taskcompleted: Int,
        sumbudget: Float,
        task: Task
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
//        cardsecondarytext.setText("Summary of pending and done tasks")
//        action1Button.setText("add more")
//        action2Button.visibility = View.INVISIBLE

        val taskentries = ArrayList<PieEntry>()
        taskentries.add(PieEntry(taskpending.toFloat(), "To Do"))
        taskentries.add(PieEntry(taskcompleted.toFloat(), "Done"))

        val dataSettask = PieDataSet(taskentries, "").apply {
            setDrawIcons(false)
            sliceSpace = 3f //separation between slices
            iconsOffset = MPPointF(0f, 20f) //position of labels to slices. 40f seems too much
            //selectionShift = 5f // Think is the padding
            colors = chartcolors
            //xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        }

        val datatask = PieData(dataSettask).apply {
            setValueFormatter(DefaultValueFormatter(0))
            setValueTextSize(14f)
            //Aqui hay un bug cuando aparentemente no dejo que cargue este control y me muevo a otra pantalla
            setValueTextColor(context!!.resources.getColor(R.color.secondaryText))
            setValueTypeface(tfRegular)
        }

        charttask.legend.apply {
            //formSize = 12.0f
            form = Legend.LegendForm.CIRCLE
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            textSize = 12.0f
            textColor = (context!!.resources.getColor(R.color.secondaryText))
            typeface = tfRegular
            setDrawInside(true)
            xEntrySpace = 10f
            yEntrySpace = 4f
            yOffset = 5f
        }

        charttask.apply {
            setDrawEntryLabels(false)
            setUsePercentValues(false)
            description.isEnabled = false
            setExtraOffsets(5f, -5f, -20f, 15f) //apparently this is padding
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
            transparentCircleRadius = 55f
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = false
            isHighlightPerTapEnabled = true
            //animateY(1400, Easing.EaseInOutQuad)
            setEntryLabelColor(context!!.resources.getColor(R.color.secondaryText))
            setEntryLabelTypeface(tfRegular)
            setEntryLabelTextSize(14f)
            centerText = "${taskcompleted + taskpending}"
            data = datatask
            highlightValues(null)
            invalidate()
        }

        val duenextcard = inflatedView.findViewById<View>(R.id.duenextcard)
        if (task.key == "") {
            duenextcard.visibility = View.GONE
        } else {
            val cardtitle = duenextcard.findViewById<TextView>(R.id.cardtitle)
            val cardsecondarytext = duenextcard.findViewById<TextView>(R.id.secondarytext)
            val action1Button = duenextcard.findViewById<Button>(R.id.action1)
            val action2Button = duenextcard.findViewById<Button>(R.id.action2)

            cardtitle.setText("Due next task")
            cardsecondarytext.setText("${task.name} due by ${task.date}")
            action1Button.setText("mark complete")
            action2Button.visibility = View.INVISIBLE
        }
    }

    override fun onTaskStatsError(inflatedView: View, errcode: String) {
        inflatedView.withdata.visibility = ConstraintLayout.GONE
        inflatedView.withnodata.visibility = ConstraintLayout.VISIBLE

        inflatedView.withnodata.newtaskbutton.visibility = FloatingActionButton.VISIBLE
        inflatedView.withnodata.newtaskbutton.setOnClickListener {
            val newtask = Intent(activity, TaskCreateEdit::class.java)
//                newtask.putExtra("userid", userid)
//                newtask.putExtra("eventid", eventid)
            startActivity(newtask)
        }
    }

    override fun onPaymentsStats(
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
//        cardsecondarytext.setText("Summary of payments")
//        action1Button.setText("add more")
//        action2Button.visibility = View.INVISIBLE

        val paymententries = ArrayList<PieEntry>()
        paymententries.add(PieEntry(sumpayment, "Spent"))
        paymententries.add(PieEntry(sumbudget - sumpayment, "Available"))

        val dataSetpayment = PieDataSet(paymententries, "").apply {
            setDrawIcons(false)
            sliceSpace = 3f //separation between slices
            iconsOffset =
                MPPointF(0f, 20f) //position of labels to slices. 40f seems too much
            //selectionShift = 5f // Think is the padding
            colors = chartcolors
            xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        }

        val datapayment = PieData(dataSetpayment).apply {
            setValueFormatter(myPercentageFormatter())
            setValueTextSize(14f)
            setValueTextColor(context!!.resources.getColor(R.color.secondaryText))
            setValueTypeface(tfRegular)
        }

        chartpayment.legend.apply {
            form = Legend.LegendForm.CIRCLE
            //formSize = 12.0f
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            textSize = 12.0f
            textColor = (context!!.resources.getColor(R.color.secondaryText))
            typeface = tfRegular
            setDrawInside(false)
            xEntrySpace = 10f
            yEntrySpace = 4f
            yOffset = 5f
        }

        chartpayment.apply {
            setDrawEntryLabels(false)
            setUsePercentValues(true)
            description.isEnabled = false
            setExtraOffsets(5f, -5f, -45f, 0f) //apparently this is padding
            dragDecelerationFrictionCoef = 0.95f
            //setCenterTextTypeface(tfRegular)
            //setCenterTextColor(context!!.resources.getColor(R.color.rosaChillon))
            //setCenterTextSize(12f)
            isDrawHoleEnabled = false
            //setHoleColor(Color.WHITE)
//            Don't really care too much about having a transparent circle
            //setTransparentCircleColor(Color.WHITE)
            //setTransparentCircleAlpha(110)
            //holeRadius = 50f
//            transparentCircleRadius = 61f
            //setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = false
            isHighlightPerTapEnabled = true
//            animateY(1400, Easing.EaseInOutQuad)
            setEntryLabelColor(context!!.resources.getColor(R.color.secondaryText))
            setEntryLabelTypeface(tfRegular)
            setEntryLabelTextSize(14f)
            //centerText = "Budget\n${formatter.format(sumbudget)}"
            data = datapayment
            highlightValues(null)
            invalidate()
        }

        val loadingscreen = activity!!.findViewById<ConstraintLayout>(R.id.loadingscreen)
        val drawerlayout = activity!!.findViewById<DrawerLayout>(R.id.drawerlayout)
        loadingscreen.visibility = ConstraintLayout.GONE
        drawerlayout.visibility = ConstraintLayout.VISIBLE
    }

    override fun onPaymentsStatsError(inflatedView: View, errcode: String) {
        val paymentlayout = inflatedView.findViewById<View>(R.id.paymentchart)
        paymentlayout.visibility =
            View.INVISIBLE

//        val emptypayments = paymentlayout.findViewById<View>(R.id.emptypayments)
//        emptypayments.visibility = View.VISIBLE
//
//        val cardtitle = emptypayments.findViewById<TextView>(R.id.cardtitleempty)
//        val cardsecondarytext =
//            emptypayments.findViewById<TextView>(R.id.secondarytextempty)
//        val emptycardmessage = emptypayments.findViewById<TextView>(R.id.emptycardmessage)
//        val action1Button = emptypayments.findViewById<Button>(R.id.newpayment)
//
//        cardtitle.setText("Payments")
//        cardsecondarytext.setText("There are currently no payments made")
//        emptycardmessage.setText("You also can keep track of the payments you made so you can control how much you are spending")
//        action1Button.setText("add payment")
//
//        action1Button.setOnClickListener {
//            val newpayment = Intent(activity, NewTask_PaymentDetail::class.java)
////                newpayment.putExtra("userid", userid)
////                newpayment.putExtra("eventid", eventid)
//            startActivity(newpayment)
//        }
        val loadingscreen = activity!!.findViewById<ConstraintLayout>(R.id.loadingscreen)
        val drawerlayout = activity!!.findViewById<DrawerLayout>(R.id.drawerlayout)
        loadingscreen.visibility = ConstraintLayout.GONE
        drawerlayout.visibility = ConstraintLayout.VISIBLE
    }

//    override fun onDueNextTask(inflatedView: View, task: Task) {
//        inflatedView.withdata.visibility = ConstraintLayout.VISIBLE
//
//        val cardlayout = inflatedView.findViewById<View>(R.id.duenextcard)
//        if (task.key == "") {
//            cardlayout.visibility = View.GONE
//        } else {
//            val cardtitle = cardlayout.findViewById<TextView>(R.id.cardtitle)
//            val cardsecondarytext = cardlayout.findViewById<TextView>(R.id.secondarytext)
//            val action1Button = cardlayout.findViewById<Button>(R.id.action1)
//            val action2Button = cardlayout.findViewById<Button>(R.id.action2)
//
//            cardtitle.setText("Due next task")
//            cardsecondarytext.setText("${task.name} due by ${task.date}")
//            action1Button.setText("mark complete")
//            action2Button.visibility = View.INVISIBLE
//        }
//    }

//    override fun onDueNextTaskError(inflatedView: View, errcode: String) {
//        inflatedView.duenextcard.visibility = ConstraintLayout.GONE
//    }

    class CurrencyFormatter : ValueFormatter {
        var mFormat: DecimalFormat? = null

        constructor() {
            mFormat = DecimalFormat("$###,###.00")
        }

        override fun getFormattedValue(value: Float): String? {
            return mFormat!!.format(value.toDouble())
        }
    }

    class myPercentageFormatter : ValueFormatter {
        var mFormat: DecimalFormat? = null

        constructor() {
            mFormat = DecimalFormat("###,###,##0.0")
        }

        override fun getFormattedValue(value: Float): String? {
            return mFormat!!.format(value.toDouble()) + " %"
        }
    }

    override fun onGuestConfirmation(
        inflatedView: View,
        confirmed: Int,
        rejected: Int,
        pending: Int
    ) {
        inflatedView.acceptednumber.text = confirmed.toString()
        inflatedView.rejectednumber.text = rejected.toString()
        inflatedView.pendingnumber.text = pending.toString()
    }

    override fun onGuestConfirmationError(inflatedView: View, errcode: String) {
        inflatedView.guestlayout.visibility = ConstraintLayout.INVISIBLE
        inflatedView.noguestlayout.visibility = ConstraintLayout.VISIBLE
    }
}


