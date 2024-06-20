package com.bridesandgrooms.event.UI.Fragments

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.Google.PlacesSearchServiceKT
import com.bridesandgrooms.event.Functions.PermissionUtils
import com.bridesandgrooms.event.Model.Guest
import com.bridesandgrooms.event.Model.Permission
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.Model.Vendor
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.Adapters.SearchVendorAdapter
import com.bridesandgrooms.event.databinding.SearchVendorsFragmentBinding
import com.google.android.libraries.places.api.model.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.pow

class SearchVendorFragment : Fragment(), SearchVendorFragmentActionListener {

    private lateinit var context: Context
    private lateinit var binding: SearchVendorsFragmentBinding
    private lateinit var recyclerViewSearchVendor: RecyclerView
    private lateinit var rvAdapter: SearchVendorAdapter
    private lateinit var user: User

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        context = requireContext()
        user = User().getUser(context)
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

        if (!PermissionUtils.checkPermissions(context, "location")) {
            val permissions = PermissionUtils.requestPermissionsList("location")
            requestPermissions(permissions, PERMISSION_CODE)
        } else {
            searchVendors()
        }
    }

    /**
     * This handles the permissions result and displays an information asking the user to grant location permissions in case the app is not able to make the search because of that reason
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    val packageName = requireContext().packageName
                    val locationPermissions = Permission.getPermission("location")
                    val resourceId = this.resources.getIdentifier(
                        locationPermissions.drawable, "drawable", packageName
                    )
                    val language = this.resources.configuration.locales.get(0).language
                    val permissionWording = when (language) {
                        "en" -> locationPermissions.permission_wording_en
                        else -> locationPermissions.permission_wording_es
                    }

                    binding.loadingScreen.visibility = ConstraintLayout.GONE
                    binding.noPermissionsLayout.visibility = ConstraintLayout.VISIBLE
                    binding.noPermissionsLayout.findViewById<ImageView>(R.id.permissionicon)
                        .setImageResource(resourceId)
                    binding.noPermissionsLayout.findViewById<TextView>(R.id.permissionwording).text =
                        permissionWording
                    binding.noPermissionsLayout.findViewById<Button>(R.id.permissionsbutton)
                        .setOnClickListener {
                            // Create an intent to open the app settings for your app
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            // Start the intent
                            startActivity(intent)
                        }
                } else {
                    searchVendors()
                }
            }
        }
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

    /**
     * Coroutine that given a selection of the kind of provider needed, and what is currently loaded from the CMS via the Category class, it gets Place details from Google Places SDK
     * @param query Search criteria
     * @return A list of pairs consisting of the Place object and the current user Location
     */
    private suspend fun fetchPlaces(query: String): Pair<List<Place>, Location> {
        return suspendCoroutine { continuation ->
            val placesSearchService = PlacesSearchServiceKT(requireContext())
            Log.d(TAG, "Query: $query")
            placesSearchService.fetchPlaces(
                query,
                object : PlacesSearchServiceKT.PlacesSearchCallback {
                    override fun onPlacesFound(places: List<Place>, location: Location) {
                        continuation.resume(Pair(places, location))
                    }

                    override fun onError(error: String) {
                        continuation.resumeWithException(Exception("Error fetching places: $error"))
                    }
                })
        }
    }

    /**
     * Once the List of Places is obtained from the SDK, this method sorts them by the closeness to the user's location and feeds them to SearchVendorAdapter
     * @param placesAndLocation List of pairs consisting of the Place object and the current user Location
     * @param category This is the category that will be displayed in the Viewholder
     */
    private fun handlePlaces(placesAndLocation: Pair<List<Place>, Location>, category: String) {
        val places = placesAndLocation.first
        val location = placesAndLocation.second

        Log.d(TAG, "Number of places: ${places.count()}")
        binding.loadingScreen.visibility = ConstraintLayout.GONE

        if (places.isEmpty()) {
            showNoVendorsFoundView()
        } else {
            val showKms = when (user.distanceunit) {
                "miles" -> false
                "kilometers" -> true
                else -> false
            }

            val placesWithDistances = places.map { place ->
                val placeLat = place.latLng?.latitude // Assuming these methods exist
                val placeLng = place.latLng?.longitude
                val distance = "${
                    calculateDistance(
                        location.latitude,
                        location.longitude,
                        placeLat!!,
                        placeLng!!,
                        showKms
                    )
                } ${if (!showKms) "mi" else "km"}"
                Pair(place, distance)
            }
            // Sort places by distance
            val sortedPlaces = placesWithDistances.sortedByDescending { it.second }

            // Update your RecyclerView adapter here
            recyclerViewSearchVendor = binding.recyclerViewSearchVendors
            recyclerViewSearchVendor.apply {
                layoutManager = LinearLayoutManager(binding.root.context).apply {
                    reverseLayout = true
                }
            }
            //Making the call to the VendorSearchAdapter. Takes Places
            try {
                rvAdapter = SearchVendorAdapter(this, sortedPlaces, category, context)
                rvAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                println(e.message)
            }
            recyclerViewSearchVendor.adapter = null
            recyclerViewSearchVendor.adapter = rvAdapter
        }
    }

    /**
     * This function gets the query from SearchVendorTab and in an asynchronous function calls fetchplaces()
     */
    private fun searchVendors() {
        val query = requireArguments().getString("query")!!
        val category = requireArguments().getString("category")!!
        lifecycleScope.launch {
            try {
                val places = withContext(Dispatchers.IO) {
                    fetchPlaces(query)
                }
                handlePlaces(places, category)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching places: ${e.message}")
                showNoVendorsFoundView()
            }
        }
    }

    /**
     * This function is called when there are no results to show and turns visibility on a message saying that there were no results for the category near the user
     */
    private fun showNoVendorsFoundView() {
        binding.noVendorsLayout.visibility = ConstraintLayout.VISIBLE

        val emptystate_message = binding.emptystateFragment.emptystateMessage
        val emptystate_cta = binding.emptystateFragment.emptystateCta
        val fab_action = binding.emptystateFragment.fabAction

        emptystate_message.text = getString(R.string.emptystate_nosearchvendors)
        emptystate_cta.visibility = ConstraintLayout.INVISIBLE
        fab_action.visibility = ConstraintLayout.INVISIBLE
    }

    /**
     * Helps in the calculation of distance between each Place and the User's location, depending on the parameters, the results can be obtained in Kms or Mi
     * @param inKilometers If False it returns results in Miles
     */
    private fun calculateDistance(
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

    override fun onVendorAdded(vendor: Vendor) {
        Toast.makeText(
            context,
            getString(R.string.searchvendor_addedmsg, vendor.name),
            Toast.LENGTH_SHORT
        ).show()
    }
}

interface SearchVendorFragmentActionListener {
    fun onVendorAdded(vendor: Vendor)
}

