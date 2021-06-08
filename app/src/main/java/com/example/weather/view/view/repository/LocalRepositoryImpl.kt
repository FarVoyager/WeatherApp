package com.example.weather.view.view.repository

import com.example.weather.view.view.model.City
import com.example.weather.view.view.model.Weather
import com.example.weather.view.view.room.HistoryDao
import com.example.weather.view.view.room.HistoryEntity
import com.example.weather.view.view.utils.convertHistoryEntityToWeather
import com.example.weather.view.view.utils.convertWeatherToEntity

class LocalRepositoryImpl(private val localDataSource: HistoryDao) :LocalRepository {
    override fun getAllHistory(): List<Weather> {
        return convertHistoryEntityToWeather(localDataSource.all())
    }

    override fun saveEntity(weather: Weather) {
        localDataSource.insert(convertWeatherToEntity(weather))
    }

}