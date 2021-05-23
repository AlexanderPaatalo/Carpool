package com.carpool.application

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.carpool.application.RideManager.Companion.rides
import com.carpool.application.databinding.ActivityRidesBinding

class RidesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRidesBinding
    private var lastRelativeViewId: Int = 0
    private var lastTextViewId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRidesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(findViewById(R.id.toolbar))

        populateRides()
    }

    // Populates screen with all available rides.
    private fun populateRides() {
        val view = binding.root
        val content = view.findViewById(R.id.scrollViewContent) as RelativeLayout

        // Creates new RelativeLayouts for every existing ride.
        for (ride in rides) {
            val location = ride.getLocation()
            val currentAddress = ride.getCurrentAddress()
            val destination = ride.getDestination()
            val vacancies = ride.getVacancies()
            val departure = ride.getDeparture()

            val relativeLayout = RelativeLayout(this)
            relativeLayout.id = View.generateViewId()
            relativeLayout.background = ContextCompat.getDrawable(this, R.drawable.border_bottom)

            // Defining the RelativeLayout layout parameters.
            val rlp = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )

            relativeLayout.setPadding(30, 30, 0,30)

            // Adds rides below the last one.
            if(ride != rides.first()) {
                rlp.addRule(RelativeLayout.BELOW, lastRelativeViewId)
                relativeLayout.layoutParams = rlp
            }

            // Creates new text views.
            for (i in 1..4) {
                val textViewLeft = TextView(this)
                val textViewRight = TextView(this)
                textViewLeft.id = View.generateViewId()
                textViewRight.id = View.generateViewId()

                // Defining the layout parameters of the left TextView
                val layoutLeft = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )

                // Defining the layout parameters of the right TextView
                val layoutRight = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )

                // Sets unique attributes, first text view determines position of other text views.
                when (i) {
                    1 -> {
                        textViewLeft.text = getString(R.string.from)
                        layoutLeft.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE)
                        layoutLeft.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)

                        textViewRight.text = currentAddress
                    }
                    2 -> {
                        textViewLeft.text = getString(R.string.to)
                        textViewRight.text = destination
                    }
                    3 -> {
                        textViewLeft.text = getString(R.string.vacancies)
                        textViewRight.text = vacancies
                    }
                    4 -> {
                        textViewLeft.text = getString(R.string.departure)
                        textViewRight.text = departure
                    }
                }

                // Adds every text view except the first below the last one.
                if (lastTextViewId != 0) {
                    layoutLeft.addRule(RelativeLayout.BELOW, lastTextViewId)
                }

                // Add right text view to right of left text view.
                layoutRight.addRule(RelativeLayout.ALIGN_BASELINE, textViewLeft.id)
                layoutRight.addRule(RelativeLayout.END_OF, textViewLeft.id)

                // Sets layout parameters and adds views.
                textViewLeft.layoutParams = layoutLeft
                textViewRight.layoutParams = layoutRight

                // Add button to ride
                val button = Button(this)
                button.text = getString(R.string.carpool)
                val layoutButton = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                layoutButton.addRule(RelativeLayout.ALIGN_PARENT_END, relativeLayout.id)
                layoutButton.addRule(RelativeLayout.CENTER_VERTICAL, relativeLayout.id)
                button.layoutParams = layoutButton

                button.setOnClickListener {
                    val intent = ActivityManager.passRideInfo(
                        this,
                        "BookRideConfirmationActivity",
                        location,
                        currentAddress,
                        destination,
                        vacancies,
                        departure
                    )
                    startActivity(intent)
                }

                relativeLayout.addView(textViewLeft)
                relativeLayout.addView(textViewRight)
                relativeLayout.addView(button)

                lastTextViewId = textViewLeft.id
            }

            // Add ride to view.
            content.addView(relativeLayout)

            lastRelativeViewId = relativeLayout.id
        }
    }
}