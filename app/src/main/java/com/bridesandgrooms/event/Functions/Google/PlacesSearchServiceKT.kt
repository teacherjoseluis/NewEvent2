package com.bridesandgrooms.event.Functions.Google

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.bridesandgrooms.event.Functions.PermissionUtils
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
    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    companion object {
        const val TAG = "PlacesSearchServiceKT"
        const val LOCATION_PERMISSION_CODE = 102
    }

    interface PlacesSearchCallback {
        fun onPlacesFound(places: List<Place>, location: Location)
        fun onError(error: String)
    }

    //    fun fetchPlaces(query: String, callback: PlacesSearchCallback) {
//        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            callback.onError("Location permission not granted")
//            Log.d(TAG, "Location permission not granted")
//            return
//        }
//
//        fusedLocationProviderClient.lastLocation
//            .addOnSuccessListener { location: Location? ->
//                if (location != null) {
//                    Log.d(TAG, "Searching on location {${location.altitude},${location.latitude}}")
//                    val latLng = LatLng(location.latitude, location.longitude)
//
//                    val cachedPlaces = PlacesCache()
//                    val listCachedPlaces = cachedPlaces.getPlaces(query)
//                    if (listCachedPlaces.isNotEmpty()) {
//                        callback.onPlacesFound(listCachedPlaces, location)
//                    } else {
//                        placesClient.searchPlacesNearby(
//                            latLng,
//                            query,
//                            10,
//                            object : PlacesSearchService.PlacesSearchCallback {
//                                override fun onPlacesFound(places: List<Place>) {
//                                    cachedPlaces.putPlaces(query, places)
//                                    callback.onPlacesFound(
//                                        places,
//                                        location
//                                    )  // Map Java callback to Kotlin callback
//                                }
//
//                                override fun onError(error: String) {
//                                    callback.onError(error)
//                                }
//                            })
//                    }
//                } else {
//                    callback.onError("Location is null")
//                    Log.d(TAG, "Location is null")
//                }
//            }
//            .addOnFailureListener { e ->
//                callback.onError("Failed to get location: ${e.message}")
//                Log.d(TAG, "Failed to get location: ${e.message}")
//            }
//    }
    fun fetchPlaces(query: String, callback: PlacesSearchCallback) {
        if (!PermissionUtils.checkPermissions(context, "location")) {
            val permissions = PermissionUtils.requestPermissionsList("location")
            if (context is Activity) {
                ActivityCompat.requestPermissions(context, permissions, LOCATION_PERMISSION_CODE)
            }
            callback.onError("Location permission not granted")
            Log.d(TAG, "Location permission not granted")
            return
        }

        try {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        Log.d(TAG, "Searching on location {${location.altitude},${location.latitude}}")
                        val latLng = LatLng(location.latitude, location.longitude)

                        val cachedPlaces = PlacesCache()
                        val listCachedPlaces = cachedPlaces.getPlaces(query)
                        if (listCachedPlaces.isNotEmpty()) {
                            callback.onPlacesFound(listCachedPlaces, location)
                        } else {
                            placesClient.searchPlacesNearby(
                                latLng,
                                query,
                                10,
                                object : PlacesSearchService.PlacesSearchCallback {
                                    override fun onPlacesFound(places: List<Place>) {
                                        cachedPlaces.putPlaces(query, places)
                                        callback.onPlacesFound(places, location)
                                    }

                                    override fun onError(error: String) {
                                        callback.onError(error)
                                    }
                                }
                            )
                        }
                    } else {
                        callback.onError("Location is null")
                        Log.d(TAG, "Location is null")
                    }
                }
                .addOnFailureListener { e ->
                    callback.onError("Failed to get location: ${e.message}")
                    Log.d(TAG, "Failed to get location: ${e.message}")
                }
        } catch (e: SecurityException) {
            callback.onError("Security exception when accessing location")
            Log.e(TAG, "Security exception: ${e.message}")
        }
    }
}

class PlacesCache {
    private val cache = mutableMapOf<String, Pair<Long, List<Place>>>()
    private val cacheDuration = 300_000L  // 5 minutes in milliseconds

    fun getPlaces(query: String): List<Place> {
        return cache[query]?.let {
            if (System.currentTimeMillis() - it.first < cacheDuration) {
                it.second  // Return the cached list of places
            } else {
                cache.remove(query)  // Expire the cache entry
                emptyList()  // Return an empty list instead of null
            }
        } ?: emptyList()  // Return an empty list if there's no cache entry
    }

    fun putPlaces(query: String, places: List<Place>) {
        cache[query] = Pair(System.currentTimeMillis(), places)
    }
}
