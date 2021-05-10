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
    private lateinit var to: EditText
    private lateinit var number: EditText
    private lateinit var depart: EditText
    private lateinit var drive: Button
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sure)
        hundredsure = findViewById(R.id.hundredsure)
        from = findViewById(R.id.from)
        to = findViewById(R.id.to)
        number = findViewById(R.id.number)
        depart = findViewById(R.id.depart)
        drive = findViewById(R.id.drive)
        /*drive.setOnClickListener(object : View.OnClickListener
        {
            override fun onClick(v: View)
            {
                if (!hundredsure.getText().toString().isEmpty() && !!from.getText().toString()
                        .isEmpty() && !to.getText().toString().isEmpty() &&
                    !number.getText().toString().isEmpty() && !depart.getText().toString()
                        .isEmpty()
                )
                {
                    val intent = Intent(Intent.ACTION_INSERT)
                    intent.type = ContactsContract.RawContacts.CONTENT_TYPE
                    intent.putExtra(
                        ContactsContract.Intents.Insert.NAME,
                        hundredsure.getText().toString()
                    )
                    intent.putExtra(ContactsContract.Intents.Insert.NAME, from.getText().toString())
                    intent.putExtra(ContactsContract.Intents.Insert.NAME, to.getText().toString())
                    intent.putExtra(
                        ContactsContract.Intents.Insert.NAME,
                        number.getText().toString()
                    )
                    intent.putExtra(
                        ContactsContract.Intents.Insert.NAME,
                        depart.getText().toString()
                    )
                    if (intent.resolveActivity(packageManager) != null)
                    {
                        startActivity(intent)
                    }
                    when (v.id)
                        {
                            R.id.drive -> print ( "Nu finns du som en förare på listan:")

                        }
                }
            }
        })*/
    }
}