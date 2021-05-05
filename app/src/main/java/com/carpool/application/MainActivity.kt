package com.carpool.application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapButton: View = findViewById(R.id.floatingActionButton4)
        mapButton.setOnClickListener { view ->
            startActivity(Intent(this, MapsActivity::class.java))
        }

    }
}