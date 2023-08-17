package com.bridesandgrooms.event

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Model.MyFirebaseApp
import com.bridesandgrooms.event.Model.TaskDBHelper
import com.bridesandgrooms.event.ui.ViewAnimation
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.empty_state.view.*
import kotlinx.android.synthetic.main.mainevent_summary.*
import kotlinx.android.synthetic.main.mainevent_summary.view.*
import kotlinx.android.synthetic.main.onboardingcard.view.*
import kotlinx.android.synthetic.main.taskpayment_tasks.view.*

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

        if (list.isNotEmpty()) {
            //Creating the recyclerview to show the Categories, 2 columns
            recyclerViewCategory = inf.categoryrv
            recyclerViewCategory.apply {
                layoutManager = GridLayoutManager(context, 2).apply {
                    reverseLayout = true
                }
            }
            val rvAdapter = rvCategoryAdapter(list)
            recyclerViewCategory.adapter = rvAdapter

            val showads = RemoteConfigSingleton.get_showads()
            if (showads) {
                val adRequest = AdRequest.Builder().build()
                inf.adView.loadAd(adRequest)
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
                        MyFirebaseApp.mFirebaseAnalytics.logEvent(
                            FirebaseAnalytics.Event.SELECT_ITEM,
                            bundle
                        )
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
            }
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
                MyFirebaseApp.mFirebaseAnalytics.logEvent(
                    FirebaseAnalytics.Event.SELECT_ITEM,
                    bundle
                )
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
                MyFirebaseApp.mFirebaseAnalytics.logEvent(
                    FirebaseAnalytics.Event.SELECT_ITEM,
                    bundle
                )
                //----------------------------------------
                // As it's a new payment, the value userid comes empty
                val newpayment = Intent(context, PaymentCreateEdit::class.java)
                newpayment.putExtra("userid", "")
                startActivityForResult(newpayment, REQUEST_CODE_PAYMENT)
            }
        } else {
            //If no tasks are retrieved from the presenter the component is marked as invisible
            inf.withdatacategory.visibility = ConstraintLayout.GONE

            val emptystateLayout =
                inf.findViewById<ConstraintLayout>(R.id.withnodatacategory)
            val fadeAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation)
            //----------------------------------------------------------------
            emptystateLayout.visibility = ConstraintLayout.VISIBLE
            emptystateLayout.empty_card.onboardingmessage.text =
                getString(R.string.emptystate_notasksmsg)
            emptystateLayout.newtaskbutton.startAnimation(fadeAnimation)
            emptystateLayout.newtaskbutton.setOnClickListener {
                val newTask = Intent(context, TaskCreateEdit::class.java)
                newTask.putExtra("userid", "")
                startActivity(newTask)
            }
        }
        return inf
    }

    //Hopefully this doesn't break
    override fun onResume() {
        super.onResume()

        val taskdb = TaskDBHelper(requireContext())
        list = taskdb.getActiveCategories()

        if (list.isNotEmpty()) {
            //Creating the recyclerview to show the Categories, 2 columns
            recyclerViewCategory = inf.categoryrv
            recyclerViewCategory.apply {
                layoutManager = GridLayoutManager(context, 2).apply {
                    reverseLayout = true
                }
            }
            val rvAdapter = rvCategoryAdapter(list)
            recyclerViewCategory.adapter = rvAdapter

            inf.withdatacategory.visibility = ConstraintLayout.VISIBLE
            val emptystateLayout = inf.findViewById<ConstraintLayout>(R.id.withnodatacategory)
            emptystateLayout.visibility = ConstraintLayout.GONE
        } else {
            inf.withdatacategory.visibility = ConstraintLayout.GONE

            val emptystateLayout =
                inf.findViewById<ConstraintLayout>(R.id.withnodatacategory)
            val fadeAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation)
            //----------------------------------------------------------------
            emptystateLayout.visibility = ConstraintLayout.VISIBLE
            emptystateLayout.empty_card.onboardingmessage.text =
                getString(R.string.emptystate_notasksmsg)
            emptystateLayout.newtaskbutton.startAnimation(fadeAnimation)
            emptystateLayout.newtaskbutton.setOnClickListener {
                val newTask = Intent(context, TaskCreateEdit::class.java)
                newTask.putExtra("userid", "")
                startActivity(newTask)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (((requestCode == REQUEST_CODE_TASK) || (requestCode == REQUEST_CODE_PAYMENT)) && resultCode == Activity.RESULT_OK) {
            // Getting the list of categories that I'm actually going to show from the local DB
            val taskdb = TaskDBHelper(requireContext())
            val list = taskdb.getActiveCategories()

            val rvAdapter = rvCategoryAdapter(list)
            recyclerViewCategory.adapter = rvAdapter
        }
    }
}