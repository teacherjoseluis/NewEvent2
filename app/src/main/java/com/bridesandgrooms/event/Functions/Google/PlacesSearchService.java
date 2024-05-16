package com.bridesandgrooms.event.Functions.Google;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.SearchByTextRequest;

import java.util.Arrays;
import java.util.List;

public class PlacesSearchService {

    private final PlacesClient placesClient;

    public PlacesSearchService(Context context) {
        //Places.initialize(context, "YOUR_API_KEY");
        placesClient = Places.createClient(context);
    }

    public void searchPlacesNearby(LatLng center, String query, int maxResults, PlacesSearchCallback callback) {
        double latitudeOffset = 0.2252;  // approximately 25 km in latitude
        double longitudeOffset = 0.2503; // approximately 25 km in longitude

        LatLng southwest = new LatLng(center.latitude - latitudeOffset, center.longitude - longitudeOffset);
        LatLng northeast = new LatLng(center.latitude + latitudeOffset, center.longitude + longitudeOffset);

        RectangularBounds bounds = RectangularBounds.newInstance(southwest, northeast);

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.RATING, Place.Field.ADDRESS);

        SearchByTextRequest request = SearchByTextRequest.builder(query, placeFields)
                .setMaxResultCount(maxResults)
                .setLocationRestriction(bounds)
                .build();

        placesClient.searchByText(request)
                .addOnSuccessListener(response -> {
                    List<Place> places = response.getPlaces();
                    callback.onPlacesFound(places);
                })
                .addOnFailureListener(e -> {
                    callback.onError("Failed to fetch place details: " + e.getMessage());
                });
    }

    public interface PlacesSearchCallback {
        void onPlacesFound(List<Place> places);
        void onError(String error);
    }
}
