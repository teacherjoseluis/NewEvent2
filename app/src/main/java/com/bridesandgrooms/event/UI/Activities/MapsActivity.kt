package com.bridesandgrooms.event.UI.Activities
//
//import android.Manifest
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.databinding.DataBindingUtil
//import com.bridesandgrooms.event.ActivityContainer.Companion.TAG
//import com.bridesandgrooms.event.R
//import com.bridesandgrooms.event.databinding.ActivityGalleryBinding
////import com.bridesandgrooms.event.databinding.ActivityMapsVendorBinding
//import com.bridesandgrooms.event.databinding.MapFragmentBinding
//import com.google.android.gms.common.api.ApiException
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.MarkerOptions
//import com.google.android.libraries.places.api.Places
//import com.google.android.libraries.places.api.model.Place
//import com.google.android.libraries.places.api.net.FetchPlaceRequest
//import com.google.android.libraries.places.api.net.PlacesClient
//
//class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
//
//    private lateinit var googleMap: GoogleMap
//    private lateinit var placesClient: PlacesClient
//    private val LOCATION_PERMISSION_REQUEST_CODE = 1
//
//    //private lateinit var binding: ActivityMapsVendorBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Initialize Places API
//        Places.initialize(applicationContext, getString(R.string.google_maps_key))
//        placesClient = Places.createClient(this)
//
//        // Setup the toolbar
//        val toolbar: Toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
//
//        //binding = DataBindingUtil.setContentView(this, R.layout.activity_maps_vendor)
//
//        // Load the map fragment
//        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//        mapFragment?.getMapAsync(this)
//    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        this.googleMap = googleMap
//        setupMap()
//
//        // Assuming you have the place ID passed as an extra
//        intent.getStringExtra("placeId")?.let {
//            getPlaceById(it)
//        }
//
//        googleMap.setOnMarkerClickListener { marker ->
//            // Launch Google Maps intent here
//            openGoogleMaps(marker.position, marker.title)
//            true // Return true to indicate that we have consumed the event.
//        }
//    }
//
//    private fun openGoogleMaps(latlng: LatLng, title: String?) {
//        val gmmIntentUri =
//            Uri.parse("geo:${latlng.latitude},${latlng.longitude}?q=${Uri.encode(title)}")
//        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//        mapIntent.setPackage("com.google.android.apps.maps")
//        if (mapIntent.resolveActivity(packageManager) != null) {
//            startActivity(mapIntent)
//        } else {
//            Log.e(TAG, "Google Maps is not installed.")
//        }
//    }
//
//    private fun setupMap() {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        } else {
//            googleMap.isMyLocationEnabled = true
//        }
//    }
//
//    private fun getPlaceById(placeId: String) {
//        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
//        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
//
//        placesClient.fetchPlace(request).addOnSuccessListener { response ->
//            val place = response.place.latLng
//            if (place != null) {
//                googleMap.addMarker(MarkerOptions().position(place).title(response.place.name))
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 15f))
//                populateVendorCart(response.place)
//            } else {
//                Log.e(TAG, "Place details received but missing latLng.")
//            }
//        }.addOnFailureListener { exception ->
//            if (exception is ApiException) {
//                Log.e(TAG, "Place not found: ${exception.statusCode} - ${exception.message}")
//            }
//        }
//    }
//
//    private fun populateVendorCart(place: Place) {
//        binding.vendorName.text = place.name
//        binding.vendorAddress.text = place.address
//        binding.vendorPhone.text = place.phoneNumber
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                if (ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return
//                }
//                googleMap.isMyLocationEnabled = true
//            } else {
//                Log.e(TAG, "Location permission was denied by the user.")
//            }
//        }
//    }
//
//    companion object {
//        const val TAG = "MapsActivity"
//    }
//}
