package com.example.weather.view.view.repository

import com.example.weather.view.view.model.Weather

//Repository - интерфейс для взаимодействия между view и viewmodel
//методы реализуются в классе RepositoryImpl
interface MainRepository {
    fun getWeatherFromServer() : Weather
    fun getWeatherFromLocalSourceRus() : List<Weather>
    fun getWeatherFromLocalSourceWorld() : List<Weather>

}