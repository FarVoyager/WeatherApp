package com.example.weather.view.view.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
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
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import coil.api.load
import com.bumptech.glide.Glide
import com.example.weather.BuildConfig
import com.example.weather.R
import com.example.weather.databinding.FragmentDetailsBinding
import com.example.weather.view.view.model.*
import com.example.weather.view.view.viewmodel.AppState
import com.example.weather.view.view.viewmodel.DetailsViewModel
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.File
import java.io.IOException


class DetailsFragment : Fragment() {

    //binding - аналог findViewById, конструкция ниже нужна в том числе для ситуаций когда binding = null
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherBundle: Weather

    private val viewModel: DetailsViewModel by lazy {   //используем ленивую инициализацию чтобы предотвратить утечки памяти
        ViewModelProvider(this).get(DetailsViewModel::class.java)  //привязываем viewModel  ЖЦ фрагмента (this)
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

        //создаем переменную типа Weather, которой присваивается сохраненный ранее Weather в Bundle, т.е. город, на который мы нажали
        //элвис в данном случае присвоит weatherBundle значение по города умолчанию если бандл будет null
        weatherBundle = arguments?.getParcelable(BUNDLE_EXTRA) ?: Weather()

        //экран заполняется данными из только что взятого из Bundle экземпляра класса Weather
        viewModel.detailsLiveData.observe(viewLifecycleOwner, Observer { renderData(it) })
        viewModel.getWeatherFromRemoteSource(weatherBundle.city.lat, weatherBundle.city.lon)

        //выполняем требование для использования API Яндекс.Погоды
        //при тапе на Яндекс.Погода открывается ее сайт
        binding.yandex.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://yandex.ru/pogoda/moscow"))
            startActivity(browserIntent)
        }

        //при тапе на город переходим в main fragment
        binding.cityName.setOnClickListener {
            val manager: FragmentManager = requireActivity().supportFragmentManager
            manager.beginTransaction()
                .replace(R.id.activityContainer, MainFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                binding.fragmentContainer.visibility = View.VISIBLE
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
                displayWeather(appState.weatherData[0])
            }
            is AppState.Loading -> {
                binding.fragmentContainer.visibility = View.GONE
                binding.includedLoadingLayout.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.fragmentContainer.visibility = View.VISIBLE
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
                Toast.makeText(context, "ERROR", Toast.LENGTH_LONG).show()
            }
        }
    }

    //метод для отображения данных погоды на экране
    private fun displayWeather(weather: Weather) {

        val city = weatherBundle.city
        saveCity(city, weather) //сохраняем данные погоды для истории
        val temp = weather.temp
        val feelsLike = weather.feels_like
        val humidity = weather.humidity
        val windSpeed = weather.wind
        val condition = weather.condition
        val daytime = weather.daytime

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

        val daytimeView = binding.backgroundWeatherFrame
        when (daytime) {
            "d" -> daytimeView.setBackgroundResource(R.drawable.city_day)
            "e" -> daytimeView.setBackgroundResource(R.drawable.city_evening)
            "n" -> daytimeView.setBackgroundResource(R.drawable.city_night1)
            "m" -> daytimeView.setBackgroundResource(R.drawable.city_morning)
            else -> daytimeView.setBackgroundResource(R.drawable.city_day)
        }


        val weatherView = binding.weatherView
        when (condition) {
            "clear" -> {
                weatherView.setImageResource(0)
                Picasso.get().load(R.drawable.sunny).into(weatherView)
            }
            "cloudy" -> {
                weatherView.setImageResource(0)
                Picasso.get().load(R.drawable.cloudy).into(weatherView)
            }

            "overcast" -> {
                weatherView.setImageResource(0)
                Picasso.get().load(R.drawable.rainy).into(weatherView)
            }
            else -> {
                weatherView.setImageResource(0)
                Picasso.get().load(R.drawable.clouds).into(weatherView)
            }
        }
    }

    private fun saveCity(city: City, weather: Weather) {
        viewModel.saveCityToDB(
            Weather(city, weather.temp, weather.feels_like, weather.humidity, weather.wind, weather.condition, weather.daytime))
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