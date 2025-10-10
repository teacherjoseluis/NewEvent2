package com.bridesandgrooms.event

import Application.UserRetrievalException
import android.app.Activity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bridesandgrooms.event.Model.User
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.launch
import java.util.*

class MapsActivity : AppCompatActivity() {

    private var eventkey: String? = null
    private lateinit var user: User
    private lateinit var placesClient: PlacesClient

    private lateinit var inputSearch: EditText
    private lateinit var suggestionList: ListView
    private lateinit var countryText: TextView

    private val predictions = mutableListOf<AutocompletePrediction>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        eventkey = intent.getStringExtra("eventkey")

        // Initialize views
        inputSearch = findViewById(R.id.input_search)
        suggestionList = findViewById(R.id.list_suggestions)
        countryText = findViewById(R.id.country_name)

        // Initialize Places SDK if not done yet
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key), Locale.US)
        }
        placesClient = Places.createClient(this)

        // Retrieve user info
        lifecycleScope.launch {
            try {
                user = User.getUserAsync()
                setupUI()
            } catch (e: UserRetrievalException) {
                displayErrorMsg(getString(R.string.errorretrieveuser))
            } catch (e: Exception) {
                displayErrorMsg(getString(R.string.error_unknown) + " - " + e.toString())
            }
        }
    }

    private fun setupUI() {
        countryText.text = user.country

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        suggestionList.adapter = adapter

        inputSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.length > 2) { // Start suggesting after 3 chars
                    fetchPredictions(query)
                } else {
                    adapter.clear()
                    predictions.clear()
                }
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        suggestionList.setOnItemClickListener { _, _, position, _ ->
            val prediction = predictions[position]
            fetchPlaceDetails(prediction.placeId)
        }
    }

    private fun fetchPredictions(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setCountries(listOf(user.country))
            .setTypesFilter(listOf(PlaceTypes.ESTABLISHMENT))
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                predictions.clear()
                predictions.addAll(response.autocompletePredictions)
                adapter.clear()
                adapter.addAll(predictions.map { it.getFullText(null).toString() })
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                displayErrorMsg("Autocomplete error: ${exception.message}")
            }
    }

    private fun fetchPlaceDetails(placeId: String) {
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.LOCATION,
            Place.Field.SHORT_FORMATTED_ADDRESS,
            Place.Field.NATIONAL_PHONE_NUMBER,
            Place.Field.RATING,
            Place.Field.USER_RATING_COUNT
        )
        val request = FetchPlaceRequest.builder(placeId, placeFields).build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                val resultIntent = android.content.Intent().apply {
                    putExtra("eventid", eventkey)
                    putExtra("place_name", place.displayName)
                    putExtra("place_id", place.id)
                    putExtra("place_latitude", place.location?.latitude)
                    putExtra("place_longitude", place.location?.longitude)
                    putExtra("place_address", place.shortFormattedAddress)
                    putExtra("place_phone", place.nationalPhoneNumber)
                    putExtra("place_rating", place.rating!!)
                    putExtra("place_userrating", place.userRatingCount!!)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
            .addOnFailureListener { exception ->
                displayErrorMsg("Failed to get place details: ${exception.message}")
            }
    }

    private fun displayErrorMsg(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
