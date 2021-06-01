package com.example.weather.view.view.experiments

import android.app.IntentService
import android.content.Intent
import android.util.Log

private const val TAG = "MainServiceTAG"
const val MAIN_SERVICE_STRING_EXTRA = "MainServiceExtra"
const val MAIN_SERVICE_INT_EXTRA = "MainServiceIntExtra"

class MainService(name: String = "MainService") : IntentService(name) {

    override fun onHandleIntent(intent: Intent?) {
        sendBack(intent?.getIntExtra(MAIN_SERVICE_INT_EXTRA, 0).toString())

        createLogMessage("onHandleIntent ${intent?.getStringExtra(MAIN_SERVICE_STRING_EXTRA)}")
    }

    override fun onCreate() {
        createLogMessage("onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createLogMessage("onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        createLogMessage("onDestroy")
        super.onDestroy()
    }

    //выводим уведомление в строке состояния
    private fun createLogMessage(message: String) {
        Log.d(TAG, message)
    }

    //Отправка уведомления о завершении сервиса
    private fun sendBack(result: String) {
        val broadCastIntent = Intent(TEST_BROADCAST_INTENT_FILTER)
        broadCastIntent.putExtra(THREADS_FRAGMENT_BROADCAST_EXTRA, result)
        sendBroadcast(broadCastIntent)
    }

}