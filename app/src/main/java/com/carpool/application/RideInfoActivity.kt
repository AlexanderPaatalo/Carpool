package com.carpool.application

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.carpool.application.MapsActivity.Companion.trackLocation

class RideInfoActivity : AppCompatActivity() {
    companion object {
        var markerLocationButtonWasPressed = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride_info)

        val location = intent.extras?.getParcelable<Location>("LOCATION")
        val currentAddress = intent.extras?.getString("CURRENT_ADDRESS")
        val destination = intent.extras?.getString("DESTINATION")
        val vacancies = intent.extras?.getString("VACANCIES")
        val departure = intent.extras?.getString("DEPARTURE_TIME")

        val phoneNumber: Uri = Uri.parse("tel:0761234567")

        // Opens up phone call app and enters phoneNumber.
        val callButton = findViewById<View>(R.id.button_call_driver)
        callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, phoneNumber)
            startActivity(intent)
        }

        // Opens the map and moves camera to the location of the ride.
        val locationButton = findViewById<View>(R.id.button_location)
        locationButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("LOCATION", location)
            MapsActivity.shouldMoveCameraToMarker = true
            trackLocation = false
            startActivity(intent)
        }

        val relativeLayout: RelativeLayout = findViewById(R.id.ride_info_text_box)

        // Create new text views to the right of existing text views.
        for (i in 1..4) {
            val textView = TextView(this)
            textView.id = View.generateViewId()

            // Defining the layout parameters of the TextView
            val layout = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )

            // Sets unique attributes
            when (i) {
                1 -> {
                    textView.text = currentAddress
                    layout.addRule(RelativeLayout.ALIGN_BASELINE, R.id.ride_info_text_from)
                    layout.addRule(RelativeLayout.END_OF, R.id.ride_info_text_from)
                }
                2 -> {
                    textView.text = destination
                    layout.addRule(RelativeLayout.ALIGN_BASELINE, R.id.ride_info_text_destination)
                    layout.addRule(RelativeLayout.END_OF, R.id.ride_info_text_destination)
                }
                3 -> {
                    textView.text = vacancies
                    layout.addRule(RelativeLayout.ALIGN_BASELINE, R.id.ride_info_text_vacancies)
                    layout.addRule(RelativeLayout.END_OF, R.id.ride_info_text_vacancies)
                }
                4 -> {
                    textView.text = departure
                    layout.addRule(RelativeLayout.ALIGN_BASELINE, R.id.ride_info_text_departure)
                    layout.addRule(RelativeLayout.END_OF, R.id.ride_info_text_departure)
                }
            }
            textView.layoutParams = layout
            relativeLayout.addView(textView)
        }
    }
}