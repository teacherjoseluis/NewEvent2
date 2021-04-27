package com.example.newevent2

import android.content.ContentResolver
import android.content.Intent
import android.content.Intent.getIntent
import android.content.Intent.getIntentOld
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.eventdetail_event.*
import kotlinx.android.synthetic.main.eventdetail_event.view.*
import java.util.*
import kotlin.collections.ArrayList


class EventDetailEvent : Fragment() {

    lateinit var storage: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inf = inflater.inflate(R.layout.eventdetail_event, container, false)

        // Get Event details

        val event = Event().apply {
            key = arguments!!.get("eventkey").toString()
            name = arguments!!.get("name").toString()
            placeid = arguments!!.get("placeid").toString()
            date = arguments!!.get("date").toString()
            time = arguments!!.get("time").toString()
            about = arguments!!.get("about").toString()
            location= arguments!!.get("location").toString()
            address = arguments!!.get("address").toString()
            latitude = arguments!!.get("latitude") as Double
            longitude = arguments!!.get("longitude") as Double
            imageurl = arguments!!.get("imageurl").toString()
        }

        inf.findViewById<TextView>(R.id.textView2).text = event.name
        inf.findViewById<TextView>(R.id.textView3).text = event.date
        inf.findViewById<TextView>(R.id.textView4).text = event.time
        inf.findViewById<TextView>(R.id.eventabout).text = event.about
        inf.findViewById<TextView>(R.id.MyLocation).text = event.location
        inf.findViewById<TextView>(R.id.eventlocation).text = event.address

        // Load thumbnail
        var storageRef: Any
        if (event.imageurl != "") {
            storage = FirebaseStorage.getInstance()
            storageRef =
                storage.getReferenceFromUrl("gs://brides-n-grooms.appspot.com/images/${event.key}/${event.imageurl}")
        } else {
            storageRef =
                Uri.parse(
                    ContentResolver.SCHEME_ANDROID_RESOURCE +
                            "://" + resources.getResourcePackageName(R.drawable.frame_206)
                            + '/' + resources.getResourceTypeName(R.drawable.frame_206) + '/' + resources.getResourceEntryName(
                        R.drawable.frame_206
                    )
                ).toString()
        }
        Glide.with(inf.context)
            .load(storageRef)
            .centerCrop()
            .into(inf.imageView5)

        val eventLocationLnLg = LatLng(event.latitude, event.longitude)
        val latLng = LatLngBounds(
            LatLng(event.latitude - 5, event.longitude - 5),
            LatLng(event.latitude + 5, event.longitude + 5)
        )

        val mapview = inf.findViewById<MapView>(R.id.mapView)
        // Show Map
        mapview.onCreate(savedInstanceState)
        mapview.onResume()
        mapview.getMapAsync { p0 ->
            val googleMap = p0!!
            googleMap!!.addMarker(
                MarkerOptions().position(eventLocationLnLg).title(event.location)
            )
            googleMap!!.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng!!.center,
                    16f
                )
            )
            googleMap!!.uiSettings.setAllGesturesEnabled(false)

            mapView.setOnClickListener(object : View.OnClickListener {
                val uri =
                    String.format(Locale.ENGLISH, "geo:%f,%f", event.latitude, event.longitude)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                override fun onClick(p0: View?) {
                    context!!.startActivity(intent)
                }
            })
        }

        inf.editEventButton.setOnClickListener {
            val eventedit = Intent(context, Event_EditDetail::class.java)
            eventedit.putExtra("evententity", event)
            context!!.startActivity(eventedit)
        }

//-------------------------------------------------------------------------------------
//        val guestentity = GuestEntity()
////        guestentity.eventid = eventkey!!
//        guestentity.getGuestsEvent(activity!!.applicationContext, object : FirebaseSuccessListenerGuest {
//            override fun onGuestList(list: ArrayList<Guest>) {
//                TODO("Not yet implemented")
//            }

//            override fun onGuestConfirmation(confirmed: Int, rejected: Int, pending: Int) {
//                totaltext.text = (confirmed + rejected + pending).toString()
//                acceptedtext.text = confirmed.toString()
//                rejectedtext.text = rejected.toString()
//                pendingtext.text = pending.toString()
//            }
        //})
//-------------------------------------------------------------------------------------
        return inf
    }
}


