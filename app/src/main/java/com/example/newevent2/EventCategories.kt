package com.example.newevent2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Model.MyFirebaseApp
import com.example.newevent2.Model.TaskDBHelper
import com.example.newevent2.ui.ViewAnimation
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.mainevent_summary.*
import kotlinx.android.synthetic.main.mainevent_summary.view.*

class EventCategories : Fragment() {

    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var inf: View
    private lateinit var list: ArrayList<Category>
    private var isRotate = false
    private val REQUEST_CODE_TASK = 4
    private val REQUEST_CODE_PAYMENT = 5

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inf = inflater.inflate(R.layout.mainevent_summary, container, false)

        // Getting the list of categories that I'm actually going to show from the local DB
        val taskdb = TaskDBHelper(requireContext())
        list = taskdb.getActiveCategories()

        // Creates and loads the Ad
        val adRequest = AdRequest.Builder().build()
        inf.adView.loadAd(adRequest)

        //Creating the recyclerview to show the Categories, 2 columns
        recyclerViewCategory = inf.categoryrv
        recyclerViewCategory.apply {
            layoutManager = GridLayoutManager(context, 2).apply {
                reverseLayout = true
            }
        }
        val rvAdapter = rvCategoryAdapter(list)
        recyclerViewCategory.adapter = rvAdapter

        // This segment initializes the animation for Task and Payment buttons
        ViewAnimation.init(inf.TaskLayout)
        ViewAnimation.init(inf.PaymentLayout)

        inf.NewTaskPaymentActionButton.setOnClickListener()
        {
            isRotate = ViewAnimation.rotateFab(inf.NewTaskPaymentActionButton, !isRotate)
            if (isRotate) {
                //when it rotates, it shows the two additional options to create Tasks and Payments
                ViewAnimation.showIn(TaskLayout)
                ViewAnimation.showIn(PaymentLayout)
            } else {
                // when it's not rotating, both options are hidden
                ViewAnimation.showOut(TaskLayout)
                ViewAnimation.showOut(PaymentLayout)
            }
        }

        inf.fabTask.setOnClickListener {
            // Before invoking the view to create the task, the Analytics records the action
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "NEWTASK")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------
            // As it's a new task, the value userid comes empty
            val newtask = Intent(context, TaskCreateEdit::class.java)
            newtask.putExtra("userid", "")
            startActivityForResult(newtask, REQUEST_CODE_TASK)
        }

        inf.fabPayment.setOnClickListener {
            // Before invoking the view to create the payment, the Analytics records the action
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "NEWPAYMENT")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------
            // As it's a new payment, the value userid comes empty
            val newpayment = Intent(context, PaymentCreateEdit::class.java)
            newpayment.putExtra("userid", "")
            startActivityForResult(newpayment, REQUEST_CODE_PAYMENT)
        }

        // For the Ad in this View, the below is a listener that catches events
        inf.adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                inf.adView.isVisible = true
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that covers the screen.
                // The Analytics catches whenever the user opens an Ad
                // ------- Analytics call ----------------
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ADOPENED")
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
                MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
                //----------------------------------------
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }
        return inf
    }

    //Hopefully this doesn't break
    override fun onResume() {
        super.onResume()
        //Creating the recyclerview to show the Categories, 2 columns
        recyclerViewCategory = inf.categoryrv
        recyclerViewCategory.apply {
            layoutManager = GridLayoutManager(context, 2).apply {
                reverseLayout = true
            }
        }
        val rvAdapter = rvCategoryAdapter(list)
        recyclerViewCategory.adapter = rvAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (((requestCode == REQUEST_CODE_TASK) || (requestCode == REQUEST_CODE_PAYMENT)) && resultCode == Activity.RESULT_OK){
            // Getting the list of categories that I'm actually going to show from the local DB
            val taskdb = TaskDBHelper(requireContext())
            val list = taskdb.getActiveCategories()

            val rvAdapter = rvCategoryAdapter(list)
            recyclerViewCategory.adapter = rvAdapter
        }
    }
}