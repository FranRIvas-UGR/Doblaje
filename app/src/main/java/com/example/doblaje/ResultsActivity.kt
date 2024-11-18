package com.example.doblaje

import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import android.app.Activity
import android.widget.TableLayout
import android.widget.TableRow
import org.json.JSONObject
import java.io.File
import android.content.Intent
import androidx.core.content.ContextCompat
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat

class ResultsActivity : Activity() {
    private lateinit var peliculasList: List<Pelicula>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        peliculasList = readJSONfromDevice()

        if (!intent.hasExtra("USER_INPUT")) {
            val messageTextView = findViewById<TextView>(R.id.message)
            messageTextView.visibility = TextView.VISIBLE
            messageTextView.text = "No se encontraron resultados"
            return
        }

        if (peliculasList.isNotEmpty() && peliculasList[0].nombre == "Error") {
            val messageTextView = findViewById<TextView>(R.id.message)
            messageTextView.visibility = TextView.VISIBLE
            messageTextView.text = "No se pudo leer el archivo 'peliculas.json'. Se va a crear uno nuevo. Reemplace el archivo con uno con contenido válido." +
                    "El archivo se encuentra en la siguiente ruta: ${getExternalFilesDir(null)}/peliculas.json"

            val fileToDelete = File(filesDir, "peliculas.json")
            if (fileToDelete.exists()) {
                fileToDelete.delete()
            }

            val file = File(getExternalFilesDir(null), "peliculas.json")
            file.writeText("{ \"peliculas\": [] }")

            messageTextView.text = "Ahora el archivo se encuentra en la siguiente ruta:${getExternalFilesDir(null)}/peliculas.json"
            return
        }
        val userInput = intent.getStringExtra("USER_INPUT")
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)
        if (userInput == null) {
            val messageTextView = findViewById<TextView>(R.id.message)
            messageTextView.visibility = TextView.VISIBLE
            messageTextView.text = "Debe ingresar un nombre de película"
            return
        }
        val realName = intent.getBooleanExtra("REAL_NAME", false)

        val filteredPeliculas = if (realName) {
            peliculasList.filter { pelicula ->
                pelicula.nombre.equals(userInput, ignoreCase = true)
            }
        } else {
            peliculasList.filter { pelicula ->
                pelicula.nombre.replace("_", " ").contains(userInput, ignoreCase = true)
            }
        }
        if (filteredPeliculas.isEmpty()) {
            val messageTextView = findViewById<TextView>(R.id.message)
            messageTextView.visibility = TextView.VISIBLE
            messageTextView.text = "No se encontraron resultados para '$userInput'"
            return
        }
        tableLayout.visibility = TableLayout.VISIBLE
        tableLayout.removeAllViews()

        if(filteredPeliculas.size > 1) {
            val messageTextView = findViewById<TextView>(R.id.message)
            messageTextView.visibility = TextView.VISIBLE
            messageTextView.text = "Se encontraron ${filteredPeliculas.size} resultados para '$userInput'. Por favor, elija uno."
            var index = 0
            val buttonContainer = findViewById<TableLayout>(R.id.buttonContainer)
            for (pelicula in filteredPeliculas) {
                val button = Button(this)
                button.text = pelicula.nombre.replace("_", " ")
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
                    intent.putExtra("USER_INPUT", pelicula.nombre)
                    intent.putExtra("REAL_NAME", true)
                    startActivity(intent)
                }
                buttonContainer.addView(button)
                index++
            }
        } else{
            val pelicula = filteredPeliculas[0]
            val messageTextView = findViewById<TextView>(R.id.message)
            messageTextView.visibility = TextView.VISIBLE
            messageTextView.text = "Película: ${pelicula.nombre.replace("_", " ")} (${pelicula.año})"
            var index = 0
            for (actor in pelicula.actores) {
                val row = TableRow(this)
                row.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                row.background = resources.getDrawable(R.drawable.table_pelicula, null)

                val cellActorOriginal = TextView(this)
                cellActorOriginal.text = actor.actorOriginal
                cellActorOriginal.textSize = 14f // Ajusta según tus necesidades
                cellActorOriginal.setTypeface(null, android.graphics.Typeface.ITALIC)
                cellActorOriginal.layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                )
                row.addView(cellActorOriginal)

                val cellActorDoblaje = TextView(this)
                cellActorDoblaje.text = actor.actorDoblaje
                cellActorDoblaje.textSize = 14f // Ajusta según tus necesidades
                cellActorDoblaje.setTypeface(null, android.graphics.Typeface.BOLD)
                cellActorDoblaje.layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    setMargins(10, 0, 10, 0)
                }
                row.addView(cellActorDoblaje)

                val cellPersonaje = TextView(this)
                cellPersonaje.text = actor.personaje
                cellPersonaje.textSize = 14f // Ajusta según tus necesidades
                cellPersonaje.setTypeface(null, android.graphics.Typeface.NORMAL)
                cellPersonaje.layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                )
                row.addView(cellPersonaje)

                if (index % 2 == 0) {
                    row.setBackgroundColor(ContextCompat.getColor(this, R.color.even_row_color))
                } else {
                    row.setBackgroundColor(ContextCompat.getColor(this, R.color.odd_row_color))
                }

                tableLayout.addView(row)
                index++
            }
        }

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun readJSONfromDevice(): List<Pelicula> {
        val filename = "peliculas.json"
        val file = File(getExternalFilesDir(null), filename)
        val text = file.readText()
        val peliculasList: MutableList<Pelicula> = mutableListOf()
        val peliculas = JSONObject(text).getJSONArray("peliculas")
        for (i in 0 until peliculas.length()) {
            val pelicula = peliculas.getJSONObject(i)
            val nombre = pelicula.getString("nombre")
            val año = pelicula.getInt("año")
            val actoresArray = pelicula.getJSONArray("actores")
            val actoresList = mutableListOf<Actor>()
            for (j in 0 until actoresArray.length()) {
                val actor = actoresArray.getJSONObject(j)
                val actorOriginal = actor.getString("actor_original")
                val actorDoblaje = actor.getString("actor_doblaje")
                val personaje = actor.getString("personaje")
                actoresList.add(Actor(actorOriginal, actorDoblaje, personaje))
            }
            peliculasList.add(Pelicula(nombre, año, actoresList))
        }

        return peliculasList
    }
}

data class Pelicula(val nombre: String, val año: Int, val actores: List<Actor>)
data class Actor(val actorOriginal: String, val actorDoblaje: String, val personaje: String)
