package com.example.weather.view.view.model

interface Repository {
    fun getWeatherFromServer() : Weather
    fun getWeatherFromLocalSource() : Weather
}