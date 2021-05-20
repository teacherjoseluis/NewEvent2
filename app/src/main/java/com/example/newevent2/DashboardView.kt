package com.example.newevent2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.newevent2.Functions.Loginfo
import com.example.newevent2.MVP.BlogPresenter
import com.example.newevent2.MVP.LogPresenter
import com.example.newevent2.MVP.PaymentPresenter
import com.example.newevent2.MVP.TaskPresenter
import com.example.newevent2.Model.User
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.dashboardcharts.*
import kotlinx.android.synthetic.main.welcome.*
import kotlinx.android.synthetic.main.welcome.duenextdate
import kotlinx.android.synthetic.main.welcome.duenexttask
import kotlinx.android.synthetic.main.welcome0.*
import java.text.DecimalFormat

class DashboardView : AppCompatActivity() {

    private var usersession = User()
    lateinit var drawerLayout: DrawerLayout
//    private lateinit var presentertask: TaskPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_navigation)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_google)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = "Event Detail"

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
                        Intent(this, MainEventView::class.java)
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
    }

    override fun onStart() {
        super.onStart()
        usersession = com.example.newevent2.Functions.getUserSession(this)
        if (usersession.key == "") {
            val loginactivity =
                Intent(this, LoginView::class.java)
            startActivity(loginactivity)
        }

        //--------------------------------------------------------------------------------------------------
        val tablayout = findViewById<TabLayout>(R.id.dashboard_tabLayout)
        val viewPager = findViewById<View>(R.id.dashboardpager) as ViewPager
        if (viewPager != null) {
            val adapter =
                Dashboard_PagerAdapter(
                    usersession.key,
                    usersession.eventid,
                    supportFragmentManager,
                    tablayout.tabCount
                )
            viewPager.adapter = adapter
            viewPager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(
                    tablayout
                )
            )
        }

        tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                viewPager.currentItem = p0!!.position
//                if (p0.position == 0) {
//                    findViewById<FloatingActionButton>(R.id.NewTaskPaymentActionButton).isVisible =
//                        true
//                } else if (p0.position == 1) {
//                    findViewById<FloatingActionButton>(R.id.NewTaskPaymentActionButton).isVisible =
//                        false
//
//                    findViewById<ConstraintLayout>(R.id.TaskLayout).isVisible = false
//                    findViewById<ConstraintLayout>(R.id.PaymentLayout).isVisible = false
//                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }
        })
//--------------------------------------------------------------------------------------------------
        //presentertask = TaskPresenter(this, usersession.key, usersession.eventid)
        //presentertask.getDueNextTask()
//--------------------------------------------------------------------------------------------------
//        usershortname0.text = usersession.shortname
//--------------------------------------------------------------------------------------------------
//        progress0.text = "Your profile has a ${getProfileprogress()}% progress"
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun getProfileprogress(): Int {
        //to be used to show the profile completion progress
        var profileprogress = 0
        if (usersession.hasevent == "Y") profileprogress += 20
        if (usersession.hastask == "Y") profileprogress += 20
        if (usersession.haspayment == "Y") profileprogress += 20
        if (usersession.hasguest == "Y") profileprogress += 20
        if (usersession.hasvendor == "Y") profileprogress += 20
        return profileprogress
    }

}