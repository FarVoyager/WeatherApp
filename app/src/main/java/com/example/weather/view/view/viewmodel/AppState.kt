package com.example.weather.view.view.viewmodel

import com.example.weather.view.view.model.Weather

sealed class AppState {
    data class Success(val weatherData: Weather) : AppState()
    data class Error(val message: Throwable) : AppState()
    object Loading : AppState()
}