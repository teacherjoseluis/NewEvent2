package com.bridesandgrooms.event.UI.Fragments

import android.content.Context
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.Google.PlacesSearchServiceKT
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.TaskPaymentPayments
import com.bridesandgrooms.event.UI.Adapters.SearchVendorAdapter
import com.bridesandgrooms.event.UI.SwipeControllerTasks
import com.bridesandgrooms.event.VendorsAll
import com.bridesandgrooms.event.databinding.SearchVendorsFragmentBinding
import com.google.android.libraries.places.api.model.Place
import java.lang.Exception
import kotlin.math.pow

class SearchVendorFragment : Fragment() {

    private lateinit var context: Context
    private lateinit var binding: SearchVendorsFragmentBinding

    private lateinit var recyclerViewSearchVendor: RecyclerView
    private lateinit var rvAdapter: SearchVendorAdapter

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        context = requireContext()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.search_vendors_fragment, container, false)
        return binding.root 
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val query = requireArguments().getString("query")!!
        val category = requireArguments().getString("category")!!
        //val query = "wedding venues"
        val placesSearchService = PlacesSearchServiceKT(context)
        Log.d(TAG, "Query: $query")
        placesSearchService.fetchPlaces(query, object : PlacesSearchServiceKT.PlacesSearchCallback {
            override fun onPlacesFound(places: List<Place>, location: Location) {
                Log.d(TAG, "Number of places: ${places.count()}")

                val placesWithDistances = places.map { place ->
                    val placeLat = place.latLng?.latitude // Assuming these methods exist
                    val placeLng = place.latLng?.longitude
                    val distance = calculateDistance(location.latitude, location.longitude, placeLat!!, placeLng!!, false)
                    Pair(place, distance)
                }
                // Sort places by distance
                val sortedPlaces = placesWithDistances.sortedByDescending{it.second}

                // Update your RecyclerView adapter here
                recyclerViewSearchVendor = binding.recyclerViewSearchVendors
                recyclerViewSearchVendor.apply {
                    layoutManager = LinearLayoutManager(binding.root.context).apply {
                        reverseLayout = true
                    }
                }
                //Making the call to the VendorSearchAdapter. Takes Places
                try {
                    rvAdapter = SearchVendorAdapter(sortedPlaces, category, context)
                    rvAdapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    println(e.message)
                }
                recyclerViewSearchVendor.adapter = null
                recyclerViewSearchVendor.adapter = rvAdapter

            }

            override fun onError(error: String) {
                //Toast.makeText(applicationContext, "Error: $error", Toast.LENGTH_LONG).show()
                //TODO "Emprty framgent saying that Google couldn't retrieve any results"
            }
        })
    }


    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
        inKilometers: Boolean = false
    ): Double {
        val earthRadiusKm = 6371.0
        val earthRadiusMiles = 3958.8

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val originLat = Math.toRadians(lat1)
        val destinationLat = Math.toRadians(lat2)

        val a = Math.sin(dLat / 2).pow(2) +
                Math.sin(dLon / 2).pow(2) * Math.cos(originLat) * Math.cos(destinationLat)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        val distance = if (inKilometers) earthRadiusKm * c else earthRadiusMiles * c
        // Round down to two decimal places
        return Math.floor(distance * 100) / 100
    }

    fun finish() {
        val fragment = VendorsAll()
        Handler(Looper.getMainLooper()).postDelayed({
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }, 500)
    }

    companion object {
        //Permission code
        internal const val PERMISSION_CODE = 1001
        const val SCREEN_NAME = "Search Vendor"
        const val TAG = "Search Vendor Fragment"
    }
}

