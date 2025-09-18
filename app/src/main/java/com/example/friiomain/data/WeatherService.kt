package com.example.friiomain.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query



interface WeatherService {
    @GET("data/2.5/weather?units=metric&lang=ru&appid=ТВОЙ_API_KEY")
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Call<WeatherResponse>
}

