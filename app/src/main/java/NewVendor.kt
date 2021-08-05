//package com.example.newevent2
//
//import android.content.Intent
//import android.content.res.Resources
//import android.net.Uri
//import android.os.Bundle
//import android.view.View
//import android.widget.Button
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.constraintlayout.widget.ConstraintLayout
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.MapView
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.LatLngBounds
//import com.google.android.gms.maps.model.MarkerOptions
//import kotlinx.android.synthetic.main.custom_vendor_contents.*
//import kotlinx.android.synthetic.main.custom_vendor_contents.view.*
//import kotlinx.android.synthetic.main.new_guest.*
//import kotlinx.android.synthetic.main.new_vendor2.*
//import java.util.*
//
//class NewVendor : AppCompatActivity() {
//
//    var googleMap: GoogleMap? = null
//    var latLng: LatLngBounds? = null
//    lateinit var eventLocation: LatLng
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        //setContentView(R.layout.new_vendor2.xml)
//
//        val source = intent.getStringExtra("source").toString()
//
//        if (source == "google") {
//            setContentView(R.layout.`new_vendor2.xml`)
//
//            // Toolbar
//            setSupportActionBar(findViewById(R.id.toolbar))
//            supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
//            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//
//            val apptitle = findViewById<TextView>(R.id.appbartitle)
//            apptitle.text = "New Vendor"
//
//            val height = Resources.getSystem().displayMetrics.heightPixels
//            val maplayout = findViewById<ConstraintLayout>(R.id.constraintLayout8)
//            maplayout.layoutParams.height = height - 800
//
//            val intent = intent
//            //val eventkey = intent.getStringExtra("eventkey").toString()
//            val location = intent.getStringExtra("location").toString()
//            val placeid = intent.getStringExtra("placeid").toString()
//            val latitude = intent.getDoubleExtra("latitude", 0.0)
//            val longitude = intent.getDoubleExtra("longitude", 0.0)
//            val address = intent.getStringExtra("address").toString()
//            val phone = intent.getStringExtra("phone").toString()
//            val rating = intent.getDoubleExtra("rating", 0.0).toString()
//            val userrating = intent.getIntExtra("userrating", 0).toString()
//
//            vendorname.text = location
//            vendoraddress.text = address
//            vendorphone.text = phone
//
//            eventLocation = LatLng(latitude, longitude)
//            latLng = LatLngBounds(
//                LatLng(latitude - 5, longitude - 5),
//                LatLng(latitude + 5, longitude + 5)
//            )
//
//            val mapview = findViewById<MapView>(R.id.mapView2)
//
//            // Show Map
//            mapview.onCreate(savedInstanceState)
//            mapview.onResume()
//            mapview.getMapAsync { p0 ->
//                googleMap = p0!!
//                googleMap!!.addMarker(
//                    MarkerOptions().apply {
//                        title(location)
//                        position(eventLocation)
//                        snippet("Rating $rating ($userrating)")
//                    }
//                )
//
//                mapview.setOnClickListener {
//                    val uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude)
//                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
//                    applicationContext.startActivity(intent)
//                }
//
//                googleMap!!.moveCamera(
//                    CameraUpdateFactory.newLatLngZoom(
//                        latLng!!.center,
//                        16f
//                    )
//                )
//                googleMap!!.uiSettings.apply {
//                    setAllGesturesEnabled(false)
//                }
//            }
//
//            val button = findViewById<Button>(R.id.newvendorbutton)
//            button.setOnClickListener {
//                val vendor = VendorEntity().apply {
//                    //eventid = eventkey
//                    contactid = "google"
//                    name = location
//                    this.phone = phone
//                    this.placeid = placeid
//                    this.latitude = latitude
//                    this.longitude = longitude
//                }
//                vendor.addVendor()
//                finish()
//            }
//        }
//        else if (source == "local") {
//            setContentView(R.layout.vendor_editdetail)
//
//            // Toolbar
//            setSupportActionBar(findViewById(R.id.toolbar))
//            supportActionBar!!.setHomeAsUpIndicator(R.drawable.icons8_left_24)
//            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//
//            val apptitle = findViewById<TextView>(R.id.appbartitle)
//            apptitle.text = "New Vendor"
//
//            val vendormap = findViewById<ConstraintLayout>(R.id.vendormap)
//            vendormap.visibility = View.INVISIBLE
//
//            val button = findViewById<Button>(R.id.button)
//            button.visibility = View.VISIBLE
//
//            nameinputedit.setOnClickListener {
//                nameinputedit.error = null
//            }
//
//            phoneinputedit.setOnClickListener {
//                phoneinputedit.error = null
//            }
//
//            button.setOnClickListener {
//                var inputvalflag = true
//                if (nameinputedit.text.toString().isEmpty()) {
//                    nameinputedit.error = "Guest name is required!"
//                    inputvalflag = false
//                }
//                if (phoneinputedit.text.toString().isEmpty()) {
//                    phoneinputedit.error = "Task due date is required!"
//                    inputvalflag = false
//                }
//
//                if (inputvalflag) {
//                    savevendor()
//                    finish()
//                }
//            }
//        }
//    }
//
//    private fun savevendor() {
//        val vendor = VendorEntity().apply {
//            name = nameinputedit.text.toString()
//            phone = phoneinputedit.text.toString()
//            contactid = "local"
//        }
//        vendor.addVendor()
//        finish()
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        finish()
//        return true
//    }
//
//}