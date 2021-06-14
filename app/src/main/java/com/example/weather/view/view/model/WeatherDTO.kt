package com.example.weather.view.view.model

data class WeatherDTO(
    val fact: FactDTO?,

)


data class FactDTO(
    val temp: Int?,
    val feels_like: Int?,
    val humidity: String?,
    val wind_speed: String?,
    val condition: String?,
    val daytime: String?
)