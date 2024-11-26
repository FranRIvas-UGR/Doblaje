package com.example.doblaje

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TableLayout
import org.json.JSONObject
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import java.io.File

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

        

        val buttonContainer = findViewById<LinearLayout>(R.id.buttonContainer)
        val textView = findViewById<TextView>(R.id.message)
        val file = File(this.getExternalFilesDir(null), "peliculas.json")

        if (file.exists() && file.length() > 0) {
            buttonContainer.visibility = View.VISIBLE
            Thread {
                // Leer y convertir el archivo a JSONObject
                val json = JSONObject(file.readText())
                
                // Obtener el arreglo "peliculas"
                val peliculasArray = json.getJSONArray("peliculas")
                var index = 0
                
                // Actualizar la interfaz de usuario
                runOnUiThread {
                    for (i in peliculasArray.length() - 1 downTo peliculasArray.length() - 5) {
                        val pelicula = peliculasArray.getJSONObject(i)
                        val nombrePelicula = pelicula.getString("nombre")
                        
                        // Crear un botón para cada película
                        val button = Button(this)
                        button.text = nombrePelicula.replace("_", " ")
                        button.textSize = 14f
                        button.setTextColor(ContextCompat.getColor(this, R.color.white))
                        val drawable = ContextCompat.getDrawable(this, R.drawable.rounded_button)?.mutate()
                        val wrappedDrawable = DrawableCompat.wrap(drawable!!) // Hacerlo "tintable"

                        // Cambiar el color dinámicamente según la posición
                        when (index % 4) {
                            0 -> DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this, R.color.color1))
                            1 -> DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this, R.color.color2))
                            2 -> DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this, R.color.color3))
                            3 -> DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(this, R.color.color4))
                        }

                        // Aplicar el drawable tintado al botón
                        button.background = wrappedDrawable
                        val layoutParams = TableLayout.LayoutParams(
                            TableLayout.LayoutParams.WRAP_CONTENT,
                            TableLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 10, 0, 10)
                        }
                        button.layoutParams = layoutParams
                        button.setOnClickListener {
                            val intent = Intent(this, ResultsActivity::class.java)
                            val nombreSinGuiones = nombrePelicula.replace("_", " ")
                            intent.putExtra("USER_INPUT", nombreSinGuiones)
                            intent.putExtra("MOVIE_SEARCH", true)
                            intent.putExtra("IS_MOVIE", true)
                            startActivity(intent)
                        }
                        buttonContainer.addView(button)
                        index++
                    }
                }
            }.start()
            textView.visibility = View.VISIBLE
        } else {
            buttonContainer.visibility = View.GONE
        }

    
        //val btnSrchSurname = findViewById<Button>(R.id.btnSrchSurname)
        //val btnAddActor = findViewById<Button>(R.id.btnAddActor)

        val btnAddMovie = findViewById<ImageButton>(R.id.btnAddMovie)
        val btnQueryMovie = findViewById<ImageButton>(R.id.btnSrchMovie)
        val btnQueryActorByName = findViewById<ImageButton>(R.id.btnQueryActorByName)

        btnAddMovie.setOnClickListener {
            val intent = Intent(this, AddMovieActivity::class.java)
            startActivity(intent)
        }

        btnQueryMovie.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

         btnQueryActorByName.setOnClickListener {
            val intent = Intent(this, SearchActorActivity::class.java)
            startActivity(intent)
        }
        /*
        btnSrchSurname.setOnClickListener {
            val intent = Intent(this, SurnameActorActivity::class.java)
            startActivity(intent)
        } */
    }
}