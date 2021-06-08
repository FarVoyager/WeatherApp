package com.example.weather.view.view.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.view.view.app.App.Companion.getHistoryDao
import com.example.weather.view.view.model.Weather
import com.example.weather.view.view.model.WeatherDTO
import com.example.weather.view.view.repository.*
import com.example.weather.view.view.utils.convertDtoToModel
import com.google.gson.Gson
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

private const val SERVER_ERROR = "Ошибка сервера"
private const val REQUEST_ERROR = "Ошибка запроса на сервер"
private const val CORRUPTED_DATA = "Неполные данные"

class DetailsViewModel( //открытие коструктора
    //Property #1. LiveData позволяет подписываться на события элемента типа <?> и уведомлять о его изменении
    val detailsLiveData: MutableLiveData<AppState> = MutableLiveData(),
    //Property #2
    private val detailsRepositoryImpl: DetailsRepository = DetailsRepositoryImpl(RemoteDataSource()),
    //Property #3
    private val historyRepository: LocalRepository = LocalRepositoryImpl(getHistoryDao())
//закрытие конструктора
) : ViewModel() {
    //тело класса


    fun getWeatherFromRemoteSource(lat: Double, lon: Double) {
        detailsLiveData.value = AppState.Loading  //обновляем состояние AppState, liveData слушает
        detailsRepositoryImpl.getWeatherDetailsFromServer(lat, lon, callBack)  // через интерфейс вызываем метод получения данных у API
    }

    fun saveCityToDB(weather: Weather) {
        historyRepository.saveEntity(weather)
    }

    private val callBack = object :
        Callback<WeatherDTO> {

        override fun onResponse(call: Call<WeatherDTO>, response: Response<WeatherDTO>) {
            val serverResponse: WeatherDTO? = response.body()
            detailsLiveData.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    checkResponse(serverResponse)
                } else {
                    AppState.Error(Throwable(SERVER_ERROR))
                }
            )
        }

        override fun onFailure(call: Call<WeatherDTO>, t: Throwable) {
            detailsLiveData.postValue(AppState.Error(Throwable(t.message ?: REQUEST_ERROR)))
        }

        private fun checkResponse(serverResponse: WeatherDTO): AppState {
            val fact = serverResponse.fact

            return if (fact?.temp == null || fact.feels_like == null || fact.condition.isNullOrEmpty() || fact.daytime == null) {
                AppState.Error(Throwable(CORRUPTED_DATA))
            } else {
                AppState.Success(convertDtoToModel(serverResponse))

            }
        }
    }
}