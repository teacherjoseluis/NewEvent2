package com.example.newevent2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar

class Rv_GuestAdapter(
    val contactlist: MutableList<Guest>
) :
    RecyclerView.Adapter<Rv_GuestAdapter.ViewHolder>(), ItemTouchAdapterAction {

    lateinit var context: Context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        // Instantiates a layout XML file into its corresponding View objects
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.guest_item_layout, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return contactlist.size
    }

    // public abstract void onBindViewHolder (VH holder, int position)

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.contactname.text = contactlist[p1].name
        if (contactlist[p1].imageurl.isNotEmpty()) {
            Glide.with(p0.itemView.context)
                .load(contactlist[p1].imageurl)
                .circleCrop()
                .into(p0.contactavatar)
        }
        p0.rsvp.text = contactlist[p1].rsvp

        p0.itemView.setOnClickListener {
            val guestdetail = Intent(context, Guest_EditDetail::class.java)
            guestdetail.putExtra("contactid", contactlist[p1].contactid)
            guestdetail.putExtra("rsvp", contactlist[p1].rsvp)
            guestdetail.putExtra("companion", contactlist[p1].companion)
            guestdetail.putExtra("table", contactlist[p1].table)
            guestdetail.putExtra("key", contactlist[p1].key)

            guestdetail.putExtra("name", contactlist[p1].name)
            guestdetail.putExtra("imageurl", contactlist[p1].imageurl)
            guestdetail.putExtra("phone", contactlist[p1].phone)
            guestdetail.putExtra("email", contactlist[p1].email)

            guestdetail.putExtra("eventid", contactlist[p1].eventid)
            context.startActivity(guestdetail)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactname = itemView.findViewById<TextView>(R.id.contactname)!!
        val contactavatar = itemView.findViewById<ImageView>(R.id.contactavatar)!!
        val rsvp = itemView.findViewById<TextView>(R.id.rsvp)!!
    }

    override fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView, action: String) {
        TODO("Not yet implemented")
    }

    override fun onItemSwiftRight(position: Int, recyclerView: RecyclerView, action: String) {
        val guest = GuestEntity().apply {
            eventid = contactlist[position].eventid
            contactid = contactlist[position].contactid
            rsvp = contactlist[position].rsvp
            companion = contactlist[position].companion
            table = contactlist[position].table
            key = contactlist[position].key

            name = contactlist[position].name
            imageurl = contactlist[position].imageurl
            phone = contactlist[position].phone
            email = contactlist[position].email
        }

        contactlist.removeAt(position)
        notifyItemRemoved(position)

        if (action == "delete") {
            guest.deleteGuest(context)

            Snackbar.make(recyclerView, "Guest deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    contactlist.add(guest)
                    notifyItemInserted(contactlist.lastIndex)
                    guest.addGuest(context)
                }.show()
        }
    }
}

