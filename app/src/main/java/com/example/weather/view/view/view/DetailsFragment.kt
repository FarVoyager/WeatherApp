package com.example.weather.view.view.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.weather.BuildConfig
import com.example.weather.R
import com.example.weather.databinding.FragmentDetailsBinding
import com.example.weather.view.view.model.*
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

const val DETAILS_INTENT_FILTER = "DETAILS INTENT FILTER"
const val DETAILS_LOAD_RESULT_EXTRA = "LOAD RESULT"
const val DETAILS_INTENT_EMPTY_EXTRA = "INTENT IS EMPTY"
const val DETAILS_DATA_EMPTY_EXTRA = "DATA IS EMPTY"
const val DETAILS_RESPONSE_EMPTY_EXTRA = "RESPONSE IS EMPTY"
const val DETAILS_REQUEST_ERROR_EXTRA = "REQUEST ERROR"
const val DETAILS_REQUEST_ERROR_MESSAGE_EXTRA = "REQUEST ERROR MESSAGE"
const val DETAILS_URL_MALFORMED_EXTRA = "URL MALFORMED"
const val DETAILS_RESPONSE_SUCCESS_EXTRA = "RESPONSE SUCCESS"
const val DETAILS_HUMIDITY_EXTRA = "HUMIDITY"
const val DETAILS_WINDSPEED_EXTRA = "WINDSPEED"
const val DETAILS_TEMP_EXTRA = "TEMPERATURE"
const val DETAILS_FEELS_LIKE_EXTRA = "FEELS LIKE"
const val DETAILS_CONDITION_EXTRA = "CONDITION"
private const val TEMP_INVALID = -100
private const val FEELS_LIKE_INVALID = -100
private const val PROCESS_ERROR = "Обработка ошибки"
private const val MAIN_LINK = "https://api.weather.yandex.ru/v2/informers?"

class DetailsFragment : Fragment() {

