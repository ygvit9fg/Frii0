package com.example.friiomain.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request

// --- Модели ответа ---
data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>
)

data class Main(
    val temp: Double
)

data class Weather(
    @SerializedName("description") val description: String
)

// --- Репозиторий ---
class WeatherRepository(private val apiKey: String) {
    private val client = OkHttpClient()

    fun getWeather(lat: Double, lon: Double): WeatherResponse {
        val url =
            "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=metric&lang=ru&appid=$apiKey"

        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            val body = response.body?.string() ?: throw Exception("Пустой ответ")
            return Gson().fromJson(body, WeatherResponse::class.java)
        }
    }
}
