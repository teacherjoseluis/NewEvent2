package com.example.newevent2

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newevent2.Functions.addGuest
import com.example.newevent2.Functions.addVendor
import com.example.newevent2.Functions.deleteGuest
import com.example.newevent2.Functions.deleteVendor
import com.example.newevent2.Model.Vendor
import com.example.newevent2.ui.LetterAvatar
import com.google.android.material.snackbar.Snackbar
import com.redmadrobot.acronymavatar.AvatarView
import java.lang.Exception


class Rv_VendorAdapter(val contactlist: ArrayList<Vendor>, val context: Context) :
    RecyclerView.Adapter<Rv_VendorAdapter.ViewHolder>(), ItemTouchAdapterAction {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        // Instantiates a layout XML file into its corresponding View objects
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.vendor_item_layout, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return contactlist.size
    }

    // public abstract void onBindViewHolder (VH holder, int position)

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.contactname.text = contactlist[p1].name

        try {
//            p0.contactavatar.setImageDrawable(
//                LetterAvatar(
//                    context,
//                    context.getColor(R.color.azulmasClaro),
//                    p0.contactname.text.toString().substring(0, 2),
//                    10
//                )
//            )
            p0.contactavatar.setText(p0.contactname.text.toString())

        } catch (e: Exception) {
            p0.contactavatar.setImageResource(R.drawable.avatar2)
        }

        p0.itemView.setOnClickListener {
            val vendordetail = Intent(context, VendorCreateEdit::class.java)
            vendordetail.putExtra("vendor", contactlist[p1])
            context.startActivity(vendordetail)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactname = itemView.findViewById<TextView>(R.id.contactname)!!
        val contactavatar = itemView.findViewById<AvatarView>(R.id.contactavatar)!!
    }

    override fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView, action: String) {
        TODO("Not yet implemented")
    }

    override fun onItemSwiftRight(position: Int, recyclerView: RecyclerView, action: String) {
        val vendorswift = contactlist[position]
        val vendorbackup = Vendor().apply {
            key = contactlist[position].key
            name = contactlist[position].name
            phone = contactlist[position].phone
            email = contactlist[position].email
            category = contactlist[position].category
            eventid = contactlist[position].eventid
            placeid = contactlist[position].placeid
            location = contactlist[position].location
        }

        if (action == DELETEACTION) {
            contactlist.removeAt(position)
            notifyItemRemoved(position)
            deleteVendor(context, vendorswift)

            Snackbar.make(recyclerView, "Vendor deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    contactlist.add(vendorbackup)
                    notifyItemInserted(contactlist.lastIndex)
                    addVendor(context, vendorbackup, CALLER)
                }.show()
        }
    }

    companion object {
        const val DELETEACTION = "delete"
        const val TAG = "Rv_VendorAdapter"
        const val CALLER = "none"
    }
}
