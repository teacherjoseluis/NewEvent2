package com.example.newevent2

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
import kotlinx.android.synthetic.main.eventdetail_event.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EventDetail_Event.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventDetail_Event : Fragment() {

    lateinit var storage: FirebaseStorage
    lateinit var eventkey: String
    lateinit var eventitem: Event
    var googleMap: GoogleMap? = null
    val TAG = null
    var latLng: LatLngBounds? = null
    lateinit var event_location: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventkey = this.arguments!!.get("eventkey").toString()
        eventitem = Event()

//        if (!Places.isInitialized()) {
//            Places.initialize(
//                this.context!!,
//                getString(R.string.google_api_key),
//                Locale.US
//            )
//        }

//        val database = FirebaseDatabase.getInstance()
//        val myRef = database.reference
//        val postRef = myRef.child("User").child("Event")
//        postRef.child(eventkey)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//
//                override fun onDataChange(p0: DataSnapshot) {
//                    eventitem = p0.getValue(Event::class.java)!!
//                    //eventitem!!.imageurl=p0.child("imageurl").value as String
//                    eventitem!!.placeid = p0.child("placeid").value as String
//                    eventitem!!.name = p0.child("name").value as String
//                    eventitem!!.date = p0.child("date").value as String
//                    eventitem!!.time = p0.child("time").value as String
//                    eventitem!!.about = p0.child("about").value as String
//                }
//
//                override fun onCancelled(p0: DatabaseError) {
//                    println("loadPost:onCancelled ${p0.toException()}")
//                }
//
//            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //val eventkey = this.arguments!!.get("eventkey")
        val imageurl = this.arguments!!.get("imageurl")
        val inf = inflater.inflate(R.layout.eventdetail_event, container, false)

//        if (!Places.isInitialized()) {
//            Places.initialize(inf.context, getString(R.string.google_api_key), Locale.US)
//        }


        if (eventkey != "") {

            //val texttitle = inf.findViewById<TextView>(R.id.textView2)
            //texttitle!!.text = eventkey.toString()

            storage = FirebaseStorage.getInstance()
            val storageRef =
                storage.getReferenceFromUrl("gs://brides-n-grooms.appspot.com/images/${eventkey}/${imageurl}")

            Glide.with(inf.context)
                .load(storageRef)
                .centerCrop()
                .into(inf.imageView5)

            val database = FirebaseDatabase.getInstance()
            val myRef = database.reference
            val postRef = myRef.child("User").child("Event")
            postRef.child(eventkey)
                .addListenerForSingleValueEvent(object : ValueEventListener, OnMapReadyCallback {

                    override fun onDataChange(p0: DataSnapshot) {
                        eventitem = p0.getValue(Event::class.java)!!
                        //eventitem!!.imageurl=p0.child("imageurl").value as String
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
                        eventaddress.text=eventitem!!.address

                        event_location = LatLng(eventitem.latitude,eventitem.longitude)
                        latLng = LatLngBounds(LatLng(eventitem.latitude-5,eventitem.longitude-5),LatLng(eventitem.latitude+5,eventitem.longitude+5))
//                        val placesclient = Places.createClient(inf.context)
//                        val placefields = listOf(Place.Field.ID, Place.Field.LAT_LNG)
//                        var sessionToken  = AutocompleteSessionToken.newInstance()
//
//                        val request =
//                            FetchPlaceRequest.builder(eventitem.placeid, placefields).setSessionToken(sessionToken).build()
//                        //val placeTask = placesclient.fetchPlace(request)
//                        Log.v(TAG, "Before Request")
//                        val taskplace = placesclient.fetchPlace(request)
//                        Log.v(TAG, "After Request")
//                        taskplace.addOnSuccessListener { response ->
//                                    val place = response.place
//                                    latlng = place.latLng!!
//                        }
//                        taskplace.addOnFailureListener { exception: Exception ->
//                            if (exception is ApiException) {
//                                Log.e(TAG, "Place not found: ${exception.message}")
//                                val statusCode = exception.statusCode
//                            }
//                        }

                        val mapview = inf.findViewById<MapView>(R.id.mapView)

                        mapview.onCreate(savedInstanceState)
                        mapview.onResume()
                        mapview.getMapAsync(this)
                        //val mapFragment = fragmentManager?.findFragmentById(R.id.mapView) as SupportMapFragment

//                                try {
//                                    MapsInitializer.initialize(context)
//                                } catch (e: GooglePlayServicesNotAvailableException) {
//                                    e.printStackTrace()
//                                }


                        //}


                    }

                    override fun onCancelled(p0: DatabaseError) {
                        println("loadPost:onCancelled ${p0.toException()}")
                    }

                    override fun onMapReady(p0: GoogleMap?) {
                        googleMap = p0!!
                        //googleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(latLng,0))
                        googleMap!!.addMarker(MarkerOptions().position(event_location).title("My Event"))
                        googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng!!.center, 17f))

                    }
                })

//            val eventtitle = inf.findViewById<TextView>(R.id.textView2)
//            eventtitle.text=eventitem!!.name
//
//            val eventdate= inf.findViewById<TextView>(R.id.textView3)
//            eventdate.text=eventitem!!.date
//            val eventtime= inf.findViewById<TextView>(R.id.textView4)
//            eventtime.text=eventitem!!.time
        }
        return inf
    }
}

