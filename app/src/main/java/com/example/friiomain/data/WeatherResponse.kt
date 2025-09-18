package com.example.friiomain.data

data class WeatherResponse(
    val weather: List<WeatherDescription>,
    val main: WeatherMain
)

data class WeatherDescription(
    val description: String
)

data class WeatherMain(
    val temp: Double
)
