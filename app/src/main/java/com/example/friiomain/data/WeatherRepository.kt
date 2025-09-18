package com.example.friiomain.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository(private val apiKey: String) {

    private val api: WeatherService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(WeatherService::class.java)
    }

    suspend fun getWeather(lat: Double, lon: Double): WeatherResponse {
        return api.getWeather(lat, lon, apiKey)
    }
}
