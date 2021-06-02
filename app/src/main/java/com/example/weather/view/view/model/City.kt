package com.example.weather.view.view.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class City(
    val name: String,
    val region: String,
    val lat: Double,
    val lon: Double
) : Parcelable