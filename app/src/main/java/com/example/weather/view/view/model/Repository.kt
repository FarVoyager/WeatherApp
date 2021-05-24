package com.example.weather.view.view.model

//Repository - интерфейс для взаимодействия между view и viewmodel
//методы реализуются в классе RepositoryImpl
interface Repository {
    fun getWeatherFromServer() : Weather
    fun getWeatherFromLocalSourceRus() : List<Weather>
    fun getWeatherFromLocalSourceWorld() : List<Weather>

}