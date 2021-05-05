package com.carpool.application

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.core.content.ContextCompat

class PermissionsDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.permission_message)
                    .setPositiveButton(R.string.allow,
                            DialogInterface.OnClickListener { dialog, id ->
                                //ActivityCompat.requestPermissions((activity as MapsActivity), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
                                //MapsActivity().permissionGranted = true
                                //(activity as MapsActivity).askLocationPermission()
                                //(activity as MapsActivity).getLastLocation()
                            })
                    .setNegativeButton(R.string.cancel,
                            DialogInterface.OnClickListener { dialog, id ->
                                // User cancelled the dialog
                            })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
