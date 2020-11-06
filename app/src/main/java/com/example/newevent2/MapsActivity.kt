package com.example.newevent2

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*


class MapsActivity() : AppCompatActivity() {

    var eventkey: String? = null
//    private var wasPaused: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        //val prefs = getSharedPreferences("MyPref", MODE_PRIVATE)
        //prefs.edit().remove("waspaused").commit()

        eventkey = intent.getStringExtra("eventkey").toString()

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_api_key), Locale.US)
        }

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        autocompleteFragment.setCountry(tm.simCountryIso)
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT)
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER,
                Place.Field.RATING,
                Place.Field.USER_RATINGS_TOTAL
            )
        );
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                val resultIntent = Intent()
                resultIntent.putExtra("eventid", eventkey)
                resultIntent.putExtra("place_name", p0.name)
                resultIntent.putExtra("place_id", p0.id)
                resultIntent.putExtra("place_latitude", p0.latLng!!.latitude)
                resultIntent.putExtra("place_longitude", p0.latLng!!.longitude)
                resultIntent.putExtra("place_address", p0.address)
                resultIntent.putExtra("place_phone", p0.phoneNumber)
                resultIntent.putExtra("place_rating", p0.rating)
                resultIntent.putExtra("place_userrating", p0.userRatingsTotal)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }

            override fun onError(p0: Status) {
                Toast.makeText(applicationContext, "" + p0.toString(), Toast.LENGTH_LONG).show();
            }
        })

    }

//    override fun onPause() {
//        super.onPause()
//        if (isApplicationInBackground()) {
//            val editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit()
//            editor.putBoolean("waspaused", true)
//            editor.apply()
//            //wasPaused = true
//       }
//    }

//    override fun onResume() {
//        super.onResume()
//
//        val prefs = getSharedPreferences("MyPref", MODE_PRIVATE)
//
///        if (prefs.getBoolean("waspaused", false)) {
//            val prefs = getSharedPreferences("MyPref", MODE_PRIVATE)
//            prefs.edit().remove("waspaused").commit()
//            this.finish()
//
//
//            //lockScreen()
//            //wasPaused = false
//        }
//    }

//    override fun onPlaceSelected(place: Place) {
//        //Toast.makeText(applicationContext,""+place.name+ place.latLng,Toast.LENGTH_LONG).show();
//
//        val resultIntent = Intent()
//        resultIntent.putExtra("eventid", eventkey)
//        resultIntent.putExtra("place_name", place.name)
//        resultIntent.putExtra("place_id", place.id)
//        resultIntent.putExtra("place_latitude", place.latLng!!.latitude)
//        resultIntent.putExtra("place_longitude", place.latLng!!.longitude)
//        resultIntent.putExtra("place_address", place.address)
//        resultIntent.putExtra("place_phone", place.phoneNumber)
//        resultIntent.putExtra("place_rating", place.rating)
//        resultIntent.putExtra("place_userrating", place.userRatingsTotal)
//        setResult(Activity.RESULT_OK, resultIntent)
//        finish()
//
//    }
//
//    override fun onError(status: Status) {
//        Toast.makeText(applicationContext, "" + status.toString(), Toast.LENGTH_LONG).show();
//    }
}


