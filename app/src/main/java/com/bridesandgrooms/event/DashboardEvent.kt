package com.bridesandgrooms.event

import Application.AnalyticsManager
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
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
import androidx.lifecycle.lifecycleScope
import com.bridesandgrooms.event.Functions.PermissionUtils
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bridesandgrooms.event.Functions.converttoDate
import com.bridesandgrooms.event.Functions.daystoDate
import com.bridesandgrooms.event.Functions.getImgfromPlaces
import com.bridesandgrooms.event.MVP.DashboardEventPresenter
import com.bridesandgrooms.event.MVP.ImagePresenter
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.Model.DashboardImage.DashboardRepository
import com.bridesandgrooms.event.Model.DashboardImage.FirebaseDataSourceImpl
import com.bridesandgrooms.event.Model.Event
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.UI.Adapters.CategoryAdapter
import com.bridesandgrooms.event.UI.Fragments.CategoryFragmentActionListener
import com.bridesandgrooms.event.UI.Fragments.MainActivity
import com.bridesandgrooms.event.databinding.DashboardchartsBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class DashboardEvent : Fragment(), DashboardEventPresenter.TaskStats,
    DashboardEventPresenter.PaymentStats, DashboardEventPresenter.GuestStats,
    DashboardEventPresenter.EventInterface, DashboardEventPresenter.DashboardImagesStats,
    DashboardEventPresenter.EventCategoryInterface,
    DashboardEventPresenter.TaskNextInterface,
    ImagePresenter.EventImage, ImagePresenter.PlaceImage,
    CategoryFragmentActionListener, GalleryFragmentActionListener {

    private lateinit var BandG_Colors: ArrayList<Int>
    private lateinit var BandG_Colors2: ArrayList<Int>
    private lateinit var dashboardEP: DashboardEventPresenter
    private lateinit var imagePresenter: ImagePresenter
    private lateinit var adView: AdView
    private lateinit var toolbar: MaterialToolbar

    private var placeid = ""
    private var showAds = false

    private var tfLarge: Typeface? = null
    private var tfRegular: Typeface? = null
    private var tfLight: Typeface? = null

    private lateinit var inf: DashboardchartsBinding
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        val rosaPalido = ContextCompat.getColor(requireContext(), R.color.Primary_cream)
        val azulmasClaro = ContextCompat.getColor(requireContext(), R.color.Tertiary_cream)
        val palodeRosa = ContextCompat.getColor(requireContext(), R.color.PrimaryContainer_cream)
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

        user = User().getUser(requireContext())

        // Initialize the MobileAds SDK with your AdMob App ID
        showAds = RemoteConfigSingleton.get_showads()
        if (showAds) {
            MobileAds.initialize(requireContext()) { initializationStatus ->
                // You can leave this empty or handle initialization status if needed
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables", "ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.myeventtitle)

        inf = DataBindingUtil.inflate(inflater, R.layout.dashboardcharts, container, false)

        if (showAds) {
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }

        if (user.hastask == "Y" || user.haspayment == "Y") {
            inf.weddingavatarCard.setOnClickListener {
                AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME, "Edit_Event")

                val fragment = MainActivity()
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.fragment_container, fragment)
                    .commit()
            }

            try {
                dashboardEP = DashboardEventPresenter(requireContext(), this, inf.root)
                dashboardEP.getTaskList()
                dashboardEP.getPaymentList()
                dashboardEP.getEvent()
                dashboardEP.getGuestList()

                val repository = DashboardRepository(FirebaseDataSourceImpl(requireContext()))
                dashboardEP.setRepository(repository)
                dashboardEP.fetchDashboardImages()

                dashboardEP.getActiveCategories()
                dashboardEP.getUpcomingTasks()
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        } else {
            Log.i(TAG, "No data was obtained from the Event")

            val bottomView = activity?.findViewById<BottomNavigationView>(R.id.bottomnav)!!
            for (i in 0 until bottomView.menu.size()) {
                bottomView.menu.getItem(i).isEnabled = false
            }
        }
        return inf.root
    }

    override fun onDashboardImages(images: List<DashboardEventPresenter.CategoryThumbnails>) {
        val rvAdapter = rvDashboardImageAdapter(this, images, requireContext())
        inf.categoryrv.adapter = rvAdapter
        inf.welcomelabel2.visibility = View.VISIBLE
        Log.d("DashboardEvent", "Entering onDashboardImages")
    }

    override fun onDashboardImagesError(errcode: String) {
        Log.d("DashboardImages", "onDashboardImagesError: Not yet implemented")
    }

    @SuppressLint("SetTextI18n")
    override fun onTasksStats(
        taskpending: Int,
        taskcompleted: Int,
        sumbudget: Float,
        task: Task
    ) {
        val holeColor = ContextCompat.getColor(requireContext(), R.color.SurfaceContainer_cream)

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
            setExtraOffsets(0f, 5f, -8f, 15f) //apparently this is padding
            dragDecelerationFrictionCoef = 0.95f
            setCenterTextTypeface(tfRegular)
            setCenterTextColor(ContextCompat.getColor(requireContext(), R.color.OnSecondaryContainer_cream))
            setCenterTextSize(30f)
            isDrawHoleEnabled = true
            setHoleColor(holeColor)
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

    }

    override fun onTaskStatsError(errcode: String) {
        val cardlayoutvisible = inf.taskchart
        val cardlayoutinvisible = inf.taskchartnodata

        cardlayoutvisible.root.visibility = ConstraintLayout.GONE
        cardlayoutinvisible.root.visibility = ConstraintLayout.VISIBLE
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

        val paymententries1 = ArrayList<BarEntry>()
        paymententries1.add(BarEntry(0f, sumpayment))

        val paymententries2 = ArrayList<BarEntry>()
        paymententries2.add(BarEntry(1f, sumbudget))

        val dataSetpayment1 = BarDataSet(paymententries1, getString(R.string.payments)).apply {
            color = ContextCompat.getColor(requireContext(), R.color.Tertiary_cream)
        }

        val dataSetpayment2 = BarDataSet(paymententries2, getString(R.string.budget)).apply {
            color = ContextCompat.getColor(requireContext(), R.color.Primary_cream)
        }

        val dataSetpayment = ArrayList<BarDataSet>().apply {
            add(dataSetpayment1)
            add(dataSetpayment2)
        }

        val datapayment = BarData(dataSetpayment as List<IBarDataSet>?).apply {
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
            axisLeft.axisMinimum = 0f
            axisRight.axisMinimum = 0f
            axisRight.isEnabled = false
            axisLeft.isEnabled = false
            xAxis.isEnabled = false
            data = datapayment
            setFitBars(true)
            invalidate()
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
        val cardlayoutvisible = inf.dashboardVerticalLayout6
        cardlayoutvisible.visibility = View.VISIBLE

        inf.dashboardVerticalLayout6.findViewById<TextView>(R.id.acceptednumber).text =
            confirmed.toString()
        inf.dashboardVerticalLayout6.findViewById<TextView>(R.id.rejectednumber).text =
            rejected.toString()
        inf.dashboardVerticalLayout6.findViewById<TextView>(R.id.pendingnumber).text =
            pending.toString()
    }

    override fun onGuestConfirmationError(errcode: String) {
        inf.dashboardVerticalLayout6.visibility = ConstraintLayout.GONE
        //inf.noguestlayout.visibility = ConstraintLayout.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    override fun onEvent(context: Context, event: Event) {
        placeid = event.placeid

        inf.weddinginvitation.eventgreeting.text =
            getString(R.string.event_greeting, user.shortname)
        inf.weddinginvitation.eventname.text = event.name
        inf.weddinginvitation.eventdate.text = event.date
        inf.weddinginvitation.eventaddress.text = event.location
        inf.weddinginvitation.eventfulladdress.text = event.address

        var daysleft = 0
        try {
            daysleft = daystoDate(converttoDate(event.date))
        } catch (e: Exception) {
            user.softlogout(requireActivity())
            AnalyticsManager.getInstance().trackError(
                SCREEN_NAME,
                e.message.toString(),
                "daystoDate()",
                e.stackTraceToString()
            )
            Log.e(TAG, e.message.toString())
        }

        inf.weddinginvitation.deadline.text = try {
            daysleft.toString().plus(" ").plus(
                getString(
                    R.string.daysleft
                )
            )
        } catch (e: Exception) {
            AnalyticsManager.getInstance()
                .trackError(SCREEN_NAME, e.message.toString(), "daysleft", e.stackTraceToString())
            Log.e(TAG, e.message.toString())
            ""
        }

        // Load thumbnail
        imagePresenter = ImagePresenter(context, this, inf.root)
        imagePresenter.getEventImage()
        imagePresenter.getPlaceImage()

//        inf.weddinglocation2.root.setOnClickListener {
//            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Visit_GoogleMaps")
//
//            val gmmIntentUri =
//                Uri.parse("geo:${event.latitude},${event.longitude}?z=10&q=${event.location}")
//            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//            mapIntent.setPackage("com.google.android.apps.maps")
//            mapIntent.resolveActivity(context.packageManager)?.let {
//                startActivity(mapIntent)
//            }
//            val loadingscreen = requireActivity().findViewById<ConstraintLayout>(R.id.loadingscreen)
//            val drawerlayout = requireActivity().findViewById<DrawerLayout>(R.id.drawerlayout)
//            loadingscreen.visibility = ConstraintLayout.GONE
//            drawerlayout.visibility = ConstraintLayout.VISIBLE
//        }
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
//        inf.weddinglocation2.placesimage.setImageBitmap(image)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onEmptyPlaceImageSD() {
        if (!PermissionUtils.checkPermissions(requireActivity().applicationContext, "storage")) {
            val permissions = PermissionUtils.requestPermissionsList("storage")
            requestPermissions(permissions, PERMISSION_CODE)
        } else {
            //permission already granted
            lifecycleScope.launch {
                val bitmap = getImgfromPlaces(
                    requireContext(), // context
                    placeid,
                    resources.getString(R.string.google_maps_key),
                    ImagePresenter.PLACEIMAGE
                )

                // Handle the loaded bitmap here
                if (bitmap != null) {
                    // Load bitmap into ImageView
//                    inf.weddinglocation2.placesimage.setImageBitmap(bitmap)
                } else {
                    // Handle case when bitmap is null (e.g., show default image)
//                    inf.weddinglocation2.placesimage.setImageResource(R.drawable.avatar2)
                }
            }
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
            }, 500) //1 seconds
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
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
                    lifecycleScope.launch {
                        val bitmap = getImgfromPlaces(
                            requireContext(), // context
                            placeid,
                            resources.getString(R.string.google_maps_key),
                            ImagePresenter.PLACEIMAGE
                        )

                        // Handle the loaded bitmap here
                        if (bitmap != null) {
                            // Load bitmap into ImageView
//                            inf.weddinglocation2.placesimage.setImageBitmap(bitmap)
                        } else {
                            // Handle case when bitmap is null (e.g., show default image)
//                            inf.weddinglocation2.placesimage.setImageResource(R.drawable.avatar2)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val SUCCESS_RETURN = 1
        internal const val PERMISSION_CODE = 1001
        const val SCREEN_NAME = "Dashboard_Event"
        const val TAG = "DashboardEvent"
    }

    override fun onCategories(list: List<Category>?) {
        if (list != null) {
            if (list.isNotEmpty()) {
                //Creating the recyclerview to show the Categories, 2 columns

                val rvAdapter = CategoryAdapter(this, list, requireContext())
                inf.taskcategoryrv.adapter = rvAdapter
            }
        }
    }

    override fun onCategoriesError(errcode: String) {
        inf.dashboardVerticalLayout5.visibility = View.GONE
    }

    override fun onCategoryFragmentWithData(category: String) {
        val fragment = TaskPaymentList()
        val bundle = Bundle()
        bundle.putString("category", category)
        fragment.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
            ?.replace(
                R.id.fragment_container,
                fragment
            )
            ?.commit()
    }

    override fun onTaskNextList(taskList: ArrayList<String>?) {
        if (taskList != null) {
            if (taskList.isNotEmpty()) {
                inf.tasksduenextLayout.root.visibility = View.VISIBLE
                val numberOfTasks = minOf(taskList.size, 3)
                for (i in 1..numberOfTasks) {
                    val taskName = "taskname$i"
                    val taskViewField =
                        inf.tasksduenextLayout::class.java.getDeclaredField(taskName).apply {
                            isAccessible = true
                        }
                    val taskView = taskViewField.get(inf.tasksduenextLayout) as TextView
                    taskView.visibility = View.VISIBLE
                    taskView.text = taskList[i - 1]
                }
            }
        }
    }

    override fun onTaskNextListError(errcode: String) {
    }

    override fun onGalleryFragmentWithData(category: String) {
        val fragment = FragmentGallery()
        val bundle = Bundle()
        bundle.putString("category", category)
        fragment.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
            ?.replace(
                R.id.fragment_container,
                fragment
            )
            ?.commit()
    }


}

interface GalleryFragmentActionListener {
    fun onGalleryFragmentWithData(category: String)
}



