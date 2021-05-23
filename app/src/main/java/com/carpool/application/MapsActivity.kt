package com.carpool.application

import android.Manifest
import android.content.ContentValues.TAG
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.carpool.application.RideManager.Companion.locationFinder
import com.carpool.application.RideManager.Companion.rides
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        private var firstTimeStarted = true
        var isRunning = false
        var trackLocation = true
        var shouldMoveCameraToMarker = false

        var windowToShow = 0

        var rideMarkers = mutableListOf<Marker>()
        //var rideMarkerOptions = mutableListOf<MarkerOptions>()
        var placeMarker: Marker? = null
    }

    private val apiKey = BuildConfig.API_KEY

    private lateinit var map: GoogleMap
    private var mapView: View? = null

    private lateinit var cameraPosition: CameraPosition

    private var lastLocation: Location? = null
    private var userLocation: Location? = null

    @Suppress("PrivatePropertyName")
    private var LOCATION_REQUEST_CODE = 101
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var requestingLocationUpdates = false
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var defaultLatLng: LatLng = LatLng(59.32, 17.83)

    override fun onStart() {
        super.onStart()
        if (requestingLocationUpdates) startLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
        isRunning = false
        firstTimeStarted = false
        RideInfoActivity.markerLocationButtonWasPressed = false
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)

        isRunning = true

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapView = mapFragment.view
        mapFragment.getMapAsync(this)

        places()
        createTestRides()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        requestingLocationUpdates = true
        createLocationRequest()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    // Update UI with location data

                    val latestLocation = locationResult.locations.last()
                    updateUserLocation(latestLocation)
                    Log.d("MapsActivity", locationResult.locations.toString())

                    if(!RideInfoActivity.markerLocationButtonWasPressed && trackLocation){
                        userLocation?.let { updateCameraPosition(it) }
                    } else if (firstTimeStarted && !RideInfoActivity.markerLocationButtonWasPressed && trackLocation) {
                        updateCameraPosition(lastLocation!!)
                    }
                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isMapToolbarEnabled = false
        map.moveCamera(CameraUpdateFactory.newLatLng(defaultLatLng))

        if (firstTimeStarted) {
            trackLocation = true
        }

        if (shouldMoveCameraToMarker) {
            val markerLocation = intent.extras?.getParcelable<Location>("LOCATION")
            if (markerLocation != null) {
                updateCameraPosition(markerLocation)
            }
        }

        // My location button
        if (mapView != null && mapView!!.findViewById<View?>("1".toInt()) != null) {
            val locationButton = (mapView!!.findViewById<View>("1".toInt()).parent as View).findViewById<View>("2".toInt())
            val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 30, 420)
        }

        addMarkersToMap()

        getLastLocation()

        map.setOnMapClickListener {
            trackLocation = false
            removeLocationInfoWindow()
            Log.d("Map_Tag", "CLICK")
        }

        map.setOnCameraMoveStartedListener {

        }

        // Gets called when a marker is clicked. Checks which ride is associated with the marker depending on location and displays the information window.
        map.setOnMarkerClickListener { marker ->
            for (ride in rides) {
                val location = ride.getLocation()
                val latLng = LatLng(location.latitude, location.longitude)
                if (latLng == marker.position) {
                    val id = ride.getId()
                    windowToShow = 1
                    showLocationInfoWindow(id, null, null)
                }
            }
            false
        }

        // Gets called when camera moves. Sets marker visibility depending on zoom level.
        map.setOnCameraMoveListener {
            for (marker in rideMarkers) {
                marker.isVisible = map.cameraPosition.zoom > 7
            }
        }

        // MyLocationButton works as a toggle for tracking location.
        map.setOnMyLocationButtonClickListener {
            trackLocation = !trackLocation
            false
        }
    }

    // Gets the latest location provided by FusedLocationProvider if permissions are granted. Otherwise ask for permissions.
    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location: Location? ->
                map.isMyLocationEnabled = true
                if (location != null) {
                    lastLocation = location
                    updateUserLocation(location)
                }
            }
                .addOnFailureListener(this) {

                    }
        } else {
            askLocationPermission()
        }
    }

    // Asks for permission to use location if not already granted
    private fun askLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                // Todo: Show explanation dialog instead
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            }
        }
    }

    // Gets called when requesting permissions.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    getLastLocation()

                } else {
                    Toast.makeText(this, "Map functionality won't work correctly if location data is denied.", Toast.LENGTH_LONG)
                        .show()
                    // "Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision." - Google Android docs
                }
                return
            }

            // "Add other 'when' lines to check for other
            // permissions this app might request." - Google Android docs
            else -> {
                // Ignore all other requests.
            }
        }
    }

    // Starts making location requests, asks for permission if not granted
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askLocationPermission()
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    // Updates the users location
    private fun updateUserLocation(location: Location){
        userLocation = location
    }

    // Moves the camera to the location.
    private fun updateCameraPosition(location: Location){
        cameraPosition = CameraPosition.Builder()
            .target(LatLng(location.latitude, location.longitude))
            //.tilt(30F)
            .zoom(17f)
            .build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    // Creates a location request with data object locationRequest, that contains quality of service parameters.
    private fun createLocationRequest(){
        locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2500
            priority = PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // Todo All location settings are satisfied. The client can initialize location requests here.
            Log.d("createLocationRequest()", "Location settings are satisfied")
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Todo "Location settings are not satisfied, but this can be fixed
                // Todo by showing the user a dialog." - Google Android docs
                Log.d("createLocationRequest()", "Location settings are not satisfied")
                try {
                    // "Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    //exception.startResolutionForResult(this@MainActivity,
                    //       REQUEST_CHECK_SETTINGS)" - Google Android docs
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    // Initializes Google Places and handles the PlaceSelectionListener.
    private fun places(){
        // Initialize the SDK
        Places.initialize(applicationContext, apiKey)

        // Create a new PlacesClient instance
        Places.createClient(this)

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setHint("Sök")

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        // RectangularBounds for sweden
        val latLngNorthEast = LatLng( 69.162174, 24.499916 )
        val latLngSouthWest = LatLng(55.241083, 10.344167)
        val sweden = RectangularBounds.newInstance(latLngSouthWest, latLngNorthEast)
        autocompleteFragment.setLocationBias(sweden)

        // Set up a PlaceSelectionListener to handle the response.
        // Gets the location and passes it to updateCameraPosition.
        // Gets the name of the place and passes it to showLocationInfoWindow.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLng = place.latLng
                val location = Location("")
                if (latLng != null) {
                    location.latitude = latLng.latitude
                    location.longitude = latLng.longitude

                    trackLocation = false
                    updateCameraPosition(location)

                    // Shows new ride window.
                    windowToShow = 0
                    showLocationInfoWindow(null, userLocation, place.name.toString(), getCurrentAddress().toString())

                    val markerOptions = MarkerOptions().position(latLng)
                        .title(place.name)
                    placeMarker = map.addMarker(markerOptions)
                }
                Log.i(TAG, "Place: ${place.name}, ${place.id}")
            }
            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: $status")
            }
        })
    }

    // Tries to get users current address based on location.
    private fun getCurrentAddress(): String? {
        val location = userLocation
        val addresses: List<Address>
        val geocoder = Geocoder(this)

        if (location != null) {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

            // Tries to get formatted strings.
            try {
                val addressLine = addresses.elementAt(0).getAddressLine(0).substringBefore(',')
                val locality = addresses.elementAt(0).getAddressLine(0).substringAfter(',')
                    .substringBefore(',')
                return "$addressLine, $locality"

            } catch (e: IndexOutOfBoundsException) {
                Log.e("MapsActivity", "$e couldn't get any addresses")
            }
        }
        return null
    }

    // Adds a new LocationInfoWindow fragment.
    private fun showLocationInfoWindow(id: UUID? = null, location: Location? = null, destination: String?, currentAddress: String? = null) {
        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.places_info_window)
        val transaction = supportFragmentManager.beginTransaction()

        if(fragment == null){
            transaction
                .add(R.id.places_info_window, LocationInfoWindow(id, location, destination, currentAddress))
                .commit()
        } else {
            removeLocationInfoWindow()
            transaction
                .add(R.id.places_info_window, LocationInfoWindow(id, location, destination, currentAddress))
                .commit()
        }
    }

    // Removes the information window and associated marker.
    private fun removeLocationInfoWindow() {
        placeMarker?.remove()
        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.places_info_window)
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }
    }

    // Add a marker to the map for every ride. Also adds markers to rideMarkers list.
    private fun addMarkersToMap () {
        // Set bitmap attributes
        val height = 100
        val width = 100
        val b = BitmapFactory.decodeResource(resources, R.drawable.car_placeholder)
        val scaledBitmap = Bitmap.createScaledBitmap(b, width, height, true)
        val scaledMarkerIcon = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

        for (ride in rides) {
            val carMarkerLocation = ride.getLocation()
            val latLng = LatLng(carMarkerLocation.latitude, carMarkerLocation.longitude)
            val markerOptions = MarkerOptions().position(latLng)
                .title("Driver location")
                .icon(scaledMarkerIcon)

            val marker = map.addMarker(markerOptions)

            if (marker != null) {
                rideMarkers.add(marker)
            }
        }
    }

    // Creates rides to test the application with
    private fun createTestRides() {
        if (firstTimeStarted) {
            val ride1Location = locationFinder(this, "Dalgatan 14, 852 37 Sundsvall")
            val ride2Location = locationFinder(this, "Stockholm Centralstation, 111 20 Stockholm")
            val ride3Location = locationFinder(this, "UNIVERSITETSTORGET 4, 907 36 Umeå")

            val ride1 = ride1Location?.let { Ride(it, "Dalgatan 14, 852 37 Sundsvall", "Stockholm", "1 Plats", "12:00") }
            val ride2 = ride2Location?.let { Ride(it,"Stockholm Centralstation, 111 20 Stockholm", "Sundsvall", "2 Platser", "13:00") }
            val ride3 = ride3Location?.let { Ride(it,"UNIVERSITETSTORGET 4, 907 36 Umeå", "Sundsvall", "3 Platser", "14:00") }
            rides.add(ride1!!)
            rides.add(ride2!!)
            rides.add(ride3!!)
        }
    }
}