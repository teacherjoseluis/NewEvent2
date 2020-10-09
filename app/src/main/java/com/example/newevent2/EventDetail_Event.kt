package com.example.newevent2

import android.content.Intent
import android.content.Intent.getIntent
import android.content.Intent.getIntentOld
import android.os.Bundle
import android.view.LayoutInflater
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


class EventDetail_Event : Fragment() {

    lateinit var storage: FirebaseStorage
    lateinit var eventkey: String
    lateinit var eventitem: Event
    var googleMap: GoogleMap? = null
    var latLng: LatLngBounds? = null
    lateinit var eventLocation: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventkey = this.arguments!!.get("eventkey").toString()
        eventitem = Event()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val imageurl = this.arguments!!.get("imageurl")
        val inf = inflater.inflate(R.layout.eventdetail_event, container, false)

        if (eventkey != "") {
            // Load thumbnail
            storage = FirebaseStorage.getInstance()
            val storageRef =
                storage.getReferenceFromUrl("gs://brides-n-grooms.appspot.com/images/${eventkey}/${imageurl}")

            Glide.with(inf.context)
                .load(storageRef)
                .centerCrop()
                .into(inf.imageView5)

            // Get Event details
            val database = FirebaseDatabase.getInstance()
            val myRef = database.reference
            val postRef = myRef.child("User").child("Event")
            postRef.child(eventkey)
                .addListenerForSingleValueEvent(object : ValueEventListener, OnMapReadyCallback {

                    override fun onDataChange(p0: DataSnapshot) {
                        eventitem = p0.getValue(Event::class.java)!!
                        eventitem!!.placeid = p0.child("placeid").value as String
                        eventitem!!.name = p0.child("name").value as String
                        eventitem!!.date = p0.child("date").value as String
                        eventitem!!.time = p0.child("time").value as String
                        eventitem!!.about = p0.child("about").value as String
                        eventitem!!.location = p0.child("location").value as String
                        eventitem!!.latitude = p0.child("latitude").value as Double
                        eventitem!!.longitude = p0.child("longitude").value as Double

                        val eventtitle = inf.findViewById<TextView>(R.id.textView2)
                        eventtitle.text = eventitem!!.name
                        val eventdate = inf.findViewById<TextView>(R.id.textView3)
                        eventdate.text = eventitem!!.date
                        val eventtime = inf.findViewById<TextView>(R.id.textView4)
                        eventtime.text = eventitem!!.time
                        val eventabout = inf.findViewById<TextView>(R.id.textView5)
                        eventabout.text = eventitem!!.about
                        val eventlocation = inf.findViewById<TextView>(R.id.textView6)
                        eventlocation.text = eventitem!!.location
                        val eventaddress = inf.findViewById<TextView>(R.id.textView7)
                        eventaddress.text = eventitem!!.address

                        eventLocation = LatLng(eventitem.latitude, eventitem.longitude)
                        latLng = LatLngBounds(
                            LatLng(eventitem.latitude - 5, eventitem.longitude - 5),
                            LatLng(eventitem.latitude + 5, eventitem.longitude + 5)
                        )

                        val mapview = inf.findViewById<MapView>(R.id.mapView)

                        // Show Map
                        mapview.onCreate(savedInstanceState)
                        mapview.onResume()
                        mapview.getMapAsync(this)


                        editEventButton.setOnClickListener{
                            val eventedit = Intent(context, Event_EditDetail::class.java)
                            eventedit.putExtra("eventkey", eventkey)
                            eventedit.putExtra("placeid", eventitem.placeid)
                            eventedit.putExtra("name", eventitem.name)
                            eventedit.putExtra("date", eventitem.date)
                            eventedit.putExtra("time", eventitem.time)
                            eventedit.putExtra("about", eventitem.about)
                            eventedit.putExtra("location", eventitem.location)
                            eventedit.putExtra("latitude", eventitem.latitude)
                            eventedit.putExtra("longitude", eventitem.longitude)
                            eventedit.putExtra("imageurl", eventitem.imageurl)
                            eventedit.putExtra("address", eventitem.address)

                            context!!.startActivity(eventedit)
                        }

                    }

                    override fun onCancelled(p0: DatabaseError) {
                        println("loadPost:onCancelled ${p0.toException()}")
                    }

                    override fun onMapReady(p0: GoogleMap?) {
                        googleMap = p0!!
                        googleMap!!.addMarker(
                            MarkerOptions().position(eventLocation).title("My Event")
                        )
                        googleMap!!.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                latLng!!.center,
                                16f
                            )
                        )
                        googleMap!!.uiSettings.setAllGesturesEnabled(false)
                    }
                })
        }
        return inf
    }
}

