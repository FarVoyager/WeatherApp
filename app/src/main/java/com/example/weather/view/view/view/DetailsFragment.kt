package com.example.weather.view.view.view

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.weather.R
import com.example.weather.databinding.FragmentDetailsBinding
import com.example.weather.view.view.model.Weather
import com.example.weather.view.view.model.WeatherDTO
import com.example.weather.view.view.model.WeatherLoader


class DetailsFragment : Fragment() {

    //binding - аналог findViewById, конструкция ниже нужна в том числе для ситуаций когда binding = null
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherBundle: Weather

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //создается переменная типа Weather, которой присваивается сохраненный ранее Weather в Bundle, т.е. город, на который мы нажали
        //элвис в данном случае присвоит weatherBundle значение по города умолчанию если бандл будет null
        weatherBundle = arguments?.getParcelable(BUNDLE_EXTRA) ?: Weather()
        //экран заполняется данными из только что взятого из Bundle экземпляра класса Weather
        binding.loadingLayout.visibility = View.VISIBLE
        val loader = WeatherLoader(onLoadListener, weatherBundle.city.lat, weatherBundle.city.lon)
        loader.loadWeather()

        //выполняем требование для использования API Яндекс.Погоды
        //при нажатии на Яндекс.Погода открывается ее сайт
        binding.yandex.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://yandex.ru/pogoda/moscow"))
            startActivity(browserIntent)
        }
    }

    //метод для отображения данных погоды на экране
    private fun displayWeather(weatherDTO: WeatherDTO) {
        binding.apply {
            container.visibility = View.VISIBLE
            loadingLayout.visibility = View.GONE
            val city = weatherBundle.city
            cityName.text = city.name
            forecastDate.text = weatherDTO.forecast?.date

            val windInfoStr: String = weatherDTO.fact?.wind_speed + " м/с"
            windInfo.text = windInfoStr
            val humidityInfoStr: String = weatherDTO.fact?.humidity + "%"
            humidityInfo.text = humidityInfoStr
            temperatureFactInfo.text = weatherDTO.fact?.temp.toString()
            temperatureSensedInfo.text = weatherDTO.fact?.feels_like.toString()

            if (weatherDTO.fact?.temp != null && weatherDTO.fact.temp > 0) {
                pointerFact.text = "+"
                pointerSensed.text = "+"
            } else if (weatherDTO.fact?.temp != null && weatherDTO.fact.temp < 0) {
                pointerFact.text = "-"
                pointerSensed.text = "-"
            } else {
                pointerFact.text = ""
                pointerSensed.text = ""
            }

            when (weatherDTO.fact?.condition) {
                "clear" -> {
                    binding.backgroundWeatherFrame.setBackgroundResource(R.drawable.sunny)
                }
                "cloudy" -> {
                    binding.backgroundWeatherFrame.setBackgroundResource(R.drawable.cloudy)
                }
                "overcast" -> {
                    binding.backgroundWeatherFrame.setBackgroundResource(R.drawable.rainy)
                }
                else -> {
                    binding.backgroundWeatherFrame.setBackgroundResource(R.drawable.sunny)
                }
            }
        }
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