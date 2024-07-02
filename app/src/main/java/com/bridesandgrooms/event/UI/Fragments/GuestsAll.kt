package com.bridesandgrooms.event.UI.Fragments

import Application.AnalyticsManager
import android.annotation.SuppressLint
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
import android.content.Context
import android.util.Log
import android.widget.TextView
import com.bridesandgrooms.event.UI.Adapters.GuestAdapter
import com.bridesandgrooms.event.Model.contactGuest
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.databinding.GuestsAllBinding
import com.bridesandgrooms.event.UI.ViewAnimation
import com.google.android.material.appbar.MaterialToolbar
import java.util.*
import kotlin.collections.ArrayList

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
        val addVendor = menu.findItem(R.id.add_vendor)
        addVendor.isVisible = false

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
                rvAdapter = GuestAdapter(this@GuestsAll, filteredModelList, mContext!!)
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

    /**
     * Callback that loads a recyclerview with a list of Guests when they are successfully retrieved from the Backend
     */
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
            list.sortedBy {it.table}

            try {
                rvAdapter = GuestAdapter(this, guestList, mContext!!)
                rvAdapter.notifyDataSetChanged()
            } catch (e: java.lang.Exception) {
                Log.e(TAG, e.message.toString())
            }

            recyclerViewAllGuests.adapter = null
            recyclerViewAllGuests.adapter = rvAdapter

            inf.withdata.visibility = ConstraintLayout.VISIBLE
            val emptystateLayout = inf.withnodata
            emptystateLayout.root.visibility = ConstraintLayout.GONE
            //----------------------------------------------------------------
        }
    }

    /**
     * Callback that loads an emptystate fragment whenever the app cannot retrieve Guests, in this case we are assuming that's because there are none. The fragment allows the user to add new Vendors
     */
    override fun onGAGuestsError(errcode: String) {
        val message = getString(R.string.emptystate_novendorsmsg)
        val cta = getString(R.string.emptystate_novendorscta)
        val actionClass =
            GuestCreateEdit::class.java
        val fragment = EmptyStateFragment.newInstance(message, cta, actionClass)
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
            ?.commit()
    }

    companion object {
        const val SCREEN_NAME = "Guest All"
        const val TAG = "GuestsAll"
    }

    /**
     * Whenever a Guest selection is made and the User clicks on the ViewHolder in the RecyclerView this function will be called in GuestsAll to open the Guest edition fragment
     */
    override fun onGuestFragmentWithData(guest: Guest) {
        val fragment = GuestCreateEdit()
        val bundle = Bundle()
        bundle.putParcelable("guest", guest)
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                fragment
            )
            .commit()
    }
}

interface GuestFragmentActionListener {
    fun onGuestFragmentWithData(guest: Guest)
}
