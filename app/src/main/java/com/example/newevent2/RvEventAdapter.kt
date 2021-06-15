package com.example.newevent2

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(
            StorageReference::class.java, InputStream::class.java,
            FirebaseImageLoader.Factory()
        )
    }
}

// Recyclerview - Displays a scrolling list of elements based on large datasets
// The view holder objects are managed by an adapter, which you create by extending RecyclerView.Adapter.
// The adapter creates view holders as needed. The adapter also binds the view holders to their data.
// It does this by assigning the view holder to a position, and calling the adapter's onBindViewHolder() method.

class RvEventAdapter(private val eventList: MutableList<Event>) :
    RecyclerView.Adapter<RvEventAdapter.ViewHolder>() {
    // ViewGroup - Views container

    lateinit var storage: FirebaseStorage
    lateinit var context: Context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0?.context).inflate(R.layout.event_item_layout, p0, false)
        context = p0.context
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    // public abstract void onBindViewHolder (VH holder, int position)
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.eventname?.text = eventList[p1].name
        p0.eventdate?.text = eventList[p1].date
        p0.eventtime?.text = eventList[p1].time

        val dateformatter = SimpleDateFormat("dd/MM/yyyy")
        val eventdate = dateformatter.parse(eventList[p1].date)
        val currdate = Date()
        val diffinmillisec = eventdate.time - currdate.time

        val seconds = diffinmillisec / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        when {
            days > 0 -> {
                p0.daysleft?.text = "$days days left"
            }
            days < 0 -> {
                p0.daysleft?.text = (days * -1).toString() + " days passed"
            }
            else -> {
                p0.daysleft?.text = "Today"
            }
        }


        var storageRef: Any
        if (eventList[p1].imageurl != "null") {
            storage = FirebaseStorage.getInstance()
            storageRef =
                storage.getReferenceFromUrl("gs://brides-n-grooms.appspot.com/images/${eventList[p1].key}/${eventList[p1].imageurl}")
            Log.i(TAG, eventList[p1].imageurl)
        } else {
            storageRef =
                Uri.parse(
                    ContentResolver.SCHEME_ANDROID_RESOURCE +
                            "://" + context.resources.getResourcePackageName(R.drawable.frame_206)
                            + '/' + context.resources.getResourceTypeName(R.drawable.frame_206) + '/' + context.resources.getResourceEntryName(
                        R.drawable.frame_206
                    )
                ).toString()
        }

        Glide.with(p0.itemView.context)
            .load(storageRef)
            .centerCrop()
            .into(p0.imageView)

//        p0.itemView.setOnClickListener {
//            val eventdetail = Intent(context, EventDetail::class.java)
//            eventdetail.putExtra("eventkey", eventList[p1].key)
//            eventdetail.putExtra("placeid", eventList[p1].placeid)
//            eventdetail.putExtra("name", eventList[p1].name)
//            eventdetail.putExtra("date", eventList[p1].date)
//            eventdetail.putExtra("time", eventList[p1].time)
//            eventdetail.putExtra("about", eventList[p1].about)
//            eventdetail.putExtra("location", eventList[p1].location)
//            eventdetail.putExtra("latitude", eventList[p1].latitude)
//            eventdetail.putExtra("longitude", eventList[p1].longitude)
//            eventdetail.putExtra("imageurl", eventList[p1].imageurl)
//            eventdetail.putExtra("address", eventList[p1].address)
//            context.startActivity(eventdetail)
//        }
    }


    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    //class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.imageView)!!
        val eventname: TextView? = itemView.findViewById<TextView>(R.id.eventname)
        val eventdate: TextView? = itemView.findViewById<TextView>(R.id.eventdate)
        val eventtime: TextView? = itemView.findViewById<TextView>(R.id.eventtime)
        val daysleft = itemView.findViewById<TextView>(R.id.daysleft)!!

    }
}