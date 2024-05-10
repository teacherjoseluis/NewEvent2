package com.bridesandgrooms.event.UI.Components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.bridesandgrooms.event.Model.Vendor
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.Adapters.VendorAdapter
import com.bridesandgrooms.event.VendorCreateEdit

class CustomVendorCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private var placeId: String = ""
    private var currentState: State = State.CONTRACTED

    var vendorName = ""
    var vendorAddress = ""
    var vendorPhone = ""
    var amountPaid = ""

    enum class State {
        CONTRACTED,
        EXPANDED
    }

    init {
        setOnLongClickListener {
            if (currentState == State.CONTRACTED) {
                handleClick()
            }
            true
        }
        setOnClickListener {
            toggle()
        }
    }

    fun setPlaceId(placeId: String) {
        this.placeId = placeId
    }

    fun toggle() {
        currentState = if (currentState == State.CONTRACTED) {
            setExpandedState()
            State.EXPANDED
        } else {
            setContractedState()
            State.CONTRACTED
        }
    }

    private fun setExpandedState() {
        currentState = State.EXPANDED
        removeAllViews()
//        val expandedView =
//            LayoutInflater.from(context).inflate(R.layout.vendor_cardview_expanded, this, false)
//        addView(expandedView)
//
//        // Set layout params
//        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
//        expandedView.layoutParams = layoutParams
//
//        val vendorNameTextView = expandedView.findViewById<TextView>(R.id.vendorName)
//        val vendorAddressTextView = expandedView.findViewById<TextView>(R.id.vendorAddress)
//        val vendorPhoneTextView = expandedView.findViewById<TextView>(R.id.vendorPhone)
//        val vendorAmountPaidTextView = expandedView.findViewById<TextView>(R.id.amountpaid)
//        // Bind data to views in contracted layout
//        vendorNameTextView.text = vendorName
//        vendorAddressTextView.text = vendorAddress
//        vendorPhoneTextView.text = vendorPhone
//        vendorAmountPaidTextView.text = amountPaid
    }

    private fun setContractedState() {
        currentState = State.CONTRACTED
        removeAllViews()
        val contractedView =
            LayoutInflater.from(context).inflate(R.layout.vendor_cardview_contracted, this, false)
        addView(contractedView)

        // Set layout params
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        contractedView.layoutParams = layoutParams

        val vendorNameTextView = contractedView.findViewById<TextView>(R.id.vendorName)
        val vendorAddressTextView = contractedView.findViewById<TextView>(R.id.vendorAddress)
        val vendorPhoneTextView = contractedView.findViewById<TextView>(R.id.vendorPhone)
        // Bind data to views in contracted layout
        vendorNameTextView.text = vendorName
        vendorAddressTextView.text = vendorAddress
        vendorPhoneTextView.text = vendorPhone

    }

    private fun handleClick() {
        if (placeId.isNotEmpty()) {
            // Start Google Maps intent
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data =
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=Google&query_place_id=$placeId")
                setPackage("com.google.android.apps.maps")
            }
            context.startActivity(intent)
        } else {
            // Start new activity
//            val intent = Intent(context, VendorCreateEdit::class.java)
//            context.startActivity(intent)
        }
    }

    private fun toggleLayout() {
        if (currentState == State.CONTRACTED) {
            setContractedState()
        } else {
            setExpandedState()
        }
    }
}
