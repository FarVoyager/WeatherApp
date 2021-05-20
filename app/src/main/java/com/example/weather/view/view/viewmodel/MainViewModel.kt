package com.example.weather.view.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.view.view.model.Repository
import com.example.weather.view.view.model.RepositoryImpl
import java.lang.Thread.sleep

class MainViewModel(
    //1-ый аргумент - LiveData с нашим классом AppState
    //LiveData позволяет подписаться на изменения состояний класса (AppState)
    private val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData(),
    // 2-ой аргумент - RepositoryImpl типа интерфейса Repository
    private val repositoryImpl: Repository = RepositoryImpl()
) : ViewModel() { //класс MainViewModel возвращает объект класса ViewModel

    //метод getLiveData просто возвращает 1-ый аргумент
    fun getLiveData() = liveDataToObserve

    //методы получения класса Weather из локального хранилища и с удаленного хранилища
    fun getWeatherFromLocalSource() = getDataFromLocalSource()
    fun getWeatherFromRemoteSource() = getDataFromLocalSource()

    private fun getDataFromLocalSource() {
        liveDataToObserve.value = AppState.Loading
        Thread {
            sleep(1000)
            liveDataToObserve.postValue(AppState.Success(repositoryImpl.getWeatherFromLocalSource()))
        }.start()
    }
}