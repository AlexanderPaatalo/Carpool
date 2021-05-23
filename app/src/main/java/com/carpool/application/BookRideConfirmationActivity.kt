package com.carpool.application

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.carpool.application.ActivityManager.Companion.passRideInfo

class BookRideConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_ride_confirmation)

        // Text views for the information entered by the user.
        val fromView: TextView = findViewById(R.id.text_book_ride_confirmation_from_input)
        val toView: TextView = findViewById(R.id.text_ride_confirmation_to_input)
        val vacanciesView: TextView = findViewById(R.id.text_ride_confirmation_vacancies_input)
        val departureView: TextView = findViewById(R.id.text_ride_confirmation_departure_input)

        // Buttons
        val buttonCreateRide: Button = findViewById(R.id.button_book_ride)
        val buttonCancel: Button = findViewById(R.id.button_cancel)

        val location = intent.extras?.getParcelable<Location>("LOCATION")
        val currentAddress = intent.extras?.getString("CURRENT_ADDRESS")
        val destination = intent.extras?.getString("DESTINATION")
        val vacancies = intent.extras?.getString("VACANCIES")
        val departure = intent.extras?.getString("DEPARTURE_TIME")

        // Set text of text views.
        fromView.text = currentAddress
        toView.text = destination
        vacanciesView.text = vacancies
        departureView.text = departure

        // Creates a ride based on valid information entered if user clicks the button.
        buttonCreateRide.setOnClickListener {
            if (location != null && currentAddress != null && destination != null && vacancies != null && departure != null) {
                intent = passRideInfo(this, "RideInfoActivity", location, currentAddress, destination, vacancies, departure)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please make sure fields are valid.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        // Cancel button.
        buttonCancel.setOnClickListener {
            finish()
        }
    }
}