package com.bridesandgrooms.event

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.MVP.TableGuestsActivityPresenter
import com.bridesandgrooms.event.Model.MyFirebaseApp
import com.bridesandgrooms.event.Model.TableGuests
import com.bridesandgrooms.event.ui.ViewAnimation
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.onboardingcard.view.*
import kotlinx.android.synthetic.main.tableguestsactivity.*
import kotlinx.android.synthetic.main.tableguestsactivity.view.*
import kotlinx.android.synthetic.main.tableguestsactivity.view.adView
import kotlin.collections.ArrayList

class TableGuestsActivity : Fragment(), TableGuestsActivityPresenter.TableGuestList {

    private lateinit var recyclerViewActivity: RecyclerView
    private lateinit var presenterguest: TableGuestsActivityPresenter
    private var isRotate = false

    private val REQUEST_CODE_CONTACTS = 1 //ContactsAll
    private val REQUEST_CODE_GUESTS = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val inf = inflater.inflate(R.layout.tableguestsactivity, container, false)

        val adRequest = AdRequest.Builder().build()
        inf.adView.loadAd(adRequest)

        recyclerViewActivity = inf.tableguestsparentrv
        recyclerViewActivity.apply {
            layoutManager = LinearLayoutManager(inf.context).apply {
                stackFromEnd = true
                reverseLayout = true
                isNestedScrollingEnabled = false
            }
        }

        try {
            presenterguest = TableGuestsActivityPresenter(requireContext(), this)
        } catch (e: Exception) {
            println(e.message)
        }

        ViewAnimation.init(inf.NewGuestTG)
        ViewAnimation.init(inf.ContactGuestTG)

        inf.floatingActionButtonGuestTG.setOnClickListener()
        {
            isRotate = ViewAnimation.rotateFab(inf.floatingActionButtonGuestTG, !isRotate)
            if (isRotate) {
                ViewAnimation.showIn(NewGuestTG)
                ViewAnimation.showIn(ContactGuestTG)
            } else {
                ViewAnimation.showOut(NewGuestTG)
                ViewAnimation.showOut(ContactGuestTG)
            }
        }

        inf.fabNewGuestTG.setOnClickListener {
            val newguest = Intent(context, GuestCreateEdit::class.java)
            newguest.putExtra("userid", "")
            startActivityForResult(newguest, REQUEST_CODE_GUESTS)
        }

        inf.fabContactGuestTG.setOnClickListener {
            val newguest = Intent(context, ContactsAll::class.java)
            newguest.putExtra("guestid", "")
            startActivityForResult(newguest, REQUEST_CODE_CONTACTS)
        }

        inf.adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                inf.adView.isVisible = true
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
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
        return inf
    }

    override fun onTableGuestList(list: ArrayList<TableGuests>) {
        val rvAdapter = Rv_TableGuestsAdapter(list)
        recyclerViewActivity.adapter = null
        recyclerViewActivity.adapter = rvAdapter
    }

    override fun onTableGuestListError(errcode: String) {
        withdata.visibility = View.GONE
        withnodata.visibility = View.VISIBLE
        withnodata.onboardingmessage.text = getString(R.string.emptystate_noguestsmsg)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        try {
            presenterguest = TableGuestsActivityPresenter(requireContext(), this)
        } catch (e: Exception) {
            println(e.message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (((requestCode == REQUEST_CODE_CONTACTS) || (requestCode == REQUEST_CODE_GUESTS)) && resultCode == Activity.RESULT_OK) {
            //val guestarray = data?.getSerializableExtra("guests") as ArrayList<Guest>
            try {
                presenterguest = TableGuestsActivityPresenter(requireContext(), this)
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}

