package com.example.weather.view.view.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weather.R
import com.example.weather.databinding.FragmentDetailsBinding
import com.example.weather.view.view.model.Weather


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
        val weather = arguments?.getParcelable<Weather>(BUNDLE_EXTRA)
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

    companion object {

        const val BUNDLE_EXTRA = "weather"

        fun newInstance(bundle: Bundle): DetailsFragment {
            val fragment = DetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }


//    private fun renderData(appState: AppState) {
//        when (appState) {
//            //сли состояние Success - вызываем метод setData
//            is AppState.Success -> {
//                val weatherData = appState.weatherData
//                binding.loadingLayout.visibility = View.GONE
//                setData(weatherData)
//            }
//            is AppState.Loading -> {
//                binding.loadingLayout.visibility = View.VISIBLE
//            }
//            is AppState.Error -> {
//                binding.loadingLayout.visibility = View.GONE
//                Snackbar
//                    .make(binding.container, getString(R.string.error), Snackbar.LENGTH_SHORT)
//                    .setAction(getString(R.string.reload)) {viewModel.getWeatherFromLocalSource() }
//                    .show()
//            }
//        }
//    }
//
//    //Метод setData обновляет отображение данных во фрагменте
//    private fun setData(weatherData: Weather) {
//        binding.cityName.text = weatherData.city.name
//        binding.temperatureFactInfo.text = weatherData.temperatureFact
//        binding.temperatureSensedInfo.text = weatherData.temperatureSensed
//        binding.humidityInfo.text = weatherData.humidity
//        binding.windInfo.text = weatherData.wind
//    }


}