package com.example.weather.view.view.repository

import com.example.weather.view.view.model.RemoteDataSource
import com.example.weather.view.view.model.WeatherDTO


class DetailsRepositoryImpl(private val remoteDataSource: RemoteDataSource) : DetailsRepository {
    override fun getWeatherDetailsFromServer(lat: Double, lon: Double, callback: retrofit2.Callback<WeatherDTO>) {
        remoteDataSource.getWeatherDetails(lat, lon, callback)

    }

}