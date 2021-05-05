package com.carpool.application

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ActionButtons.newInstance] factory method to
 * create an instance of this fragment.
 */
class ActionButtons : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var mapButton: FloatingActionButton
    private lateinit var optionButton: FloatingActionButton
    private lateinit var chatButton: FloatingActionButton
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param2 = it.getString(ARG_PARAM2)
        }
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ActionButtons.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                ActionButtons().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}