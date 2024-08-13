package com.bridesandgrooms.event.UI.Fragments

import Application.AnalyticsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.*
import com.bridesandgrooms.event.MVP.ContactsAllPresenter
import com.bridesandgrooms.event.Model.Contact
import com.bridesandgrooms.event.Model.Guest
import com.bridesandgrooms.event.Model.Permission
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.TaskCreateEdit
import com.bridesandgrooms.event.UI.Adapters.ContactAdapter
import com.bridesandgrooms.event.databinding.ContactsAllBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.collections.ArrayList

class ContactsAll : Fragment(), ContactsAllPresenter.GAContacts, ContactsAllFragmentActionListener {



    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var presenterguest: ContactsAllPresenter
    private lateinit var rvAdapter: ContactAdapter
    private lateinit var recyclerViewContacts: RecyclerView
    private lateinit var inf: ContactsAllBinding

    private var mContext: Context? = null
    private var contactList = ArrayList<Contact>()

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
                val filteredModelList = filter(contactList, p0)
                val rvAdapter = ContactAdapter(this@ContactsAll, filteredModelList as ArrayList<Contact>, context!!)
                recyclerViewContacts.adapter = rvAdapter
                return true
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.findViewById<TextView>(R.id.appbartitle)?.text = getString(R.string.title_contacts)

        inf = DataBindingUtil.inflate(inflater, R.layout.contacts_all, container, false)

