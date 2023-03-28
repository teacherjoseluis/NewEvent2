package com.example.newevent2

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
import com.example.newevent2.Functions.userdbhelper
import com.example.newevent2.MVP.ContactsAllPresenter
import com.example.newevent2.MVP.DashboardEventPresenter
import com.example.newevent2.MVP.ImagePresenter
import com.example.newevent2.Model.Event
import com.example.newevent2.Model.MyFirebaseApp
import com.example.newevent2.Model.Task
import com.example.newevent2.ui.CurrencyValueFormatter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.chartcard_layoutpayment.view.*
import kotlinx.android.synthetic.main.dashboardcharts.view.*
import kotlinx.android.synthetic.main.empty_state.view.*
import kotlinx.android.synthetic.main.onboardingcard.view.*
import kotlinx.android.synthetic.main.summary_weddingguests.view.*
import kotlinx.android.synthetic.main.summary_weddinglocation.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.collections.ArrayList

class DashboardEvent : Fragment(), DashboardEventPresenter.TaskStats,
    DashboardEventPresenter.PaymentStats, DashboardEventPresenter.GuestStats,
    DashboardEventPresenter.EventInterface, ImagePresenter.EventImage, ImagePresenter.PlaceImage {

    private lateinit var BandG_Colors: ArrayList<Int>
    private lateinit var BandG_Colors2: ArrayList<Int>
    private lateinit var dashboardEP: DashboardEventPresenter
    private lateinit var imagePresenter: ImagePresenter

    private var placeid = ""

    private var tfLarge: Typeface? = null
    private var tfRegular: Typeface? = null
    private var tfLight: Typeface? = null

    private lateinit var inflatedView: View

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
    }

    @SuppressLint("UseCompatLoadingForDrawables", "ResourceType")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.dashboardcharts, container, false)

        //Calling the presenter that will pull of the data I need for this view
        dashboardEP = DashboardEventPresenter(requireContext(), this, inflatedView)
        //this needs to evaluate if it's true to continue the process, else it will stop it
        val user = userdbhelper.getUser(userdbhelper.getUserKey())

        if (user.hastask == "Y" || user.haspayment == "Y") {
            inflatedView.withnodata1.visibility = ConstraintLayout.GONE
            inflatedView.withdata.visibility = ConstraintLayout.VISIBLE
            //----------------------------------------------------------------------------------


            //Load with the achievements obtained by the user -------------------------------------------
            val stepsBeanList = user.onboardingprogress(context!!)
            val stepview = inflatedView.findViewById<HorizontalStepView>(R.id.step_view)
            stepview
                .setStepViewTexts(stepsBeanList)//总步骤
                .setTextSize(12)//set textSize
                .setStepsViewIndicatorCompletedLineColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.azulmasClaro
                    )
                )//设置StepsViewIndicator完成线的颜色
                .setStepsViewIndicatorUnCompletedLineColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.rosaChillon
                    )
                )//设置StepsViewIndicator未完成线的颜色
                .setStepViewComplectedTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.azulmasClaro
                    )
                )//设置StepsView text完成线的颜色
                .setStepViewUnComplectedTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.rosaChillon
                    )
                )//设置StepsView text未完成线的颜色
                .setStepsViewIndicatorCompleteIcon(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.icons8_checked_rosachillon
                    )
                )//设置StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorDefaultIcon(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.circle_rosachillon
                    )
                )//设置StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.alert_icon_rosachillon
                    )
                )//设置StepsViewIndicator AttentionIcon
            //--------------------------------------------------------------------------------------------

            val weddingphotodetail = inflatedView.findViewById<LinearLayout>(R.id.weddingphotodetail)
            weddingphotodetail.setOnClickListener {
                // ------- Analytics call ----------------
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "EDITEVENT")
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
                MyFirebaseApp.mFirebaseAnalytics.logEvent(
                    FirebaseAnalytics.Event.SELECT_ITEM,
                    bundle
                )
                //----------------------------------------
                val editevent = Intent(context, MainActivity::class.java)
                startActivityForResult(editevent, SUCCESS_RETURN)
            }

            val weddingprogress = inflatedView.findViewById<ConstraintLayout>(R.id.weddingprogress)
            weddingprogress.setOnClickListener {
                // ------- Analytics call ----------------
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "MYPROGRESS")
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
                MyFirebaseApp.mFirebaseAnalytics.logEvent(
                    FirebaseAnalytics.Event.SELECT_ITEM,
                    bundle
                )
                //----------------------------------------
            }

            //There were children coming out from the Event. Success
            //This section could potentially start launching the next calls
            dashboardEP.getTaskList()
            dashboardEP.getPaymentList()
            dashboardEP.getEvent()
            dashboardEP.getGuestList()
        } else {
            //Noting to do here. The whole process must end and we need to show a layout
            //expressing that there is nothing to see here.
            Toast.makeText(
                context,
                "You can start by adding either tasks or payments related to your event",
                Toast.LENGTH_SHORT
            ).show()
            Log.i("EventSummary.TAG", "No data was obtained from the Event")
            inflatedView.withdata.visibility = ConstraintLayout.GONE
            inflatedView.onboarding.visibility = ConstraintLayout.VISIBLE
            inflatedView.onboarding_card.onboardingmessage.text = getString(R.string.onboarding_message_createtask)

            //inflatedView.withnodata1.newtaskbutton.visibility = FloatingActionButton.VISIBLE
            val mAlphaAnimation =   AnimationUtils.loadAnimation(context,R.xml.alpha_animation)
            inflatedView.onboarding.newtaskbutton.startAnimation(mAlphaAnimation)
            inflatedView.onboarding.newtaskbutton.setOnClickListener {
                val newtask = Intent(activity, TaskCreateEdit::class.java)
                startActivity(newtask)
            }
        }
        return inflatedView
    }

    @SuppressLint("SetTextI18n")
    override fun onTasksStats(
        inflatedView: View,
        taskpending: Int,
        taskcompleted: Int,
        sumbudget: Float,
        task: Task
    ) {
        val cardlayoutinvisible = inflatedView.findViewById<View>(R.id.taskchartnodata)
        cardlayoutinvisible.visibility = View.GONE

        val cardlayout = inflatedView.findViewById<View>(R.id.taskchart)
        cardlayout.visibility = View.VISIBLE
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
            setValueTextColor(ContextCompat.getColor(requireContext(), R.color.secondaryText))
            setValueTypeface(tfRegular)
        }

        charttask.legend.apply {
            form = Legend.LegendForm.CIRCLE
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            textSize = 12.0f
            textColor = (ContextCompat.getColor(requireContext(), R.color.secondaryText))
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
            setCenterTextColor(ContextCompat.getColor(requireContext(), R.color.rosaChillon))
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
            setEntryLabelColor(ContextCompat.getColor(requireContext(), R.color.secondaryText))
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
            cardmsg.append(task.name).append(" ").append(getString(R.string.dueby)).append(" ")
                .append(task.date)
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
        inflatedView.withdata.taskchart.visibility = ConstraintLayout.GONE
        inflatedView.withdata.taskchartnodata.visibility = ConstraintLayout.VISIBLE
//        inflatedView.withnodata.newtaskbutton.visibility = FloatingActionButton.VISIBLE
//        inflatedView.withnodata.newtaskbutton.setOnClickListener {
//            val newtask = Intent(activity, TaskCreateEdit::class.java)
//            startActivity(newtask)
//        }
    }

    override fun onPaymentsStats(
        inflatedView: View,
        countpayment: Int,
        sumpayment: Float,
        sumbudget: Float
    ) {
        val cardlayoutinvisible = inflatedView.findViewById<View>(R.id.paymentchartnodata)
        cardlayoutinvisible.visibility = View.GONE

        val cardlayout = inflatedView.findViewById<View>(R.id.paymentchart)
        cardlayout.visibility = View.VISIBLE
        val cardtitle = cardlayout.findViewById<TextView>(R.id.cardtitle)
        val chartpayment = cardlayout.findViewById<BarChart>(R.id.chartpayment)
        chartpayment.description.isEnabled = false

        val chartcolors = ArrayList<Int>()
        for (c in BandG_Colors2) chartcolors.add(c)
        cardtitle.text = getString(R.string.payments)

//        val paymententries = ArrayList<PieEntry>()
//        paymententries.add(PieEntry(sumpayment, getString(R.string.spent)))
//        paymententries.add(PieEntry(sumbudget - sumpayment, getString(R.string.available)))

        val paymententries1 = ArrayList<BarEntry>()
        paymententries1.add(BarEntry(0f, sumpayment))

        val paymententries2 = ArrayList<BarEntry>()
        paymententries2.add(BarEntry(1f, sumbudget))

        val dataSetpayment1 = BarDataSet(paymententries1, getString(R.string.payments)).apply {
            //setDrawIcons(false)
            //sliceSpace = 3f //separation between slices
            //iconsOffset = MPPointF(0f, 20f) //position of labels to slices. 40f seems too much
            color = ContextCompat.getColor(requireContext(), R.color.azulmasClaro)
            //xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        }

        val dataSetpayment2 = BarDataSet(paymententries2, getString(R.string.budget)).apply {
            //setDrawIcons(false)
            //sliceSpace = 3f //separation between slices
            //iconsOffset = MPPointF(0f, 20f) //position of labels to slices. 40f seems too much
            color = ContextCompat.getColor(requireContext(), R.color.rosapalido)
            //xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        }

        val dataSetpayment = ArrayList<BarDataSet>()
        dataSetpayment.add(dataSetpayment1)
        dataSetpayment.add(dataSetpayment2)


        val datapayment = BarData(dataSetpayment as List<IBarDataSet>?).apply {
            //setValueFormatter(CurrencyValueFormatter())
            barWidth = 0.5f
            setValueTextSize(14f)
            setValueTextColor(ContextCompat.getColor(requireContext(), R.color.secondaryText))
            setValueTypeface(tfRegular)
        }

        chartpayment.legend.apply {
            form = Legend.LegendForm.CIRCLE
            isEnabled = true
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            textSize = 12.0f
            textColor = (ContextCompat.getColor(requireContext(), R.color.secondaryText))
            typeface = tfRegular
            setDrawInside(false)
            xEntrySpace = 10f
            yEntrySpace = 4f
            yOffset = 5f
        }

        chartpayment.apply {
            //setUsePercentValues(true)
            //description.isEnabled = false
            //setExtraOffsets(5f, -5f, -20f, 15f) //apparently this is padding
            //dragDecelerationFrictionCoef = 0.95f
            //isDrawHoleEnabled = false
            //rotationAngle = 0f
            //isRotationEnabled = false
            //isHighlightPerTapEnabled = true
            //setEntryLabelColor(ContextCompat.getColor(requireContext(), R.color.secondaryText))
            //setEntryLabelTypeface(tfRegular)
            //setEntryLabelTextSize(14f)
            axisLeft.axisMinimum = 0f
            axisRight.axisMinimum = 0f
            axisRight.isEnabled = false
            axisLeft.isEnabled = false
            xAxis.isEnabled = false
            data = datapayment
            setFitBars(true)
            //highlightValues(null)
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
    }

    override fun onPaymentsStatsError(inflatedView: View, errcode: String) {
        inflatedView.withdata.paymentchart.visibility = ConstraintLayout.GONE
        inflatedView.withdata.paymentchartnodata.visibility = ConstraintLayout.VISIBLE
        inflatedView.withdata.paymentchartnodata.cardtitle.text = getString(R.string.payments)
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
        val cardlayoutinvisible = inflatedView.findViewById<View>(R.id.noguestlayout)
        cardlayoutinvisible.visibility = View.GONE

        inflatedView.dashboard_vertical_layout_5.setOnClickListener {
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
        inflatedView.guestlayout.visibility = ConstraintLayout.GONE
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
        inflatedview.findViewById<TextView>(R.id.deadline).text =
            daysleft.toString().plus(" ").plus(
                getString(
                    R.string.daysleft
                )
            )

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

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onEmptyPlaceImageSD(inflatedView: View?) {
        //Checking for permissions to read the contacts information
        if (ContextCompat.checkSelfPermission(
                activity!!.applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) ==
            PackageManager.PERMISSION_DENIED
        ) {
            //permission denied
            val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            //show popup to request runtime permission
            requestPermissions(permissions, PERMISSION_CODE)
        } else {
            //permission already granted
            getImgfromPlaces(
                requireContext(),
                placeid,
                resources.getString(R.string.google_maps_key),
                ImagePresenter.PLACEIMAGE,
                inflatedView!!.placesimage
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fm = requireActivity().supportFragmentManager
        if (resultCode == Activity.RESULT_OK) {
            Toast.makeText(activity, getString(R.string.success_eventchanges), Toast.LENGTH_LONG)
                .show()
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                val newfragment = DashboardEvent()
                fm.beginTransaction()
                    .replace(R.id.fragment_container, newfragment)
                    .commit()
            }, 2000) //1 seconds
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    getImgfromPlaces(
                        requireContext(),
                        placeid,
                        resources.getString(R.string.google_maps_key),
                        ImagePresenter.PLACEIMAGE,
                        inflatedView!!.placesimage
                    )
                }
            }
        }
    }

    companion object {
        const val SUCCESS_RETURN = 1
        internal const val PERMISSION_CODE = 1001
    }
}


