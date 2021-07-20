package com.example.newevent2


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.newevent2.Functions.clone
import com.example.newevent2.MVP.GuestsAllPresenter
import com.example.newevent2.Model.Guest
import com.example.newevent2.Model.GuestDBHelper
import com.example.newevent2.ui.ViewAnimation
import kotlinx.android.synthetic.main.guests_all.*
import kotlinx.android.synthetic.main.guests_all.view.*
import kotlin.collections.ArrayList

class GuestsAll : Fragment(), GuestsAllPresenter.GAGuests {

    private var contactlist = ArrayList<Guest>()
    private var isRotate = false

    private lateinit var recyclerViewAllGuests: RecyclerView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var presenterguest: GuestsAllPresenter
    private lateinit var inf: View
    private lateinit var rvAdapter: Rv_GuestAdapter
    private lateinit var swipeController: SwipeControllerTasks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = activity!!.findViewById(R.id.toolbar)
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
    ): View? {

        inf = inflater.inflate(R.layout.guests_all, container, false)

//        val pulltoRefresh = inf.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)

//        pulltoRefresh.setOnRefreshListener {
//            presenterguest = GuestsAllPresenter(context!!, this, inf)
//            pullToRefresh.isRefreshing = false
//        }

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

        presenterguest = GuestsAllPresenter(context!!, this, inf)

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
            val newguest = Intent(context, GuestCreateEdit::class.java)
            newguest.putExtra("userid", "")
            startActivity(newguest)
        }

        inf.fabContactGuest.setOnClickListener {
            val newguest = Intent(context, ContactsAll::class.java)
            newguest.putExtra("userid", "")
            startActivity(newguest)
        }
        return inf
    }

    override fun onResume() {
        super.onResume()
////Just want to enter here after a new guest was added not every time. I don't like this.

        if (GuestsAll.guestcreated_flag == 1){
//            presenterguest = GuestsAllPresenter(context!!, this, inf)

            val guestdb = GuestDBHelper(context!!)
            val guestlist = guestdb.getAllGuests()

            rvAdapter = Rv_GuestAdapter(guestlist, context!!)

//            recyclerViewAllGuests.adapter = null
            recyclerViewAllGuests.adapter = rvAdapter
            contactlist = clone(guestlist)!!

//            swipeController = SwipeControllerTasks(
//                context!!,
//                rvAdapter,
//                recyclerViewAllGuests,
//                null,
//                RIGHTACTION
//            )
//            val itemTouchHelper = ItemTouchHelper(swipeController)
//            itemTouchHelper.attachToRecyclerView(recyclerViewAllGuests)

            GuestsAll.guestcreated_flag = 0
        }

    }

    private fun filter(models: ArrayList<Guest>, query: String?): List<Guest> {
        val lowerCaseQuery = query!!.toLowerCase()
        val filteredModelList: ArrayList<Guest> = ArrayList()
        for (model in models) {
            val text: String = model.name.toLowerCase()
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model)
            }
        }
        return filteredModelList
    }

    override fun onGAGuests(
        inflatedView: View,
        list: ArrayList<Guest>
    ) {
//        recyclerViewAllGuests = inflatedView.recyclerViewGuests
//        recyclerViewAllGuests.apply {
//            layoutManager = LinearLayoutManager(inflatedView.context).apply {
//                stackFromEnd = true
//                reverseLayout = true
//            }
//        }
        rvAdapter = Rv_GuestAdapter(list, context!!)

        recyclerViewAllGuests.adapter = rvAdapter
        contactlist = clone(list)!!

        swipeController = SwipeControllerTasks(
            inflatedView.context,
            rvAdapter,
            recyclerViewAllGuests,
            null,
            RIGHTACTION
        )
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(recyclerViewAllGuests)
    }

    override fun onGAGuestsError(inflatedView: View, errcode: String) {
        inflatedView.withdata.visibility = ConstraintLayout.GONE
        inflatedView.withnodata.visibility = ConstraintLayout.VISIBLE
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

    companion object {
        const val RIGHTACTION = "delete"
        internal val GUESTCREATION = 1
        var guestcreated_flag = 0
    }
}