package com.bridesandgrooms.event

import GalleryAdapter
import android.content.Context
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bridesandgrooms.event.MVP.ActivityGalleryPresenter
import com.bridesandgrooms.event.Model.DashboardImage.DashboardRepository
import com.bridesandgrooms.event.Model.DashboardImage.FirebaseDataSourceImpl
import com.bridesandgrooms.event.databinding.ActivityGalleryBinding
import com.google.android.material.appbar.MaterialToolbar

class FragmentGallery : Fragment(), ActivityGalleryPresenter.ActiveGalleryImages {

    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var activityGP: ActivityGalleryPresenter
    private lateinit var binding: ActivityGalleryBinding

    private lateinit var toolbar: MaterialToolbar

    private var mContext: Context? = null
//    private lateinit var list: ArrayList<Category>
//    private var isRotate = false
//    private val REQUEST_CODE_TASK = 4
//    private val REQUEST_CODE_PAYMENT = 5

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

        binding = DataBindingUtil.inflate(inflater, R.layout.activity_gallery, container, false)

        binding.loadingscreen.root.visibility = View.VISIBLE
        binding.activitygalleryrv.visibility = View.INVISIBLE

        val category = arguments?.getString("category") ?: ""

        try {
            activityGP = ActivityGalleryPresenter(this)

            val repository = DashboardRepository(FirebaseDataSourceImpl(mContext!!))
            activityGP.setRepository(repository)
            activityGP.fetchActivityGallery(category)

        } catch (e: Exception) {
            Log.e(DashboardEvent.TAG, e.message.toString())
        }

//        if (list.isNotEmpty()) {
//            //Creating the recyclerview to show the Categories, 2 columns
//            recyclerViewCategory = inf.activitygalleryrv
//            recyclerViewCategory.apply {
//                layoutManager = GridLayoutManager(context, 2).apply {
//                    reverseLayout = true
//                }
//            }
//            val rvAdapter = rvCategoryAdapter(list)
//            recyclerViewCategory.adapter = rvAdapter

//            val showads = RemoteConfigSingleton.get_showads()
//            if (showads) {
//                val adRequest = AdRequest.Builder().build()
//                inf.adView.loadAd(adRequest)
//                // For the Ad in this View, the below is a listener that catches events
//                inf.adView.adListener = object : AdListener() {
//                    override fun onAdLoaded() {
//                        // Code to be executed when an ad finishes loading.
//                        inf.adView.isVisible = true
//                    }
//
//                    override fun onAdFailedToLoad(adError: LoadAdError) {
//                        // Code to be executed when an ad request fails.
//                    }
//
//                    override fun onAdOpened() {
//                        // Code to be executed when an ad opens an overlay that covers the screen.
//                        // The Analytics catches whenever the user opens an Ad
//                        // ------- Analytics call ----------------
//                        val bundle = Bundle()
//                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ADOPENED")
//                        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
//                        MyFirebaseApp.mFirebaseAnalytics.logEvent(
//                            FirebaseAnalytics.Event.SELECT_ITEM,
//                            bundle
//                        )
//                        //----------------------------------------
//                    }
//
//                    override fun onAdClicked() {
//                        // Code to be executed when the user clicks on an ad.
//                    }
//
//                    override fun onAdClosed() {
//                        // Code to be executed when the user is about to return
//                        // to the app after tapping on an ad.
//                    }
//                }
//            }
//            // This segment initializes the animation for Task and Payment buttons
//            ViewAnimation.init(inf.TaskLayout)
//            ViewAnimation.init(inf.PaymentLayout)
//
//            inf.NewTaskPaymentActionButton.setOnClickListener()
//            {
//                isRotate = ViewAnimation.rotateFab(inf.NewTaskPaymentActionButton, !isRotate)
//                if (isRotate) {
//                    //when it rotates, it shows the two additional options to create Tasks and Payments
//                    ViewAnimation.showIn(inf.TaskLayout)
//                    ViewAnimation.showIn(inf.PaymentLayout)
//                } else {
//                    // when it's not rotating, both options are hidden
//                    ViewAnimation.showOut(inf.TaskLayout)
//                    ViewAnimation.showOut(inf.PaymentLayout)
//                }
//            }
//
//            inf.fabTask.setOnClickListener {
//                // Before invoking the view to create the task, the Analytics records the action
//                // ------- Analytics call ----------------
//                val bundle = Bundle()
//                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "NEWTASK")
//                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
//                MyFirebaseApp.mFirebaseAnalytics.logEvent(
//                    FirebaseAnalytics.Event.SELECT_ITEM,
//                    bundle
//                )
//                //----------------------------------------
//                // As it's a new task, the value userid comes empty
//                val newtask = Intent(context, TaskCreateEdit::class.java)
//                newtask.putExtra("userid", "")
//                startActivityForResult(newtask, REQUEST_CODE_TASK)
//            }
//
//            inf.fabPayment.setOnClickListener {
//                // Before invoking the view to create the payment, the Analytics records the action
//                // ------- Analytics call ----------------
//                val bundle = Bundle()
//                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "NEWPAYMENT")
//                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
//                MyFirebaseApp.mFirebaseAnalytics.logEvent(
//                    FirebaseAnalytics.Event.SELECT_ITEM,
//                    bundle
//                )
//                //----------------------------------------
//                // As it's a new payment, the value userid comes empty
//                val newpayment = Intent(context, PaymentCreateEdit::class.java)
//                newpayment.putExtra("userid", "")
//                startActivityForResult(newpayment, REQUEST_CODE_PAYMENT)
//            }
        return binding.root
        }

    override fun onActiveGalleryImages(images: List<Pair<Bitmap, String>>) {
//        val container = binding.root as ViewGroup?
//        container?.removeAllViews()
//
//        val newView = layoutInflater.inflate(R.layout.activity_gallery, container, false)
//        container?.addView(newView)

        binding.loadingscreen.root.visibility = View.INVISIBLE
        binding.activitygalleryrv.visibility = View.VISIBLE

        recyclerViewCategory = binding.activitygalleryrv
        recyclerViewCategory.apply {
            layoutManager = GridLayoutManager(context, 2).apply {
                reverseLayout = true
            }
        }
        val rvAdapter = GalleryAdapter(mContext!!, images)
        binding.activitygalleryrv.adapter = rvAdapter
    }

    override fun onActiveGalleryImagesError(errcode: String) {
        TODO("Not yet implemented")
    }

