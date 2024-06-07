package com.bridesandgrooms.event


import Application.AnalyticsManager
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.clone
import com.bridesandgrooms.event.MVP.GuestsAllPresenter
import com.bridesandgrooms.event.Model.Guest
import Application.MyFirebaseApp
import android.content.Context
import android.util.Log
import android.widget.TextView
import com.bridesandgrooms.event.Model.contactGuest
import com.bridesandgrooms.event.UI.Fragments.EmptyStateFragment
import com.bridesandgrooms.event.UI.Fragments.SearchVendorTab
import com.bridesandgrooms.event.UI.Fragments.VendorCreateEdit
import com.bridesandgrooms.event.UI.Fragments.VendorsAll
import com.bridesandgrooms.event.databinding.GuestsAllBinding
import com.bridesandgrooms.event.UI.ViewAnimation
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.analytics.FirebaseAnalytics
//import kotlinx.android.synthetic.main.empty_state.view.*
//import kotlinx.android.synthetic.main.guests_all.*
//import kotlinx.android.synthetic.main.guests_all.view.*
import java.util.*
import kotlin.collections.ArrayList

//import kotlinx.android.synthetic.main.onboardingcard.view.*
//import kotlinx.android.synthetic.main.taskpayment_payments.view.*
//import kotlinx.android.synthetic.main.vendors_all.*


class GuestsAll : Fragment(), GuestsAllPresenter.GAGuests, GuestFragmentActionListener {

    private lateinit var recyclerViewAllGuests: RecyclerView
    private lateinit var presenterguest: GuestsAllPresenter
    private lateinit var rvAdapter: GuestAdapter
    private lateinit var inf: GuestsAllBinding
    private lateinit var toolbar: MaterialToolbar

