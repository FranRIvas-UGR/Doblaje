package com.example.doblaje

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast

class SelectActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        val addMovie = intent.getBooleanExtra("ADD_MOVIE", false)
        val errorMessage = intent.getStringExtra("ERROR_MESSAGE")
        val start = intent.getBooleanExtra("START", false)

        if(!addMovie) {
            val message = errorMessage
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        } else if (addMovie) {
            Toast.makeText(this, "Película añadida correctamente.", Toast.LENGTH_LONG).show()
        } else if (start) {
            Toast.makeText(this, "Bienvenido", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
        }

        //val btnSrchSurname = findViewById<Button>(R.id.btnSrchSurname)
        //val btnAddActor = findViewById<Button>(R.id.btnAddActor)

        val btnAddMovie = findViewById<ImageButton>(R.id.btnAddMovie)
        val btnQueryMovie = findViewById<ImageButton>(R.id.btnSrchMovie)

        btnAddMovie.setOnClickListener {
            val intent = Intent(this, AddMovieActivity::class.java)
            startActivity(intent)
        }

        btnQueryMovie.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        /* btnQueryActorByName.setOnClickListener {
            val intent = Intent(this, NameActorActivity::class.java)
            startActivity(intent)
        }

        btnSrchSurname.setOnClickListener {
            val intent = Intent(this, SurnameActorActivity::class.java)
            startActivity(intent)
        } */
    }
}