package com.example.doblaje

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.app.Activity
import android.content.Intent
import android.widget.ImageButton


class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.editText)
        val button = findViewById<Button>(R.id.button)

        var backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        button.setOnClickListener {
            val userInput = editText.text.toString()
            val intent = Intent(this, ResultsActivity::class.java).apply {
                putExtra("USER_INPUT", userInput)
                putExtra("START", false)
                putExtra("MOVIE_SEARCH", true)
            }
            startActivity(intent)
        }
    }
}