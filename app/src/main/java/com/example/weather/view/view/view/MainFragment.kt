package com.example.weather.view.view.view

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.databinding.FragmentMainBinding
import com.example.weather.view.view.model.Weather
import com.example.weather.view.view.viewmodel.AppState
import com.example.weather.view.view.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

private const val IS_WORLD_KEY = "LIST_OF_CITIES_KEY"

class MainFragment : Fragment() {

    //binding - аналог findViewById, конструкция ниже нужна в том числе для ситуаций когда binding = null
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    // Значение для определения, список каких городов на данный момент отображается
    private var isDataSetRus: Boolean = true

    //Экземпляр класса MainViewModel теперь здесь, во фрагменте MainFragment
    //lazy - ленивая инициализация,
    private val viewModel: MainViewModel by lazy {
        //привязываем жизненный цикл MainViewModel ко фрагменту (this)
        ViewModelProvider(this).get(MainViewModel::class.java)
    }



    //Вложенный интерфейс для реализации переключения на фрагмент Details по нажатию на элемент списка
    interface OnItemViewClickListener {
        fun onItemViewClick(weather: Weather)
    }

    //создаем экземпляр адаптера RecyclerView, в поле аргументов наш интерфейс с реализацией
    private val adapter = MainFragmentAdapter(object : OnItemViewClickListener {
        //переопределяем (реализуем) метод интерфейса
        override fun onItemViewClick(weather: Weather) {
            val manager = activity?.supportFragmentManager
            if (manager != null) {
                //создаем пустой Bundle, пихаем в него элемент типа Weather из ранее созданного ListOf (метод bind в адаптере)
                //переходим на фрагмент Details
                val bundle = Bundle()
                bundle.putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
                manager.beginTransaction()
                    .replace(R.id.activityContainer, DetailsFragment.newInstance(bundle))
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //соединяем наш RecyclerView с адаптером, созданным ранее
        binding.mainFragmentRecyclerView.adapter = adapter
        //реализуем исполнение метода changeWeatherDataSet по нажатию на FAB
        binding.mainFragmentFAB.setOnClickListener { changeWeatherDataSet() }
        //привязываем LiveData, в качестве 2-го аргумента метода observe вызываем метод renderData, it - AppState
        viewModel.getLiveData().observe(viewLifecycleOwner, Observer { renderData(it) })
        //получаем данные с локального хранилища
        showListOfCities()
    }

    //метод реализует действия при нажатии на FAB
    private fun changeWeatherDataSet() {

        if (isDataSetRus)  {
            viewModel.getWeatherFromLocalSourceWorld()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_earth)
        } else {
            viewModel.getWeatherFromLocalSourceRus()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
        }
        isDataSetRus = !isDataSetRus
        saveListOfCities(isDataSetRus)

    }

    //метод получает в аргументах текущий AppState и в зависимости от его состояния отображает содержимое экрана
    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                adapter.setWeather(appState.weatherData)
            }
            is AppState.Loading -> {
                binding.mainFragmentLoadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.mainFragmentLoadingLayout.visibility = View.GONE
                Snackbar
                    .make(
                        binding.mainFragmentFAB,
                        getString(R.string.error),
                        Snackbar.LENGTH_INDEFINITE
                    )
                    .setAction(getString(R.string.reload)) {
                        viewModel.getWeatherFromLocalSourceRus()
                    }.show()
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }


    //при уничтожении фрагмента удаляем лисенер из адаптера чтобы предотвратить утечки памяти
    override fun onDestroy() {
        adapter.removeListener()
        super.onDestroy()
    }

    private fun saveListOfCities(isDataSetRus: Boolean) {
        val pref = activity?.getPreferences(Context.MODE_PRIVATE)
        pref?.edit()?.putBoolean(IS_WORLD_KEY, isDataSetRus)?.apply()
    }

    private fun showListOfCities() {
        activity?.let {

            if (it.getPreferences(Context.MODE_PRIVATE).getBoolean(IS_WORLD_KEY, false)) {
                viewModel.getWeatherFromLocalSourceRus()
            } else {
                changeWeatherDataSet()
            }
        }
    }

}