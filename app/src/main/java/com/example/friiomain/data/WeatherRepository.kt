package com.example.friiomain.data

import com.example.weatherfriends.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository {
    private val api: WeatherService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(WeatherService::class.java)
    }

    suspend fun getWeather(city: String): WeatherResponse {
        return api.getWeather(city, BuildConfig.WEATHER_API_KEY)
    }
}
