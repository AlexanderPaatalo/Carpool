package com.carpool.application

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.carpool.application.RideManager.Companion.rides
import java.util.*

// Information window fragment that pops up when a user searches for a place or clicks on a ride marker.
class LocationInfoWindow() : Fragment() {
    constructor(id: UUID? = null, location: Location? = null, destination: String? = null, currentAddress: String? = null) : this(){
        this.id = id
        this.location = location
        this.destination = destination
        this.currentAddress = currentAddress
    }

    private var destination: String? = null
    private var currentAddress: String? = null
    private var location: Location? = null
    private var id: UUID? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View

        // Use different view depending on if user searched for a place or clicked on a ride marker.
        return when(MapsActivity.windowToShow) {
            0 -> {
                view= inflater.inflate(R.layout.fragment_location_info_window_ride_new, container, false)
                showNewRideWindow(view)
                view
            }
            else -> {
                view = inflater.inflate(R.layout.fragment_location_info_window_ride_info, container, false)
                showRideInfoWindow(view)
                view
            }
        }
    }

    // Shows the info window to create a new ride.
    private fun showNewRideWindow(view: View) {
        val tvDestination: TextView = view.findViewById(R.id.text_location_info_window_destination_input)
        // Set text to destination searched for
        tvDestination.text = destination

        // Spinner input field
        val vacanciesSpinner: Spinner = view.findViewById(R.id.vacancies_selection)
        // Create an ArrayAdapter using the string array and a default spinner layout
        context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.vacancies_selection,
                R.layout.spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                vacanciesSpinner.adapter = adapter
            }
        }

        // Departure text-field. OnClickListener creates a TimePickerFragment and sets the text of text-field to the selected time.
        val departure: TextView = view.findViewById(R.id.departure_time)
        departure.setOnClickListener {
            TimePickerFragment().showTimePicker(requireContext(), departure)
        }

        // Choose time button. OnClickListener creates a TimePickerFragment and sets the text of text-field to the selected time.
        val buttonChooseTime: Button = view.findViewById(R.id.button_choose_time)
        buttonChooseTime.setOnClickListener {
            TimePickerFragment().showTimePicker(requireContext(), departure)
        }

        // Create ride button. OnClickListener takes entered information and passes to next activity.
        val buttonCreateRide: Button = view.findViewById(R.id.button_create_ride)
        buttonCreateRide.setOnClickListener {
            val intent = ActivityManager.passRideInfo(
                context,
                "CreateRideConfirmationActivity",
                location,
                currentAddress,
                destination,
                vacanciesSpinner.selectedItem.toString(),
                departure.text.toString()
            )
            // Remove marker from map before starting activity.
            MapsActivity.placeMarker?.remove()
            startActivity(intent)
        }
    }

    // Show the info window for the ride.
    private fun showRideInfoWindow(view: View) {
        var ride: Ride? = null

        // Get the associated ride id.
        for (r in rides) {
            if (this.id == r.getId()) {
                ride = r
            }
        }

        // Populate information window if ride is found.
        if(ride != null) {
            val currentAddress = ride.getCurrentAddress()
            val destination = ride.getDestination()
            val vacancies = ride.getVacancies()
            val departure = ride.getDeparture()

            val relativeLayout: RelativeLayout = view.findViewById(R.id.location_info_window_content)
            val button: Button = view.findViewById(R.id.button_carpool)

            // Create new text views to the right of existing text views.
            for (i in 1..4) {
                val textView = TextView (context)

                textView.id = View.generateViewId()

                val layout = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )

                // Sets unique attributes
                when(i) {
                    1 -> {
                        textView.text = currentAddress
                        // Add rules to layout
                        layout.addRule(RelativeLayout.ALIGN_BASELINE, R.id.text_location_info_window_from)
                        layout.addRule(RelativeLayout.END_OF, R.id.text_location_info_window_from)
                    }
                    2 -> {
                        textView.text = destination
                        // Add rules to layout
                        layout.addRule(RelativeLayout.ALIGN_BASELINE, R.id.text_location_info_window_to)
                        layout.addRule(RelativeLayout.END_OF, R.id.text_location_info_window_to)
                    }
                    3 -> {
                        textView.text = vacancies
                        // Add rules to layout
                        layout.addRule(RelativeLayout.ALIGN_BASELINE, R.id.text_location_info_window_vacancies)
                        layout.addRule(RelativeLayout.END_OF, R.id.text_location_info_window_vacancies)
                    }
                    4 -> {
                        textView.text = departure
                        // Add rules to layout
                        layout.addRule(RelativeLayout.ALIGN_BASELINE, R.id.text_location_info_window_departure)
                        layout.addRule(RelativeLayout.END_OF, R.id.text_location_info_window_departure)
                    }
                }
                // Set layout and add view to relativeLayout.
                textView.layoutParams = layout
                relativeLayout.addView(textView)
            }
            // Create OnClickListener that passes ride information to next activity.
            button.setOnClickListener {
                val intent = ActivityManager.passRideInfo(
                    requireContext(),
                    "RideInfoActivity",
                    ride.getLocation(),
                    currentAddress,
                    destination,
                    vacancies,
                    departure
                )
                startActivity(intent)
            }
        }
    }
}