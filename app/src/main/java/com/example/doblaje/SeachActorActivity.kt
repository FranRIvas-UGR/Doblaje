package com.example.doblaje

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.app.Activity
import android.content.Intent
import android.widget.ImageButton


class SearchActorActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_actor)

        val editText = findViewById<EditText>(R.id.editText)
        val button = findViewById<Button>(R.id.button)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, SelectActivity::class.java)
            startActivity(intent)
        }

        button.setOnClickListener {
            val userInput = editText.text.toString()
            val intent = Intent(this, ResultsActivity::class.java).apply {
                putExtra("ACTOR_NAME", userInput)
            }
            startActivity(intent)
        }
    }
}

