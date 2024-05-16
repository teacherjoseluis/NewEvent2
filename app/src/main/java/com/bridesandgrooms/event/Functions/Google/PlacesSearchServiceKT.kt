package com.bridesandgrooms.event.Functions.Google

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks.whenAllComplete
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

class PlacesSearchServiceKT(private val context: Context) {
    private val placesClient = PlacesSearchService(context)
    private val fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    companion object {
        const val TAG = "PlacesSearchServiceKT"
    }

    interface PlacesSearchCallback {
        fun onPlacesFound(places: List<Place>, location: Location)
        fun onError(error: String)
    }

    fun fetchPlaces(query: String, callback: PlacesSearchCallback) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback.onError("Location permission not granted")
            Log.d(TAG, "Location permission not granted")
            return
        }

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Log.d(TAG, "Searching on location {${location.altitude},${location.latitude}}")
                    val latLng = LatLng(location.latitude, location.longitude)
                    placesClient.searchPlacesNearby(latLng, query, 10, object : PlacesSearchService.PlacesSearchCallback {
                        override fun onPlacesFound(places: List<Place>) {
                            callback.onPlacesFound(places, location)  // Map Java callback to Kotlin callback
                        }
                        override fun onError(error: String) {
                            callback.onError(error)
                        }
                    })
                } else {
                    callback.onError("Location is null")
                    Log.d(TAG, "Location is null")
                }
            }
            .addOnFailureListener { e ->
                callback.onError("Failed to get location: ${e.message}")
                Log.d(TAG, "Failed to get location: ${e.message}")
            }
    }

//    private fun searchPlacesNearby(location: Location, query: String, callback: PlacesSearchCallback) {
//        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.RATING, Place.Field.ADDRESS)
//        val latLng = LatLng(location.latitude, location.longitude)
//        // Create bounds that are more area-like rather than a single point
//        val latitudeOffset = 0.2252  // approximately 25 km in latitude
//        val longitudeOffset = 0.2503 // approximately 25 km in longitude, adjusted for latitude
//
//        val southwest = LatLng(latLng.latitude - latitudeOffset, latLng.longitude - longitudeOffset)
//        val northeast = LatLng(latLng.latitude + latitudeOffset, latLng.longitude + longitudeOffset)
//        val bounds = LatLngBounds.builder()
//            .include(southwest)
//            .include(northeast)
//            .build()
//
//        val sessionToken = AutocompleteSessionToken.newInstance() // For managing session in autocomplete
//
//        val request = FindAutocompletePredictionsRequest.builder()
//            .setQuery(query) // Broader query
//            .setLocationRestriction(RectangularBounds.newInstance(bounds))
//            .setLocationBias(RectangularBounds.newInstance(bounds)) // Using bias instead of restriction for more flexibility
//            .setTypeFilter(TypeFilter.ESTABLISHMENT) // Focus on establishments
//            .setSessionToken(sessionToken) // Use session token if applicable
//            .build()
//
//        Log.d(TAG, "Making the request to places: ${request.toString()}")
//        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
//            val places = mutableListOf<Place>()
//            val tasks = mutableListOf<Task<FetchPlaceResponse>>()
//            response.autocompletePredictions.forEach { prediction ->
//                val fetchTask = placesClient.fetchPlace(FetchPlaceRequest.builder(prediction.placeId, fields).build())
//                    .addOnSuccessListener { fetchResponse ->
//                        Log.d(TAG, "Response from Places: ${response.toString()}")
//                        places.add(fetchResponse.place)
//                        if (places.size == response.autocompletePredictions.size) {
//                            callback.onPlacesFound(places)
//                        }
//                    }
//                    .addOnFailureListener { e ->
//                        callback.onError("Failed to fetch place details: ${e.message}")
//                        Log.d(TAG, "Failed to fetch place details: ${e.message}")
//                    }
//                tasks.add(fetchTask)
//            }
//            whenAllComplete(tasks)
//                .addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        places.sortBy { place -> place.latLng?.let { latLng -> location.distanceTo(Location("").apply { latitude = latLng.latitude; longitude = latLng.longitude }) } }
//                        callback.onPlacesFound(places)
//                    }
//                }
//        }.addOnFailureListener { e ->
//            callback.onError("Place predictions failure: ${e.message}")
//            Log.d(TAG, "Place predictions failure: ${e.message}")
//        }
//    }
}