    //binding - аналог findViewById, конструкция ниже нужна в том числе для ситуаций когда binding = null
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherBundle: Weather

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            LocalBroadcastManager.getInstance(it).registerReceiver(
                loadResultsReceiver,
                IntentFilter(DETAILS_INTENT_FILTER)
            )
        }
    }

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
        getWeather()

        //выполняем требование для использования API Яндекс.Погоды
        //при нажатии на Яндекс.Погода открывается ее сайт
        binding.yandex.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://yandex.ru/pogoda/moscow"))
            startActivity(browserIntent)
        }
    }

    private fun getWeather() {

        val client = OkHttpClient()
        //создаем строитель запроса
        val builder: Request.Builder = Request.Builder()
        //создаем заголовок запроса
        builder.header(REQUEST_API_KEY, BuildConfig.WEATHER_API_KEY)
        //формируем URL
        builder.url(MAIN_LINK + "lat=${weatherBundle.city.lat}&lon=${weatherBundle.city.lon}")
        //создаем запрос
        val request: Request = builder.build()
        //ставим запрос в очередь и отправляем
        val call: Call = client.newCall(request)
        call.enqueue(object : Callback {
            val handler: Handler = Handler(Looper.getMainLooper())

            //вызывается если ответ от сервера пришел
            override fun onResponse(call: Call?, response: Response) {
                val serverResponse: String? = response.body()?.string()

                if (response.isSuccessful && serverResponse != null) {
                    handler.post {
                        displayWeather(Gson().fromJson(serverResponse, WeatherDTO::class.java))
                    }
                } else {
                    Toast.makeText(context, "Ошибка: response error", Toast.LENGTH_LONG).show()
                }
            }
            //вызывается при сбое в процессе запроса на сервер
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(context, "Ошибка: response failed", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        })
    }

    //метод для отображения данных погоды на экране
    private fun displayWeather(weatherDTO: WeatherDTO) {
        binding.fragmentContainer.visibility = View.VISIBLE
        binding.loadingLayout.visibility = View.GONE

        val fact = weatherDTO.fact
        val temp = fact?.temp
        val feelsLike = fact?.feels_like
        val humidity = fact?.humidity
        val windSpeed = fact?.wind_speed
        val condition = fact?.condition

        if (temp == FEELS_LIKE_INVALID || feelsLike == FEELS_LIKE_INVALID || condition.isNullOrEmpty() || humidity == null || windSpeed == null) {
            Toast.makeText(context, "Ошибка значений данных", Toast.LENGTH_LONG).show()
        } else {
            val city = weatherBundle.city
            binding.cityName.text = city.name

            binding.temperatureFactInfo.text = temp.toString()
            binding.temperatureSensedInfo.text = feelsLike.toString()
            val windInfoString = "$windSpeed м/с"
            binding.windInfo.text = windInfoString
            val humidityInfoString = "$humidity %"
            binding.humidityInfo.text = humidityInfoString
        }

        if (weatherDTO.fact?.temp != null && weatherDTO.fact.temp > 0) {
            binding.pointerFact.text = "+"
            binding.pointerSensed.text = "+"
        } else {
            binding.pointerFact.text = ""
            binding.pointerSensed.text = ""
        }

        when (condition) {
            "clear" -> binding.backgroundWeatherFrame.setBackgroundResource(R.drawable.sunny)
            "cloudy" -> binding.backgroundWeatherFrame.setBackgroundResource(R.drawable.cloudy)
            "overcast" -> binding.backgroundWeatherFrame.setBackgroundResource(R.drawable.rainy)
            else -> binding.backgroundWeatherFrame.setBackgroundResource(R.drawable.sunny)
        }
    }

    //Реализация Parcelable, в Gradle был прописан     id 'kotlin-parcelize'
    companion object {
        //ключ для сохранения и загрузки элемента Weather
        const val BUNDLE_EXTRA = "weather"
        fun newInstance(bundle: Bundle): DetailsFragment {
            //создается пустой фрагмент Details
            val fragment = DetailsFragment()
            //с помощью метода arguments мы присваиваем переданные Bundle этому фрагменту
            fragment.arguments = bundle
            //и возвращаем его
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        context?.let {
            LocalBroadcastManager.getInstance(it).unregisterReceiver(loadResultsReceiver)
        }
        super.onDestroy()
    }


    //реализуем ресивер
    private val loadResultsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.getStringExtra(DETAILS_LOAD_RESULT_EXTRA)) {
                DETAILS_INTENT_EMPTY_EXTRA -> Toast.makeText(
                    context,
                    "Ошибка: пустой интент",
                    Toast.LENGTH_LONG
                ).show()
                DETAILS_DATA_EMPTY_EXTRA -> Toast.makeText(
                    context,
                    "Ошибка: пустые координаты",
                    Toast.LENGTH_LONG
                ).show()
                DETAILS_RESPONSE_EMPTY_EXTRA -> Toast.makeText(
                    context,
                    "Ошибка: пустой DTO",
                    Toast.LENGTH_LONG
                ).show()
                DETAILS_URL_MALFORMED_EXTRA -> Toast.makeText(
                    context,
                    "Ошибка: неверный URL",
                    Toast.LENGTH_LONG
                ).show()

                DETAILS_RESPONSE_SUCCESS_EXTRA -> displayWeather(
                    WeatherDTO(
                        FactDTO(
                            intent.getIntExtra(DETAILS_TEMP_EXTRA, TEMP_INVALID),
                            intent.getIntExtra(DETAILS_FEELS_LIKE_EXTRA, FEELS_LIKE_INVALID),
                            intent.getStringExtra(DETAILS_HUMIDITY_EXTRA),
                            intent.getStringExtra(DETAILS_WINDSPEED_EXTRA),
                            intent.getStringExtra(DETAILS_CONDITION_EXTRA)
                        )
                    )
                )
                else -> Toast.makeText(context, "Неизвестная ошибка", Toast.LENGTH_LONG).show()
            }
        }
    }


    private val onLoadListener: WeatherLoader.WeatherModelListener =
        object : WeatherLoader.WeatherModelListener {
            override fun onLoaded(weatherDTO: WeatherDTO) {
                displayWeather(weatherDTO)
            }

            override fun onFailed(throwable: Throwable) {
                Log.e(null, "Ошибка", throwable)
                Toast.makeText(context, "Error loading data", Toast.LENGTH_LONG).show()
            }
        }








}