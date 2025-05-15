package com.example.bookcloudapp.network

import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object ApiService {
    private const val BASE_URL = "https://bookcloud.es/api/"
    private val client = OkHttpClient()

    var token: String? = null

    fun login(email: String, contrasena: String, callback: (Boolean, String?) -> Unit) {
        val json = JSONObject()
        json.put("email", email)
        json.put("contraseña", contrasena)

        val body = json.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(BASE_URL + "auth/login.php")
            .post(body)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, "Error de red: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyStr = response.body?.string()
                if (bodyStr != null) {
                    val jsonResponse = JSONObject(bodyStr)
                    val success = jsonResponse.optBoolean("success", false)
                    val mensaje = jsonResponse.optString("mensaje", "Error")
                    token = jsonResponse.optString("token", null)
                    if (success) {
                        callback(true, mensaje)
                    } else {
                        callback(false, mensaje)
                    }
                } else {
                    callback(false, "Respuesta vacía del servidor")
                }
            }
        })
    }

    fun register(nombre: String, email: String, contrasena: String, callback: (Boolean, String?) -> Unit) {
        val json = JSONObject()
        json.put("usuario", nombre)
        json.put("email", email)
        json.put("password", contrasena)

        val body = json.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(BASE_URL + "auth/register.php")
            .post(body)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, "Error de red: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyStr = response.body?.string()
                if (response.isSuccessful && bodyStr != null) {
                    val json = JSONObject(bodyStr)
                    val success = json.optBoolean("success", false)
                    val mensaje = json.optString("mensaje", "Registro finalizado")
                    callback(success, mensaje)
                } else {
                    callback(false, "Error al registrarse")
                }
            }
        })
    }

    fun obtenerLibros(callback: (List<Map<String, String>>) -> Unit) {
        val tokenLocal = token

        if (tokenLocal.isNullOrEmpty()) {
            callback(emptyList())
            return
        }

        val request = Request.Builder()
            .url(BASE_URL + "data/librosapp.php")
            .addHeader("Authorization", "Bearer $tokenLocal")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(emptyList())
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyStr = response.body?.string()
                val libros = mutableListOf<Map<String, String>>()

                if (response.isSuccessful && !bodyStr.isNullOrEmpty()) {
                    try {
                        val jsonArray = JSONArray(bodyStr)
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val libro = mapOf(
                                "titulo" to obj.optString("titulo", ""),
                                "autor" to obj.optString("autor", ""),
                                "portada" to obj.optString("portada", ""),
                                "descripcion" to obj.optString("descripcion", "")
                            )
                            libros.add(libro)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                callback(libros)
            }
        })
    }

}
