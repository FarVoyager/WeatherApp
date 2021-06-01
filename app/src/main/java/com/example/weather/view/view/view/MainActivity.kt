package com.example.weather.view.view.view

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.weather.R
import com.example.weather.view.view.experiments.MainBroadcastReceiver
import com.example.weather.view.view.experiments.ThreadsFragment

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //переходим на фрагмент MainFragment при создании активити
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.activityContainer, MainFragment.newInstance())
                .commitNow()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_screen_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_threads -> {
                supportFragmentManager.apply {
                    beginTransaction()
                        .replace(R.id.activityContainer, ThreadsFragment.newInstance())
                        .addToBackStack(null)
                        .commitAllowingStateLoss()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



}