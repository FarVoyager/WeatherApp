package com.example.weather.view.view.repository

import com.example.weather.BuildConfig
import com.example.weather.view.view.model.WeatherDTO
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//класс реализует запрос к API Яндекса с помощью retrofit и интерфейса WeatherAPI
class RemoteDataSource {

    private val weatherApi = Retrofit.Builder() //объявляем экземпляр
        .baseUrl("https://api.weather.yandex.ru/") //определяем основной адрес
        .addConverterFactory(  //вызываем конвертер форматов
            GsonConverterFactory.create( //опрределяем что преобразовать надо в Gson
                GsonBuilder().setLenient().create() //создаем экземпляр Gson'а
            )
        ) //конец определения конвертера
        .build().create(WeatherAPI::class.java) //определяем какого типа нужны данные

    //метод достраивает строку запроса к API с помощью интерфейса WeatherAPI
    fun getWeatherDetails(lat: Double, lon: Double, callback: Callback<WeatherDTO>) {
        weatherApi.getWeather(BuildConfig.WEATHER_API_KEY, lat, lon).enqueue(callback)
    }
}