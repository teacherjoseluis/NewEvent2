package com.example.newevent2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Functions.addGuest
import com.example.newevent2.Functions.deleteGuest
import com.example.newevent2.Model.Guest
import com.google.android.material.snackbar.Snackbar
import com.redmadrobot.acronymavatar.AvatarView

class Rv_GuestAdapter(
    private val contactlist: ArrayList<Guest>, val context: Context
) :
    RecyclerView.Adapter<Rv_GuestAdapter.ViewHolder>(), ItemTouchAdapterAction {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        // Instantiates a layout XML file into its corresponding View objects
        val v = LayoutInflater.from(p0.context).inflate(R.layout.guest_item_layout, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return contactlist.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.contactname.text = contactlist[p1].name
        p0.rsvp.text = when (contactlist[p1].rsvp) {
            "y" -> "Yes"
            "n" -> "No"
            else -> "Pending"
        }
//        try {
//            p0.contactavatar.setImageDrawable(
//                LetterAvatar(
//                    context,
//                    context.getColor(R.color.azulmasClaro),
//                    p0.contactname.text.toString().substring(0, 2),
//                    10
//                )
//            )
//        } catch (e: Exception) {
//            p0.contactavatar.setImageResource(R.drawable.avatar2)
//        }
        try {
            p0.contactavatar.setText(p0.contactname.text.toString())

        } catch (e: Exception) {
            p0.contactavatar.setImageResource(R.drawable.avatar2)
        }
        p0.companions.text = contactlist[p1].companion
//        p0.table.text = contactlist[p1].table

        p0.itemView.setOnClickListener {
            val guestdetail = Intent(context, GuestCreateEdit::class.java)
            guestdetail.putExtra("guest", contactlist[p1])
            context.startActivity(guestdetail)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactname = itemView.findViewById<TextView>(R.id.contactname)!!
        val contactavatar = itemView.findViewById<AvatarView>(R.id.contactavatar)!!
        val rsvp = itemView.findViewById<TextView>(R.id.rsvp)!!
        val companions = itemView.findViewById<TextView>(R.id.companions)!!
//        val table = itemView.findViewById<TextView>(R.id.table)!!
    }

    override fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView, action: String) {
        TODO("Not yet implemented")
    }

    @SuppressLint("ShowToast")
    override fun onItemSwiftRight(position: Int, recyclerView: RecyclerView, action: String) {
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
            deleteGuest(context, guestswift)

            Snackbar.make(recyclerView, "Guest deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    contactlist.add(guestbackup)
                    notifyItemInserted(contactlist.lastIndex)
                    addGuest(context, guestbackup, CALLER)
                }
                .show()
        }
    }

    companion object {
        const val DELETEACTION = "delete"
        const val TAG = "Rv_GuestAdapter"
        const val CALLER = "none"
    }
}

