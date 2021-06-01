package com.example.weather.view.view.experiments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.lang.StringBuilder

class MainBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val stringBuilder : StringBuilder = StringBuilder().apply {
            append("СООБЩЕНИЕ ОТ СИСТЕМЫ\n")
            append("Action: ${intent?.action}")
        }
        Toast.makeText(context, stringBuilder.toString(), Toast.LENGTH_LONG).show()
    }
}