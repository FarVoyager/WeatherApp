package com.example.weather.view.view.repository

import com.example.weather.view.view.model.Weather
import com.example.weather.view.view.room.HistoryDao
import com.example.weather.view.view.utils.convertHistoryEntityToWeather
import com.example.weather.view.view.utils.convertWeatherToEntity
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class LocalRepositoryImpl(private val localDataSource: HistoryDao) :LocalRepository {

    //реализуем Callable + Future т.к. они позволяют вернуть результат из стороннего потока
    override fun getAllHistory(): List<Weather> {
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val future = executor.submit<List<Weather>>{
            convertHistoryEntityToWeather(localDataSource.all())
        }
        return future.get()
    }

    //метод ничего не возвращает, поэтому просто выделяем отдельный поток
    override fun saveEntity(weather: Weather) {
        Thread{
            localDataSource.insert(convertWeatherToEntity(weather))
        }.start()

    }

}