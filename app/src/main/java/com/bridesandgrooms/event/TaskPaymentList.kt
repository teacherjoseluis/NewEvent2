package com.bridesandgrooms.event

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bridesandgrooms.event.Functions.userdbhelper
import com.bridesandgrooms.event.Model.TaskModel
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.Model.UserDBHelper
import com.google.android.material.tabs.TabLayout
import java.util.*
import kotlin.collections.ArrayList

class TaskPaymentList : AppCompatActivity() {

    val userid = ""
    val eventid = ""
    var usersession = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taskpayment_list)

        //I assume user data is needed to pass to Task and Payments classes in charge
        // of displaying data. As TaskPaymentList doesn't get user as parameter, we better
        // get it here.
//        usersession = com.example.newevent2.Functions.getUserSession(this)
//        if (usersession.key == "") {
//            val loginactivity =
//                Intent(this, LoginView::class.java)
//            startActivity(loginactivity)
//        }
        userdbhelper = UserDBHelper(this)
        usersession = userdbhelper.getUser(userdbhelper.getUserKey())

        val passedcategory = intent.getStringExtra("category").toString()

        // Declaring and initializing the Toolbar
        val tablayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<View>(R.id.pager) as ViewPager

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Declaring the PageAdapter and invoking it
        val adapter = TaskPayment_PagerAdapter(
            supportFragmentManager,
            usersession.userid!!,
            usersession.eventid,
            passedcategory,
            TaskModel.ACTIVESTATUS,
            tablayout.tabCount
        )
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))
        tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                viewPager.currentItem = p0!!.position
            }
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabReselected(p0: TabLayout.Tab?) {}
        })

        // Every container page will have the title name for the associated category
        val apptitle = findViewById<TextView>(R.id.appbartitle)
        val list = ArrayList<Category>(EnumSet.allOf(Category::class.java))
        val language = this.resources.configuration.locales.get(0).language

        for (category in list) {
            if (category.code == passedcategory) {
                apptitle.text = when (language) {
                    "en" -> category.en_name
                    else -> category.es_name
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}