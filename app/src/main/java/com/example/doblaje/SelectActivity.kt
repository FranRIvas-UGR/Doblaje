package com.example.doblaje

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton

class SelectActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        //val btnSrchSurname = findViewById<Button>(R.id.btnSrchSurname)
        //val btnAddActor = findViewById<Button>(R.id.btnAddActor)
        //val btnAddMovie = findViewById<Button>(R.id.btnAddMovie)
        val btnQueryMovie = findViewById<ImageButton>(R.id.btnSrchMovie)

        /* btnAddMovie.setOnClickListener {
            val intent = Intent(this, AddMovieActivity::class.java)
            startActivity(intent)
        } */

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