    private var guestList = ArrayList<Guest>()
    private var isRotate = false
    private var mContext: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.guests_menu, menu)
        val addGuest = menu.findItem(R.id.add_guest)
        addGuest.isVisible = false

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.isIconified = false
        searchView.setOnSearchClickListener {}
        searchView.setOnCloseListener {
            toolbar.collapseActionView()
            true
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val filteredModelList = ArrayList<contactGuest>()
                filter(guestList, p0).forEach { guest->filteredModelList.add(contactGuest(guest)) }
                val rvAdapter = GuestAdapter(this, filteredModelList, mContext!!)
                recyclerViewAllGuests.adapter = rvAdapter
                return true
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.guests)

        inf = DataBindingUtil.inflate(inflater, R.layout.guests_all, container, false)

        recyclerViewAllGuests = inf.recyclerViewGuests
        recyclerViewAllGuests.apply {
            layoutManager = LinearLayoutManager(inf.root.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

        try {
            presenterguest = GuestsAllPresenter(mContext!!, this)
            presenterguest.getGuestList()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }

        ViewAnimation.init(inf.NewGuest)
        ViewAnimation.init(inf.ContactGuest)

        inf.floatingActionButtonGuest.setOnClickListener()
        {
            isRotate = ViewAnimation.rotateFab(inf.floatingActionButtonGuest, !isRotate)
            if (isRotate) {
                ViewAnimation.showIn(inf.NewGuest)
                ViewAnimation.showIn(inf.ContactGuest)
            } else {
                ViewAnimation.showOut(inf.NewGuest)
                ViewAnimation.showOut(inf.ContactGuest)
            }
        }

        inf.fabNewGuest.setOnClickListener {
            AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME, "Add_Guest")
            val fragment = GuestCreateEdit()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // R.id.fragment_container is the ID of the container where the fragment will be placed
                .addToBackStack(null) // Add this transaction to the back stack, so the user can navigate back to the previous fragment
                .commit()
        }

        inf.fabContactGuest.setOnClickListener {
            AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME, "Guest_Contacts")

            val fragment = ContactsAll()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // R.id.fragment_container is the ID of the container where the fragment will be placed
                .addToBackStack(null) // Add this transaction to the back stack, so the user can navigate back to the previous fragment
                .commit()
        }
        return inf.root
    }

    private fun filter(models: ArrayList<Guest>, query: String?): List<Guest> {
        val lowerCaseQuery = query!!.toLowerCase(Locale.ROOT)
        val filteredModelList: ArrayList<Guest> = ArrayList()
        for (model in models) {
            val text: String = model.name.toLowerCase(Locale.ROOT)
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model)
            }
        }
        return filteredModelList
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onGAGuests(
        list: ArrayList<Guest>
    ) {
        if (list.size != 0) {
            guestList = clone(list)
            val guestList = ArrayList<contactGuest>()
            list.forEach { guest ->
                guestList.add(contactGuest(guest))
            }

            try {
                rvAdapter = GuestAdapter(this, guestList, mContext!!)
                rvAdapter.notifyDataSetChanged()
                //rvAdapter.notifyDataSetChanged()
            } catch (e: java.lang.Exception) {
                println(e.message)
            }

            recyclerViewAllGuests.adapter = null
            recyclerViewAllGuests.adapter = rvAdapter

            inf.withdata.visibility = ConstraintLayout.VISIBLE
            val emptystateLayout = inf.withnodata
            emptystateLayout.root.visibility = ConstraintLayout.GONE
            //----------------------------------------------------------------
        }
//        else if (list.size == 0) {
//            inf.withdata.visibility = ConstraintLayout.GONE
//
//            val emptystateLayout = inf.withnodata
//            val topMarginInPixels = resources.getDimensionPixelSize(R.dimen.emptystate_topmargin)
//            val bottomMarginInPixels =
//                resources.getDimensionPixelSize(R.dimen.emptystate_marginbottom)
//            val params = emptystateLayout.root.layoutParams as ViewGroup.MarginLayoutParams
//
//            params.topMargin = topMarginInPixels
//            params.bottomMargin = bottomMarginInPixels
//            emptystateLayout.root.layoutParams = params
//
//            emptystateLayout.root.visibility = ConstraintLayout.VISIBLE
//            emptystateLayout.emptyCard.onboardingmessage.text =
//                getString(R.string.emptystate_noguestsmsg)
//
////        val fadeAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation)
////        emptystateLayout.newtaskbutton.startAnimation(fadeAnimation)
////
////        emptystateLayout.newtaskbutton.setOnClickListener {
////            val newTask = Intent(context, GuestCreateEdit::class.java)
////            startActivity(newTask)
//            //}
//            ViewAnimation.init(inf.NewGuest)
//            ViewAnimation.init(inf.ContactGuest)
//
//            inf.floatingActionButtonGuest.setOnClickListener()
//            {
//                isRotate = ViewAnimation.rotateFab(inf.floatingActionButtonGuest, !isRotate)
//                if (isRotate) {
//                    ViewAnimation.showIn(inf.NewGuest)
//                    ViewAnimation.showIn(inf.ContactGuest)
//                } else {
//                    ViewAnimation.showOut(inf.NewGuest)
//                    ViewAnimation.showOut(inf.ContactGuest)
//                }
//            }
//            inf.NewGuest.setOnClickListener {
//                // ------- Analytics call ----------------
//                val bundle = Bundle()
//                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "inf.NewGuest")
//                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
//                MyFirebaseApp.mFirebaseAnalytics.logEvent(
//                    FirebaseAnalytics.Event.SELECT_ITEM,
//                    bundle
//                )
//                //----------------------------------------
//
//                val NewGuest = Intent(context, GuestCreateEdit::class.java)
//                NewGuest.putExtra("userid", "")
//
//                startActivityForResult(NewGuest, REQUEST_CODE_GUESTS)
//            }
//
//            inf.ContactGuest.setOnClickListener {
//                // ------- Analytics call ----------------
//                val bundle = Bundle()
//                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "GUESTFROMCONTACTS")
//                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
//                MyFirebaseApp.mFirebaseAnalytics.logEvent(
//                    FirebaseAnalytics.Event.SELECT_ITEM,
//                    bundle
//                )
//                //----------------------------------------
//
//                val NewGuest = Intent(context, ContactsAll::class.java)
//                NewGuest.putExtra("guestid", "")
//                //startActivity(inf.NewGuest)
//
//                startActivityForResult(NewGuest, REQUEST_CODE_CONTACTS)
//            }
//        }
    }

    override fun onGAGuestsError(errcode: String) {
        val message = getString(R.string.emptystate_novendorsmsg)
        val cta = getString(R.string.emptystate_novendorscta)
        val actionClass =
            VendorCreateEdit::class.java
        val fragment = EmptyStateFragment.newInstance(message, cta, actionClass)
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
            ?.commit()
    }
