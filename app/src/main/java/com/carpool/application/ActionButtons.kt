package com.carpool.application

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

// Navigation FloatingActionButtons
class ActionButtons : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var mapButton: FloatingActionButton
    private lateinit var optionButton: FloatingActionButton
    private lateinit var chatButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_action_buttons, container, false)

        mapButton = view.findViewById<View>(R.id.floatingActionButton4) as FloatingActionButton
        optionButton = view.findViewById<View>(R.id.floatingActionButton3) as FloatingActionButton
        chatButton = view.findViewById<View>(R.id.floatingActionButton2) as FloatingActionButton

        mapButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(activity, MapsActivity::class.java))
        })

        optionButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(activity, choose::class.java))
        })

        chatButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(activity, ChatActivity::class.java))
        })

        return view
    }
}