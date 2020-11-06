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
import com.google.android.material.snackbar.Snackbar

class Rv_VendorAdapter(val contactlist: MutableList<Vendor>) :
    RecyclerView.Adapter<Rv_VendorAdapter.ViewHolder>(), ItemTouchHelperAdapter {

    lateinit var context: Context
    private val selected: ArrayList<Int> = ArrayList()
    private var selectedPos = RecyclerView.NO_POSITION

    var mOnItemClickListener: OnItemClickListener? = null
    var countselected: Int = 0

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        // Instantiates a layout XML file into its corresponding View objects
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.vendor_item_layout, p0, false)
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

        p0.itemView.setOnClickListener {
            val vendordetail = Intent(context, Vendor_EditDetail::class.java)
            vendordetail.putExtra("contactid", contactlist[p1].contactid)
            vendordetail.putExtra("key", contactlist[p1].key)
            vendordetail.putExtra("latitude", contactlist[p1].latitude)
            vendordetail.putExtra("longitude", contactlist[p1].longitude)

            vendordetail.putExtra("name", contactlist[p1].name)
            vendordetail.putExtra("imageurl", contactlist[p1].imageurl)
            vendordetail.putExtra("phone", contactlist[p1].phone)
            vendordetail.putExtra("email", contactlist[p1].email)

           // vendordetail.putExtra("eventid", contactlist[p1].eventid)
            context.startActivity(vendordetail)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactname = itemView.findViewById<TextView>(R.id.contactname)!!
        val contactavatar = itemView.findViewById<ImageView>(R.id.contactavatar)!!
    }

    override fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView) {
        TODO("Not yet implemented")
    }

    override fun onItemSwiftRight(position: Int, recyclerView: RecyclerView) {
        val vendor = VendorEntity().apply {
//            eventid = contactlist[position].eventid
            contactid = contactlist[position].contactid
            key = contactlist[position].key

            name = contactlist[position].name
//            imageurl = contactlist[position].imageurl
            phone = contactlist[position].phone
//            email = contactlist[position].email
            placeid= contactlist[position].placeid
            latitude= contactlist[position].latitude
            longitude= contactlist[position].longitude
        }

        contactlist.removeAt(position)
        notifyItemRemoved(position)
        vendor.deleteVendor()

        Snackbar.make(recyclerView, "Vendor deleted", Snackbar.LENGTH_LONG)
            .setAction("UNDO") {
                contactlist.add(vendor)
                notifyItemInserted(contactlist.lastIndex)
                vendor.addVendor()
            }.show()
    }
}
