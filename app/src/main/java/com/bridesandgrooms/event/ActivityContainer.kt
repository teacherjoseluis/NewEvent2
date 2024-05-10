package com.bridesandgrooms.event

import Application.AnalyticsManager
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton
import com.bridesandgrooms.event.Functions.getUserSession
import com.bridesandgrooms.event.Functions.isEventDate
import com.bridesandgrooms.event.Model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.nordan.dialog.Animation
import com.nordan.dialog.NordanAlertDialog
//import kotlinx.android.synthetic.main.header_navview.*
//import kotlinx.android.synthetic.main.header_navview.view.*

import sendEmail

class ActivityContainer : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var newfragment: Fragment

    private val fm = supportFragmentManager
    private var usersession = User()
    private var clickNavItem = 0

    private val TIME_DELAY = 2000
    private val LOGINACTIVITY = 123
    private var back_pressed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        if (loginValidation()) {
            val loginactivity =
                Intent(this, LoginView::class.java)
            startActivityForResult(loginactivity, LOGINACTIVITY)
        } else {
            createView()
        }
    }

//    override fun onResume() {
//        super.onResume()
//        val serviceIntent = Intent(this, BG_BackgroundService::class.java)
//        try {
//            startService(serviceIntent)
//        } catch (e: Exception) {
//            Log.e(TAG, e.message.toString())
//        }
//    }

    private fun loginValidation(): Boolean {
        //Evaluate if given the amount passed since the last login, the user should re-login to the app
        //Last Signed In date
        val lastSignedInAt = try {
            getUserSession(this@ActivityContainer, "last_signed_in_at") as Long
        } catch (e: Exception) {
            AnalyticsManager.getInstance().trackError(
                SCREEN_NAME,
                e.message.toString(),
                "getUserSession",
                e.stackTraceToString()
            )
            0L
        }
        // Email Id???
        val emailid = try {
            getUserSession(this@ActivityContainer, "event_id") as String
        } catch (e: Exception) {
            AnalyticsManager.getInstance().trackError(
                SCREEN_NAME,
                e.message.toString(),
                "getUserSession",
                e.stackTraceToString()
            )
            ""
        }

        val currentTimeMillis = System.currentTimeMillis()
        val oneWeekInMillis = 604800000L
        //val oneWeekInMillis = 3600000L

        //168 hrs
        // This will validate if the user should re-login
        return (currentTimeMillis - lastSignedInAt >= oneWeekInMillis || lastSignedInAt == 0L || emailid == "")
    }

    private fun createView() {
        setContentView(R.layout.activitycontainer)

        // UI Elements Declaration/Startup
        // Declare the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = getString(R.string.myeventtitle)
        drawerLayout = findViewById(R.id.drawerlayout)
        //val loadingscreen = findViewById<ConstraintLayout>(R.id.loadingscreen)
        val sidenavView = findViewById<NavigationView>(R.id.sidenav)
        val headerView = sidenavView.getHeaderView(0)
        val navView = findViewById<BottomNavigationView>(R.id.bottomnav)
        val androidVersion = headerView.findViewById<TextView>(R.id.androidVersionNumber)
        val androidCode = headerView.findViewById<TextView>(R.id.androidVersionCode)
        val headershortname =
            sidenavView.getHeaderView(0).findViewById<TextView>(R.id.headershortname)
        headershortname.text = usersession.shortname

        if (isEventDate(this) == 0) {
            NordanAlertDialog.Builder(this)
                .setAnimation(Animation.SLIDE)
                .isCancellable(false)
                .setTitle(getString(R.string.congratulations))
                .setMessage(getString(R.string.weddingday))
                .setIcon(R.drawable.love_animated_gif_2018_8, true)
                .setPositiveBtnText(getString(R.string.great))
                .build().show()
        }

        // Populating Android Version and Code
        try {
            val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
            androidVersion.text = packageInfo.versionName
            androidCode.text = packageInfo.versionCode.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            AnalyticsManager.getInstance().trackError(
                SCREEN_NAME,
                e.message.toString(),
                "getPackageInfo",
                e.stackTraceToString()
            )
            Log.e(TAG, e.message.toString())
            androidVersion.text = "0.0"
            androidCode.text = "0.0"
        }

        // Validating if needed and if so, enabling Developer contact
        val developer_mail = RemoteConfigSingleton.get_developer_mail()
        if (!developer_mail) {
            val contactMenuItem = sidenavView.getMenu().findItem(R.id.contact_fragment);
            contactMenuItem.setVisible(false)
        }

        // Declaration of the sidebar and the different calls it makes
        // In this implementation, the classes are assigned but fragments are not yet created
        sidenavView.setNavigationItemSelectedListener { p0 ->
            when (p0.itemId) {
                R.id.event_fragment -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "SideNavigationBar_Home")
                    clickNavItem = R.id.event_fragment
                    newfragment = DashboardView_clone()
                }

                R.id.task_fragment -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "SideNavigationBar_Task")
                    clickNavItem = R.id.task_fragment
                    newfragment = DashboardActivity()
                }

                R.id.notes_fragment -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "SideNavigationBar_Notes")
                    clickNavItem = R.id.notes_fragment
                    newfragment = MyNotes()
                }

                R.id.settings_fragment -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "SideNavigationBar_Settings")
                    clickNavItem = R.id.settings_fragment
                    newfragment = Settings()
                }

                R.id.contact_fragment -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "SideNavigationBar_Contact")
                    clickNavItem = R.id.contact_fragment
                    sendEmail(this)
                }

                R.id.account_logoff -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "SideNavigationBar_LogOff")
                    logoffapp()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
        sidenavView.menu.getItem(0).isChecked = true

        //Creation of the navigation bar and the different calls it makes.
        //In this section, the fragments are indeed invoked and called
        navView.setOnNavigationItemSelectedListener { p0 ->
            when (p0.itemId) {
                R.id.home -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "BottomNavigation_Home")
                    val newfragment = DashboardView_clone()
                    fm.beginTransaction()
                        .replace(R.id.fragment_container, newfragment)
                        .commit()
                }

                R.id.events -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "BottomNavigation_EventCategories")
                    val newfragment = EventCategories()
                    fm.beginTransaction()
                        .replace(R.id.fragment_container, newfragment)
                        .commit()
                }

                R.id.tasks -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "BottomNavigation_Tasks")
                    val newfragment = DashboardActivity()
                    fm.beginTransaction()
                        .replace(R.id.fragment_container, newfragment)
                        .commit()
                }

                R.id.guests -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "BottomNavigation_Guests")
                    val newfragment = TableGuestsActivity()
                    fm.beginTransaction()
                        .replace(R.id.fragment_container, newfragment)
                        .commit()
                }

                R.id.vendor -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "BottomNavigation_Vendors")
                    val newfragment = VendorsAll()
                    fm.beginTransaction()
                        .replace(R.id.fragment_container, newfragment)
                        .commit()
                }
            }
            true
        }
        navView.menu.getItem(0).isChecked = true

        // Create the toggle that will open and close the sidebar
        drawerLayout = findViewById(R.id.drawerlayout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            0,
            0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                when (clickNavItem) {
                    R.id.event_fragment -> {
                        fm.beginTransaction()
                            .replace(R.id.fragment_container, newfragment!!)
                            .commit()
                    }

                    R.id.task_fragment -> {
                        fm.beginTransaction()
                            .replace(R.id.fragment_container, newfragment!!)
                            .commit()
                    }

                    R.id.notes_fragment -> {
                        fm.beginTransaction()
                            .replace(R.id.fragment_container, newfragment!!)
                            .commit()
                    }

                    R.id.settings_fragment -> {
                        fm.beginTransaction()
                            .replace(R.id.fragment_container, newfragment!!)
                            .commit()
                    }
                }
            }

            override fun onDrawerStateChanged(newState: Int) {}
        })

        var activefragment = fm.findFragmentById(R.id.fragment_container)
        if (activefragment == null) {
            activefragment = DashboardView_clone()
            fm.beginTransaction()
                .add(R.id.fragment_container, activefragment, "DashboardView")
                .commit()
        }
    }

    // This catches the back option in Android, if the sidebar is displayed, then it gets closed
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            var fragment =
                fm.findFragmentById(R.id.fragment_container)
            val currentFragment =
                fragment?.parentFragmentManager?.fragments?.get(1)
            when (currentFragment) {
                is DashboardView_clone -> {
                    if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
                        super.onBackPressed()
                    } else {
                        Toast.makeText(
                            baseContext, getString(R.string.pressexit),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    back_pressed = System.currentTimeMillis()
                }

                is VendorCreateEdit -> {
                    fragment = VendorsAll()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                }

                else -> {
                    fragment = DashboardView_clone()
                    fm.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                }
            }
//            val fragmentstring = currentFragment?.javaClass.toString()
//
//            if (fragmentstring.contains("DashboardView_clone") || fragmentstring == "null") {
//                if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
//                    super.onBackPressed()
//                } else {
//                    Toast.makeText(
//                        baseContext, getString(R.string.pressexit),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//                back_pressed = System.currentTimeMillis()
//            } else {
//                val newfragment = DashboardView_clone()
//                fm.beginTransaction()
//                    .replace(R.id.fragment_container, newfragment)
//                    .commit()
//            }
        }
    }

    // This function is invoked when the user decides to logoff
    private fun logoffapp() {
        // Dialog
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.logoff))
        builder.setMessage(getString(R.string.logoff_question))

        builder.setPositiveButton(
            getString(R.string.accept)
        ) { _, _ ->
            when (usersession.authtype) {
                //Logoff in case the user was logged in with Google
                "google" -> {
                    val gso =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                    val googleSignInClient = GoogleSignIn.getClient(this@ActivityContainer, gso)
                    googleSignInClient.signOut()
                }
            }
            //Logoff from Firebase
            lifecycleScope.launchWhenResumed {
                usersession.logout(this@ActivityContainer)
            }
            finish()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { p0, _ -> p0!!.dismiss() }

        val dialog = builder.create()
        dialog.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOGINACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                //
            } else if (resultCode == Activity.RESULT_CANCELED) {
                super.onBackPressed()
            }
        }
    }

    companion object {
        const val TAG = "ActivityContainer"
        const val SCREEN_NAME = "Activity_Container"
    }
}

interface IOnBackPressed {
    fun onBackPressed(): Boolean
}