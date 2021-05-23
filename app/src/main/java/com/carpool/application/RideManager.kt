package com.carpool.application

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast

class RideManager {
    companion object {
        var rides = mutableListOf<Ride>()

        fun locationFinder(context: Context, currentAddress: String): Location? {
            // Get location from address
            val addresses: List<Address>
            val geocoder = Geocoder(context)
            val location = Location("")

            try {
                addresses = geocoder.getFromLocationName(currentAddress, 4)
                if (addresses == null) {
                    Toast.makeText(context, "Couldn't get the location.", Toast.LENGTH_SHORT).show()
                }
                val locationAddress = addresses[0]
                location.latitude = locationAddress.latitude
                location.longitude = locationAddress.longitude

                return location
            }catch (e: Exception) {
                Log.e("RideInfoActivity", e.toString())
            }
            return null
        }
    }
}