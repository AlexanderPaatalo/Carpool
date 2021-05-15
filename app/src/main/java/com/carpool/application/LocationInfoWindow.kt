package com.carpool.application

import android.app.TimePickerDialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment

import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LocationInfoWindow.newInstance] factory method to
 * create an instance of this fragment.
 */

// Information window fragment that pops up when a user searches for a place.
class LocationInfoWindow() : Fragment() {

    constructor(destination: String) : this(){
        this.destination = destination
    }

    // TODO: Rename and change types of parameters
    private lateinit var destination: String

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_location_info_window, container, false)

        val destinationTextView = view.findViewById(R.id.text_destination) as TextView
        val departure = view.findViewById<View>(R.id.departure_time) as TextView
        val buttonChooseTime = view.findViewById<View>(R.id.button_chooseTime) as Button
        val vacancies = view.findViewById<View>(R.id.vacancies_selection) as Spinner

        // Adds text to the destination field
        destinationTextView?.append(" $destination")

        val spinner: Spinner = view.findViewById(R.id.vacancies_selection)
        // Create an ArrayAdapter using the string array and a default spinner layout
        context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.vacancies_selection,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                spinner.adapter = adapter
            }
        }

        buttonChooseTime.setOnClickListener(View.OnClickListener {
            TimePickerFragment().showTimePicker(departure, requireContext(), buttonChooseTime)
        })

        val startRideButton = view.findViewById<View>(R.id.button_startRide) as Button
        startRideButton.setOnClickListener(View.OnClickListener {

            val intent = Intent(context, sure::class.java)
            intent.putExtra("DESTINATION", destination)
            intent.putExtra("VACANCIES", vacancies.selectedItem.toString())
            intent.putExtra("DEPARTURE_TIME", departure.text)
            startActivity(intent)
        })

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LocationInfoWindow.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LocationInfoWindow().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    // Adds text to the
    private fun addText(destination: String?){

        //vacanciesText?.append(" 4")
        //departureText?.append(" 14:00")
    }
}