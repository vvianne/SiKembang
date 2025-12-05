package com.example.sikembang.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine

class LocationHelper(private val context: Context) {

    suspend fun getCurrentLocation(): Pair<Double, Double>? {
        return suspendCancellableCoroutine { cont ->
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            cont.resume(Pair(location.latitude, location.longitude)) {}
                        } else {
                            cont.resume(null) {}
                        }
                    }
                    .addOnFailureListener {
                        cont.resume(null) {}
                    }
            } catch (e: SecurityException) {
                cont.resume(null) {}
            }
        }
    }
}

