package com.carpool.application

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class sure : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sure)

        val fromView: TextView = findViewById(R.id.text_rideConfirmationFromInput)
        val toView: TextView = findViewById(R.id.text_rideConfirmationToInput)
        val vacanciesView: TextView = findViewById(R.id.text_rideConfirmationVacanciesInput)
        val departureView: TextView = findViewById(R.id.text_rideConfirmationDepartureInput)

        fromView.text

        val destination = intent.extras?.getString("DESTINATION")
        toView.text = destination

        val vacancies = intent.extras?.getString("VACANCIES")
        vacanciesView.text = vacancies

        val departure = intent.extras?.getString("DEPARTURE_TIME")
        departureView.text = departure

    }
}