package com.bridesandgrooms.event.UI.Adapters

import Application.AnalyticsManager
import Application.GuestCreationException
import Application.VendorCreationException
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.addGuest
import com.bridesandgrooms.event.Functions.addVendor
import com.bridesandgrooms.event.Model.Contact
import com.bridesandgrooms.event.Model.Guest
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.Model.Vendor
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.ClearSelected
import com.bridesandgrooms.event.UI.Fragments.ContactsAllFragmentActionListener
import com.bridesandgrooms.event.UI.Fragments.FragmentActionListener
import com.bridesandgrooms.event.UI.LetterAvatar
import com.bridesandgrooms.event.databinding.ContactItemLayoutBinding
import com.bridesandgrooms.event.UI.OnItemClickListener
import com.google.android.libraries.places.api.model.Place

class ContactAdapter(
    private val fragmentActionListener: ContactsAllFragmentActionListener,
    private val contactlist: ArrayList<Contact>,
    val context: Context
) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(), ClearSelected {

    private val selected: ArrayList<Int> = ArrayList()
    private lateinit var view: View

    var mOnItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_item_layout, parent, false)
        return ContactViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactlist.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
//        val checkDrawable = ContextCompat.getDrawable(context, R.drawable.icons8_checkmark)!!
//        checkDrawable.setTint(Color.parseColor("#C2185B"))
        val contact = contactlist[position]
        holder.bind(contact)

//        p0.contactname.text = contactlist[p1].name
//        p0.contactavatar.apply {
//            isSelected = (selectedPos == p1)
//            //Get an avatar of the contacts name if it's not selected
//            if (!selected.contains(p1)) {
//                setImageDrawable(
//                    LetterAvatar(
//                        context,
//                        context.getColor(R.color.white),
//                        context.getColor(R.color.magentaHaze),
//                        p0.contactname.text.toString().substring(0, 2),
//                        10
//                    )
//                )
//            } else {
//                //If it's previously selected show a checkmark
//                setImageDrawable(checkDrawable)
//            }
//
//            setOnClickListener {
//                //If a previously checked contact is clicked on again, then show the avatar
//                if (selected.contains(p1)) {
//                    selected.remove(p1)
//                    setImageDrawable(
//                        LetterAvatar(
//                            context,
//                            context.getColor(R.color.azulmasClaro),
//                            context.getColor(R.color.magentaHaze),
//                            p0.contactname.text.toString().substring(0, 2),
//                            10
//                        )
//                    )
//                } else {
//                    selected.add(p1)
//                    setImageDrawable(checkDrawable)
//                }
//                selectedPos = p0.layoutPosition
//                notifyItemChanged(selectedPos)
//
//                mOnItemClickListener?.onItemClick(p1, selected)
//            }
//        }
    }


    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
//class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val customContactCardView: ConstraintLayout = itemView.findViewById(R.id.cardLayout)
        private val contactImageView: ImageView = itemView.findViewById(R.id.contactImageView)
        private var currentGuest: Contact? = null

        init {
            customContactCardView.setOnClickListener {
                handleClick()
            }
        }

        private fun handleClick() {
            AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME, "AddContact_Guest")
            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.add_as_a_guest))
                .setMessage(context.getString(R.string.addguest_confirmation))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    currentGuest?.let { contact ->
                        val user = User().getUser(context)
//                        val newGuest = Guest().apply {
//                            name = contact.name ?: ""
//                            phone = contact.phone ?: ""
//                            email = contact.email ?: ""
//                        }
                        val newGuest = Guest().contacttoGuest(context, contact.key)
                        try {
                            addGuest(context, user, newGuest)
                        } catch (e: GuestCreationException) {
                            AnalyticsManager.getInstance().trackError(
                                SCREEN_NAME,
                                e.message.toString(),
                                "addGuest()",
                                e.stackTraceToString()
                            )
                            Log.e(TAG, e.message.toString())
                        }
                        fragmentActionListener.onContactAdded(newGuest)
                    }
                }
                .setNegativeButton(android.R.string.no, null)
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }

        fun bind(contact: Contact) {
            currentGuest = contact
            contactImageView.apply {
                val textColor =
                    ContextCompat.getColor(context, R.color.OnSecondaryContainer)
                val circleColor =
                    ContextCompat.getColor(context, R.color.SecondaryContainer)
                val density = context.resources.displayMetrics.density
                setImageDrawable(
                    LetterAvatar(
                        textColor,
                        circleColor,
                        contact.name.substring(0, 2),
                        10,
                        density
                    )
                )
            }

            // Set common fields
            view.findViewById<TextView>(R.id.contactName).text = contact.name
        }


    }
    companion object {
        const val TAG = "ContactAdapter"
        const val SCREEN_NAME = "Contact Adapter"
    }

    override fun onClearSelected(): Boolean {
        selected.clear()
        notifyDataSetChanged()
        return true
    }
}






