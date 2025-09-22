package com.example.friiomain.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.friiomain.data.WeatherRepository
import com.example.friiomain.data.WeatherResponse
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await


suspend fun loadWeather(context: Context): WeatherResponse? {
    return try {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return null

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val location: Location? = fusedLocationClient.lastLocation.await()

        if (location != null) {
            val repo = WeatherRepository("4731afa59235bbee6a194fc02cff4f8b")
            repo.getWeather(location.latitude, location.longitude)
        } else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
