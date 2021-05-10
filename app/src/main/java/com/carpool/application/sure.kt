package com.carpool.application

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class sure : AppCompatActivity() {
    private lateinit var hundredsure: TextView
    private lateinit var from: EditText
    private lateinit var to: TextView
    private lateinit var number: TextView
    private lateinit var depart: TextView
    private lateinit var drive: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sure)
        hundredsure = findViewById(R.id.ethundredsure)
        from = findViewById(R.id.etFrom)
        to = findViewById(R.id.etTo)
        number = findViewById(R.id.etNumber)
        depart = findViewById(R.id.etDepart)
        drive = findViewById<View>(R.id.drive) as Button
        drive.setOnClickListener {
            if (!hundredsure.getText().toString().isEmpty() && !!from.getText().toString()
                    .isEmpty() && !to.getText().toString().isEmpty() &&
                !number.getText().toString().isEmpty() && !depart.getText().toString().isEmpty()
            ) {
                val intent = Intent(Intent.ACTION_INSERT)
                intent.type = ContactsContract.RawContacts.CONTENT_TYPE
                intent.putExtra(
                    ContactsContract.Intents.Insert.NAME,
                    hundredsure.getText().toString()
                )
                intent.putExtra(ContactsContract.Intents.Insert.NAME, from.getText().toString())
                intent.putExtra(ContactsContract.Intents.Insert.NAME, to.getText().toString())
                intent.putExtra(ContactsContract.Intents.Insert.NAME, number.getText().toString())
                intent.putExtra(ContactsContract.Intents.Insert.NAME, depart.getText().toString())
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
                val toast = Toast.makeText(applicationContext, "Nu finns du som en förare på listan:", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }
}