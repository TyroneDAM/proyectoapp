package com.example.bookcloudapp.ui.screens

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.bookcloudapp.R
import com.example.bookcloudapp.network.ApiService

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmacion by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_bosque),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.6f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .shadow(8.dp, shape = RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.8f), shape = RoundedCornerShape(16.dp))
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.zorro_diploma),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Crear cuenta",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    OutlinedTextField(
                        value = confirmacion,
                        onValueChange = { confirmacion = it },
                        label = { Text("Confirmar contraseña") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
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
                                        Toast.makeText(context, "Registro exitoso. Revisa tu correo para activar la cuenta", Toast.LENGTH_LONG).show()
                                        if (success) {
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
    }
}
