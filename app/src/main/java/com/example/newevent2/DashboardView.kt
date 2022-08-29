package com.example.newevent2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.example.newevent2.Functions.getUserSession
import com.example.newevent2.Functions.userdbhelper
import com.example.newevent2.Model.User
import com.example.newevent2.Model.UserDBHelper
import com.google.android.material.tabs.TabLayout

class DashboardView : AppCompatActivity() {

    private var usersession = User()
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_navigation)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_google)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = getString(R.string.eventdetails)

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
    }

    override fun onStart() {
        super.onStart()
        userdbhelper = UserDBHelper(this)
        usersession = userdbhelper.getUser(userdbhelper.getUserKey())
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
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }
        })
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}