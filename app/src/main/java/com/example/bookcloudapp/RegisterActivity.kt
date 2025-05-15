package com.example.bookcloudapp

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    val URL = "https://bookcloud.es/api/data/register.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etUsuario = findViewById<EditText>(R.id.etNuevoUsuario)
        val etPassword = findViewById<EditText>(R.id.etNuevoPassword)
        val etEmail = findViewById<EditText>(R.id.etNuevoEmail)
        val btnCrear = findViewById<Button>(R.id.btnCrearCuenta)

        btnCrear.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val correo = etEmail.text.toString().trim()

            if (usuario.isEmpty() || password.isEmpty() || correo.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val json = JSONObject()
            json.put("usuario", usuario)
            json.put("password", password)
            json.put("email", correo)

            val body = json.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Error de red", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val mensaje = response.body?.string()
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, mensaje ?: "Registro enviado", Toast.LENGTH_LONG).show()
                        if (response.isSuccessful) {
                            finish()
                        }
                    }
                }
            })
        }
    }
}
