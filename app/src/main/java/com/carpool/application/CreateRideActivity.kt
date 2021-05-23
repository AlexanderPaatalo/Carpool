package com.carpool.application

import android.content.ContentValues
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.carpool.application.ActivityManager.Companion.passRideInfo
import com.carpool.application.BuildConfig.apiKey
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*

class CreateRideActivity : AppCompatActivity() {

    private val location = Location("")
    private var currentAddress: String? = null
    private var destination: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_ride)

        if (!Places.isInitialized()) {
            // Initialize places.
            Places.initialize(applicationContext, apiKey)
        }

        // Create a new PlacesClient instance.
        Places.createClient(this)

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragmentFrom = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment_from) as AutocompleteSupportFragment
        val autocompleteFragmentTo = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment_to) as AutocompleteSupportFragment

        // Create an ArrayList with two AutoCompleteSupportFragments.
        val arrayList = ArrayList<AutocompleteSupportFragment>()
        arrayList.add(autocompleteFragmentFrom)
        arrayList.add(autocompleteFragmentTo)

        // Sets attributes of both AutocompleteSupportFragments in arrayList to the same.
        for (autocompleteFragment in arrayList) {
            autocompleteFragment.setHint("Sök")
            autocompleteFragment.view?.findViewById<EditText>(R.id.places_autocomplete_search_input)?.textSize = 14f
            autocompleteFragment.view?.findViewById<View>(R.id.places_autocomplete_search_button)?.visibility = View.GONE

            // Specify the types of place data to return.
            autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

            // Set up a PlaceSelectionListener to handle the response depending on which AutocompleteSupportFragment it is.
            autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    if (autocompleteFragment == arrayList[0]) {
                        val latLng = place.latLng
                        if (latLng != null) {
                            location.latitude = latLng.latitude
                            location.longitude = latLng.longitude
                            currentAddress = place.name
                        }
                    } else if (autocompleteFragment == arrayList[1]){
                        destination = place.name
                    }
                    Log.i(ContentValues.TAG, "Place: ${place.name}, ${place.id}")
                }

                override fun onError(status: Status) {
                    // TODO: Handle the error.
                    Log.i(ContentValues.TAG, "An error occurred: $status")
                }
            })
        }

        // Spinner field.
        val vacanciesSpinner: Spinner = findViewById(R.id.vacancies_selection)
        // Create an ArrayAdapter using the provided string array and a custom spinner layout.
        this.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.vacancies_selection,
                R.layout.spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears.
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner.
                vacanciesSpinner.adapter = adapter
            }
        }

        // Departure field.
        val tvDeparture: TextView = findViewById(R.id.text_create_ride_departure_input)
        tvDeparture.setOnClickListener {
            // Shows the time picker.
            TimePickerFragment().showTimePicker(this, tvDeparture)
        }

        val createRideButton: Button = findViewById(R.id.button_create_ride)
        // Create OnClickListener that passes ride information to next activity.
        createRideButton.setOnClickListener {
            if (currentAddress != null && destination != null) {
                val intent = passRideInfo(
                    this,
                    "CreateRideConfirmationActivity",
                    location,
                    currentAddress,
                    destination,
                    vacanciesSpinner.selectedItem.toString(),
                    tvDeparture.text.toString()
                )
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please make sure fields are valid.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}