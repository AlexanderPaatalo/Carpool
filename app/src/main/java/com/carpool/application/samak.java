package com.carpool.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.drm.DrmStore;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class samak extends AppCompatActivity {
    EditText hundredsure;
    EditText from;
    EditText to;
    EditText number;
    EditText depart;
    EditText drive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_samak);

        hundredsure = findViewById(R.id.ethundredsure);
        from = findViewById(R.id.etFrom);
        to = findViewById(R.id.etTo);
        number = findViewById(R.id.etNumber);
        depart = findViewById(R.id.etDepart);

        drive.setOnClickListener(new View.OnClickListener()
    {
        @Override
                public void onClick(View v)
        {
            if(!hundredsure.getText().toString().isEmpty()&& !!from.getText().toString().isEmpty()&&!to.getText().toString().isEmpty()&&
                    !number.getText().toString().isEmpty()&&!depart.getText().toString().isEmpty())
            {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType((ContactsContract.RawContacts.CONTENT_TYPE));
                intent.putExtra(ContactsContract.Intents.Insert.NAME,hundredsure.getText().toString());
                intent.putExtra(ContactsContract.Intents.Insert.NAME,from.getText().toString());
                intent.putExtra(ContactsContract.Intents.Insert.NAME,to.getText().toString());
                intent.putExtra(ContactsContract.Intents.Insert.NAME,number.getText().toString());
                intent.putExtra(ContactsContract.Intents.Insert.NAME,depart.getText().toString());

                if (intent.resolveActivity(getPackageManager())!= null) {
                    startActivity(intent);
                }

            }

        }

    });
}}