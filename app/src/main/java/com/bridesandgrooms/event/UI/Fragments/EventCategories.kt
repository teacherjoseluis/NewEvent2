package com.bridesandgrooms.event.UI.Fragments

import Application.AnalyticsManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton
import com.bridesandgrooms.event.Model.Category
import Application.MyFirebaseApp
import android.content.Context
import android.util.Log
import android.widget.TextView
import com.bridesandgrooms.event.LoginView
import com.bridesandgrooms.event.LoginView.Companion
import com.bridesandgrooms.event.MVP.EventCategoryPresenter
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.Adapters.CategoryAdapter
import com.bridesandgrooms.event.UI.Fragments.GuestsAll.Companion.SCREEN_NAME
import com.bridesandgrooms.event.UI.Fragments.GuestsAll.Companion.TAG
import com.bridesandgrooms.event.databinding.MaineventSummaryBinding
import com.bridesandgrooms.event.UI.ViewAnimation
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.analytics.FirebaseAnalytics

class EventCategories : Fragment(), EventCategoryPresenter.EventCategoryInterface,
    CategoryFragmentActionListener {

    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var presenterCategory: EventCategoryPresenter
    private lateinit var inf: MaineventSummaryBinding
    private lateinit var toolbar: MaterialToolbar

    private var isRotate = false
    private var mContext: Context? = null

    private var showAds = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.categories)

        inf = DataBindingUtil.inflate(inflater, R.layout.mainevent_summary, container, false)

        recyclerViewCategory = inf.categoryrv
        recyclerViewCategory.apply {
            layoutManager = GridLayoutManager(context, 2).apply {
                reverseLayout = true
            }
        }

        try {
            presenterCategory = EventCategoryPresenter(mContext!!, this)
            presenterCategory.getActiveCategories()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
        return inf.root
    }

    override fun onCategories(list: List<Category>?) {
        if (list != null) {
            if (list.isNotEmpty()) {
                //Creating the recyclerview to show the Categories, 2 columns

                val rvAdapter = CategoryAdapter(this, list, mContext!!)
                recyclerViewCategory.adapter = rvAdapter

                showAds = RemoteConfigSingleton.get_showads()
                if (showAds) {
                    MobileAds.initialize(requireContext()) { initializationStatus ->
                        // You can leave this empty or handle initialization status if needed
                    }

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
                            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Ad", "click")
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
                        ViewAnimation.showIn(inf.TaskLayout)
                        ViewAnimation.showIn(inf.PaymentLayout)
                    } else {
                        // when it's not rotating, both options are hidden
                        ViewAnimation.showOut(inf.TaskLayout)
                        ViewAnimation.showOut(inf.PaymentLayout)
                    }
                }

                inf.fabTask.setOnClickListener {
                    AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME, "Add_Task")

                    val fragment = TaskCreateEdit()
                    val bundle = Bundle()
                    bundle.putString("calling_fragment", "EventCategories")
                    fragment.arguments = bundle
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
                        .replace(
                            R.id.fragment_container,
                            fragment
                        ) // R.id.fragment_container is the ID of the container where the fragment will be placed
                        .addToBackStack(null) // Add this transaction to the back stack, so the user can navigate back to the previous fragment
                        .commit()
                }

                inf.fabPayment.setOnClickListener {
                    AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME, "Add_Payment")

                    val fragment = PaymentCreateEdit()
                    val bundle = Bundle()
                    bundle.putString("calling_fragment", "EventCategories")
                    fragment.arguments = bundle
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
                        .replace(
                            R.id.fragment_container,
                            fragment
                        ) // R.id.fragment_container is the ID of the container where the fragment will be placed
                        .addToBackStack(null) // Add this transaction to the back stack, so the user can navigate back to the previous fragment
                        .commit()
                }
            }
        }
    }

    override fun onCategoriesError(errcode: String) {
        emptyStateFragment()
    }

    fun emptyStateFragment() {
        val container = inf.root as ViewGroup?
        container?.removeAllViews()

        val newView = layoutInflater.inflate(R.layout.empty_state_fragment, container, false)
        container?.addView(newView)

        newView.findViewById<TextView>(R.id.emptystate_message).setText(R.string.emptystate_notasksmsg)
        newView.findViewById<TextView>(R.id.emptystate_cta).setText(R.string.emptystate_notasksscta)
        newView.findViewById<FloatingActionButton>(R.id.fab_action).setOnClickListener {
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "fabAction", "click")
            callTaskCreateFragment()
        }
    }

    fun callTaskCreateFragment(){
        val fragment = TaskCreateEdit()
        val bundle = Bundle()
        bundle.putString("calling_fragment", "EmptyState")
        fragment.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(
                R.id.fragment_container,
                fragment
            )
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun onCategoryFragmentWithData(category: String) {
        val fragment = TaskPaymentList()
        val bundle = Bundle()
        bundle.putString("category", category)
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                fragment
            )
            .commit()
    }
}

interface CategoryFragmentActionListener {
    fun onCategoryFragmentWithData(category: String)
}