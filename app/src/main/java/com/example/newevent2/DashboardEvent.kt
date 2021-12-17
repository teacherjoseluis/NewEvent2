package com.example.newevent2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.baoyachi.stepview.HorizontalStepView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.newevent2.Functions.converttoDate
import com.example.newevent2.Functions.daystoDate
import com.example.newevent2.Functions.getImgfromPlaces
import com.example.newevent2.MVP.DashboardEventPresenter
import com.example.newevent2.MVP.ImagePresenter
import com.example.newevent2.Model.Event
import com.example.newevent2.Model.MyFirebaseApp
import com.example.newevent2.Model.Task
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.dashboardcharts.view.*
import kotlinx.android.synthetic.main.dashboardcharts.view.withnodata
import kotlinx.android.synthetic.main.empty_state.view.*
import kotlinx.android.synthetic.main.summary_weddingguests.view.*
import kotlinx.android.synthetic.main.summary_weddinglocation.view.*
import java.text.DecimalFormat
import kotlin.collections.ArrayList

class DashboardEvent : Fragment(), DashboardEventPresenter.TaskStats,
    DashboardEventPresenter.PaymentStats, DashboardEventPresenter.GuestStats,
    DashboardEventPresenter.EventInterface, ImagePresenter.EventImage, ImagePresenter.PlaceImage {

    private lateinit var BandG_Colors: ArrayList<Int>
    private lateinit var BandG_Colors2: ArrayList<Int>
    private lateinit var presentertask: DashboardEventPresenter
    private lateinit var imagePresenter: ImagePresenter

    private var placeid = ""

    private var tfLarge: Typeface? = null
    private var tfRegular: Typeface? = null
    private var tfLight: Typeface? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        //Declaring the colors and fonts to be used by charts
        //-------------------------------------------------------------------------
        val rosaPalido = ContextCompat.getColor(requireContext(), R.color.rosapalido)
        val azulmasClaro = ContextCompat.getColor(requireContext(), R.color.azulmasClaro)
        val palodeRosa = ContextCompat.getColor(requireContext(), R.color.paloderosa)
        tfLight = ResourcesCompat.getFont(requireContext(), R.font.raleway_thin)
        tfRegular = ResourcesCompat.getFont(requireContext(), R.font.raleway_medium)
        tfLarge = ResourcesCompat.getFont(requireContext(), R.font.raleway_medium)

        @SuppressLint("ResourceType")
        BandG_Colors = arrayListOf(
            rosaPalido,
            azulmasClaro
        )

        BandG_Colors2 = arrayListOf(
            azulmasClaro,
            palodeRosa
        )
        //-------------------------------------------------------------------------
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.dashboardcharts, container, false)
        val user = com.example.newevent2.Functions.getUserSession(requireContext())

        //Load with the achievements obtained by the user -------------------------------------------
        val stepsBeanList = user.onboardingprogress(context!!)
        val stepview = inf.findViewById<HorizontalStepView>(R.id.step_view)
        stepview
            .setStepViewTexts(stepsBeanList)//总步骤
            .setTextSize(12)//set textSize
            .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(requireContext(),R.color.azulmasClaro))//设置StepsViewIndicator完成线的颜色
            .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(requireContext(),R.color.rosaChillon))//设置StepsViewIndicator未完成线的颜色
            .setStepViewComplectedTextColor(ContextCompat.getColor(requireContext(),R.color.azulmasClaro))//设置StepsView text完成线的颜色
            .setStepViewUnComplectedTextColor(ContextCompat.getColor(requireContext(),R.color.rosaChillon))//设置StepsView text未完成线的颜色
            .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(requireContext(),R.drawable.icons8_checked_rosachillon))//设置StepsViewIndicator CompleteIcon
            .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(requireContext(),R.drawable.circle_rosachillon))//设置StepsViewIndicator DefaultIcon
            .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(requireContext(),R.drawable.alert_icon_rosachillon))//设置StepsViewIndicator AttentionIcon
        //--------------------------------------------------------------------------------------------

        val weddingphotodetail = inf.findViewById<ConstraintLayout>(R.id.weddingphotodetail)
        weddingphotodetail.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "EDITEVENT")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------
            val editevent = Intent(context, MainActivity::class.java)
            startActivityForResult(editevent, SUCCESS_RETURN)
        }

        val weddingprogress = inf.findViewById<ConstraintLayout>(R.id.weddingprogress)
        weddingprogress.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "MYPROGRESS")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------
        }

        //Calling the presenter that will pull of the data I need for this view
        presentertask = DashboardEventPresenter(requireContext(), this, inf)
        return inf
    }

    @SuppressLint("SetTextI18n")
    override fun onTasksStats(
        inflatedView: View,
        taskpending: Int,
        taskcompleted: Int,
        sumbudget: Float,
        task: Task
    ) {
        val cardlayout = inflatedView.findViewById<View>(R.id.taskchart)
        val cardtitle = cardlayout.findViewById<TextView>(R.id.cardtitle)
        val charttask = cardlayout.findViewById<PieChart>(R.id.charttask)

        val chartcolors = ArrayList<Int>()
        for (c in BandG_Colors) chartcolors.add(c)
        cardtitle.text = getString(R.string.tasks)

        val taskentries = ArrayList<PieEntry>()
        taskentries.add(PieEntry(taskpending.toFloat(), getString(R.string.todo)))
        taskentries.add(PieEntry(taskcompleted.toFloat(), getString(R.string.done)))

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
            //Aqui hay un bug cuando aparentemente no dejo que cargue este control y me muevo a otra pantalla
            setValueTextColor(ContextCompat.getColor(requireContext(),R.color.secondaryText))
            setValueTypeface(tfRegular)
        }

        charttask.legend.apply {
            form = Legend.LegendForm.CIRCLE
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            textSize = 12.0f
            textColor = (ContextCompat.getColor(requireContext(),R.color.secondaryText))
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
            setCenterTextColor(ContextCompat.getColor(requireContext(),R.color.rosaChillon))
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
            setEntryLabelColor(ContextCompat.getColor(requireContext(),R.color.secondaryText))
            setEntryLabelTypeface(tfRegular)
            setEntryLabelTextSize(14f)
            centerText = "${taskcompleted + taskpending}"
            data = datatask
            highlightValues(null)
            invalidate()
        }

        charttask.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "CHARTTASK")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------
        }

        val duenextcard = inflatedView.findViewById<View>(R.id.duenextcard)
        if (task.key == "") {
            duenextcard.visibility = View.GONE
        } else {
            val cardname = duenextcard.findViewById<TextView>(R.id.cardtitle)
            val cardsecondarytext = duenextcard.findViewById<TextView>(R.id.secondarytext)
            val action1Button = duenextcard.findViewById<Button>(R.id.action1)
            val action2Button = duenextcard.findViewById<Button>(R.id.action2)

            cardname.text = getString(R.string.duenext)
            //cardsecondarytext.text = "${task.name} due by ${task.date}"
            val cardmsg = StringBuilder()
            cardmsg.append(task.name).append(getString(R.string.dueby)).append(task.date)
            cardsecondarytext.text = cardmsg
            action1Button.text = getString(R.string.view)
            action2Button.visibility = View.INVISIBLE

            action1Button.setOnClickListener {
                // ------- Analytics call ----------------
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "DUENEXTTASK")
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
                MyFirebaseApp.mFirebaseAnalytics.logEvent(
                    FirebaseAnalytics.Event.SELECT_ITEM,
                    bundle
                )
                //----------------------------------------
                val taskdetail = Intent(context, TaskCreateEdit::class.java)
                taskdetail.putExtra("task", task)
                requireContext().startActivity(taskdetail)
            }
        }
    }

    override fun onTaskStatsError(inflatedView: View, errcode: String) {
        inflatedView.withdata.visibility = ConstraintLayout.GONE
        inflatedView.withnodata.visibility = ConstraintLayout.VISIBLE

        inflatedView.withnodata.newtaskbutton.visibility = FloatingActionButton.VISIBLE
        inflatedView.withnodata.newtaskbutton.setOnClickListener {
            val newtask = Intent(activity, TaskCreateEdit::class.java)
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
        val chartpayment = cardlayout.findViewById<PieChart>(R.id.chartpayment)

        val chartcolors = ArrayList<Int>()
        for (c in BandG_Colors2) chartcolors.add(c)

        cardtitle.text = getString(R.string.payments)

        val paymententries = ArrayList<PieEntry>()
        paymententries.add(PieEntry(sumpayment, getString(R.string.spent)))
        paymententries.add(PieEntry(sumbudget - sumpayment, getString(R.string.available)))

        val dataSetpayment = PieDataSet(paymententries, "").apply {
            setDrawIcons(false)
            sliceSpace = 3f //separation between slices
            iconsOffset =
                MPPointF(0f, 20f) //position of labels to slices. 40f seems too much
            colors = chartcolors
            xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        }

        val datapayment = PieData(dataSetpayment).apply {
            setValueFormatter(myPercentageFormatter())
            setValueTextSize(14f)
            setValueTextColor(ContextCompat.getColor(requireContext(),R.color.secondaryText))
            setValueTypeface(tfRegular)
        }

        chartpayment.legend.apply {
            form = Legend.LegendForm.CIRCLE
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            textSize = 12.0f
            textColor = (ContextCompat.getColor(requireContext(),R.color.secondaryText))
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
            isDrawHoleEnabled = false
            rotationAngle = 0f
            isRotationEnabled = false
            isHighlightPerTapEnabled = true
            setEntryLabelColor(ContextCompat.getColor(requireContext(),R.color.secondaryText))
            setEntryLabelTypeface(tfRegular)
            setEntryLabelTextSize(14f)
            data = datapayment
            highlightValues(null)
            invalidate()
        }

        chartpayment.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "CHARTPAYMENT")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------
        }

        val loadingscreen = requireActivity().findViewById<ConstraintLayout>(R.id.loadingscreen)
        val drawerlayout = requireActivity().findViewById<DrawerLayout>(R.id.drawerlayout)
        loadingscreen.visibility = ConstraintLayout.GONE
        drawerlayout.visibility = ConstraintLayout.VISIBLE
    }

    override fun onPaymentsStatsError(inflatedView: View, errcode: String) {
        val paymentlayout = inflatedView.findViewById<View>(R.id.paymentchart)
        paymentlayout.visibility =
            View.INVISIBLE

        val loadingscreen = requireActivity().findViewById<ConstraintLayout>(R.id.loadingscreen)
        val drawerlayout = requireActivity().findViewById<DrawerLayout>(R.id.drawerlayout)
        loadingscreen.visibility = ConstraintLayout.GONE
        drawerlayout.visibility = ConstraintLayout.VISIBLE
    }

    class myPercentageFormatter : ValueFormatter() {
        private var mFormat: DecimalFormat? = null
        init {
            mFormat = DecimalFormat("###,###,##0.0")
        }

        override fun getFormattedValue(value: Float): String {
            return mFormat!!.format(value.toDouble()) + " %"
        }
    }

    override fun onGuestConfirmation(
        inflatedView: View,
        confirmed: Int,
        rejected: Int,
        pending: Int
    ) {
        inflatedView.guestlayout.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "GUESTINFOCARD")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------
        }

        inflatedView.acceptednumber.text = confirmed.toString()
        inflatedView.rejectednumber.text = rejected.toString()
        inflatedView.pendingnumber.text = pending.toString()
    }

    override fun onGuestConfirmationError(inflatedView: View, errcode: String) {
        inflatedView.guestlayout.visibility = ConstraintLayout.INVISIBLE
        inflatedView.noguestlayout.visibility = ConstraintLayout.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    override fun onEvent(context: Context, inflatedview: View, event: Event) {
        placeid = event.placeid

        inflatedview.findViewById<TextView>(R.id.eventname).text = event.name
        inflatedview.findViewById<TextView>(R.id.eventdate).text = event.date
        inflatedview.findViewById<TextView>(R.id.eventaddress).text = event.location
        inflatedview.findViewById<TextView>(R.id.eventfulladdress).text = event.address

        val daysleft = daystoDate(converttoDate(event.date))
        inflatedview.findViewById<TextView>(R.id.deadline).text = daysleft.toString().plus(getString(
                    R.string.daysleft))

        // Load thumbnail
        imagePresenter = ImagePresenter(context, this, inflatedview)
        imagePresenter.getEventImage()
        //TODO Waiting this to fail and see how it's used
       // imagePresenter.apiKey = resources.getString(R.string.google_maps_key)
        imagePresenter.getPlaceImage()

        inflatedview.weddinglocation2.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "LOCATIONEVENT")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------

            val gmmIntentUri =
                Uri.parse("geo:${event.latitude},${event.longitude}?z=10&q=${event.location}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            mapIntent.resolveActivity(context.packageManager)?.let {
                startActivity(mapIntent)
            }
            val loadingscreen = requireActivity().findViewById<ConstraintLayout>(R.id.loadingscreen)
            val drawerlayout = requireActivity().findViewById<DrawerLayout>(R.id.drawerlayout)
            loadingscreen.visibility = ConstraintLayout.GONE
            drawerlayout.visibility = ConstraintLayout.VISIBLE
        }
    }

    override fun onEventError(inflatedview: View, errorcode: String) {
        Toast.makeText(
            context,
            getString(R.string.error_gettingdata),
            Toast.LENGTH_SHORT
        ).show()
        Log.i("EventSummary.TAG", "No data was obtained from the Event")
        val loadingscreen = requireActivity().findViewById<ConstraintLayout>(R.id.loadingscreen)
        val drawerlayout = requireActivity().findViewById<DrawerLayout>(R.id.drawerlayout)
        loadingscreen.visibility = ConstraintLayout.GONE
        drawerlayout.visibility = ConstraintLayout.VISIBLE
    }

    override fun onEventImage(context: Context, inflatedView: View?, packet: Any) {
        val wedavater = inflatedView!!.findViewById<ImageView>(R.id.weddingavatar)

        Glide.with(context)
            .load(packet)
            .apply(RequestOptions.circleCropTransform())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            }).placeholder(R.drawable.avatar2)
            .into(wedavater)
        Log.i("EventSummary.TAG", "Image for the event is loaded")
    }

    override fun onPlaceImage(context: Context, inflatedView: View?, image: Bitmap) {
        inflatedView!!.placesimage.setImageBitmap(image)
    }

    override fun onEmptyPlaceImageSD(inflatedView: View?) {
        getImgfromPlaces(
            requireContext(),
            placeid,
            resources.getString(R.string.google_maps_key),
            ImagePresenter.PLACEIMAGE,
            inflatedView!!.placesimage
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fm = requireActivity().supportFragmentManager
        if (resultCode == Activity.RESULT_OK) {
            Toast.makeText(activity, getString(R.string.success_eventchanges), Toast.LENGTH_LONG).show()
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                val newfragment = DashboardEvent()
                fm.beginTransaction()
                    .replace(R.id.fragment_container, newfragment)
                    .commit()
            }, 2000) //1 seconds
        }
    }

    companion object {
        const val SUCCESS_RETURN = 1
    }
}


