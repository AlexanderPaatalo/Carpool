package com.carpool.application

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

// Activity where the user can choose which mode to use app as.
class ChooseUserModeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)

        // Driver button
        val driver: View = findViewById(R.id.driverButton)
        driver.setOnClickListener {
            val intent = Intent(this, CreateRideActivity::class.java)
            startActivity(intent)
        }

        // Passenger button
        val driver2: View = findViewById(R.id.passengerButton)
        driver2.setOnClickListener {
            val intent = Intent(this, RidesActivity::class.java)
            startActivity(intent)
        }
    }
}
