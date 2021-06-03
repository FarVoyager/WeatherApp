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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.weather.BuildConfig
import com.example.weather.R
import com.example.weather.databinding.FragmentDetailsBinding
import com.example.weather.view.view.model.*
import com.example.weather.view.view.viewmodel.AppState
import com.example.weather.view.view.viewmodel.DetailsViewModel
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException


class DetailsFragment : Fragment() {

    //binding - аналог findViewById, конструкция ниже нужна в том числе для ситуаций когда binding = null
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherBundle: Weather
    private val viewModel: DetailsViewModel by lazy {
        ViewModelProvider(this).get(DetailsViewModel::class.java)
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
//        getWeather()
        viewModel.getLiveData().observe(viewLifecycleOwner, Observer { renderData(it) })

        //выполняем требование для использования API Яндекс.Погоды
        //при нажатии на Яндекс.Погода открывается ее сайт
        binding.yandex.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://yandex.ru/pogoda/moscow"))
            startActivity(browserIntent)
        }
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                binding.fragmentContainer.visibility = View.VISIBLE
                binding.loadingLayout.visibility = View.GONE
                displayWeather(appState.weatherData[0])
            }
            is AppState.Loading -> {
                binding.fragmentContainer.visibility = View.GONE
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.fragmentContainer.visibility = View.VISIBLE
                binding.loadingLayout.visibility = View.GONE
                Toast.makeText(context, "ERROR", Toast.LENGTH_LONG).show()
            }
        }
    }

//    private fun getWeather() {
//
//        val client = OkHttpClient()
//        //создаем строитель запроса
//        val builder: Request.Builder = Request.Builder()
//        //создаем заголовок запроса
//        builder.header(REQUEST_API_KEY, BuildConfig.WEATHER_API_KEY)
//        //формируем URL
//        builder.url(MAIN_LINK + "lat=${weatherBundle.city.lat}&lon=${weatherBundle.city.lon}")
//        //создаем запрос
//        val request: Request = builder.build()
//        //ставим запрос в очередь и отправляем
//        val call: Call = client.newCall(request)
//        call.enqueue(object : Callback {
//            val handler: Handler = Handler(Looper.getMainLooper())
//
//            //вызывается если ответ от сервера пришел
//            override fun onResponse(call: Call?, response: Response) {
//                val serverResponse: String? = response.body()?.string()
//
//                if (response.isSuccessful && serverResponse != null) {
//                    handler.post {
//                        displayWeather(Gson().fromJson(serverResponse, WeatherDTO::class.java))
//                    }
//                } else {
//                    Toast.makeText(context, "Ошибка: response error", Toast.LENGTH_LONG).show()
//                }
//            }
//            //вызывается при сбое в процессе запроса на сервер
//            override fun onFailure(call: Call, e: IOException) {
//                Toast.makeText(context, "Ошибка: response failed", Toast.LENGTH_LONG).show()
//                e.printStackTrace()
//            }
//        })
//    }

    //метод для отображения данных погоды на экране
    private fun displayWeather(weather: Weather) {

        val city = weatherBundle.city
        val temp = weather.temp
        val feelsLike = weather.feels_like
        val humidity = weather.humidity
        val windSpeed = weather.wind
        val condition = weather.condition

        binding.cityName.text = city.name

        binding.temperatureFactInfo.text = temp.toString()
        binding.temperatureSensedInfo.text = feelsLike.toString()
        val windInfoString = "$windSpeed м/с"
        binding.windInfo.text = windInfoString
        val humidityInfoString = "$humidity %"
        binding.humidityInfo.text = humidityInfoString


        if (temp > 0) {
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
}