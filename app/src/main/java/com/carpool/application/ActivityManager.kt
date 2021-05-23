package com.carpool.application

import android.content.Context
import android.content.Intent
import android.location.Location
import java.lang.StringBuilder

class ActivityManager {
    companion object {

        // Passes ride information from one activity to another. Returns the intent.
        fun passRideInfo(
            context: Context?,
            activityName: String,
            location: Location?, currentAddress:
            String?, destination: String?,
            vacancies: String,
            departure: String
        ): Intent {
            // Format string
            val packageName: String = BuildConfig.APPLICATION_ID
            val stringBuilder = StringBuilder()
            val className = stringBuilder.append(packageName).append(".").append(activityName)

            // Get Class object
            val c = Class.forName(className.toString())

            // Create intent and set data to pass to the next activity
            val intent = Intent(context, c)
            intent.putExtra("LOCATION", location)
            intent.putExtra("CURRENT_ADDRESS", currentAddress)
            intent.putExtra("DESTINATION", destination)
            intent.putExtra("VACANCIES", vacancies)
            intent.putExtra("DEPARTURE_TIME", departure)
            return intent
        }
    }
}