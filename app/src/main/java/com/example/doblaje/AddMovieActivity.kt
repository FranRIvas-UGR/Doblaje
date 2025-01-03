package com.example.doblaje

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.app.Activity
import android.content.Intent
import android.widget.ImageButton
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddMovieActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_movie)

        val intent = intent
        val action = intent.action
        val type = intent.type

        val editText = findViewById<EditText>(R.id.editText)
        val button = findViewById<Button>(R.id.button)

        if (Intent.ACTION_SEND == action && type != null) {
            if ("text/plain" == type) {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (sharedText != null) {
                    editText.setText(sharedText)
                }
            }
        }

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, SelectActivity::class.java)
            startActivity(intent)
        }

        button.setOnClickListener {
            val userInput = editText.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                val pelicula = scrapeMovieDetails(userInput)
                withContext(Dispatchers.Main) {
                    if (pelicula.nombre.isEmpty()) {
                        val intent = Intent(this@AddMovieActivity, SelectActivity::class.java).apply {
                            intent.putExtra("ADD_MOVIE", false)
                            intent.putExtra("ERROR_MESSAGE", "Error al leer los detalles de la película.")
                        }
                        startActivity(intent)
                        return@withContext
                    }
                    val guardado = saveMovieToJSON(pelicula)
                    if (!guardado) {
                        val intent = Intent(this@AddMovieActivity, SelectActivity::class.java).apply {
                            putExtra("ADD_MOVIE", false)
                            putExtra("ERROR_MESSAGE", "La película ya existe en la base de datos.")
                            putExtra("START", false)
                        }
                        startActivity(intent)
                        return@withContext
                    }
                    val intent = Intent(this@AddMovieActivity, SelectActivity::class.java).apply {
                        putExtra("ADD_MOVIE", true)
                    }
                    startActivity(intent)
                }
            }
        }
    }
}

fun Activity.saveMovieToJSON(movie: MovieDetails): Boolean {
    val gson = GsonBuilder().setPrettyPrinting().create()
    val file = File(this.getExternalFilesDir(null), "peliculas.json")
    val jsonObject = if (file.exists()) {
        val existingJson = file.readText()
        JsonParser.parseString(existingJson).asJsonObject
    } else {
        JsonObject().apply {
            add("peliculas", JsonArray())
            add("ultimas_peliculas", JsonArray())
        }
    }

    val peliculasArray = jsonObject.getAsJsonArray("peliculas")
    val existingMovie = peliculasArray.firstOrNull { it.asJsonObject.get("nombre").asString == movie.nombre }
    if (existingMovie != null) {
        return false
    }

    // Crear el array "ultimas_peliculas" si no existe
    if (!jsonObject.has("ultimas_peliculas")) {
        jsonObject.add("ultimas_peliculas", JsonArray())
    }

    // Añadir la nueva película al array "películas"
    peliculasArray.add(JsonParser.parseString(gson.toJson(movie)))


    // Manejo del array "ultimas_peliculas"
    val ultimasPeliculasArray = jsonObject.getAsJsonArray("ultimas_peliculas")
    if (ultimasPeliculasArray.size() >= 4) {
        // Si hay 4 películas, eliminar la primera
        ultimasPeliculasArray.remove(0)
    }
    // Añadir la nueva película al final del array
    ultimasPeliculasArray.add(JsonParser.parseString(gson.toJson(movie)))

    // Actualizar el archivo JSON con los cambios
    file.writeText(gson.toJson(jsonObject))
    ordenarJson(file)
    return true
}


fun scrapeMovieDetails(url: String): MovieDetails {
    val doc: Document = try {
        Jsoup.connect(url).get()
    } catch (e: Exception) {
        e.printStackTrace()
        return MovieDetails(nombre = "", año = "", actores = emptyList())
    }
    var title = doc.select("title").text()
    title = title.replace(" - Ficha eldoblaje.com - Doblaje", "").replace(" ", "_").replace(".", "").replace(":", "")
    
    val yearTd = doc.select("td.trebuchett").firstOrNull { it.text().contains("Año de Grabación:") }
    val year = yearTd?.text()?.split(":")?.get(1)?.trim() ?: "Desconocido"

    val table = doc.select("table[border=0][cellspacing=4][cellpadding=9]").first()
    val actors = mutableListOf<Map<String, String>>()

    table?.select("tr")?.drop(1)?.forEach { row ->
        val cells = row.select("td")
        if (cells.size >= 3) {
            val originalActor = cells[0].text().trim().uppercase()
            val dubbingActor = cells[1].text().trim().uppercase()
            val character = cells[2].text().trim().uppercase()

            actors.add(
                mapOf(
                    "actor_original" to originalActor,
                    "actor_doblaje" to dubbingActor,
                    "personaje" to character
                )
            )
        }
    }

    return MovieDetails(
        nombre = title,
        año = year,
        actores = actors
    )
}

fun ordenarJson(file: File) {
    val gson = GsonBuilder().setPrettyPrinting().create()
    val json = file.readText()
    val jsonObject = JsonParser.parseString(json).asJsonObject
    val peliculasArray = jsonObject.getAsJsonArray("peliculas")
    val sortedArray = peliculasArray.sortedBy { it.asJsonObject.get("año").asString }

    // Crear un nuevo JsonArray y agregar los elementos ordenados
    val newPeliculasArray = JsonArray()
    sortedArray.forEach { newPeliculasArray.add(it) }

    // Reemplazar el array antiguo con el nuevo array ordenado
    jsonObject.remove("peliculas")
    jsonObject.add("peliculas", newPeliculasArray)

    file.writeText(gson.toJson(jsonObject))
}

data class MovieDetails(
    val nombre: String,
    val año: String,
    val actores: List<Map<String, String>>
)
