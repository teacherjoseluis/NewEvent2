package com.bridesandgrooms.event.UI.Adapters

import Application.AnalyticsManager
import Application.VendorCreationException
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.addVendor
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.Model.Vendor
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.Adapters.PaymentAdapter.Companion
import com.bridesandgrooms.event.UI.Fragments.ContactsAllFragmentActionListener
import com.bridesandgrooms.event.UI.Fragments.SearchVendorFragmentActionListener
import com.bridesandgrooms.event.UI.LetterAvatar
import com.google.android.libraries.places.api.model.Place

/**
 * This Adapter displays the detail of Vendors found through Place SDK and gives the possibility to save those as providers for the event
 * @property contactlist Contains a list of Pairs of Place and the Distance to the User's location
 * @property category This is the category of Vendor to which the list of places relates to
 */
class SearchVendorAdapter(
    private val fragmentActionListener: SearchVendorFragmentActionListener,
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

    /**
     * This Viewholder class handles the click action on which the user will have the ability to add the vendor of his preference to his list within the app
     */
    inner class VendorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val customVendorCardView: ConstraintLayout = itemView.findViewById(R.id.cardLayout)
        private val vendorImageView: ImageView = itemView.findViewById(R.id.vendorImageView)
        private var currentVendor: Place? = null

        init {
            customVendorCardView.setOnClickListener {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "customVendorCardView", "click")
                handleClick()
            }
        }

        private fun handleClick() {
            AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME, "AddVendor_Search")
            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.add_as_vendor))
                .setMessage(context.getString(R.string.addvendor_confirmation))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    currentVendor?.let { vendor ->
                        //val user = User().getUser()
                        val newVendor = Vendor().apply {
                            name = vendor.name ?: ""
                            phone = vendor.phoneNumber ?: ""
                            category = this@SearchVendorAdapter.category
                            placeid = vendor.id ?: ""
                            location = vendor.address ?: ""
                        }
                        try {
                            addVendor(newVendor)
                        } catch (e: VendorCreationException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                e.message.toString(),
                                "addVendor()",
                                e.stackTraceToString()
                            )
                            Log.e(TAG, e.message.toString())
                        }
                        fragmentActionListener.onVendorAdded(newVendor)
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
                    ContextCompat.getColor(context, R.color.OnSecondaryContainer_cream)
                val circleColor =
                    ContextCompat.getColor(context, R.color.SecondaryContainer_cream)
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

    companion object {
        const val TAG = "SearchVendorAdapter"
        const val SCREEN_NAME = "Search Vendor Fragment"
    }
}
