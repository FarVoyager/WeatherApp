package com.example.weather.view.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.view.view.repository.MainRepository
import com.example.weather.view.view.repository.MainRepositoryImpl
import java.lang.Thread.sleep

class MainViewModel(
    //1-ый аргумент - LiveData с нашим классом AppState
    //LiveData позволяет подписаться на изменения состояний класса (AppState)
    private val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData(),
    // 2-ой аргумент - RepositoryImpl типа интерфейса Repository
    private val mainRepositoryImpl: MainRepository = MainRepositoryImpl()
) : ViewModel() { //класс MainViewModel возвращает объект класса ViewModel

    //метод getLiveData просто возвращает 1-ый аргумент
    fun getLiveData() = liveDataToObserve

    //методы получения класса Weather из локального хранилища и с удаленного хранилища
    fun getWeatherFromLocalSourceRus() = getDataFromLocalSource(isRussian = true)
    fun getWeatherFromLocalSourceWorld() = getDataFromLocalSource(isRussian = false)

    fun getWeatherFromRemoteSource() = getDataFromLocalSource(isRussian = true)

    private fun getDataFromLocalSource(isRussian : Boolean) {
        liveDataToObserve.value = AppState.Loading
        Thread {
            sleep(1000)
            liveDataToObserve.postValue(AppState.Success(if (isRussian)
                mainRepositoryImpl.getWeatherFromLocalSourceRus()
            else
                mainRepositoryImpl.getWeatherFromLocalSourceWorld()))
        }.start()
    }
}