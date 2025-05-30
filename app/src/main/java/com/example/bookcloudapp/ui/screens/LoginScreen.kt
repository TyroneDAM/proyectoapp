package com.example.bookcloudapp.ui.screens

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.bookcloudapp.R
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
    var passwordVisible by remember { mutableStateOf(false) }

    var showNameDialog by remember { mutableStateOf(false) }
    var nombreUsuario by remember { mutableStateOf("") }

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
                    modifier = Modifier.padding(16.dp)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(200.dp))

                    Image(
                        painter = painterResource(id = R.drawable.fox_icon),
                        contentDescription = "Zorro",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 2.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
                            .padding(24.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
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
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                                        )
                                    }
                                }
                            )

                            Button(
                                onClick = {
                                    if (email.isNotBlank() && contrasena.isNotBlank()) {
                                        isLoading = true
                                        ApiService.login(email, contrasena) { success, mensaje ->
                                            isLoading = false
                                            Handler(Looper.getMainLooper()).post {
                                                if (success) {
                                                    val prefs = context.getSharedPreferences("bookcloud_prefs", Context.MODE_PRIVATE)
                                                    prefs.edit().putString("token", ApiService.token).apply()

                                                    val nombreGuardado = prefs.getString("usuario", null)
                                                    if (nombreGuardado.isNullOrBlank()) {
                                                        showNameDialog = true
                                                    } else {
                                                        onLoginSuccess()
                                                    }
                                                } else {
                                                    Toast.makeText(context, mensaje ?: "Error", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            ) {
                                Text(text = if (isLoading) "Cargando..." else "Iniciar sesión")
                            }

                            TextButton(onClick = onGoToRegister) {
                                Text("¿No tienes cuenta? Regístrate aquí")
                            }
                        }
                    }
                }
            }
        }

        if (showNameDialog) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    TextButton(
                        onClick = {
                            val prefs = context.getSharedPreferences("bookcloud_prefs", Context.MODE_PRIVATE)
                            prefs.edit().putString("usuario", nombreUsuario.trim()).apply()
                            showNameDialog = false
                            onLoginSuccess()
                        },
                        enabled = nombreUsuario.isNotBlank()
                    ) {
                        Text("Guardar")
                    }
                },
                title = { Text("¿Cómo quieres que te llamemos?") },
                text = {
                    OutlinedTextField(
                        value = nombreUsuario,
                        onValueChange = { nombreUsuario = it },
                        label = { Text("Tu nombre") }
                    )
                }
            )
        }
    }
}
