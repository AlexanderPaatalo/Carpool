package com.carpool.application

import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.carpool.application.ActivityManager.Companion.passRideInfo

class BookRideConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_ride_confirmation)

        // Text views for the ride information
        val fromView: TextView = findViewById(R.id.text_book_ride_confirmation_from_input)
        val toView: TextView = findViewById(R.id.text_ride_confirmation_to_input)
        val vacanciesView: TextView = findViewById(R.id.text_ride_confirmation_vacancies_input)
        val departureView: TextView = findViewById(R.id.text_ride_confirmation_departure_input)

        // Buttons
        val buttonBookRide: Button = findViewById(R.id.button_book_ride)
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

        // Books the ride based on the information in the text views.
        buttonBookRide.setOnClickListener {
            if (location != null && currentAddress != null && destination != null && vacancies != null && departure != null) {
                intent = passRideInfo(this, "RideInfoActivity", location, currentAddress, destination, vacancies, departure)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        // Cancel button.
        buttonCancel.setOnClickListener {
            finish()
        }
    }
}