package com.example.weather.view.view.model

class RepositoryImpl : Repository {
    override fun getWeatherFromServer() = Weather()
    override fun getWeatherFromLocalSourceRus() = getRussianCities()
    override fun getWeatherFromLocalSourceWorld() = getWorldCities()
}