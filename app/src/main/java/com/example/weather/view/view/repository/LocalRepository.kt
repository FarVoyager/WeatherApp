package com.example.weather.view.view.repository

import com.example.weather.view.view.model.Weather

interface LocalRepository {
    fun getAllHistory() : List<Weather>
    fun saveEntity(weather: Weather)
}