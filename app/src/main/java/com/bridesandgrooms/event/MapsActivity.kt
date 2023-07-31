package com.bridesandgrooms.event

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*


class MapsActivity : AppCompatActivity() {

    private var eventkey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        eventkey = intent.getStringExtra("eventkey").toString()

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key), Locale.US)
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
        )
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
                // Here it's where I Should be implementing the case when the provider has not been found
                //Toast.makeText(applicationContext, "" + p0.toString(), Toast.LENGTH_LONG).show();
            }
        })
    }
}


