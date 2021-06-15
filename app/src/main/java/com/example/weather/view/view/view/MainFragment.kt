package com.example.weather.view.view.view

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.databinding.FragmentMainBinding
import com.example.weather.view.view.model.City
import com.example.weather.view.view.model.Weather
import com.example.weather.view.view.viewmodel.AppState
import com.example.weather.view.view.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.IOException

private const val IS_RUS_KEY = "IS_RUS_KEY"
private const val REQUEST_CODE_LOCATION = 44

private const val REFRESH_PERIOD = 60000L
private const val MINIMAL_DISTANCE = 100f

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
            openDetailsFragment(weather)
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

        binding.mainFragmentFABLocation.setOnClickListener { checkPermission() }

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

        if (isDataSetRus) {
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
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
                adapter.setWeather(appState.weatherData)
            }
            is AppState.Loading -> {
                binding.includedLoadingLayout.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
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
        pref?.edit()?.putBoolean(IS_RUS_KEY, isDataSetRus)?.apply()
    }

    private fun showListOfCities() {
        activity?.let {

            if (it.getPreferences(Context.MODE_PRIVATE).getBoolean(IS_RUS_KEY, true)) {
                viewModel.getWeatherFromLocalSourceRus()
            } else {
                changeWeatherDataSet()
            }
        }
    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
                    PackageManager.PERMISSION_GRANTED -> {
                getLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showRationaleDialog()
            }
            else -> {
                requestPermission()
            }
        }
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            //получить менеджер геолокаций
            val locationManager =
                context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val provider = locationManager.getProvider(LocationManager.GPS_PROVIDER)
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    REFRESH_PERIOD,
                    MINIMAL_DISTANCE,
                    onLocationListener
                )
            } else {
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location == null) {
                    showDialog(
                        getString(R.string.dialog_title_gps_turned_off),
                        getString(R.string.dialog_message_last_location_unknown)
                    )
                } else {
                    getAddressAsync(requireContext(), location)
                    showDialog(
                        getString(R.string.dialog_title_gps_turned_off),
                        getString(R.string.dialog_message_last_known_location)
                    )
                }
            }
        } else {
            showRationaleDialog()
        }
    }

    private val onLocationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {
            context?.let {
                getAddressAsync(it, location)
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun requestPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(activity)
            .setTitle(getString(R.string.dialog_rationale_title))
            .setMessage(getString(R.string.dialog_rationale_message))
            .setPositiveButton(getString(R.string.dialog_rationale_give_access)) { _, _ ->
                requestPermission()
            }
            .setNegativeButton(getString(R.string.dialog_rationale_decline)) { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> {
                // Проверяем, дано ли пользователем разрешение по нашему запросу
                if ((grantResults.isNotEmpty()) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                } else {
                    showDialog(
                        getString(R.string.dialog_title_no_gps),
                        getString(R.string.dialog_message_no_gps)
                    )
                }
                return
            }
        }
    }

    private fun showDialog(title: String, message: String) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.dialog_button_close)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun getAddressAsync(context: Context, location: Location) {
        val geoCoder = Geocoder(context)
        Thread {
            try {
                val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                binding.mainFragmentFAB.post {
                    showAddressDialog(addresses[0].getAddressLine(0), location)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun showAddressDialog(address: String, location: Location) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_address_title))
            .setMessage(address)
            .setPositiveButton(getString(R.string.dialog_address_get_weather)) { _, _ ->
                openDetailsFragment(Weather(City(address, location.latitude, location.longitude)))
            }
            .setNegativeButton(getString((R.string.dialog_button_close))) { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }

    private fun openDetailsFragment(weather: Weather) {
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
}