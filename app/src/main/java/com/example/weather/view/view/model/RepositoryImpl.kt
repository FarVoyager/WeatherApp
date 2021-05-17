package com.example.weather.view.view.model

class RepositoryImpl : Repository {
    override fun getWeatherFromServer() : Weather {
        return Weather()
    }

    override fun getWeatherFromLocalSource() : Weather {
        return Weather()
    }
}