package com.carpool.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

// Activity for the chat
class ChatActivity : AppCompatActivity() {

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
    }
}