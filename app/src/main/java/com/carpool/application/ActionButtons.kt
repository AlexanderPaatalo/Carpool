package com.carpool.application

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.carpool.application.MapsActivity.Companion.trackLocation
import com.google.android.material.floatingactionbutton.FloatingActionButton

// Navigation FloatingActionButtons
@Suppress("unused")
class ActionButtons : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_action_buttons, container, false)

        val mapButton = view.findViewById<View>(R.id.floatingActionButton4) as FloatingActionButton
        val chooseUserModeButton = view.findViewById<View>(R.id.floatingActionButton3) as FloatingActionButton
        val chatButton = view.findViewById<View>(R.id.floatingActionButton2) as FloatingActionButton

        // Disables the current navigation button.
        when(context) {
            is MapsActivity -> {
                chooseUserModeButton.setOnClickListener {
                    startActivity(Intent(activity, ChooseUserModeActivity::class.java))
                }
                chatButton.setOnClickListener {
                    startActivity(Intent(activity, ChatActivity::class.java))
                }
            }

            is ChooseUserModeActivity -> {
                mapButton.setOnClickListener {
                    trackLocation = true
                    MapsActivity.shouldMoveCameraToMarker = false
                    startActivity(Intent(activity, MapsActivity::class.java))
                }
                chatButton.setOnClickListener {
                    startActivity(Intent(activity, ChatActivity::class.java))
                }
            }

            is ChatActivity -> {
                mapButton.setOnClickListener {
                    trackLocation = true
                    MapsActivity.shouldMoveCameraToMarker = false
                    startActivity(Intent(activity, MapsActivity::class.java))
                }
                chooseUserModeButton.setOnClickListener {
                    startActivity(Intent(activity, ChooseUserModeActivity::class.java))
                }
            }

            else -> {
                mapButton.setOnClickListener {
                    trackLocation = true
                    MapsActivity.shouldMoveCameraToMarker = false
                    startActivity(Intent(activity, MapsActivity::class.java))
                }
                chooseUserModeButton.setOnClickListener {
                    startActivity(Intent(activity, ChooseUserModeActivity::class.java))
                }
                chatButton.setOnClickListener {
                    startActivity(Intent(activity, ChatActivity::class.java))
                }
            }
        }
        return view
    }
}