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
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Text(
                text = "Iniciar sesión",
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

            Button(
                onClick = {
                    if (email.isNotBlank() && contrasena.isNotBlank()) {
                        isLoading = true
                        ApiService.login(email, contrasena) { success, mensaje ->
                            isLoading = false
                            Handler(Looper.getMainLooper()).post {
                                if (success) {
                                    context.getSharedPreferences("bookcloud_prefs", 0)
                                        .edit()
                                        .putString("token", ApiService.token)
                                        .apply()
                                    onLoginSuccess()
                                } else {
                                    Toast.makeText(context, mensaje ?: "Error", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(text = if (isLoading) "Cargando..." else "Iniciar sesión")
            }

            TextButton(
                onClick = onGoToRegister,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }
        }
    }
}
