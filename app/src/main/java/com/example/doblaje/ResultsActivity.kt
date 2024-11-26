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
import android.widget.ImageButton
import androidx.core.graphics.drawable.DrawableCompat

class ResultsActivity : Activity() {
    private lateinit var peliculasList: List<Pelicula>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        var backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, SelectActivity::class.java)
            intent.putExtra("START", false)
            startActivity(intent)
        }

        var button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            if (intent.hasExtra("ACTOR_NAME")) {
                val intent = Intent(this, SearchActorActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }


        peliculasList = readJSONfromDevice()
        if (intent.hasExtra("MOVIE_SEARCH")) {
            if (!intent.hasExtra("USER_INPUT") ) {
                showMessage("No se encontraron resultados")
                return
            }

            if (peliculasList.isNotEmpty() && peliculasList[0].nombre == "Error") {
                handleFileError()
                return
            }

            val userInput = intent.getStringExtra("USER_INPUT")
            if (userInput == null || userInput == "") {
                showMessage("Debe ingresar un nombre de película")
                return
            }

            val realName = intent.getBooleanExtra("REAL_NAME", false)
            val filteredPeliculas = filterPeliculas(userInput, realName)

            if (filteredPeliculas.isEmpty()) {
                showMessage("No se encontraron resultados para '$userInput'")
                return
            }

            displayResults(filteredPeliculas, userInput)
        }
        else if (intent.hasExtra("ACTOR_NAME")) {
            val actorName = intent.getStringExtra("ACTOR_NAME")
            if (actorName == null || actorName == "") {
                showMessage("Debe ingresar un nombre de actor")
                return
            }

            val filteredPeliculasActor = filterPeliculasByActorName(actorName)
            if (filteredPeliculasActor.isEmpty()) {
                showMessage("No se encontraron resultados para '$actorName'")
                return
            }

            val messageTxt = findViewById<TextView>(R.id.message)
            messageTxt.text = "Peliculas: " + filteredPeliculasActor.size
            messageTxt.visibility = TextView.VISIBLE
            displayActorDetails(filteredPeliculasActor, actorName)

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

    private fun filterPeliculas(userInput: String, realName: Boolean): List<Pelicula> {
        return if (realName) {
            peliculasList.filter { pelicula ->
                pelicula.nombre.equals(userInput, ignoreCase = true)
            }
        } else {
            peliculasList.filter { pelicula ->
                pelicula.nombre.replace("_", " ").contains(userInput, ignoreCase = true)
            }
        }
    }

    private fun displayResults(filteredPeliculas: List<Pelicula>, userInput: String) {
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)
        tableLayout.visibility = TableLayout.VISIBLE
        tableLayout.removeAllViews()

        if (filteredPeliculas.size > 1) {
            showMessage("Se encontraron ${filteredPeliculas.size} resultados para '$userInput'. Por favor, elija uno.")
            displayButtons(filteredPeliculas)
        } else {
            displayPeliculaDetails(filteredPeliculas[0])
        }

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayActorDetails(filteredPeliculasActor: List<ActorPelicula>, actorName: String) {
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)
        val messageTextView = findViewById<TextView>(R.id.message)
        tableLayout.visibility = TableLayout.VISIBLE
        tableLayout.removeAllViews()
        messageTextView.visibility = TextView.VISIBLE
        messageTextView.text = "$actorName. Número de películas: ${filteredPeliculasActor.size}"
        var index = 0
        for (pel in filteredPeliculasActor) {
            val row = TableRow(this)
            row.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            row.background = resources.getDrawable(R.drawable.table_pelicula, null)

            val cellMovieName = TextView(this)
            cellMovieName.text = pel.pelicula.replace("_", " ")
            cellMovieName.textSize = 14f // Ajusta según tus necesidades
            cellMovieName.setTypeface(null, android.graphics.Typeface.ITALIC)
            cellMovieName.layoutParams = TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
            )
            row.addView(cellMovieName)

            val cellAño = TextView(this)
            cellAño.text = pel.año.toString()
            cellAño.textSize = 14f // Ajusta según tus necesidades
            cellAño.setTypeface(null, android.graphics.Typeface.BOLD)
            cellAño.layoutParams = TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                setMargins(10, 0, 10, 0)
            }
            row.addView(cellAño)

            val cellPersonaje = TextView(this)
            cellPersonaje.text = pel.personaje
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

    private fun displayButtons(filteredPeliculas: List<Pelicula>) {
        val buttonContainer = findViewById<TableLayout>(R.id.buttonContainer)
        var index = 0
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
    }

    private fun displayPeliculaDetails(pelicula: Pelicula) {
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)
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

    private fun showMessage(message: String) {
        val messageTextView = findViewById<TextView>(R.id.message)
        messageTextView.visibility = TextView.VISIBLE
        messageTextView.text = message
    }

    private fun handleFileError() {
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
    }

    private fun filterPeliculasByActorName(actorName: String?): List<ActorPelicula> {
        if (actorName == null) {
            return emptyList()
        }

        val actorNameUpper = actorName.uppercase()
        val actorPeliculas = mutableListOf<ActorPelicula>()
        val lista_peliculas = convertirActoresNombres(peliculasList)
        for (pelicula in lista_peliculas) {
            for (actor in pelicula.actores) {
                if (actor.actorDoblaje.contains(actorNameUpper)) {
                    actorPeliculas.add(ActorPelicula(pelicula.nombre, pelicula.año, actor.personaje))
                }
            }
        }
        return actorPeliculas
    }
}

fun convertirActoresNombres(peliculas: List<Pelicula>): List<Pelicula> {
    val peliculasNuevas = mutableListOf<Pelicula>()
    for (pelicula in peliculas) {
        val actoresNuevos = mutableListOf<Actor>()
        for (actor in pelicula.actores) {
            val actorOriginal = actor.actorOriginal
            val actorDoblaje = actor.actorDoblaje.split(", ").reversed().joinToString(" ") { it.capitalize() }
            val personaje = actor.personaje
            actoresNuevos.add(Actor(actorOriginal, actorDoblaje, personaje))
        }
        peliculasNuevas.add(Pelicula(pelicula.nombre, pelicula.año, actoresNuevos))
    }
    return peliculasNuevas
}

data class Pelicula(val nombre: String, val año: Int, val actores: List<Actor>)
data class Actor(val actorOriginal: String, val actorDoblaje: String, val personaje: String)
data class ActorPelicula(val pelicula: String, val año: Int, val personaje: String)
