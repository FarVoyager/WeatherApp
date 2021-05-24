package com.example.weather.view.view.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weather.R
import com.example.weather.databinding.FragmentDetailsBinding
import com.example.weather.view.view.model.Weather

//класс сократился, функции viewmodel переехали в MainFragment
class DetailsFragment : Fragment() {

    //binding - аналог findViewById, конструкция ниже нужна в том числе для ситуаций когда binding = null
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //создается переменная типа Weather, которой присваивается сохраненный ранее Weather в Bundle
        val weather : Weather? = arguments?.getParcelable(BUNDLE_EXTRA)
        //экран заполняется данными из только что взятого из Bundle экземпляра класса Weather
        if (weather != null) {
            val city = weather.city
            binding.cityName.text = city.name
            binding.temperatureFactInfo.text = weather.temperatureFact
            binding.temperatureSensedInfo.text = weather.temperatureSensed
            binding.humidityInfo.text = weather.humidity
            binding.windInfo.text = weather.wind

            when (weather.clouds) {
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