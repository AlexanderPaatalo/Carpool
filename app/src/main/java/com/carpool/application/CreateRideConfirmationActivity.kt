package com.carpool.application

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.carpool.application.RideManager.Companion.rides

class CreateRideConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_ride_confirmation)

        // Text views for the information entered by the user.
        val fromView: TextView = findViewById(R.id.text_rideConfirmationFromInput)
        val toView: TextView = findViewById(R.id.text_rideConfirmationToInput)
        val vacanciesView: TextView = findViewById(R.id.text_rideConfirmationVacanciesInput)
        val departureView: TextView = findViewById(R.id.text_rideConfirmationDepartureInput)

        // Buttons
        val buttonCreateRide: Button = findViewById(R.id.button_createRide)
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
                val ride = Ride(location, currentAddress, destination, vacancies, departure)
                rides.add(ride)
                startActivity(Intent(this, RideCreatedActivity::class.java))
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