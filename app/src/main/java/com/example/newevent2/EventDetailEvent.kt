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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inf = inflater.inflate(R.layout.eventdetail_event, container, false)
        val imageurl = this.arguments!!.get("imageurl").toString()

        // Get Event details
        val eventkey = this.arguments!!.get("eventkey").toString()
        val name = this.arguments!!.get("name").toString()
        val placeid = this.arguments!!.get("placeid").toString()
        val date = this.arguments!!.get("date").toString()
        val time = this.arguments!!.get("time").toString()
        val about = this.arguments!!.get("about").toString()
        val location = this.arguments!!.get("location").toString()
        val address = this.arguments!!.get("address").toString()
        val latitude = this.arguments!!.get("latitude") as Double
        val longitude = this.arguments!!.get("longitude") as Double

        val eventtitle = inf.findViewById<TextView>(R.id.textView2)
        eventtitle.text = name
        val eventdate = inf.findViewById<TextView>(R.id.textView3)
        eventdate.text = date
        val eventtime = inf.findViewById<TextView>(R.id.textView4)
        eventtime.text = time
        val eventabout = inf.findViewById<TextView>(R.id.eventabout)
        eventabout.text = about
        val eventlocation = inf.findViewById<TextView>(R.id.MyLocation)
        eventlocation.text = location
        val eventaddress = inf.findViewById<TextView>(R.id.eventlocation)
        eventaddress.text = address

        // Load thumbnail
        var storageRef: Any
        if (imageurl != "null") {
            storage = FirebaseStorage.getInstance()
            storageRef =
                storage.getReferenceFromUrl("gs://brides-n-grooms.appspot.com/images/${eventkey}/${imageurl}")
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

        val eventLocationLnLg = LatLng(latitude, longitude)
        val latLng = LatLngBounds(
            LatLng(latitude - 5, longitude - 5),
            LatLng(latitude + 5, longitude + 5)
        )


        val mapview = inf.findViewById<MapView>(R.id.mapView)
        // Show Map
        mapview.onCreate(savedInstanceState)
        mapview.onResume()
        mapview.getMapAsync { p0 ->
            val googleMap = p0!!
            googleMap!!.addMarker(
                MarkerOptions().position(eventLocationLnLg).title(location)
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
                    String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                override fun onClick(p0: View?) {
                    context!!.startActivity(intent)
                }
            })
        }

        inf.editEventButton.setOnClickListener {
            val eventedit = Intent(context, Event_EditDetail::class.java)
            eventedit.putExtra("eventkey", eventkey)
            eventedit.putExtra("placeid", placeid)
            eventedit.putExtra("name", name)
            eventedit.putExtra("date", date)
            eventedit.putExtra("time", time)
            eventedit.putExtra("about", about)
            eventedit.putExtra("location", location)
            eventedit.putExtra("latitude", latitude)
            eventedit.putExtra("longitude", longitude)
            eventedit.putExtra("imageurl", imageurl)
            eventedit.putExtra("address", address)
            context!!.startActivity(eventedit)
        }

//-------------------------------------------------------------------------------------
        val guestentity = GuestEntity()
        guestentity.eventid = eventkey!!
        guestentity.getGuestsEvent(object : FirebaseSuccessListenerGuest {
            override fun onGuestList(list: ArrayList<Guest>) {
                TODO("Not yet implemented")
            }

            override fun onGuestConfirmation(confirmed: Int, rejected: Int, pending: Int) {
                totaltext.text = (confirmed + rejected + pending).toString()
                acceptedtext.text = confirmed.toString()
                rejectedtext.text = rejected.toString()
                pendingtext.text = pending.toString()
            }
        })
//-------------------------------------------------------------------------------------
        return inf
    }
}


