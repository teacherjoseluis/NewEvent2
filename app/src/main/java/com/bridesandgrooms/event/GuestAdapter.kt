package com.bridesandgrooms.event

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Model.contactGuest
import com.bridesandgrooms.event.UI.LetterAvatar

class GuestAdapter(
    private val fragmentActionListener: Guest_FragmentActionListener,
    private val contactList: ArrayList<contactGuest>,
    val context: Context
) :
    RecyclerView.Adapter<GuestAdapter.GuestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuestViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.guest_item_layout, parent, false)
        return GuestViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: GuestViewHolder, position: Int) {
        val contactGuest = contactList[position]
        holder.bind(contactGuest)
    }


    inner class GuestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val customGuestCardView: ConstraintLayout = itemView.findViewById(R.id.cardLayout)
        private val transitionDuration = 300L

        init {
            customGuestCardView.setOnClickListener {
                handleClick()
            }

            customGuestCardView.setOnLongClickListener {
                toggleState()
                true
            }
        }

        fun bind(contactGuest: contactGuest) {
            customGuestCardView.removeAllViews()

            val layoutId =
                if (contactGuest.isExpanded) R.layout.guest_cardview_expanded else R.layout.guest_cardview_contracted
            val view =
                LayoutInflater.from(itemView.context).inflate(layoutId, customGuestCardView, false)

            view.findViewById<ImageView>(R.id.guestImageView).apply {
                val textColor = ContextCompat.getColor(context, R.color.OnSecondaryContainer)
                val circleColor = ContextCompat.getColor(context, R.color.SecondaryContainer)
                val density = context.resources.displayMetrics.density
                setImageDrawable(
                    LetterAvatar(
                        textColor,
                        circleColor,
                        contactGuest.guest.name.substring(0, 2),
                        10,
                        density
                    )
                )
            }

            view.findViewById<TextView>(R.id.guestName).text = contactGuest.guest.name
            view.findViewById<TextView>(R.id.guestCategory).text =
                when(contactGuest.guest.table){
                    "family" -> context.getString(R.string.family)
                    "extendedfamily" -> context.getString(R.string.extendedfamily)
                    "familyfriends" -> context.getString(R.string.familyfriends)
                    "oldfriends" -> context.getString(R.string.oldfriends)
                    "coworkers" -> context.getString(R.string.coworkers)
                    else -> context.getString(R.string.others)
                }

            if (contactGuest.isExpanded) {
                view.findViewById<TextView>(R.id.rsvp)!!
                view.findViewById<TextView>(R.id.companions)!!
            }
            customGuestCardView.addView(view)
            animateView(view)
        }

        private fun animateView(view: View) {
            view.alpha = 0f // Set initial alpha to 0
            view.animate()
                .alpha(1f) // Animate alpha to 1
                .setDuration(transitionDuration)
                .start()
        }

        private fun handleClick() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val contactGuest = contactList[position]
                fragmentActionListener.onGuestFragmentWithData(contactGuest.guest)
            }
        }

        private fun toggleState() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val contactGuest = contactList[position]
                contactGuest.isExpanded = !contactGuest.isExpanded
                notifyItemChanged(position)
            }
        }
    }
}

