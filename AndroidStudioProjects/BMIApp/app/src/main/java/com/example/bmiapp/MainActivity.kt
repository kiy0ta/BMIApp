package com.example.bmiapp

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.example.bmiapp.ui.input.InputFragment
import com.example.bmiapp.ui.record.RecordFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        var res = resources
        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_input -> {
                    title = res.getString(R.string.title_input)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, InputFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_record -> {
                    title = res.getString(R.string.title_record)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, RecordFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }
}
