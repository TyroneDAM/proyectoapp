package com.example.bookcloudapp.ui.screens

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bookcloudapp.R
import com.example.bookcloudapp.network.ApiService
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_inicio),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.5f)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { }
        )


        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Text(
                    text = "Iniciar sesión",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(16.dp)
                )
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
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
        )
    }
}
