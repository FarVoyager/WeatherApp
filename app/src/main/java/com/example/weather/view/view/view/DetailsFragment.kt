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
        viewModel.detailsLiveData.observe(viewLifecycleOwner, Observer { renderData(it) })
        viewModel.getWeatherFromRemoteSource(weatherBundle.city.lat,weatherBundle.city.lon)

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

                println(appState.weatherData[0].temp.toString() + " HUI")
                println(appState.weatherData[0].feels_like.toString() + " HUI")
                println(appState.weatherData[0].humidity + " HUI")
                println(appState.weatherData[0].wind + " HUI")
                println(appState.weatherData[0].condition + " HUI")

//                Toast.makeText(context, "SUCCESS", Toast.LENGTH_LONG).show()
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