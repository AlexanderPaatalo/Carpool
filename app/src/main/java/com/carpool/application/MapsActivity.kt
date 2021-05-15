package com.carpool.application

import android.Manifest
import android.content.ContentValues.TAG
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    val apiKey = BuildConfig.MAPS_API_KEY

    private var LOCATION_REQUEST_CODE = 101
    private var lastLocation: Location? = null
    private var userLocation: Location? = null
    private var requestingLocationUpdates = false
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var latLng: LatLng = LatLng(-33.87, 151.21)
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var sydneyMarker: Marker
    private lateinit var marker: Marker

    private var latestName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        places()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        requestingLocationUpdates = true
        createLocationRequest()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    // Update UI with location data
                    // ...

                    Log.d("MapsActivity", locationResult.locations.toString())
                }
                lastLocation = locationResult.locations.last()
                lastLocation?.let { updateUserLocation(it) }
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
        map.isMyLocationEnabled = true
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        getLastLocation()

        // Add a marker in Sydney and move the camera
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            val markerOptions = MarkerOptions().position(latLng).title("I am here!")
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))
            sydneyMarker = map.addMarker(markerOptions)
        }

        map.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(p0: LatLng?) {
                hideLocationInfoWindow()
                Log.d("Map_Tag", "CLICK")
            }
        })

        // Car marker
        val height = 100
        val width = 100
        val b = BitmapFactory.decodeResource(resources, R.drawable.car_placeholder)
        val scaledIcon = Bitmap.createScaledBitmap(b, width, height, true)
        val scaledMarkerIcon = BitmapDescriptorFactory.fromBitmap(scaledIcon)
        val carMarkerLocation = LatLng(62.3932, 17.2831)
        val markerOptions = MarkerOptions().position(carMarkerLocation)
            .title("Driver location")
            .snippet("snippet snippet snippet snippet snippet...")
            .icon(scaledMarkerIcon)

        marker = map.addMarker(markerOptions)

        map.setOnMarkerClickListener { marker ->
            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
                Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show()
            } else {
                marker.showInfoWindow()
            }
            true
        }

        // marker onclicklistener
    }

    // Gets the latest location provided by FusedLocationProvider
    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
            fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location: Location? ->
                userLocation = location
                userLocation?.let { updateCameraPosition(it) }
            }
                    .addOnFailureListener(this) {

                    }
        }else{
            askLocationPermission()
        }
    }

    // Asks for permission to use location if not already granted
    private fun askLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )){
                Log.d("MapsActivity", "askLocationPermission: you should show an alert dialog...")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            }
        }
    }

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
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (requestingLocationUpdates) startLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }

    // Starts making location requests
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    // Updates the users location
    // Put in own class
    private fun updateUserLocation(location: Location){
        userLocation = location
    }

    // Moves the camera to the location.
    private fun updateCameraPosition(location: Location){
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(location.latitude, location.longitude))
            //.tilt(30F)
            .zoom(17f)
            .build()

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    // Creates a location request with data object locationRequest, that contains quality of service parameters.
    private fun createLocationRequest(){
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                Log.d("createLocationRequest()", "Location settings are not satisfied")
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    //exception.startResolutionForResult(this@MainActivity,
                    //       REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    // Google Places. Handles the PlaceSelectionListener.
    private fun places(){
        // Initialize the SDK
        Places.initialize(applicationContext, apiKey)

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

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
                val markerOptions = MarkerOptions().position(latLng)
                    .title(place.name)
                    //.snippet("snippet snippet snippet snippet snippet...")
                marker = map.addMarker(markerOptions)

                val location = Location("")
                location.latitude = latLng!!.latitude
                location.longitude = latLng.longitude
                updateCameraPosition(location)
                showLocationInfoWindow(place.name.toString())
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: ${place.name}, ${place.id}")
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: $status")
            }
        })
    }

    // Adds a new LocationInfoWindow fragment. If the previous name is the same as current, the function restores the previous fragment instead.
    private fun showLocationInfoWindow(name: String){
        hideLocationInfoWindow()
        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.places_info_window)

        if(fragment == null || latestName != name){
            supportFragmentManager.beginTransaction()
                .add(R.id.places_info_window, LocationInfoWindow(name))
                .commit()
        }else{
            supportFragmentManager.beginTransaction()
                .show(fragment)
                .commit()
        }
        latestName = name
    }

    // Hides the LocationInfoWindow fragment
    private fun hideLocationInfoWindow(){
        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.places_info_window)
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .hide(fragment)
                .commit()
        }
    }
}