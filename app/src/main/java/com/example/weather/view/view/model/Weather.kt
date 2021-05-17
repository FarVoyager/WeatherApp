package com.example.weather.view.view.model

data class Weather(
    val city: City = getDefaultCity(),
    val temperatureFact: String = "+25",
    val temperatureSensed: String = "+21",
    val humidity: String = "68%",
    val wind: String = "3 м/с  ЮЗ",
    val clouds: String = "rainy"
        )

fun getDefaultCity() = City("Москва", "European")