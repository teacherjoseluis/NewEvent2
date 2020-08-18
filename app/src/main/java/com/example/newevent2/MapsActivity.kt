package com.example.newevent2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MapsActivity() : AppCompatActivity(), PlaceSelectionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_api_key), Locale.US)
        }

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setOnPlaceSelectedListener(this)
    }

    override fun onPlaceSelected(place: Place) {
        //Toast.makeText(applicationContext,""+place.name+ place.latLng,Toast.LENGTH_LONG).show();

        val resultIntent = Intent()
        resultIntent.putExtra("place_name", place.name)
        resultIntent.putExtra("place_id", place.id)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()

    }

    override fun onError(status: Status) {
        Toast.makeText(applicationContext,""+status.toString(),Toast.LENGTH_LONG).show();
        }
    }
