package com.example.weather.view.view.app

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.room.Room
import com.example.weather.view.view.room.HistoryDao
import com.example.weather.view.view.room.HistoryDataBase


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object {
        private var appInstance: App? = null
        private var db: HistoryDataBase? = null
        private const val DB_NAME = "History.db"

        fun getHistoryDao(): HistoryDao {
            if (db == null) {
                synchronized(HistoryDataBase::class.java) {
                    if (db == null) {
                        if (appInstance == null) throw
                        IllegalStateException("Application is null while creating database")
                        db = Room.databaseBuilder(
                            appInstance!!.applicationContext,
                            HistoryDataBase::class.java,
                            DB_NAME
                        )
                            .build()
                    }
                }
            }
            return db!!.historyDao()
        }
    }

}