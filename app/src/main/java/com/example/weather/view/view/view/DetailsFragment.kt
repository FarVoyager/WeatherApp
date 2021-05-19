package com.example.weather.view.view.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.databinding.FragmentDetailsBinding
import com.example.weather.view.view.model.Weather
import com.example.weather.view.view.viewmodel.AppState
import com.example.weather.view.view.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar


class DetailsFragment : Fragment() {

    //binding - аналог findViewById, конструкция ниже нужна в том числе для ситуаций когда binding = null
    private var _binding : FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    //создаем экземпляр нашего класса MainViewModel
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //привязываем жизненный цикл viewModel к фрагменту, this - текущий фрагмент, пока он жив, жив и viewModel
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        //viewLifeCycleOwner позволяет liveData получить состояние компонента, в котором она находится
        //viewLifeCycleOwner - это метод get, Observer описан лямбдой, внутри тела которой метод renderData, it - это наш AppState
        viewModel.getLiveData().observe(viewLifecycleOwner, Observer { renderData(it) })
        viewModel.getWeatherFromLocalSource()
    }




    private fun renderData(appState: AppState) {
        when (appState) {
            //сли состояние Success - вызываем метод setData
            is AppState.Success -> {
                val weatherData = appState.weatherData
                binding.loadingLayout.visibility = View.GONE
                setData(weatherData)
            }
            is AppState.Loading -> {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.loadingLayout.visibility = View.GONE
                Snackbar
                    .make(binding.container, getString(R.string.error), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.reload)) {viewModel.getWeatherFromLocalSource() }
                    .show()
            }
        }
    }

    //Метод setData обновляет отображение данных во фрагменте
    private fun setData(weatherData: Weather) {
        binding.cityName.text = weatherData.city.name
        binding.temperatureFactInfo.text = weatherData.temperatureFact
        binding.temperatureSensedInfo.text = weatherData.temperatureSensed
        binding.humidityInfo.text = weatherData.humidity
        binding.windInfo.text = weatherData.wind
        when (weatherData.clouds) {
            "sunny" -> {
                binding.backgroundWeatherFrame.setBackgroundResource(R.drawable.cloudy)
            }
            "cloudy" -> {
                binding.backgroundWeatherFrame.setBackgroundResource(R.drawable.noclouds)
            }
            "rainy" -> {
                binding.backgroundWeatherFrame.setBackgroundResource(R.drawable.rainy)
            }
            else -> {
                binding.backgroundWeatherFrame.setBackgroundResource(R.drawable.noclouds)
            }
        }
    }

    companion object {
        fun newInstance() = DetailsFragment()
    }

}