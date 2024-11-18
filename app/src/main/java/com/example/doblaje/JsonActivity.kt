package com.example.doblaje

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

class JsonActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_json)

        // Si el archivo peliculas.json ya existe, redirigir a MainActivity
        val file = File(getExternalFilesDir(null), "peliculas.json")
        if (file.exists()) {
            val intent = Intent(this, SelectActivity::class.java)
            startActivity(intent)
            finish() // Terminar esta actividad para evitar que el usuario vuelva atrás
            return
        }

        // Mostrar un mensaje sobre almacenamiento interno
        Toast.makeText(
            this,
            "Este proyecto almacena los datos en la memoria interna del dispositivo",
            Toast.LENGTH_LONG
        ).show()

        // Botón para crear un archivo JSON
        val createJsonButton = findViewById<Button>(R.id.create_button)
        createJsonButton.setOnClickListener {
            val file = File(getExternalFilesDir(null), "peliculas.json")
            file.writeText("{ \"peliculas\": [] }")
            // Redirigir a MainActivity
            val intent = Intent(this, SelectActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Botón para subir un archivo JSON
        val uploadJsonButton = findViewById<Button>(R.id.upload_button)
        uploadJsonButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/json"
            }
            startActivityForResult(intent, 1) // Usa un requestCode para identificar el resultado
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {
                handleJsonFile(uri)
            } else {
                Toast.makeText(this, "No se seleccionó ningún archivo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleJsonFile(uri: Uri) {
        try {
            // Crear o sobrescribir el archivo peliculas.json en memoria interna
            val file = File(getExternalFilesDir(null), "peliculas.json")
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            Toast.makeText(this, "Archivo cargado con éxito", Toast.LENGTH_SHORT).show()

            // Redirigir a MainActivity
            val intent = Intent(this, SelectActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al cargar el archivo", Toast.LENGTH_SHORT).show()
        }
    }
}