        recyclerViewContacts = inf.recyclerViewContacts
        recyclerViewContacts.apply {
            layoutManager = LinearLayoutManager(inf.root.context).apply {
                stackFromEnd = true
                reverseLayout = true
            }
        }
        if (!PermissionUtils.checkPermissions(mContext!!, "contact")) {
            val permissions = PermissionUtils.requestPermissionsList("contact")
            requestPermissions(permissions, PERMISSION_CODE)
        } else {
            try {
                presenterguest = ContactsAllPresenter(mContext!!, this)
                presenterguest.getContactsList()
            } catch (e: Exception) {
                AnalyticsManager.getInstance().trackError(
                    SCREEN_NAME,
                    e.message.toString(),
                    "ContactsAllPresenter",
                    e.stackTraceToString()
                )
                Log.e(TAG, e.message.toString())
            }
        }
        return inf.root
    }

    private fun filter(models: ArrayList<Contact>, query: String?): List<Contact> {
        val lowerCaseQuery = query!!.toLowerCase(Locale.ROOT)
        val filteredModelList: ArrayList<Contact> = ArrayList()
        for (model in models) {
            val text: String = model.name.toLowerCase(Locale.ROOT)
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model)
            }
        }
        return filteredModelList
    }

    /**
     * Callback that loads a recyclerview with a list of Contacts when they are successfully retrieved from the Backend
     */
    override fun onGAContacts(list: ArrayList<Contact>) {
        if (list.size != 0) {
            contactList = clone(list)

            try {
                rvAdapter = ContactAdapter(this, list, mContext!!)
                rvAdapter.notifyDataSetChanged()
            } catch (e: java.lang.Exception) {
                println(e.message)
            }

            recyclerViewContacts.adapter = null
            recyclerViewContacts.adapter = rvAdapter

            inf.withdata.visibility = ConstraintLayout.VISIBLE
            val emptystateLayout = inf.noContactsLayout
            emptystateLayout.visibility = ConstraintLayout.GONE

//            rvAdapter.mOnItemClickListener = object : OnItemClickListener {
//                @SuppressLint("SetTextI18n")
//                override fun onItemClick(index: Int, countselected: ArrayList<Int>) {
//                    AnalyticsManager.getInstance()
//                        .trackUserInteraction(SCREEN_NAME, "Add_Guest")
//                    if (countselected.size != 0) {
//                        apptitle.text = "${countselected.size} selected"
//                        activitymenu.findItem(R.id.add_guest).apply {
//                            isEnabled = true
//                            setOnMenuItemClickListener {
//                                when (it.itemId) {
//                                    R.id.add_guest -> {
//                                        apptitle.text = getString(R.string.title_contacts)
//                                        for (ind in countselected) {
//                                            val guest =
//                                                Guest().contacttoGuest(
//                                                    mContext!!,
//                                                    contactList[ind].key
//                                                )
//                                            guestList.add(guest)
//                                            //lifecycleScope.launch {
//                                            try {
//                                                addGuest(mContext!!, userSession, guest)
//                                            } catch (e: GuestCreationException) {
//                                                AnalyticsManager.getInstance()
//                                                    .trackError(
//                                                        SCREEN_NAME,
//                                                        e.message.toString(),
//                                                        "addGuest()",
//                                                        e.stackTraceToString()
//                                                    )
//                                                Log.e(TAG, e.message.toString())
//                                            }
//                                            //}
//                                        }
//                                        rvAdapter.onClearSelected()
//                                    }
//                                }
//                                true
//                            }
//                        }
//                    } else {
//                        //Disable the menu as nothing is selected
//                        apptitle.text = getString(R.string.title_contacts)
//                        activitymenu.findItem(R.id.add_guest).isEnabled = false
//                    }
//                }
//            }
        } else {
            emptyStateFragment()
        }
    }

    /**
     * Callback that loads an emptystate fragment whenever the app cannot retrieve Contacts, in this case we are assuming that's because there are none. The fragment allows the user to add new Contacts
     */
    override fun onGAContactsError(errcode: String) {
        emptyStateFragment()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    try {
                        presenterguest = ContactsAllPresenter(mContext!!, this)
                        presenterguest.getContactsList()
                    } catch (e: Exception) {
                        Log.e(TAG, e.message.toString())
                    }
                } else {
                    // Here goes what happens when the permission is not given
                    inf.permissions.root.visibility = ConstraintLayout.VISIBLE
                    val contactpermissions = Permission.getPermission("contact")
                    val resourceId = this.resources.getIdentifier(
                        contactpermissions.drawable, "drawable",
                        mContext!!.packageName
                    )
                    inf.permissions.root.findViewById<ImageView>(R.id.permissionicon)
                        .setImageResource(resourceId)

                    val language = this.resources.configuration.locales.get(0).language
                    val permissionwording = when (language) {
                        "en" -> contactpermissions.permission_wording_en
                        else -> contactpermissions.permission_wording_es
                    }
                    inf.permissions.root.findViewById<TextView>(R.id.permissionwording).text =
                        permissionwording

                    val openSettingsButton =
                        inf.permissions.root.findViewById<Button>(R.id.permissionsbutton)
                    openSettingsButton.setOnClickListener {
                        // Create an intent to open the app settings for your app
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val packageName = mContext!!.packageName
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri

                        // Start the intent
                        startActivity(intent)
                    }
                }
            }
        }
    }

    companion object {
        internal const val PERMISSION_CODE = 1001
        const val SCREEN_NAME = "Contacts_All"
        const val TAG = "ContactsAll"
    }

    /**
     * Whenever a Contact selection is made and the User clicks on the ViewHolder in the RecyclerView this function will be called in ContactsAll to open the Contact edition fragment
     */
    override fun onContactAdded(guest: Guest) {
        val fragment = GuestCreateEdit()
        val bundle = Bundle()
        bundle.putParcelable("guest", guest)
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // R.id.fragment_container is the ID of the container where the fragment will be placed
            .commit()
    }

    fun emptyStateFragment() {
        val container = inf.root as ViewGroup?
        container?.removeAllViews()
        val newView = layoutInflater.inflate(R.layout.empty_state_fragment, container, false)
        container?.addView(newView)

        newView.findViewById<TextView>(R.id.emptystate_message).setText(R.string.emptystate_nocontactsmsg)
        newView.findViewById<TextView>(R.id.emptystate_cta).visibility = View.INVISIBLE
        newView.findViewById<FloatingActionButton>(R.id.fab_action).visibility = View.INVISIBLE
    }
}

interface ContactsAllFragmentActionListener {
    fun onContactAdded(guest: Guest)
}

