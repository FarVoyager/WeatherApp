package com.example.weather.view.view.repository

import javax.security.auth.callback.Callback

interface DetailsRepository {
    fun getWeatherDetailsFromServer (requestLink: String, callback: okhttp3.Callback)
}