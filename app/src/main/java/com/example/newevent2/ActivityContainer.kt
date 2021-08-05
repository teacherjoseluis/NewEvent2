package com.example.newevent2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.Model.User
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class ActivityContainer : AppCompatActivity() {

    private var usersession = User()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var loadingscreen: ConstraintLayout

    private val fm = supportFragmentManager
    private var clickNavItem = 0
    private lateinit var newfragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activitycontainer)

        drawerLayout = findViewById(R.id.drawerlayout)
        loadingscreen = findViewById(R.id.loadingscreen)

        usersession = com.example.newevent2.Functions.getUserSession(this)
        if (usersession.key == "") {
            val loginactivity =
                Intent(this, LoginView::class.java)
            startActivity(loginactivity)
        }

        var activefragment = fm.findFragmentById(R.id.fragment_container)

        if (activefragment == null) {
            activefragment = DashboardView_clone(usersession)
            fm.beginTransaction()
                .add(R.id.fragment_container, activefragment, "DashboardView")
                .commit()
        }
//--------------------------------------------------------------------------------------------------
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        //supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_google)
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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

        val sidenavView = findViewById<NavigationView>(R.id.sidenav)
        sidenavView.setNavigationItemSelectedListener { p0 ->
            when (p0.itemId) {
                R.id.home_fragment -> {
                    clickNavItem = R.id.home_fragment
                    newfragment = DashboardView_clone(usersession)
                }
                R.id.event_fragment -> {
                    clickNavItem = R.id.event_fragment
                    newfragment = MainEventView_clone(usersession)
                }
                R.id.task_fragment -> {
                }
                R.id.activetasks -> {
                    clickNavItem = R.id.activetasks
                    newfragment = TaskPaymentTasks()
                }
                R.id.completedtasks -> {
                    clickNavItem = R.id.completedtasks
                    newfragment = TaskPaymentTasks()
                }
                R.id.contact_fragment -> {
                }
                R.id.notes_fragment -> {
                }
                R.id.settings_fragment -> {
                }
            }
            drawerLayout.closeDrawers()
            true
        }
        sidenavView.menu.getItem(0).isChecked = true
        //--------------------------------------------------------------------------------------------------
        val navView = findViewById<BottomNavigationView>(R.id.bottomnav)
        navView.setOnNavigationItemSelectedListener { p0 ->
            when (p0.itemId) {
                R.id.home -> {
                    val newfragment = DashboardView_clone(usersession)
                    fm.beginTransaction()
                        .replace(R.id.fragment_container, newfragment)
                        // .addToBackStack(null)
                        .commit()
                }
                R.id.events -> {
                    val newfragment = MainEventView_clone(usersession)
                    fm.beginTransaction()
                        .replace(R.id.fragment_container, newfragment)

                        //.addToBackStack(null)
                        .commit()
                }
                R.id.tasks -> {
                    val newfragment = DashboardActivity()
                    fm.beginTransaction()
                        .replace(R.id.fragment_container, newfragment)

                        //.addToBackStack(null)
                        .commit()
                }
                R.id.guests -> {
                    val newfragment = TableGuestsActivity()
                    fm.beginTransaction()
                        .replace(R.id.fragment_container, newfragment)

                        //.addToBackStack(null)
                        .commit()
                }
                R.id.notes -> {
                    val newfragment = MyNotes()
                    fm.beginTransaction()
                        .replace(R.id.fragment_container, newfragment)

                        //.addToBackStack(null)
                        .commit()
                }
            }
            true
        }
        navView.menu.getItem(0).isChecked = true

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerClosed(drawerView: View) {
                when (clickNavItem) {
                    R.id.home_fragment -> {
                        fm.beginTransaction()
                            .replace(R.id.fragment_container, newfragment)
                            .commit()
                    }
                    R.id.event_fragment -> {
                        fm.beginTransaction()
                            .replace(R.id.fragment_container, newfragment)
                            .commit()
                    }
                    R.id.activetasks -> {
                        val bundle = Bundle()
                        bundle.putString("status", TaskModel.ACTIVESTATUS)
                        newfragment.arguments = bundle
                        fm.beginTransaction()
                            .replace(R.id.fragment_container, newfragment)
                            .commit()
                    }
                    R.id.completedtasks -> {
                        val bundle = Bundle()
                        bundle.putString("status", TaskModel.COMPLETESTATUS)
                        newfragment.arguments = bundle
                        fm.beginTransaction()
                            .replace(R.id.fragment_container, newfragment)
                            .commit()
                    }
                }
            }

            override fun onDrawerStateChanged(newState: Int) {
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