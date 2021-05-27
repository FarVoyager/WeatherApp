package com.example.weather.view.view.view

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.weather.R
import com.example.weather.databinding.FragmentDetailsBinding
import com.example.weather.view.view.model.Weather
import com.example.weather.view.view.model.WeatherDTO
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

//ключ разработчика для доступа к API Яндекс.Погоды
private const val API_KEY = "df509bf9-5916-4a47-a62c-65cf24f5d49c"

class DetailsFragment : Fragment() {

    //binding - аналог findViewById, конструкция ниже нужна в том числе для ситуаций когда binding = null
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherBundle : Weather

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //создается переменная типа Weather, которой присваивается сохраненный ранее Weather в Bundle, т.е. город, на который мы нажали
        //элвис в данном случае присвоит weatherBundle значение по города умолчанию если бандл будет null
        weatherBundle = arguments?.getParcelable(BUNDLE_EXTRA) ?: Weather()
        //экран заполняется данными из только что взятого из Bundle экземпляра класса Weather
        binding.container.visibility = View.GONE
        binding.loadingLayout.visibility = View.VISIBLE
        loadWeather()
    }

    //метод для отображения данных погоды на экране
    private fun displayWeather(weatherDTO: WeatherDTO) {
        binding.apply {
            container.visibility = View.VISIBLE
            loadingLayout.visibility = View.GONE
            val city = weatherBundle.city
            cityName.text = city.name

            windInfo.text = weatherDTO.fact?.wind
            humidityInfo.text = weatherDTO.fact?.humidity
            temperatureFactInfo.text = weatherDTO.fact?.tempFact.toString()
            temperatureSensedInfo.text = weatherDTO.fact?.tempSensed.toString()

            when (weatherDTO.fact?.clouds) {
                "sunny" -> {
                    binding.backgroundWeatherFrame.setBackgroundResource(R.drawable.sunny)
                }
                "cloudy" -> {
                    binding.backgroundWeatherFrame.setBackgroundResource(R.drawable.cloudy)
                }
                "rainy" -> {
                    binding.backgroundWeatherFrame.setBackgroundResource(R.drawable.rainy)
                }
                else -> {
                    binding.backgroundWeatherFrame.setBackgroundResource(R.drawable.sunny)
                }
            }
        }
    }

    //метод загрузки данных с API и преобразования их в модель данных
    //в конце метода вызывается displayWeather
    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadWeather() {
        try {
            //создаем переменную со значением адреса
            val uri = URL("https://api.weather.yandex.ru/v2/informers?lat=${weatherBundle.city.lat}&lon=${weatherBundle.city.lon}")
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
                    urlConnection.addRequestProperty("X-Yandex-API-Key", API_KEY)
                    //определяем таймаут на чтение
                    urlConnection.readTimeout = 10000
                    //преобразовываем входящий поток данных в набор символов
                    val bufferedReader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                    println(bufferedReader)
                    print("HUI")

                    //преобразование ответа от сервера (JSON) в модель данных (WeatherDTO)
                    val weatherDTO: WeatherDTO = Gson().fromJson(getLines(bufferedReader), WeatherDTO::class.java)
                    //обращаемся к основному потоку и в нем вызываем метод вывода информации о погоде
                    handler.post { displayWeather(weatherDTO) }
                } catch (e: Exception) {
                    Log.e(null,"Fail connection", e)
                    e.printStackTrace()
                } finally {
                    urlConnection?.disconnect()
                }
            }.start()
        } catch (e: MalformedURLException) {
            Log.e(null,"Fail URI", e)
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }

    //Реализация Parcelable, в Gradle был прописан     id 'kotlin-parcelize'
    companion object {
        //ключ для сохранения и загрузки элемента Weather
        const val BUNDLE_EXTRA = "weather"

        //метод увеличился из-за парселизации, теперь в него передается Bundle
        //в который при нажатии на элемент списка был передан Weather (это происходит в MainFragment)
        fun newInstance(bundle: Bundle): DetailsFragment {
            //создается пустой фрагмент Details
            val fragment = DetailsFragment()
            //с помощью метода arguments мы присваиваем переданные Bundle этому фрагменту
            fragment.arguments = bundle
            //и возвращаем его
            return fragment
        }
    }




}