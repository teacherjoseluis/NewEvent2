package com.example.newevent2

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Functions.addGuest
import com.example.newevent2.Functions.deleteGuest
import com.example.newevent2.Model.Guest
import com.example.newevent2.ui.LetterAvatar
import com.google.android.material.snackbar.Snackbar
import com.redmadrobot.acronymavatar.AvatarView
import java.lang.Exception

class Rv_GuestAdapter2(
    val contactlist: ArrayList<Guest>, val context: Context
) :
    RecyclerView.Adapter<Rv_GuestAdapter2.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        // Instantiates a layout XML file into its corresponding View objects
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.guest_item_layout2, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return contactlist.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.contactname.text = contactlist[p1].name
        try {
            p0.contactavatar.setText(p0.contactname.text.toString())

        } catch (e: Exception) {
            p0.contactavatar.setImageResource(R.drawable.avatar2)
        }
        p0.companions.text = when (contactlist[p1].companion) {
            "none" -> "0"
            else -> "1"
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactname = itemView.findViewById<TextView>(R.id.contactname)!!
        val contactavatar = itemView.findViewById<AvatarView>(R.id.contactavatar)!!
        val companions = itemView.findViewById<TextView>(R.id.companions)!!
    }

    companion object {
        const val TAG = "Rv_GuestAdapter2"
    }
}

