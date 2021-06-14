package com.example.weather.view.view.repository

import com.example.weather.view.view.model.WeatherDTO

interface DetailsRepository {
    fun getWeatherDetailsFromServer (lat: Double, lon: Double, callback: retrofit2.Callback<WeatherDTO>)
}