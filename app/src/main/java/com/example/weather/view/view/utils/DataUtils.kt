package com.example.weather.view.view.utils

import com.example.weather.view.view.model.*
import com.example.weather.view.view.room.HistoryEntity


fun convertDtoToModel(weatherDTO: WeatherDTO): List<Weather> {
    val fact: FactDTO = weatherDTO.fact!!
    return listOf(Weather(getDefaultCity(), fact.temp!!, fact.feels_like!!, fact.humidity!!, fact.wind_speed!!, fact.condition!!, fact.daytime!!))
}

fun convertHistoryEntityToWeather(entityList: List<HistoryEntity>): List<Weather> {
    return entityList.map {
        Weather(City(it.city, "default", 0.0, 0.0), it.temperature, 0,"","",it.condition,"" )
    }
}

fun convertWeatherToEntity(weather: Weather) : HistoryEntity {
    return HistoryEntity(0,weather.city.name, weather.temp, weather.condition)
}
