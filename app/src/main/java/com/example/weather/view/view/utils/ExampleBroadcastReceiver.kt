package com.example.weather.view.view.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ExampleBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val str = "ЯЗЫК СИСТЕМЫ ИЗМЕНЕН"
        Toast.makeText(context, str,Toast.LENGTH_LONG).show()
    }
}