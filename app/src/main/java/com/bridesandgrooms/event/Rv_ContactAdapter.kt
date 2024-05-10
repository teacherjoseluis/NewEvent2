package com.bridesandgrooms.event

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Model.Contact
import com.bridesandgrooms.event.UI.ClearSelected
import com.bridesandgrooms.event.databinding.ContactItemLayoutBinding
import com.bridesandgrooms.event.UI.LetterAvatar
import com.bridesandgrooms.event.UI.OnItemClickListener

class Rv_ContactAdapter(
    private val contactlist: ArrayList<Contact>
) :
    RecyclerView.Adapter<Rv_ContactAdapter.ViewHolder>(), ClearSelected {

    lateinit var context: Context
    private val selected: ArrayList<Int> = ArrayList()
    private var selectedPos = RecyclerView.NO_POSITION

    var mOnItemClickListener: OnItemClickListener? = null


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        // Instantiates a layout XML file into its corresponding View objects
        //val v = LayoutInflater.from(p0.context).inflate(R.layout.contact_item_layout, p0, false)
        context = p0.context

        val inflater = LayoutInflater.from(p0.context)
        val binding: ContactItemLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.contact_item_layout, p0, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactlist.size
    }

    // public abstract void onBindViewHolder (VH holder, int position)
    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val checkDrawable = ContextCompat.getDrawable(context, R.drawable.icons8_checkmark)!!
        checkDrawable.setTint(Color.parseColor("#C2185B"))

        p0.contactname.text = contactlist[p1].name
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
    class ViewHolder(binding: ContactItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
//        private lateinit var mBinding : ContactItemLayoutBinding
//
//        fun ViewHolder(binding:ContactItemLayoutBinding){
//            super(binding.root)
//            mBinding = binding
//        }

        val contactname = binding.contactname
        val contactavatar = binding.contactavatar

//        val contactname = itemView.findViewById<TextView>(R.id.contactname)!!
//        val contactavatar = itemView.findViewById<ImageView>(R.id.contactavatar)!!
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClearSelected(): Boolean {
        selected.clear()
        notifyDataSetChanged()
        return true
    }
}






