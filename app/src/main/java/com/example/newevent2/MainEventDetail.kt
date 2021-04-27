package com.example.newevent2

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.newevent2.MVP.EventPresenter
import com.example.newevent2.MVP.TaskPresenter
import com.example.newevent2.Model.Event
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.eventdetail_event.*
import kotlinx.android.synthetic.main.eventdetail_event.view.*
import java.util.*
import kotlin.collections.ArrayList

class MainEventDetail : Fragment(), EventPresenter.ViewEventActivity {

    var userid = ""
    var eventid = ""

    private lateinit var presenterevent: EventPresenter
    private lateinit var mapview: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userid = this.arguments!!.get("userid").toString()
        eventid = this.arguments!!.get("eventid").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inf = inflater.inflate(R.layout.eventdetail_event, container, false)

        // Get Event details -----------------------------------------------------------------------
        presenterevent = EventPresenter(this, inf, userid, eventid)
        presenterevent.getEventDetail()
        //------------------------------------------------------------------------------------------

        // Load thumbnail

        //------------------------------------------------------------------------------------------
        mapview = inf.findViewById<MapView>(R.id.mapView)
        mapview.onCreate(savedInstanceState)
        mapview.onResume()
        //------------------------------------------------------------------------------------------
        return inf
    }

    override fun onViewEventSuccessFragment(inflatedView: View, event: Event) {
        inflatedView.findViewById<TextView>(R.id.textView2).text = event.name
        inflatedView.findViewById<TextView>(R.id.textView3).text = event.date
        inflatedView.findViewById<TextView>(R.id.textView4).text = event.time
        inflatedView.findViewById<TextView>(R.id.eventabout).text = event.about
        inflatedView.findViewById<TextView>(R.id.MyLocation).text = event.location
        inflatedView.findViewById<TextView>(R.id.eventlocation).text = event.address

        val eventLocationLnLg = LatLng(event.latitude, event.longitude)
        val latLng = LatLngBounds(
            LatLng(event.latitude - 5, event.longitude - 5),
            LatLng(event.latitude + 5, event.longitude + 5)
        )

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

        inflatedView.editEventButton.setOnClickListener {
            val eventedit = Intent(context, Event_EditDetail::class.java)
            eventedit.putExtra("evententity", event)
            context!!.startActivity(eventedit)
        }
    }

    override fun onViewEventErrorFragment(inflatedView: View, errcode: String) {
        Toast.makeText(
            context,
            "There was an error trying to get Event data",
            Toast.LENGTH_SHORT
        ).show()
    }
}


