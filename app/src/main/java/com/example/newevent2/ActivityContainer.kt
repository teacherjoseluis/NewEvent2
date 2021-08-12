package com.example.newevent2

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.newevent2.Model.User
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.header_navview.*
import kotlinx.android.synthetic.main.header_navview.view.*


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
        val sidenavView = findViewById<NavigationView>(R.id.sidenav)

        usersession = com.example.newevent2.Functions.getUserSession(this)
        if (usersession.key == "") {
            val loginactivity =
                Intent(this, LoginView::class.java)
            startActivity(loginactivity)
        } else {
            val headershortname = sidenavView.getHeaderView(0).findViewById<TextView>(R.id.headershortname)
            headershortname.setText(usersession.shortname)
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

        sidenavView.setNavigationItemSelectedListener { p0 ->
            when (p0.itemId) {
                R.id.event_fragment -> {
                    clickNavItem = R.id.event_fragment
                    newfragment = MainEventView_clone(usersession)
                }
                R.id.task_fragment -> {
                    clickNavItem = R.id.task_fragment
                    newfragment = EventCategories()
                }
                R.id.guest_fragment -> {
                    clickNavItem = R.id.guest_fragment
                    newfragment = GuestsAll()
                }
                R.id.vendor_fragment -> {
                    clickNavItem = R.id.guest_fragment
                    newfragment = VendorsAll()
                }
                R.id.settings_fragment -> {
                    clickNavItem = R.id.guest_fragment
                    newfragment = Settings()
                }
                R.id.account_logoff -> {
                    logoffapp()
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
                    R.id.event_fragment -> {
                        fm.beginTransaction()
                            .replace(R.id.fragment_container, newfragment)
                            .commit()
                    }
                    R.id.task_fragment -> {
                        fm.beginTransaction()
                            .replace(R.id.fragment_container, newfragment)
                            .commit()
                    }
                    R.id.guest_fragment -> {
                        fm.beginTransaction()
                            .replace(R.id.fragment_container, newfragment)
                            .commit()
                    }
                    R.id.vendor_fragment -> {
                        fm.beginTransaction()
                            .replace(R.id.fragment_container, newfragment)
                            .commit()
                    }
                    R.id.settings_fragment -> {
                        fm.beginTransaction()
                            .replace(R.id.fragment_container, newfragment)
                            .commit()
                    }
//                    R.id.account_logoff -> {
//                        fm.beginTransaction()
//                            .replace(R.id.fragment_container, newfragment)
//                            .commit()
//                    }
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

    private fun logoffapp() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Log off")
        builder.setMessage("Do you want to log off?")

        builder.setPositiveButton("Accept", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                when (usersession.authtype) {
                    "google" -> {
                        val gso =
                            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                        val googleSignInClient = GoogleSignIn.getClient(this@ActivityContainer, gso)
                        googleSignInClient.signOut()
                    }
                    "facebook" -> {
                        LoginManager.getInstance().logOut()
                    }
                }
                usersession.logout(this@ActivityContainer)
                usersession.deleteUserSession(this@ActivityContainer)
                finish()
            }
        })
        builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                p0!!.dismiss()
            }
        })

        val dialog = builder.create()
        dialog.show()
    }
}