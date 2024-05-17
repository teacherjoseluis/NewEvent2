package com.bridesandgrooms.event.UI.Adapters

import Application.AnalyticsManager
import Application.VendorCreationException
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.FragmentActionListener
import com.bridesandgrooms.event.Functions.Google.PlacesSearchService
import com.bridesandgrooms.event.Functions.addVendor
import com.bridesandgrooms.event.Functions.sumStrings
import com.bridesandgrooms.event.GlideApp
import com.bridesandgrooms.event.Model.PaymentDBHelper
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.Model.Vendor
import com.bridesandgrooms.event.Model.VendorPayment
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.Rv_PaymentAdapter3
import com.bridesandgrooms.event.UI.Fragments.SearchVendorFragment
import com.bridesandgrooms.event.UI.ItemTouchAdapterAction
import com.bridesandgrooms.event.UI.LetterAvatar
import com.bridesandgrooms.event.VendorCreateEdit
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.floor
import java.lang.Math.sin
import java.lang.Math.sqrt
import kotlin.math.pow

class SearchVendorAdapter(
    private val contactlist: List<Pair<Place, String>>,
    private val category: String,
    val context: Context
) :
    RecyclerView.Adapter<SearchVendorAdapter.VendorViewHolder>() {

    private lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorViewHolder {
        view = LayoutInflater.from(parent.context)
            .inflate(R.layout.searchvendor_cardview, parent, false)
        return VendorViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactlist.size
    }

    override fun onBindViewHolder(holder: VendorViewHolder, position: Int) {
        val searchVendor = contactlist[position].first
        val distance = contactlist[position].second
        holder.bind(searchVendor, distance)
    }

    inner class VendorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val customVendorCardView: ConstraintLayout = itemView.findViewById(R.id.cardLayout)
        private val vendorImageView: ImageView = itemView.findViewById(R.id.vendorImageView)
        private var currentVendor: Place? = null

        init {
            customVendorCardView.setOnClickListener {
                handleClick()
            }
        }

        private fun handleClick() {
            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.add_as_vendor))
                .setMessage(context.getString(R.string.addvendor_confirmation))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    currentVendor?.let { vendor ->
                        val user = User().getUser(context)
                        val newVendor = Vendor().apply {
                            name = vendor.name ?: ""
                            phone = vendor.phoneNumber ?: ""
                            category = this@SearchVendorAdapter.category
                            placeid = vendor.id ?: ""
                            location = vendor.address ?: ""
                        }
                        try {
                            addVendor(context, user, newVendor)
                        } catch (e: VendorCreationException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                e.message.toString(),
                                "addVendor()",
                                e.stackTraceToString()
                            )
                            Log.e(TAG, e.message.toString())
                        }
                    }
                }
                .setNegativeButton(android.R.string.no, null)
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }

        fun bind(searchVendor: Place, distance: String) {
            currentVendor = searchVendor
            vendorImageView.apply {
                val textColor =
                    ContextCompat.getColor(context, R.color.OnSecondaryContainer)
                val circleColor =
                    ContextCompat.getColor(context, R.color.SecondaryContainer)
                val density = context.resources.displayMetrics.density
                setImageDrawable(
                    LetterAvatar(
                        textColor,
                        circleColor,
                        searchVendor.name.substring(0, 2),
                        10,
                        density
                    )
                )
            }

            // Set common fields
            view.findViewById<TextView>(R.id.vendorName).text = searchVendor.name
            view.findViewById<TextView>(R.id.vendorCategory).text = category
            view.findViewById<TextView>(R.id.vendorAddress).text = searchVendor.address
            view.findViewById<TextView>(R.id.vendorPhone).text = searchVendor.phoneNumber
            view.findViewById<TextView>(R.id.vendorDistance).text = distance
        }
    }
    //        private fun handleClick() {
//            val position = adapterPosition
//            if (position != RecyclerView.NO_POSITION) {
//                val searchVendor = contactlist[position]
//                fragmentActionListener.onVendorFragmentWithData(searchVendor.name)
//            }
//        }

    companion object {
        const val TAG = "SearchVendorAdapter"
        const val SCREEN_NAME = "Search Vendor Fragment"
    }
}
