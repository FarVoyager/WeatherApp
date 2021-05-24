package com.example.weather.view.view.model

class RepositoryImpl : Repository {
    override fun getWeatherFromServer() : Weather {
        return Weather()
    }

    override fun getWeatherFromLocalSourceRus() : List<Weather> {
        return getRussianCities()
    }
    override fun getWeatherFromLocalSourceWorld() : List<Weather> {
        return getWorldCities()
    }
}