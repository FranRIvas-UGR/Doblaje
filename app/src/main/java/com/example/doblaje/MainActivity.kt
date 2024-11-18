package com.example.doblaje

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.app.Activity
import android.content.Intent


class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)        

        val editText = findViewById<EditText>(R.id.editText)
        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener {
            val userInput = editText.text.toString()
            val intent = Intent(this, ResultsActivity::class.java).apply {
                putExtra("USER_INPUT", userInput)
            }
            startActivity(intent)
        }
    }
}