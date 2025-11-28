package com.example.myweatherapp.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

/**
 * A helper class to simplify location-related tasks.
 * It handles permission checks and fetching the last known location.
 */
class LocationHelper(private val context: Context) {

    // The main client for getting location data
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    /**
     * Checks if the app already has the necessary location permissions.
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Requests the location permissions from the user.
     * We'll need to handle the result of this in MainActivity.
     */
    fun requestLocationPermission(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            requestCode
        )
    }

    /**
     * Tries to get the last known location of the device.
     *
     * @param onSuccess A callback function to run if the location is found.
     * @param onError A callback function to run if there's an error.
     */
    @SuppressWarnings("MissingPermission") // We check for permission before calling this
    fun getLastLocation(
        onSuccess: (lat: Double, lon: Double) -> Unit,
        onError: (String) -> Unit
    ) {
        if (hasLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // Location found! Call the success callback.
                        onSuccess(location.latitude, location.longitude)
                    } else {
                        // Location is null, which can happen if GPS was just turned on.
                        onError("Could not get location. Please turn on GPS and try again.")
                        Log.e("LocationHelper", "Last location is null.")
                    }
                }
                .addOnFailureListener { e ->
                    // Failed to get location
                    onError("Failed to get location: ${e.message}")
                    Log.e("LocationHelper", "Failed to get location", e)
                }
        } else {
            // This should not happen if we check permissions first, but it's a good safeguard.
            onError("Location permission not granted.")
            Log.e("LocationHelper", "Attempted to get location without permission.")
        }
    }
}