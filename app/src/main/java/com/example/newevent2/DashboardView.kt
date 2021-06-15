package com.example.newevent2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.example.newevent2.Model.User
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout

class DashboardView : AppCompatActivity() {

    private var usersession = User()
    lateinit var drawerLayout: DrawerLayout
//    private lateinit var presentertask: TaskPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_navigation)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

//        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomnavigation)
//        bottomNavigation.setOnNavigationItemSelectedListener {
//            when (it.itemId) {
//                R.id.home -> {
//                    toolbar.title = "Home"
//                    val songsFragment = SongsFragment.newInstance()
//                    openFragment(songsFragment)
//                    return@OnNavigationItemSelectedListener true
//                }
//                R.id.events -> {
//                }
//                R.id.tasks -> {
//                }
//                R.id.contacts -> {
//                }
//                R.id.notes -> {
//                }
//            }
//        }

        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_google)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = "Event Detail"

        drawerLayout = findViewById(R.id.drawerlayout)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.nav_app_bar_open_drawer_description,
            R.string.nav_app_bar_navigate_up_description
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

//        val navView = findViewById<NavigationView>(R.id.navbottom)
//        navView.setNavigationItemSelectedListener { p0 ->
//            when (p0.itemId) {
//                R.id.home_fragment -> Toast.makeText(
//                    applicationContext,
//                    "Seccion 1",
//                    Toast.LENGTH_SHORT
//                ).show()
//                R.id.event_fragment -> {
//                    val events =
//                        Intent(this, MainEventView::class.java)
//                    Log.d("Activity Starts", "EventDetail")
//                    events.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(events)
//                }
//                R.id.task_fragment -> {
//                    val calendar =
//                        Intent(this, MyCalendar::class.java)
//                    Log.d("Activity Starts", "Calendar")
//                    calendar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(calendar)
//                }
//                R.id.contact_fragment -> {
//                    val contacts =
//                        Intent(this, MyContacts::class.java)
//                    Log.d("Activity Starts", "Contacts")
//                    contacts.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(contacts)
//                }
//                R.id.notes_fragment -> {
//                    val notes =
//                        Intent(this, MyNotes::class.java)
//                    Log.d("Activity Starts", "Notes")
//                    notes.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(notes)
//                }
//                R.id.settings_fragment -> {
//                    val settings =
//                        Intent(this, Settings::class.java)
//                    Log.d("Activity Starts", "Settings")
//                    settings.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(settings)
//                }
//            }
//            drawerLayout.closeDrawers()
//            true
//        }
//        navView.menu.getItem(0).isChecked = true
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
                DashboardPagerAdapter(
                    usersession.key,
                    usersession.eventid,
                    usersession.language,
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