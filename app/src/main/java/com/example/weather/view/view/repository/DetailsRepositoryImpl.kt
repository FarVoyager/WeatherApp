package com.example.weather.view.view.repository

import javax.security.auth.callback.Callback

class DetailsRepositoryImpl(private val remoteDataSource: RemoteDataSource) : DetailsRepository {
    override fun getWeatherDetailsFromServer(requestLink: String, callback: okhttp3.Callback) {
        remoteDataSource.getWeatherDetails(requestLink, callback)
    }

}