//    {
//        inf.withdata.visibility = ConstraintLayout.GONE
//
//        val emptystateLayout = inf.withnodata
//        val topMarginInPixels = resources.getDimensionPixelSize(R.dimen.emptystate_topmargin)
//        val bottomMarginInPixels = resources.getDimensionPixelSize(R.dimen.emptystate_marginbottom)
//        val params = emptystateLayout.root.layoutParams as ViewGroup.MarginLayoutParams
//
//        params.topMargin = topMarginInPixels
//        params.bottomMargin = bottomMarginInPixels
//        emptystateLayout.root.layoutParams = params
//
//        emptystateLayout.root.visibility = ConstraintLayout.VISIBLE
//        emptystateLayout.emptyCard.onboardingmessage.text =
//            getString(R.string.emptystate_noguestsmsg)
//
////        val fadeAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation)
////        emptystateLayout.newtaskbutton.startAnimation(fadeAnimation)
////
////        emptystateLayout.newtaskbutton.setOnClickListener {
////            val newTask = Intent(context, GuestCreateEdit::class.java)
////            startActivity(newTask)
//        //}
//        ViewAnimation.init(inf.NewGuest)
//        ViewAnimation.init(inf.ContactGuest)
//
//        inf.floatingActionButtonGuest.setOnClickListener()
//        {
//            isRotate = ViewAnimation.rotateFab(inf.floatingActionButtonGuest, !isRotate)
//            if (isRotate) {
//                ViewAnimation.showIn(inf.NewGuest)
//                ViewAnimation.showIn(inf.ContactGuest)
//            } else {
//                ViewAnimation.showOut(inf.NewGuest)
//                ViewAnimation.showOut(inf.ContactGuest)
//            }
//        }
//        inf.NewGuest.setOnClickListener {
//            // ------- Analytics call ----------------
//            val bundle = Bundle()
//            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "inf.NewGuest")
//            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
//            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
//            //----------------------------------------
//
//            val NewGuest = Intent(context, GuestCreateEdit::class.java)
//            NewGuest.putExtra("userid", "")
//
//            startActivityForResult(NewGuest, REQUEST_CODE_GUESTS)
//        }
//
//        inf.ContactGuest.setOnClickListener {
//            // ------- Analytics call ----------------
//            val bundle = Bundle()
//            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "GUESTFROMCONTACTS")
//            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
//            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
//            //----------------------------------------
//
//            val NewGuest = Intent(context, ContactsAll::class.java)
//            NewGuest.putExtra("guestid", "")
//            //startActivity(inf.NewGuest)
//
//            startActivityForResult(NewGuest, REQUEST_CODE_CONTACTS)
//        }
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == GUESTCREATION && resultCode == RESULT_OK) {
//            val guestdb = GuestDBHelper(context!!)
//            // Data is exclusively taken from the local DB
//            val guestlist = guestdb.getAllGuests()
//            rvAdapter = Rv_GuestAdapter(guestlist, context!!)
//
//            recyclerViewAllGuests.adapter = null
//            recyclerViewAllGuests.adapter = rvAdapter
//            contactlist = clone(guestlist)!!
//
//            val swipeController = SwipeControllerTasks(
//                context!!,
//                rvAdapter,
//                recyclerViewAllGuests,
//                null,
//                RIGHTACTION
//            )
//            val itemTouchHelper = ItemTouchHelper(swipeController)
//            itemTouchHelper.attachToRecyclerView(recyclerViewAllGuests)
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (((requestCode == REQUEST_CODE_CONTACTS) || (requestCode == REQUEST_CODE_GUESTS)) && resultCode == Activity.RESULT_OK) {
//            //val guestarray = data?.getSerializableExtra("guests") as ArrayList<Guest>
//            try {
//                presenterguest = GuestsAllPresenter(requireContext(), this)
//            } catch (e: Exception) {
//                println(e.message)
//            }
//        }
//    }

//    @SuppressLint("NotifyDataSetChanged")
//    override fun onResume() {
//        super.onResume()
//        try {
//            presenterguest = GuestsAllPresenter(requireContext(), this)
//            presenterguest.getGuestList()
//        } catch (e: Exception) {
//            Log.e(TAG, e.message.toString())
//        }
////        recyclerViewActive.adapter = null
////        recyclerViewActive.adapter = rvAdapter
//    }

    // deal with the item yourself


    companion object {
        const val SCREEN_NAME = "Guest All"
        const val RIGHTACTION = "delete"
        var guestcreated_flag = 0
        const val TAG = "GuestsAll"
    }

    override fun onGuestFragmentWithData(guest: Guest) {
        val fragment = GuestCreateEdit()
        val bundle = Bundle()
        bundle.putParcelable("guest", guest)
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                fragment
            ) // R.id.fragment_container is the ID of the container where the fragment will be placed
            //.addToBackStack(null) // Add this transaction to the back stack, so the user can navigate back to the previous fragment
            .commit()
    }
}

interface GuestFragmentActionListener {
    fun onGuestFragmentWithData(guest: Guest)
}
