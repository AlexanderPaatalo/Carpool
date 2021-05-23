package com.carpool.application

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import java.util.*
import java.util.UUID.randomUUID

@Suppress("unused")
class Ride() {
    private var id = randomUUID()
    private lateinit var location: Location
    private lateinit var latLng: LatLng
    private var currentAddress: String = ""
    private var destination: String = ""
    private var vacancies: String = ""
    private var departure: String = ""

    constructor(location: Location, currentAddress: String, destination: String, vacancies: String, departure: String) : this() {
        this.location = location
        this.latLng = LatLng(location.latitude, location.longitude)
        this.currentAddress = currentAddress
        this.destination = destination
        this.vacancies = vacancies
        this.departure = departure
    }

    fun getId(): UUID {
        return this.id
    }
    fun getCurrentAddress(): String {
        return this.currentAddress
    }

    fun getDestination(): String {
        return this.destination
    }
    fun getVacancies(): String {
        return this.vacancies
    }
    fun getDeparture(): String {
        return this.departure
    }
    fun setLocation(location: Location) {
        this.location = location
    }
    fun getLocation(): Location {
        return this.location
    }
    fun getLatLng(): LatLng {
        return this.latLng
    }
}