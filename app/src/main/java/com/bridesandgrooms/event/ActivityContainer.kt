package com.bridesandgrooms.event

import Application.AnalyticsManager
import Application.UserRetrievalException
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
import com.bridesandgrooms.event.Functions.UserSessionHelper
import com.bridesandgrooms.event.Model.EventModel
import com.bridesandgrooms.event.Model.GuestDBHelper
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.UI.Fragments.ContactsAll
import com.bridesandgrooms.event.UI.Fragments.DashboardActivity
import com.bridesandgrooms.event.UI.Fragments.DashboardView
import com.bridesandgrooms.event.UI.Fragments.EventCategories
import com.bridesandgrooms.event.UI.Fragments.GuestCreateEdit
import com.bridesandgrooms.event.UI.Fragments.GuestsAll
import com.bridesandgrooms.event.UI.Fragments.MyNotes
import com.bridesandgrooms.event.UI.Fragments.NoteCreateEdit
import com.bridesandgrooms.event.UI.Fragments.PaymentCreateEdit
import com.bridesandgrooms.event.UI.Fragments.PaymentsAllCalendar
import com.bridesandgrooms.event.UI.Fragments.SearchVendorFragment
import com.bridesandgrooms.event.UI.Fragments.SearchVendorTab
import com.bridesandgrooms.event.UI.Fragments.Settings
import com.bridesandgrooms.event.UI.Fragments.TaskCreateEdit
import com.bridesandgrooms.event.UI.Fragments.TaskPaymentPayments
import com.bridesandgrooms.event.UI.Fragments.TaskPaymentTasks
import com.bridesandgrooms.event.UI.Fragments.TasksAllCalendar
import com.bridesandgrooms.event.UI.Fragments.VendorCreateEdit
import com.bridesandgrooms.event.UI.Fragments.VendorsAll
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
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

    //lateinit private var mp: MixpanelAPI

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        val trackAutomaticEvents = false;
        // Replace with your Project Token
        //mp = MixpanelAPI.getInstance(this, "b7f46757e0da5b27a7ad6cc07d3eafb9", trackAutomaticEvents);

        if (loginValidation()) {
            val loginactivity =
                Intent(this, LoginView::class.java)
            startActivityForResult(loginactivity, LOGINACTIVITY)
        } else {
            //Potentially it's here where I can add the Welcome flow. For those users with status =0
            lifecycleScope.launch {
                try {
                    val userSession = User.getUserAsync()
                    if (userSession.status == "0") {
                        setContentView(R.layout.welcome_user)
                        val welcomeActivity = Intent(this@ActivityContainer, WelcomeActivity::class.java)
                        startActivity(welcomeActivity)
                        //finish()
                    } else {
                        val eventModel = EventModel()
                        val needsRefresh = eventModel.checkNeedsRefresh()
                        val database = FirebaseDatabase.getInstance()
                        val eventRef = database.getReference("User")
                            .child(userSession.userid!!)
                            .child("Event")
                            .child(userSession.eventid)
                            .child("eventNeedsRefresh")

                        // Call createView() immediately to avoid UI delays
                        createView()

                        if (needsRefresh) {
                            val guestDB = GuestDBHelper()

                            // Run refresh operations in a separate coroutine (Non-blocking)
                            lifecycleScope.launch {
                                try {
                                    guestDB.firebaseImport() // Runs in the background

                                    // Reset eventNeedsRefresh flag in Firebase after the background refresh
                                    eventRef.setValue(false)
                                } catch (e: Exception) {
                                    displayErrorMsg("Error refreshing guest data: ${e.message}")
                                }
                            }
                        }
                    }
                } catch (e: UserRetrievalException) {
                    displayErrorMsg(getString(R.string.errorretrieveuser))
                } catch (e: Exception) {
                    displayErrorMsg(getString(R.string.error_unknown) + " - " + e.toString())
                }
            }
        }
    }

    private fun loginValidation(): Boolean {
        //Evaluate if given the amount passed since the last login, the user should re-login to the app
        //Last Signed In date
        val lastSignedInAt = try {
            UserSessionHelper.getUserSession("last_signed_in_at") as Long
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
            UserSessionHelper.getUserSession("event_id") as String
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

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = getString(R.string.myeventtitle)
        drawerLayout = findViewById(R.id.drawerlayout)
        val sidenavView = findViewById<NavigationView>(R.id.sidenav)
        val headerView = sidenavView.getHeaderView(0)
        val navView = findViewById<BottomNavigationView>(R.id.bottomnav)
        val androidVersion = headerView.findViewById<TextView>(R.id.androidVersionNumber)
        val androidCode = headerView.findViewById<TextView>(R.id.androidVersionCode)
        val headershortname =
            sidenavView.getHeaderView(0).findViewById<TextView>(R.id.headershortname)
        headershortname.text = usersession.shortname

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
                        .trackUserInteraction(SCREEN_NAME, "SideNavigationBar_Home", null)
                    clickNavItem = R.id.event_fragment
                    newfragment = DashboardView()
                }

                R.id.task_fragment -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "SideNavigationBar_Task", null)
                    clickNavItem = R.id.task_fragment
                    newfragment = DashboardActivity()
                }

                R.id.notes_fragment -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "SideNavigationBar_Notes", null)
                    clickNavItem = R.id.notes_fragment
                    newfragment = MyNotes()
                }

                R.id.settings_fragment -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "SideNavigationBar_Settings", null)
                    clickNavItem = R.id.settings_fragment
                    newfragment = Settings()
                }

                R.id.contact_fragment -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "SideNavigationBar_Contact", null)
                    clickNavItem = R.id.contact_fragment
                    sendEmail(this)
                }

                R.id.account_logoff -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "SideNavigationBar_LogOff", null)
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
                        .trackUserInteraction(SCREEN_NAME, "BottomNavigation_Home", null)
                    val newfragment = DashboardView()
                    fm.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                        .replace(R.id.fragment_container, newfragment)
                        .commit()
                }

                R.id.events -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "BottomNavigation_EventCategories", null)
                    val newfragment = EventCategories()
                    fm.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                        .replace(R.id.fragment_container, newfragment)
                        .commit()
                }

                R.id.tasks -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "BottomNavigation_Tasks", null)
                    val newfragment = DashboardActivity()
                    fm.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                        .replace(R.id.fragment_container, newfragment)
                        .commit()
                }

                R.id.guests -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "BottomNavigation_Guests", null)
                    val newfragment = GuestsAll()
                    fm.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                        .replace(R.id.fragment_container, newfragment)
                        .commit()
                }

                R.id.vendor -> {
                    AnalyticsManager.getInstance()
                        .trackUserInteraction(SCREEN_NAME, "BottomNavigation_Vendors", null)
                    val newfragment = VendorsAll()
                    fm.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
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
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                            .replace(R.id.fragment_container, newfragment!!)
                            .commit()
                    }

                    R.id.task_fragment -> {
                        fm.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                            .replace(R.id.fragment_container, newfragment!!)
                            .commit()
                    }

                    R.id.notes_fragment -> {
                        fm.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                            .replace(R.id.fragment_container, newfragment!!)
                            .commit()
                    }

                    R.id.settings_fragment -> {
                        fm.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                            .replace(R.id.fragment_container, newfragment!!)
                            .commit()
                    }
                }
            }

            override fun onDrawerStateChanged(newState: Int) {}
        })

        var activefragment = fm.findFragmentById(R.id.fragment_container)
        if (activefragment == null) {
            activefragment = DashboardView()
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
                is DashboardView -> {
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
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                }

                is ContactsAll -> {
                    fragment = GuestsAll()
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                }

                is GuestCreateEdit -> {
                    fragment = GuestsAll()
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                }

                is SearchVendorTab -> {
                    fragment = VendorsAll()
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                }

                is SearchVendorFragment -> {
                    fragment = VendorsAll()
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                }

                is NoteCreateEdit -> {
                    fragment = MyNotes()
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                }

                is TasksAllCalendar -> {
                    fragment = DashboardActivity()
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                }

                is PaymentsAllCalendar -> {
                    fragment = DashboardActivity()
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                }

                is TaskCreateEdit -> {
                    val callingFragment =
                        fragment?.parentFragmentManager?.fragments?.get(1)?.arguments?.getString("calling_fragment")
                    if (!callingFragment.isNullOrEmpty()) {
                        val bundle = Bundle()
                        fragment = when (callingFragment) {
                            "EventCategories" -> EventCategories()
                            "EmptyState" -> EventCategories()
                            "TasksAllCalendar" -> DashboardActivity()
                            "DashboardActivity" -> DashboardActivity()
                            "TaskPaymentTasks" -> {
                                val category =
                                    fragment?.parentFragmentManager?.fragments?.get(1)?.arguments?.getString(
                                        "category"
                                    )
                                val status =
                                    fragment?.parentFragmentManager?.fragments?.get(1)?.arguments?.getString(
                                        "status"
                                    )
                                bundle.putString("category", category)
                                bundle.putString("status", status)
                                TaskPaymentTasks()
                            }

                            else -> EventCategories()
                        }
                        fragment.arguments = bundle
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.fragment_container, fragment)
                            .commit()
                    }
                }

                is PaymentCreateEdit -> {
                    val callingFragment =
                        fragment?.parentFragmentManager?.fragments?.get(1)?.arguments?.getString("calling_fragment")
                    if (!callingFragment.isNullOrEmpty()) {
                        val bundle = Bundle()
                        fragment = when (callingFragment) {
                            "EventCategories" -> EventCategories()
                            "EmptyState" -> EventCategories()
                            "TasksAllCalendar" -> DashboardActivity()
                            "DashboardActivity" -> DashboardActivity()
                            "TaskPaymentPayments" -> {
                                val category =
                                    fragment?.parentFragmentManager?.fragments?.get(1)?.arguments?.getString(
                                        "category"
                                    )
                                val status =
                                    fragment?.parentFragmentManager?.fragments?.get(1)?.arguments?.getString(
                                        "status"
                                    )
                                bundle.putString("category", category)
                                bundle.putString("status", status)
                                TaskPaymentPayments()
                            }

                            else -> EventCategories()
                        }
                        fragment.arguments = bundle
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.fragment_container, fragment)
                            .commit()
                    }
                }

                is TaskPaymentTasks -> {
                    fragment = EventCategories()
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                }

                else -> {
                    fragment = DashboardView()
                    fm.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                }
            }
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

    private fun displayErrorMsg(message: String) {
        Toast.makeText(
            this@ActivityContainer,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    companion object {
        const val TAG = "ActivityContainer"
        const val SCREEN_NAME = "Activity_Container"
    }
}

interface IOnBackPressed {
    fun onBackPressed(): Boolean
}