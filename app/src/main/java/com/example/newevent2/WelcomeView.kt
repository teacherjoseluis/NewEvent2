package com.example.newevent2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newevent2.MVP.BlogPresenter
import com.example.newevent2.MVP.LogPresenter
import com.example.newevent2.MVP.PaymentPresenter
import com.example.newevent2.MVP.TaskPresenter
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.welcome.*
import java.text.DecimalFormat

class WelcomeView : AppCompatActivity(), LogPresenter.ViewLogActivity,
    TaskPresenter.ViewTaskWelcomeActivity, PaymentPresenter.ViewPaymentWelcomeActivity,
    BlogPresenter.ViewBlogActivity {

    private lateinit var charttask: PieChart
    private lateinit var chartpayment: PieChart

    private var userSession = User()

    private var tfRegular: Typeface? = null
    private var tfLight: Typeface? = null

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

    lateinit var drawerLayout: DrawerLayout

    override fun onStart() {
        super.onStart()

        val sharedPreference =
            getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE).edit().clear().commit()

        //--------------------------------------------
        // need to check if the user is logged and then continue, otherwise I redirect to login
        val usersessionlist = getUserSession(this)
        if (usersessionlist[0] == "") {
            val loginactivity =
                Intent(this, LoginView::class.java)
            startActivity(loginactivity)
            //finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_navigation)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_google)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        drawerLayout = findViewById(R.id.drawer_layout)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.nav_app_bar_open_drawer_description,
            R.string.nav_app_bar_navigate_up_description
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navView = findViewById<NavigationView>(R.id.navview)


        navView.setNavigationItemSelectedListener { p0 ->
            when (p0.itemId) {
                R.id.menu_seccion_1 -> Toast.makeText(
                    applicationContext,
                    "Seccion 1",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.menu_seccion_2 -> {
                    val events =
                        Intent(this, EventDetail::class.java)
                    Log.d("Activity Starts", "EventDetail")
                    events.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(events)
                }
                R.id.menu_seccion_3 -> {
                    val calendar =
                        Intent(this, MyCalendar::class.java)
                    Log.d("Activity Starts", "Calendar")
                    calendar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(calendar)
                }
                R.id.menu_seccion_4 -> {
                    val contacts =
                        Intent(this, MyContacts::class.java)
                    Log.d("Activity Starts", "Contacts")
                    contacts.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(contacts)
                }
                R.id.menu_seccion_5 -> {
                    val notes =
                        Intent(this, MyNotes::class.java)
                    Log.d("Activity Starts", "Notes")
                    notes.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(notes)
                }
                R.id.menu_opcion_1 -> {
                    val settings =
                        Intent(this, Settings::class.java)
                    Log.d("Activity Starts", "Settings")
                    settings.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(settings)
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        navView.menu.getItem(0).isChecked = true

        //---------------------------------------------------------------------------------------------------------------------

        // I think this needs to be validated. In case the Activity is not accessed via Login or Onboarding. The User information needs to be taken
        // from the User Session
//            val intent = intent
//            userSession = intent.getParcelableExtra("usersession")!!
//
//            usershortname.text = userSession!!.shortname
        usershortname.text = "Jose"
//            progress.text = "Your profile has a ${getProfileprogress()}% progress"
//-----------------------------------------------------------------------------------------------------
        sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        // Pie charts
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //---------------------------------------------------------------------------------------------------------------
        val recyclerViewActivity = recentactivityrv

        recentactivityrv.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

        getLog(this, object : FirebaseSuccessListenerLogWelcome {
            override fun onLogList(list: ArrayList<Loginfo>) {
                val rvAdapter = Rv_LogAdapter(list)
                recentactivityrv.adapter = rvAdapter
            }

        })

        //---------------------------------------------------------------------------------------------------------------
        val recyclerViewBlog = blogrv

        blogrv.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false).apply {
                    stackFromEnd = true
                    reverseLayout = true
                }
        }

        getBlog(object : FirebaseSuccessListenerBlogWelcome {
            override fun onBlogList(list: ArrayList<Blog>) {
                val rvAdapter = Rv_BlogAdapter(list)
                blogrv.adapter = rvAdapter
            }
        })

        //---------------------------------------------------------------------------------------------------------------
        tfRegular = ResourcesCompat.getFont(this.applicationContext, R.font.robotoregular)
        //Typeface.createFromAsset(assets, "fonts/robotoregular.ttf")
        tfLight = ResourcesCompat.getFont(this.applicationContext, R.font.robotolight)
        //Typeface.createFromAsset(assets, "fonts/robotolight.ttf")

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
//--------------------------------------------------------------------------------------------------
        val taskduenext = TaskEntity()
//        taskduenext.eventid =
//            "-MLy-LKwd8RnRb-Bwesn" //HARDCODE********************************************************

        taskduenext.getDueNextTask(this, object : FirebaseSuccessListenerTaskWelcome {
            override fun onTask(task: Task) {
                duenextdate.text = task.date
                duenexttask.text = task.name
            }
        })
////--------------------------------------------------------------------------------------------------
//        val taskcreated = TaskEntity()
////        taskcreated.eventid =
////            "-MLy-LKwd8RnRb-Bwesn" //HARDCODE********************************************************
//
//        taskcreated.getRecentCreatedTask(this, object : FirebaseSuccessListenerTaskWelcome {
//            override fun onTask(task: Task) {
//                recenttaskdate.text = task.createdatetime
//                recentcreatedtask.text = task.name
//            }
//        })
////--------------------------------------------------------------------------------------------------
//        val paymentcreated = PaymentEntity()
////        paymentcreated.eventid =
////            "-MLy-LKwd8RnRb-Bwesn" //HARDCODE********************************************************
//
//        paymentcreated.getRecentCreatedPayment(
//            this,
//            object : FirebaseSuccessListenerPaymentWelcome {
//                override fun onPayment(payment: Payment) {
//                    recentpaymentdate.text = payment.createdatetime
//                    recentcreatedpayment.text = payment.name
//                }
//            })
////--------------------------------------------------------------------------------------------------

    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    private fun setData() {
        val taskentries = ArrayList<PieEntry>()
        val taskentity = TaskEntity()
//        taskentity.eventid =
//            "-MLy-LKwd8RnRb-Bwesn" //HARDCODE********************************************************

        taskentity.getTasksEvent(this, object : FirebaseSuccessListenerTask {
            override fun onTasksEvent(taskpending: Int, taskcompleted: Int, sumbudget: Float) {
                taskentries.add(PieEntry(taskpending.toFloat(), " To Do "))
                taskentries.add(PieEntry(taskcompleted.toFloat(), " Done "))

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
//        paymententity.eventid =
//            "-MLy-LKwd8RnRb-Bwesn" //HARDCODE********************************************************

        paymententity.getPaymentEvent(this, object : FirebaseSuccessListenerPayment {
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

    private fun getProfileprogress(): Int {
        var profileprogress = 0
        if (userSession.hasevent == "Y") profileprogress += 20
        if (userSession.hastask == "Y") profileprogress += 20
        if (userSession.haspayment == "Y") profileprogress += 20
        if (userSession.hasguest == "Y") profileprogress += 20
        if (userSession.hasvendor == "Y") profileprogress += 20
        return profileprogress
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

    override fun onViewLogSuccess(loglist: ArrayList<com.example.newevent2.Functions.Loginfo>) {
        TODO("Not yet implemented")
    }

    override fun onViewLogError(errcode: String) {
        TODO("Not yet implemented")
    }

    override fun onViewTaskStatsSuccess(taskpending: Int, taskcompleted: Int, sumbudget: Float) {
        TODO("Not yet implemented")
    }

    override fun onViewNextTaskSuccess(task: com.example.newevent2.Model.Task) {
        TODO("Not yet implemented")
    }

    override fun onViewTaskError(errcode: String) {
        TODO("Not yet implemented")
    }

    override fun onViewPaymentStatsSuccess(countpayment: Int, sumpayment: Float, sumbudget: Float) {
        TODO("Not yet implemented")
    }

    override fun onViewPaymentError(errcode: String) {
        TODO("Not yet implemented")
    }

    override fun onViewBlogSuccess(bloglist: ArrayList<com.example.newevent2.Functions.Blog>) {
        TODO("Not yet implemented")
    }

    override fun onViewBlogError(errcode: String) {
        TODO("Not yet implemented")
    }
}