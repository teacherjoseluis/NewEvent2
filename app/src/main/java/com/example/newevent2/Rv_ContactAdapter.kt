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
import com.example.newevent2.Model.Contact
import com.example.newevent2.Model.Guest
import com.example.newevent2.ui.LetterAvatar

class Rv_ContactAdapter(
    val contactlist: ArrayList<Contact>
) :
    RecyclerView.Adapter<Rv_ContactAdapter.ViewHolder>(), ClearSelected {

    lateinit var context: Context
    val selected: ArrayList<Int> = ArrayList()
    var selectedPos = RecyclerView.NO_POSITION

    var mOnItemClickListener: OnItemClickListener? = null

    //var mClearSelected: ClearSelected? = null
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
    @SuppressLint("SetTextI18n", "ResourceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val checkDrawable = ContextCompat.getDrawable(context, R.drawable.icons8_checkmark)!!
        checkDrawable.setTint(Color.parseColor("#C2185B"))

        p0.contactname.text = contactlist[p1].name
        p0.contactavatar.apply {
            isSelected = (selectedPos == p1)
            if (!selected.contains(p1)) {
                setImageDrawable(
                    LetterAvatar(
                        context,
                        context.getColor(R.color.azulmasClaro),
                        p0.contactname.text.toString().substring(0, 2),
                        10
                    )
                )
            } else {
                setImageDrawable(checkDrawable)
            }

            setOnClickListener {
                if (selected.contains(p1)) {
                    selected.remove(p1)
                    setImageDrawable(
                        LetterAvatar(
                            context,
                            context.getColor(R.color.azulmasClaro),
                            p0.contactname.text.toString().substring(0, 2),
                            10
                        )
                    )
                } else {
                    selected.add(p1)
                    setImageDrawable(checkDrawable)
                }
                //notifyItemChanged(selectedPos)
                selectedPos = p0.layoutPosition
                notifyItemChanged(selectedPos)

                mOnItemClickListener?.onItemClick(p1, selected)
                //Toast.makeText(context, "Saliendo del listener", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
//class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactname = itemView.findViewById<TextView>(R.id.contactname)!!
        val contactavatar = itemView.findViewById<ImageView>(R.id.contactavatar)!!
    }

    override fun onClearSelected(): Boolean {
        selected.clear()
        notifyDataSetChanged()
        //selectedPos = RecyclerView.NO_POSITION
        //notifyItemChanged(selectedPos)
        return true
    }
}






