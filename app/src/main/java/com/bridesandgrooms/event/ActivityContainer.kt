package com.bridesandgrooms.event

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
import com.bridesandgrooms.event.Functions.getUserSession
import com.bridesandgrooms.event.Functions.isEventDate
import com.bridesandgrooms.event.Model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.nordan.dialog.Animation
import com.nordan.dialog.NordanAlertDialog
import kotlinx.android.synthetic.main.header_navview.*
import kotlinx.android.synthetic.main.header_navview.view.*
import sendEmail

class ActivityContainer : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var loadingscreen: ConstraintLayout
    private lateinit var newfragment: Fragment

    private val fm = supportFragmentManager
    private var usersession = User()
    private var clickNavItem = 0

    private lateinit var headershortname: TextView

    private val TIME_DELAY = 2000
    private val LOGINACTIVITY = 123
    private var back_pressed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
//        val themeId = when (getAppTheme()) {
//            "pink" -> R.style.AppTheme_Pink
//            "blue" -> R.style.AppTheme_Blue
//            else -> R.style.AppTheme // Default theme if no override is found
//        }
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activitycontainer)

        drawerLayout = findViewById(R.id.drawerlayout)
        loadingscreen = findViewById(R.id.loadingscreen)
        val sidenavView = findViewById<NavigationView>(R.id.sidenav)

        //If session is empty the user gets redirected to the login screen
//        usersession = com.example.newevent2.Functions.getUserSession(this)
//        if (usersession.key == "") {
//            val loginactivity =
//                Intent(this, LoginView::class.java)
//            startActivity(loginactivity)
//        } else {
        //Short name is shown in the sidebar header
        headershortname =
            sidenavView.getHeaderView(0).findViewById(R.id.headershortname)
        //   headershortname.text = usersession.shortname
        //}

        //Fragment container is initialized with Dashboard fragment
//        var activefragment = fm.findFragmentById(R.id.fragment_container)
//        if (activefragment == null) {
//            activefragment = DashboardView_clone()
//            fm.beginTransaction()
//                .add(R.id.fragment_container, activefragment, "DashboardView")
//                .commit()
//        }

        //If today is the day of the event, the user will get a notification to congratulate him/her
