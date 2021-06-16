package com.example.weather.view.view.model

import com.example.weather.BuildConfig
import com.example.weather.view.view.repository.WeatherAPI
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
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
        .client(createInterceptor(WeatherApiInterceptor())) //вызываем интерсептор для вывода данных запрос в лог
        .build().create(WeatherAPI::class.java) //определяем какого типа нужны данные

    //метод достраивает строку запроса к API с помощью интерфейса WeatherAPI
    fun getWeatherDetails(lat: Double, lon: Double, callback: Callback<WeatherDTO>) {
        weatherApi.getWeather(BuildConfig.WEATHER_API_KEY, lat, lon).enqueue(callback)
    }

    // интерсептор может понадобиться для отображения данных в логах
    private fun createInterceptor(interceptor: Interceptor) : OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor)
        httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        return httpClient.build()
    }

    inner class WeatherApiInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            return chain.proceed(chain.request())
        }

    }

}