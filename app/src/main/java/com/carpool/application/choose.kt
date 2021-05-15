package com.carpool.application

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class choose : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)

        val driver: View = findViewById(R.id.driverButton)
        driver.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,sure::class.java)
            startActivity(intent);
           // startActivity(Intent(this@choose,sure::class.java))

        } )
        val driver2: View = findViewById(R.id.passengerButton)
        driver2.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,PasssengerChoice::class.java)
            startActivity(intent);
            // startActivity(Intent(this@choose,sure::class.java))

        } )
    }
}
