package com.example.weather.view.view.model

data class WeatherDTO (
    val fact: FactDTO?
        )

data class FactDTO (
    val tempFact: Int?,
    val tempSensed: Int?,
    val humidity: String?,
    val wind: String?,
    val clouds: String?
)