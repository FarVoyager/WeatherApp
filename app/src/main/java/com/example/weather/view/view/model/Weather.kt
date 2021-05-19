package com.example.weather.view.view.model

data class Weather(
    val city: City = getDefaultCity(),
    val temperatureFact: String = "+25",
    val temperatureSensed: String = "+21",
    val humidity: String = "68%",
    val wind: String = "3 м/с  ЮЗ",
    val clouds: String = "rainy"
        )

fun getDefaultCity() = City("Moscow", "Europe")

//два метода возвращают список с классом Weather. listOf иммутабелен, т.е. его содержимое нельзя менять
//для создания изменяемого списка нужно использовать mutableListOf
fun getWorldCities() : List<Weather> {
    return listOf(
        Weather(City("Лондон", "Great Britain"), "+17", "+15", "55%", "2м/с СЗ", "rainy"),
        Weather(City("Токио", "Japan"), "+19", "+17", "72%", "4м/с В", "sunny"),
        Weather(City("Париж", "France"), "+22", "+19", "66%", "3м/с Ю", "cloudy"),
        Weather(City("Берлин", "Germany"), "+16", "+14", "68%", "5м/с С", "rainy"),
        Weather(City("Рим", "Greece"), "+23", "+20", "57%", "2м/с ЮВ", "sunny"),
        )
}
fun getRussianCities() : List<Weather> {
    return listOf(
        Weather(City("Москва", "Московская обл."), "+17", "+15", "55%", "2м/с СЗ", "rainy"),
        Weather(City("Санкт-Петербург", "Ленинградская обл."), "+19", "+17", "72%", "4м/с В", "sunny"),
        Weather(City("Новосибирск", "Новосибирская обл."), "+22", "+19", "66%", "3м/с Ю", "cloudy"),
        Weather(City("Екатеринбург", "Свердловская обл."), "+16", "+14", "68%", "5м/с С", "rainy"),
        Weather(City("Казань", "Республика Татарстан"), "+23", "+20", "57%", "2м/с ЮВ", "sunny"),
    )
}