package com.example.weather.view.view.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Weather(
    val city: City = getDefaultCity(),
    val temperatureFact: String = "+25",
    val temperatureSensed: String = "+21",
    val humidity: String = "68%",
    val wind: String = "3 м/с  ЮЗ",
    val clouds: String = "rainy"
        ) : Parcelable

fun getDefaultCity() = City("Moscow", "Europe", 55.755826, 37.617299900000035)

//два метода возвращают список с классом Weather. listOf иммутабелен, т.е. его содержимое нельзя менять
//для создания изменяемого списка нужно использовать mutableListOf
fun getWorldCities() = listOf(
        Weather(City("Лондон", "Great Britain", 51.5085300, -0.1257400), "+17", "+15", "55%", "2м/с СЗ", "rainy"),
        Weather(City("Токио", "Japan", 35.6895000, 139.6917100), "+19", "+17", "72%", "4м/с В", "sunny"),
        Weather(City("Париж", "France", 48.8534100, 2.3488000), "+22", "+19", "66%", "3м/с Ю", "cloudy"),
        Weather(City("Берлин", "Germany", 52.52000659999999, 13.404953999999975), "+16", "+14", "68%", "5м/с С", "rainy"),
        Weather(City("Рим", "Greece", 41.9027835, 12.496365500000024), "+23", "+20", "57%", "2м/с ЮВ", "sunny"),
        )

fun getRussianCities() = listOf(
        Weather(City("Москва", "Московская обл.", 55.755826, 37.617299900000035), "+17", "+15", "55%", "2м/с СЗ", "rainy"),
        Weather(City("Санкт-Петербург", "Ленинградская обл.", 59.9342802, 30.335098600000038), "+19", "+17", "72%", "4м/с В", "sunny"),
        Weather(City("Новосибирск", "Новосибирская обл.", 55.00835259999999, 82.93573270000002), "+22", "+19", "66%", "3м/с Ю", "cloudy"),
        Weather(City("Екатеринбург", "Свердловская обл.", 56.83892609999999, 60.60570250000001), "+16", "+14", "68%", "5м/с С", "rainy"),
        Weather(City("Казань", "Республика Татарстан", 55.8304307, 49.06608060000008), "+23", "+20", "57%", "2м/с ЮВ", "sunny"),
    )

