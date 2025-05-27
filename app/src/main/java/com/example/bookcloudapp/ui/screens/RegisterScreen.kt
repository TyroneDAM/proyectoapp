package com.example.bookcloudapp.ui.screens

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.bookcloudapp.network.ApiService

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmacion by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre de usuario") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            OutlinedTextField(
                value = confirmacion,
                onValueChange = { confirmacion = it },
                label = { Text("Confirmar contraseña") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            Button(
                onClick = {
                    val camposValidos = nombre.isNotBlank() && email.isNotBlank() && contrasena.isNotBlank() && confirmacion.isNotBlank()
                    val contrasenasCoinciden = contrasena == confirmacion

                    if (!camposValidos) {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    } else if (!contrasenasCoinciden) {
                        Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    } else {
                        isLoading = true

                        ApiService.register(nombre, email, contrasena) { success, mensaje ->
                            isLoading = false
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(context, mensaje ?: "Error", Toast.LENGTH_SHORT).show()
                                if (success) {
                                    // ✅ Guardar nombre localmente para el perfil
                                    context.getSharedPreferences("bookcloud_prefs", 0)
                                        .edit()
                                        .putString("usuario", nombre)
                                        .apply()

                                    onRegisterSuccess()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(text = if (isLoading) "Creando cuenta..." else "Registrarse")
            }
        }
    }
}
