package com.bridesandgrooms.event


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bridesandgrooms.event.Functions.clone
import com.bridesandgrooms.event.MVP.GuestsAllPresenter
import com.bridesandgrooms.event.Model.Guest
import com.bridesandgrooms.event.Model.MyFirebaseApp
import com.bridesandgrooms.event.ui.ViewAnimation
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.guests_all.*
import kotlinx.android.synthetic.main.guests_all.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.onboardingcard.view.*


class GuestsAll : Fragment(), GuestsAllPresenter.GAGuests {

    private var contactlist = ArrayList<Guest>()
    private var isRotate = false

    private lateinit var recyclerViewAllGuests: RecyclerView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var presenterguest: GuestsAllPresenter
    private lateinit var inf: View
    private lateinit var rvAdapter: Rv_GuestAdapter
    private lateinit var swipeController: SwipeControllerTasks

    private val REQUEST_CODE_CONTACTS = 1 //ContactsAll
    private val REQUEST_CODE_GUESTS = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = requireActivity().findViewById(R.id.toolbar)
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

        searchView.setOnSearchClickListener {
        }

        searchView.setOnCloseListener {
            toolbar.collapseActionView()
            true
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val filteredModelList = filter(contactlist, p0)
                val rvAdapter = Rv_GuestAdapter(filteredModelList as ArrayList<Guest>, context!!)
                recyclerViewAllGuests.adapter = rvAdapter
                return true
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        inf = inflater.inflate(R.layout.guests_all, container, false)

        val pulltoRefresh = inf.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)

        pulltoRefresh.setOnRefreshListener {
            try {
                presenterguest = GuestsAllPresenter(requireContext(), this)
            } catch (e: Exception) {
                println(e.message)
            }
            pullToRefresh.isRefreshing = false
        }

        recyclerViewAllGuests = inf.recyclerViewGuests
        recyclerViewAllGuests.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }

//        val swipeController = SwipeControllerTasks(
//            inf.context,
//            rvAdapter,
//            recyclerViewAllGuests,
//            null,
//            RIGHTACTION
//        )
//        val itemTouchHelper = ItemTouchHelper(swipeController)
//        itemTouchHelper.attachToRecyclerView(recyclerViewAllGuests)

        try {
            presenterguest = GuestsAllPresenter(requireContext(), this)
        } catch (e: Exception) {
            println(e.message)
        }

        ViewAnimation.init(inf.NewGuest)
        ViewAnimation.init(inf.ContactGuest)

        inf.floatingActionButtonGuest.setOnClickListener()
        {
            isRotate = ViewAnimation.rotateFab(inf.floatingActionButtonGuest, !isRotate)
            if (isRotate) {
                ViewAnimation.showIn(NewGuest)
                ViewAnimation.showIn(ContactGuest)
            } else {
                ViewAnimation.showOut(NewGuest)
                ViewAnimation.showOut(ContactGuest)
            }
        }

        inf.fabNewGuest.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "NEWGUEST")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------

            val newguest = Intent(context, GuestCreateEdit::class.java)
            newguest.putExtra("userid", "")

            startActivityForResult(newguest, REQUEST_CODE_GUESTS)
        }

        inf.fabContactGuest.setOnClickListener {
            // ------- Analytics call ----------------
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "GUESTFROMCONTACTS")
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, javaClass.simpleName)
            MyFirebaseApp.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            //----------------------------------------

            val newguest = Intent(context, ContactsAll::class.java)
            newguest.putExtra("guestid", "")
            //startActivity(newguest)

            startActivityForResult(newguest, REQUEST_CODE_CONTACTS)
        }
        return inf
    }

//    override fun onResume() {
//        super.onResume()
//////Just want to enter here after a new guest was added not every time. I don't like this.
//
//        if (guestcreated_flag == 1){
////            presenterguest = GuestsAllPresenter(context!!, this, inf)
//
//            val guestdb = GuestDBHelper(requireContext())
//            val guestlist = guestdb.getAllGuests()
//
//            rvAdapter = Rv_GuestAdapter(guestlist, requireContext())
//
////            recyclerViewAllGuests.adapter = null
//            recyclerViewAllGuests.adapter = rvAdapter
//            contactlist = clone(guestlist)
//
////            swipeController = SwipeControllerTasks(
////                context!!,
////                rvAdapter,
////                recyclerViewAllGuests,
////                null,
////                RIGHTACTION
////            )
////            val itemTouchHelper = ItemTouchHelper(swipeController)
////            itemTouchHelper.attachToRecyclerView(recyclerViewAllGuests)
//
//            guestcreated_flag = 0
//        }
//
//    }

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
        //inflatedView: View,
        list: ArrayList<Guest>
    ) {
//        recyclerViewAllGuests = inflatedView.recyclerViewGuests
//        recyclerViewAllGuests.apply {
//            layoutManager = LinearLayoutManager(inflatedView.context).apply {
//                stackFromEnd = true
//                reverseLayout = true
//            }
//        }
        rvAdapter = Rv_GuestAdapter(list, requireContext())
        rvAdapter.notifyDataSetChanged()
        //rvAdapter.notifyDataSetChanged()

        recyclerViewAllGuests.adapter = null
        recyclerViewAllGuests.adapter = rvAdapter
        contactlist = clone(list)

//        swipeController = SwipeControllerTasks(
//            inflatedView.context,
//            rvAdapter,
//            recyclerViewAllGuests,
//            null,
//            RIGHTACTION
//        )
//        val itemTouchHelper = ItemTouchHelper(swipeController)
//        itemTouchHelper.attachToRecyclerView(recyclerViewAllGuests)
    }

    override fun onGAGuestsError(errcode: String) {
        withdata.visibility = ConstraintLayout.GONE
        withnodata.visibility = ConstraintLayout.VISIBLE
        withnodata.onboardingmessage.text = getString(R.string.emptystate_noguestsmsg)
    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (((requestCode == REQUEST_CODE_CONTACTS) || (requestCode == REQUEST_CODE_GUESTS)) && resultCode == Activity.RESULT_OK) {
            //val guestarray = data?.getSerializableExtra("guests") as ArrayList<Guest>
            try {
                presenterguest = GuestsAllPresenter(requireContext(), this)
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        try {
            presenterguest = GuestsAllPresenter(requireContext(), this)
        } catch (e: Exception) {
            println(e.message)
        }
//        recyclerViewActive.adapter = null
//        recyclerViewActive.adapter = rvAdapter
    }

    // deal with the item yourself


    companion object {
        const val RIGHTACTION = "delete"
        var guestcreated_flag = 0
    }
}
