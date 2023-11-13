package com.bridesandgrooms.event

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
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.baoyachi.stepview.HorizontalStepView
import com.bridesandgrooms.event.Functions.PermissionUtils
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bridesandgrooms.event.Functions.converttoDate
import com.bridesandgrooms.event.Functions.daystoDate
import com.bridesandgrooms.event.Functions.getImgfromPlaces
import com.bridesandgrooms.event.Functions.userdbhelper
import com.bridesandgrooms.event.MVP.DashboardEventPresenter
import com.bridesandgrooms.event.MVP.ImagePresenter
import com.bridesandgrooms.event.Model.Event
import com.bridesandgrooms.event.Model.MyFirebaseApp
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.databinding.DashboardchartsBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
//import kotlinx.android.synthetic.main.chartcard_layoutpayment.view.*
//import kotlinx.android.synthetic.main.dashboardcharts.view.*
//import kotlinx.android.synthetic.main.empty_state.view.*
//import kotlinx.android.synthetic.main.onboardingcard.view.*
//import kotlinx.android.synthetic.main.summary_weddingguests.view.*
//import kotlinx.android.synthetic.main.summary_weddinglocation.view.*

class DashboardEvent : Fragment(), DashboardEventPresenter.TaskStats,
    DashboardEventPresenter.PaymentStats, DashboardEventPresenter.GuestStats,
    DashboardEventPresenter.EventInterface, ImagePresenter.EventImage, ImagePresenter.PlaceImage {

    private lateinit var BandG_Colors: ArrayList<Int>
    private lateinit var BandG_Colors2: ArrayList<Int>
    private lateinit var dashboardEP: DashboardEventPresenter
    private lateinit var imagePresenter: ImagePresenter
    private lateinit var adView: AdView

    private var placeid = ""

    private var tfLarge: Typeface? = null
    private var tfRegular: Typeface? = null
    private var tfLight: Typeface? = null

    private lateinit var inf: DashboardchartsBinding
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        // Initialize the MobileAds SDK with your AdMob App ID
        val showads = RemoteConfigSingleton.get_showads()

        if (showads) {
            MobileAds.initialize(requireContext()) { initializationStatus ->
                // You can leave this empty or handle initialization status if needed
            }
        }

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
    ): View {
        // Inflate the layout for this fragment
        inf = DataBindingUtil.inflate(inflater, R.layout.dashboardcharts, container, false)

        // Load the ad into the AdView
        val showads = RemoteConfigSingleton.get_showads()

        if (showads) {
            inf.adView.visibility = ConstraintLayout.VISIBLE
            adView = inf.adView
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }

        //Calling the presenter that will pull of the data I need for this view
        try {
            dashboardEP = DashboardEventPresenter(requireContext(), this, inf.root)
        } catch (e: Exception) {
            println(e.message)
        }
        //this needs to evaluate if it's true to continue the process, else it will stop it
        user = userdbhelper.getUser(userdbhelper.getUserKey())!!

        if (user.hastask == "Y" || user.haspayment == "Y") {
            inf.withnodata1.root.visibility = ConstraintLayout.GONE
            inf.withdata.visibility = ConstraintLayout.VISIBLE
            //----------------------------------------------------------------------------------


            //Load with the achievements obtained by the user -------------------------------------------
            val stepsBeanList = user.onboardingprogress(requireContext())
            val stepview = inf.root.findViewById<HorizontalStepView>(R.id.step_view)

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

            val weddingphotodetail = inf.weddingphotodetail
            weddingphotodetail.root.setOnClickListener {
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

            val weddingprogress = inf.root.findViewById<ConstraintLayout>(R.id.weddingprogress)
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
            inf.withdata.visibility = ConstraintLayout.GONE
            inf.onboarding.root.visibility = ConstraintLayout.VISIBLE
            inf.withnodata1.emptyCard.onboardingmessage.text =
                getString(R.string.onboarding_message_createtask)

            val bottomView = activity?.findViewById<BottomNavigationView>(R.id.bottomnav)!!
            for (i in 0 until bottomView.menu.size()) {
                bottomView.menu.getItem(i).isEnabled = false
            }

            //inf.withnodata1.newtaskbutton.visibility = FloatingActionButton.VISIBLE
            val mAlphaAnimation = AnimationUtils.loadAnimation(context, R.xml.alpha_animation)
            inf.onboarding.newtaskbutton.startAnimation(mAlphaAnimation)
            inf.onboarding.newtaskbutton.setOnClickListener {
                val newtask = Intent(activity, TaskCreateEdit::class.java)
                startActivity(newtask)
            }
        }
        return inf.root
    }

    @SuppressLint("SetTextI18n")
    override fun onTasksStats(
        taskpending: Int,
        taskcompleted: Int,
        sumbudget: Float,
        task: Task
    ) {
        val cardlayoutinvisible = inf.taskchartnodata
        cardlayoutinvisible.root.visibility = View.GONE

        val cardlayout = inf.taskchart
        cardlayout.root.visibility = View.VISIBLE

        val cardtitle = cardlayout.cardtitle
        val charttask = cardlayout.charttask

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

        val duenextcard = inf.duenextcard
        if (task.key == "") {
            duenextcard.root.visibility = View.GONE
        } else {
            val cardname = duenextcard.cardtitle
            val cardsecondarytext = duenextcard.secondarytext
            val action1Button = duenextcard.action1
            val action2Button = duenextcard.action2

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

    override fun onTaskStatsError(errcode: String) {

        val cardlayoutvisible = inf.taskchart
        val cardlayoutinvisible = inf.taskchartnodata

        cardlayoutvisible.root.visibility = ConstraintLayout.GONE
        cardlayoutinvisible.root.visibility = ConstraintLayout.VISIBLE
//        inf.withnodata.newtaskbutton.visibility = FloatingActionButton.VISIBLE
//        inf.withnodata.newtaskbutton.setOnClickListener {
//            val newtask = Intent(activity, TaskCreateEdit::class.java)
//            startActivity(newtask)
//        }
    }

    override fun onPaymentsStats(
        countpayment: Int,
        sumpayment: Float,
        sumbudget: Float
    ) {
        val cardlayoutinvisible = inf.paymentchartnodata
        cardlayoutinvisible.root.visibility = View.GONE

        val cardlayout = inf.paymentchart
        cardlayout.root.visibility = View.VISIBLE
        val cardtitle = cardlayout.cardtitle
        val chartpayment = cardlayout.chartpayment
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

    override fun onPaymentsStatsError(errcode: String) {
        val cardlayoutvisible = inf.paymentchart
        val cardlayoutinvisible = inf.paymentchartnodata

        cardlayoutvisible.root.visibility = ConstraintLayout.GONE
        cardlayoutinvisible.root.visibility = ConstraintLayout.VISIBLE
        cardlayoutinvisible.cardtitle.text = getString(R.string.payments)
    }

    override fun onGuestConfirmation(
        confirmed: Int,
        rejected: Int,
        pending: Int
    ) {
        val cardlayoutvisible = inf.guestlayout
        cardlayoutvisible.visibility = View.VISIBLE

        val cardlayoutinvisible = inf.noguestlayout
        cardlayoutinvisible.visibility = View.GONE

        inf.dashboardVerticalLayout5.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "GUESTINFOCARD")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------
        }
        inf.guestlayout.findViewById<TextView>(R.id.acceptednumber).text  = confirmed.toString()
        inf.guestlayout.findViewById<TextView>(R.id.rejectednumber).text = rejected.toString()
        inf.guestlayout.findViewById<TextView>(R.id.pendingnumber).text = pending.toString()
    }

    override fun onGuestConfirmationError(errcode: String) {
        inf.guestlayout.visibility = ConstraintLayout.GONE
        inf.noguestlayout.visibility = ConstraintLayout.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    override fun onEvent(context: Context, event: Event) {
        placeid = event.placeid

        inf.weddingphotodetail.eventname.text =  event.name
        inf.weddingphotodetail.eventdate.text = event.date
        inf.weddinglocation2.eventaddress.text = event.location
        inf.weddinglocation2.eventfulladdress.text = event.address

        var daysleft = 0
        try {
            daysleft = daystoDate(converttoDate(event.date))
        } catch (e: Exception) {
            user.softlogout(requireActivity())
        }

        inf.weddingphotodetail.deadline.text = try {
            daysleft.toString().plus(" ").plus(
                getString(
                    R.string.daysleft
                )
            )
        } catch (e: Exception) {
            println(e.message)
            ""
        }

        // Load thumbnail
        imagePresenter = ImagePresenter(context, this, inf.root)
        imagePresenter.getEventImage()
        //TODO Waiting this to fail and see how it's used
        // imagePresenter.apiKey = resources.getString(R.string.google_maps_key)
        imagePresenter.getPlaceImage()

        inf.weddinglocation2.root.setOnClickListener {
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

    override fun onEventError(inf: View, errorcode: String) {
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

    override fun onEventImage(context: Context, inf: View?, packet: Any) {
        val wedavater = inf!!.findViewById<ImageView>(R.id.weddingavatar)

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

    override fun onPlaceImage(context: Context, image: Bitmap) {
        inf.weddinglocation2.placesimage.setImageBitmap(image)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onEmptyPlaceImageSD() {
        //Checking for permissions to read the contacts information
        if (!PermissionUtils.checkPermissions(requireActivity().applicationContext, "storage")) {
            PermissionUtils.alertBox(requireActivity().applicationContext, "storage")
        } else {
            //permission already granted
            getImgfromPlaces(
                requireContext(),
                placeid,
                resources.getString(R.string.google_maps_key),
                ImagePresenter.PLACEIMAGE,
                inf.weddinglocation2.placesimage
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
                        inf.weddinglocation2.placesimage
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


