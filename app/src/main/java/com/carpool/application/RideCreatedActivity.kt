package com.carpool.application

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RideCreatedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride_created)

        val button: Button = findViewById(R.id.ride_created_button)
        button.setOnClickListener {
            startActivity(Intent(this, RidesActivity::class.java))
        }
    }
}