//        else {
//            //If no tasks are retrieved from the presenter the component is marked as invisible
//            inf.withdatacategory.visibility = ConstraintLayout.GONE
//
//            val emptystateLayout = inf.withnodatacategory
//                //inf.findViewById<ConstraintLayout>(R.id.withnodatacategory)
//            val fadeAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation)
//            //----------------------------------------------------------------
//            emptystateLayout.root.visibility = ConstraintLayout.VISIBLE
//            emptystateLayout.emptyCard.onboardingmessage.text =
//                getString(R.string.emptystate_notasksmsg)
//            emptystateLayout.newtaskbutton.startAnimation(fadeAnimation)
//            emptystateLayout.newtaskbutton.setOnClickListener {
//                val newTask = Intent(context, TaskCreateEdit::class.java)
//                newTask.putExtra("userid", "")
//                startActivity(newTask)
//            }
//        }
 //       return inf.root
    }


    //Hopefully this doesn't break
//    override fun onResume() {
//        super.onResume()
//
//        val taskdb = TaskDBHelper(requireContext())
//        list = taskdb.getActiveCategories()!!
//
//        if (list.isNotEmpty()) {
//            //Creating the recyclerview to show the Categories, 2 columns
//            recyclerViewCategory = inf.categoryrv
//            recyclerViewCategory.apply {
//                layoutManager = GridLayoutManager(context, 2).apply {
//                    reverseLayout = true
//                }
//            }
//            val rvAdapter = rvCategoryAdapter(list)
//            recyclerViewCategory.adapter = rvAdapter
//
//            inf.withdatacategory.visibility = ConstraintLayout.VISIBLE
//            val emptystateLayout = inf.withnodatacategory
//            emptystateLayout.root.visibility = ConstraintLayout.GONE
//        } else {
//            inf.withdatacategory.visibility = ConstraintLayout.GONE
//
//            val emptystateLayout = inf.withnodatacategory
//            val fadeAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation)
//            //----------------------------------------------------------------
//            emptystateLayout.root.visibility = ConstraintLayout.VISIBLE
//            emptystateLayout.emptyCard.onboardingmessage.text =
//                getString(R.string.emptystate_notasksmsg)
//            emptystateLayout.newtaskbutton.startAnimation(fadeAnimation)
//            emptystateLayout.newtaskbutton.setOnClickListener {
//                val newTask = Intent(context, TaskCreateEdit::class.java)
//                newTask.putExtra("userid", "")
//                startActivity(newTask)
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (((requestCode == REQUEST_CODE_TASK) || (requestCode == REQUEST_CODE_PAYMENT)) && resultCode == Activity.RESULT_OK) {
//            // Getting the list of categories that I'm actually going to show from the local DB
//            val taskdb = TaskDBHelper(requireContext())
//            val list = taskdb.getActiveCategories()
//
//            val rvAdapter = rvCategoryAdapter(list!!)
//            recyclerViewCategory.adapter = rvAdapter
//        }
//    }


