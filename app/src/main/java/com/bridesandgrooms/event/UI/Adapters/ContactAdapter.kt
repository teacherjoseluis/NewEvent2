package com.bridesandgrooms.event.UI.Adapters

import Application.AnalyticsManager
import Application.GuestCreationException
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
import com.bridesandgrooms.event.Functions.addGuest
import com.bridesandgrooms.event.LoginView
import com.bridesandgrooms.event.LoginView.Companion
import com.bridesandgrooms.event.Model.Contact
import com.bridesandgrooms.event.Model.Guest
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.ClearSelected
import com.bridesandgrooms.event.UI.Fragments.ContactsAllFragmentActionListener
import com.bridesandgrooms.event.UI.LetterAvatar
import com.bridesandgrooms.event.UI.OnItemClickListener

class ContactAdapter(
    private val fragmentActionListener: ContactsAllFragmentActionListener,
    private val contactlist: ArrayList<Contact>,
    val context: Context
) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(), ClearSelected {

    private val selected: ArrayList<Int> = ArrayList()
    private lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_item_layout, parent, false)
        return ContactViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactlist.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactlist[position]
        holder.bind(contact)
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val customContactCardView: ConstraintLayout = itemView.findViewById(R.id.cardLayout)
        private val contactImageView: ImageView = itemView.findViewById(R.id.contactImageView)
        private var currentContact: Contact? = null

        init {
            customContactCardView.setOnClickListener {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "customContactCardView", "click")
                handleClick()
            }
        }

        private fun handleClick() {
            AnalyticsManager.getInstance().trackNavigationEvent(SCREEN_NAME, "AddContact_Guest")
            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.add_as_a_guest))
                .setMessage(context.getString(R.string.addguest_confirmation))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    currentContact?.let { contact ->
                        //val user = User().getUser()
                        val newGuest = Guest().contacttoGuest(context, contact.key)
                        try {
                            addGuest(newGuest)
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
                .show()
        }

        fun bind(contact: Contact) {
            currentContact = contact
            contactImageView.apply {
                val textColor =
                    ContextCompat.getColor(context, R.color.OnSecondaryContainer_cream)
                val circleColor =
                    ContextCompat.getColor(context, R.color.SecondaryContainer_cream)
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
            view.findViewById<TextView>(R.id.contactName).text = contact.name
        }
    }

    fun updateData(newItems: List<Contact>) {
        contactlist.clear()
        contactlist.addAll(newItems)
        notifyDataSetChanged()
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






