package com.example.newevent2

import android.annotation.SuppressLint
import android.content.Context
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

class Rv_GuestAdapter(
    val contactlist: MutableList<Contact>
) :
    RecyclerView.Adapter<Rv_GuestAdapter.ViewHolder>() {

    lateinit var context: Context
    private val selected: ArrayList<Int> = ArrayList()
    private var selectedPos = RecyclerView.NO_POSITION

    var mOnItemClickListener: OnItemClickListener? = null
    var countselected: Int = 0

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        // Instantiates a layout XML file into its corresponding View objects
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.contact_item_layout, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return contactlist.size
    }

    // public abstract void onBindViewHolder (VH holder, int position)

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.contactname.text = contactlist[p1].name
        if (contactlist[p1].imageurl != "") {
            Glide.with(p0.itemView.context)
                .load(contactlist[p1].imageurl)
                .circleCrop()
                //.override(200, 200)
                .into(p0.contactavatar)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactname = itemView.findViewById<TextView>(R.id.contactname)!!
        val contactavatar = itemView.findViewById<ImageView>(R.id.contactavatar)!!
    }
}

