package com.example.weather.view.view.model

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

class WeatherLoader(
    private val listener: WeatherModelListener,
    private val lat: Double,
    private val lon: Double
) {

    interface WeatherModelListener {
        fun onLoaded(weatherDTO: WeatherDTO)
        fun onFailed(throwable: Throwable)
    }

    //метод загрузки данных с API и преобразования их в модель данных
    //в конце метода вызывается displayWeather
    @RequiresApi(Build.VERSION_CODES.N)
    fun loadWeather() {
        try {
            //создаем переменную со значением адреса
            val uri = URL("https://api.weather.yandex.ru/v2/informers?lat=${lat}&lon=${lon}")
            //создаем Handler чтобы позже обратиться из основного потока
            val handler = Handler(Looper.getMainLooper())
            //открываем новый поток
            Thread {
                //создаем экземпляр HttpsURLConnection
                var urlConnection: HttpsURLConnection? = null
                //оборачиваем в try чтобы обработать возможный эксепшн
                try {
                    //открываем соединение
                    urlConnection = uri.openConnection() as HttpsURLConnection
                    //определяем тип обращения к серверу
                    urlConnection.requestMethod = "GET"
                    //задаем ключ для доступа к API
                    //ключ теперь хранится не в теле фрагмента, а в отдельном файле проекта в целях безопасности
                    urlConnection.addRequestProperty("X-Yandex-API-Key", com.example.weather.BuildConfig.WEATHER_API_KEY)
                    //определяем таймаут на чтение
                    urlConnection.readTimeout = 10000
                    //преобразовываем входящий поток данных в набор символов
                    val bufferedReader =
                        BufferedReader(InputStreamReader(urlConnection.inputStream))
                    println(bufferedReader)
                    print("HUI")

                    //преобразование ответа от сервера (JSON) в модель данных (WeatherDTO)
                    val weatherDTO: WeatherDTO =
                        Gson().fromJson(getLines(bufferedReader), WeatherDTO::class.java)
                    //обращаемся к основному потоку и в нем вызываем метод вывода информации о погоде
                    handler.post { listener.onLoaded(weatherDTO) }
                } catch (e: Exception) {
                    Log.e(null, "Fail connection", e)
                    e.printStackTrace()
                } finally {
                    urlConnection?.disconnect()
                }
            }.start()
        } catch (e: MalformedURLException) {
            Log.e(null, "Fail URI", e)
            e.printStackTrace()
            listener.onFailed(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }

}