//        if (isEventDate(this) == 0) {
//            NordanAlertDialog.Builder(this)
//                .setAnimation(Animation.SLIDE)
//                .isCancellable(false)
//                .setTitle(getString(R.string.congratulations))
//                .setMessage(getString(R.string.weddingday))
//                .setIcon(R.drawable.love_animated_gif_2018_8, true)
//                .setPositiveBtnText(getString(R.string.great))
//                .build().show()
//        }

        // Declare the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Create the toggle that will open and close the sidebar
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

        // Adding the title for the fragment container "My Event", this will be shown in
        // all of the others fragments
        val apptitle = findViewById<TextView>(R.id.appbartitle)
        apptitle.text = getString(R.string.myeventtitle)

        // Declaration of the sidebar and the different calls it makes
        // In this implementation, the classes are assigned but fragments are not yet created
        sidenavView.setNavigationItemSelectedListener { p0 ->
            when (p0.itemId) {
                R.id.event_fragment -> {
                    clickNavItem = R.id.event_fragment
                    newfragment = DashboardView_clone()
                }
                R.id.task_fragment -> {
                    clickNavItem = R.id.task_fragment
                    newfragment = DashboardActivity()
                }
                R.id.vendor_fragment -> {
                    clickNavItem = R.id.vendor_fragment
                    newfragment = VendorsAll()
                }
                R.id.settings_fragment -> {
                    clickNavItem = R.id.settings_fragment
                    newfragment = Settings()
                }
                R.id.contact_fragment -> {
                    clickNavItem = R.id.contact_fragment
                    sendEmail(this)
                }
                R.id.account_logoff -> {
                    logoffapp()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
        sidenavView.menu.getItem(0).isChecked = true

        //Creation of the navigation bar and the different calls it makes.
        //In this section, the fragments are indeed invoked and called
        val navView = findViewById<BottomNavigationView>(R.id.bottomnav)
        navView.setOnNavigationItemSelectedListener { p0 ->
            when (p0.itemId) {
                R.id.home -> {
                    val newfragment = DashboardView_clone()
                    fm.beginTransaction()
                        .replace(R.id.fragment_container, newfragment)
                        .commit()
                }
                R.id.events -> {
                    val newfragment = EventCategories()
                    fm.beginTransaction()
                        .replace(R.id.fragment_container, newfragment)
                        .commit()
                }
                R.id.tasks -> {
                    val newfragment = DashboardActivity()
                    fm.beginTransaction()
                        .replace(R.id.fragment_container, newfragment)
                        .commit()
                }
                R.id.guests -> {
                    val newfragment = TableGuestsActivity()
                    fm.beginTransaction()
                        .replace(R.id.fragment_container, newfragment)
                        .commit()
                }
                R.id.notes -> {
                    val newfragment = MyNotes()
                    fm.beginTransaction()
                        .replace(R.id.fragment_container, newfragment)
                        .commit()
                }
            }
            true
        }
        navView.menu.getItem(0).isChecked = true


        // Whatever option has been selected for the sidebar it's invoked here and the fragment is
        // created
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}

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
                }
            }

            override fun onDrawerStateChanged(newState: Int) {}
        })
    }


    // This catches the back option in Android, if the sidebar is displayed, then it gets closed
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val fragment =
                fm.findFragmentById(R.id.fragment_container)
            val currentFragment =
                fragment?.parentFragmentManager?.fragments?.get(1)
            val fragmentstring = currentFragment?.javaClass.toString()
            if (fragmentstring.contains("DashboardView_clone") || fragmentstring == "null") {
                if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
                    super.onBackPressed()
                } else {
                    Toast.makeText(
                        baseContext, getString(R.string.pressexit),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                back_pressed = System.currentTimeMillis()
            } else {
                val newfragment = DashboardView_clone()
                fm.beginTransaction()
                    .replace(R.id.fragment_container, newfragment)
                    .commit()
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
                //Logoff in case the user was logged in with Facebook
//                "facebook" -> {
//                    LoginManager.getInstance().logOut()
//                }
            }
            //Logoff from Firebase
            lifecycleScope.launchWhenResumed {
                usersession.logout(this@ActivityContainer)
            }
            //usersession.deleteUserSession(this@ActivityContainer)
            finish()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { p0, _ -> p0!!.dismiss() }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        //If session is empty the user gets redirected to the login screen
        //usersession = com.example.newevent2.Functions.getUserSession(this)
//        userdbhelper = UserDBHelper(this)
//        usersession = userdbhelper.getUser(userdbhelper.getUserKey())


        val lastSignedInAt = try {
            getUserSession(this@ActivityContainer, "last_signed_in_at") as Long
        } catch (e: Exception) {
            println(e.message)
            0L
        }

        val emailid = try {
            getUserSession(this@ActivityContainer, "event_id") as String
        } catch (e: Exception) {
            println(e.message)
            ""
        }

        val currentTimeMillis = System.currentTimeMillis()
        val oneWeekInMillis = 604800000L
        //val oneWeekInMillis = 3600000L

        //168 hrs
        if (currentTimeMillis - lastSignedInAt >= oneWeekInMillis || lastSignedInAt == 0L || emailid == "") {
            val loginactivity =
                Intent(this, LoginView::class.java)
            startActivityForResult(loginactivity, LOGINACTIVITY)
        } else {
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

            var activefragment = fm.findFragmentById(R.id.fragment_container)
            if (activefragment == null) {
                activefragment = DashboardView_clone()
                fm.beginTransaction()
                    .add(R.id.fragment_container, activefragment, "DashboardView")
                    .commit()
            }
        }
//
//        if (usersession.email == "") {
//            val loginactivity =
//                Intent(this, LoginView::class.java)
//            startActivity(loginactivity)
//        } else {
//            headershortname.text = usersession.shortname
//            if (isEventDate(this) == 0) {
//                NordanAlertDialog.Builder(this)
//                    .setAnimation(Animation.SLIDE)
//                    .isCancellable(false)
//                    .setTitle(getString(R.string.congratulations))
//                    .setMessage(getString(R.string.weddingday))
//                    .setIcon(R.drawable.love_animated_gif_2018_8, true)
//                    .setPositiveBtnText(getString(R.string.great))
//                    .build().show()
//            }
//
//            var activefragment = fm.findFragmentById(R.id.fragment_container)
//            if (activefragment == null) {
//                activefragment = DashboardView_clone()
//                fm.beginTransaction()
//                    .add(R.id.fragment_container, activefragment, "DashboardView")
//                    .commit()
//            }
//        }
    }

//    override fun onBackPressedLogin() {
//        finishActivity(LOGINACTIVITY)
//        super.onBackPressed()
//    }

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
}

interface IOnBackPressed {
    fun onBackPressed(): Boolean
}