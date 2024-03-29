package com.bridesandgrooms.event

import Application.AnalyticsManager
import Application.GuestDeletionException
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.deleteGuest
import com.bridesandgrooms.event.Model.Guest
import com.bridesandgrooms.event.Model.PaymentModel
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.UI.ItemTouchAdapterAction
import com.bridesandgrooms.event.UI.LetterAvatar
import com.google.android.material.snackbar.Snackbar
import com.redmadrobot.acronymavatar.AvatarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Rv_GuestAdapter(
    private val contactlist: ArrayList<Guest>, val context: Context
) :
    RecyclerView.Adapter<Rv_GuestAdapter.ViewHolder>(), ItemTouchAdapterAction {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.guest_item_layout, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return contactlist.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.contactname.text = contactlist[p1].name
        p0.rsvp.text = when (contactlist[p1].rsvp) {
            "y" -> context.getString(R.string.yes)
            "n" -> context.getString(R.string.no)
            else -> context.getString(R.string.pending)
        }

        try {
            p0.contactavatar.apply {
                setImageDrawable(
                    LetterAvatar(
                        context,
                        context.getColor(R.color.azulmasClaro),
                        p0.contactname.text.toString().substring(0, 2),
                        10
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            p0.contactavatar.setImageResource(R.drawable.avatar2)
        }
        p0.companions.text = when (contactlist[p1].companion) {
            "adult" -> context.getString(R.string.adult)
            "child" -> context.getString(R.string.child)
            "baby" -> context.getString(R.string.baby)
            "none" -> context.getString(R.string.none)
            else -> context.getString(R.string.none)
        }

        p0.itemView.setOnClickListener {
            val guestdetail = Intent(context, GuestCreateEdit::class.java)
            guestdetail.putExtra("guest", contactlist[p1])
            context.startActivity(guestdetail)
            AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME,"Guest_Detail")
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactname = itemView.findViewById<TextView>(R.id.contactname)!!
        val contactavatar = itemView.findViewById<AvatarView>(R.id.contactavatar)!!
        val rsvp = itemView.findViewById<TextView>(R.id.rsvp)!!
        val companions = itemView.findViewById<TextView>(R.id.companions)!!
    }

    override fun onItemSwiftLeft(context: Context, position: Int, recyclerView: RecyclerView, action: String) {
        TODO("Not yet implemented")
    }

    @SuppressLint("ShowToast")
    override fun onItemSwiftRight(context: Context, position: Int, recyclerView: RecyclerView, action: String) {
        AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME,"Delete Guest")
        val user = User().getUser(context)

        val guestswift = contactlist[position]
        val guestbackup = Guest().apply {
            rsvp = contactlist[position].rsvp
            companion = contactlist[position].companion
            table = contactlist[position].table
            key = contactlist[position].key
            name = contactlist[position].name
            phone = contactlist[position].phone
            email = contactlist[position].email
        }

        if (action == DELETEACTION) {
                contactlist.removeAt(position)
                notifyItemRemoved(position)
                try {
                    deleteGuest(context, user, guestswift)
                } catch (e: GuestDeletionException) {
                    AnalyticsManager.getInstance().trackError(
                        GuestCreateEdit.SCREEN_NAME,
                        e.message.toString(),
                        "deleteGuest()",
                        e.stackTraceToString()
                    )
                    Log.e(TAG, e.message.toString())
            }

            Snackbar.make(recyclerView, "Guest deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    contactlist.add(guestbackup)
                    notifyItemInserted(contactlist.lastIndex)
                    //addGuest(context, guestbackup, CALLER)
                }
                .show()
        }
    }

    companion object {
        const val DELETEACTION = "delete"
        const val TAG = "Rv_GuestAdapter"
        const val SCREEN_NAME = "RvGuestAdapter"
    }
}

