package com.example.weather.view.view.repository

import com.example.weather.view.view.model.Weather
import com.example.weather.view.view.model.getRussianCities
import com.example.weather.view.view.model.getWorldCities

class MainRepositoryImpl : MainRepository {
    override fun getWeatherFromServer() = Weather()
    override fun getWeatherFromLocalSourceRus() = getRussianCities()
    override fun getWeatherFromLocalSourceWorld() = getWorldCities()
}