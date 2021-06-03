package com.example.weather.view.view.utils

import com.example.weather.view.view.model.FactDTO
import com.example.weather.view.view.model.Weather
import com.example.weather.view.view.model.WeatherDTO
import com.example.weather.view.view.model.getDefaultCity


fun convertDtoToModel(weatherDTO: WeatherDTO): List<Weather> {
    val fact: FactDTO = weatherDTO.fact!!
    return listOf(Weather(getDefaultCity(), fact.temp!!, fact.feels_like!!, fact.humidity!!, fact.wind_speed!!, fact.condition!!))
}