package com.example.weather.view.view.view

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.weather.R
import com.example.weather.databinding.FragmentContentProviderBinding
import com.example.weather.databinding.FragmentDetailsBinding
import java.util.jar.Manifest

const val REQUEST_CODE = 42

class ContentProviderFragment : Fragment() {

    private var _binding: FragmentContentProviderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentContentProviderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        println("BEB onViewCreated")
    }

    companion object {
        @JvmStatic
        fun newInstance() = ContentProviderFragment()
    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_CONTACTS
            ) ==
                    PackageManager.PERMISSION_GRANTED -> {
                println("BEB PERMISSION_GRANTED")

                //Доступ к контактам на телефоне есть
//                getContacts()
            }
            //Опционально: если нужно пояснение перед запросом рарешений
            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_CONTACTS) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Доступ к контактам")
                    .setMessage("Для открытия данного экрана необходимо предоставить доступ к конактам телефона.")
                    .setPositiveButton("Я согласен") { _, _ ->
                        requestPermission()
                    }
                    .setNegativeButton("Не надо") { dialog, _ ->
                            dialog.dismiss()
                    }
                    .create()
                    .show()
            }
            else -> {
                //Запрашиваем разрешение
                requestPermission()
            }
        }
    }

    private fun requestPermission() {
        requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), REQUEST_CODE)
    }

    // Обратный вызов после получения разрешений от пользователя
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                // Проверяем, дано ли пользователем разрешение по нашему запросу
                if ((grantResults.isNotEmpty()) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
//                    getContacts()
                } else {
                    // Поясните пользователю, что экран останется пустым, потому что доступ к контактам не предоставлен
                    AlertDialog.Builder(requireContext())
                        .setTitle("Доступ к контактам")
                        .setMessage("Вы ранее запретили доступ к контактам. Пожалуйста, разрешите доступ в настройках вручную.")
                        .setNegativeButton("Закрыть") { dialog,_ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
                return
            }
        }
    }



    private fun getContacts() {
        TODO("Not yet implemented")
    }

}