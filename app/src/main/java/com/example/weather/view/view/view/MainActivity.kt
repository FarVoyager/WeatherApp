package com.example.weather.view.view.view

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.FragmentManager
import com.example.weather.R


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
        menuInflater.inflate(R.menu.action_bar_history, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_history -> {
                val manager = this.supportFragmentManager
                manager.beginTransaction()
                    .replace(R.id.activityContainer, HistoryFragment.newInstance())
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
                true
            }
            R.id.menu_content_provider -> {
                val manager = this.supportFragmentManager
                manager.beginTransaction()
                    .replace(R.id.activityContainer, ContentProviderFragment.newInstance())
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}