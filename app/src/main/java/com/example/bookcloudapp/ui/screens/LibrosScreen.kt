package com.example.bookcloudapp.ui.screens

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.bookcloudapp.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.navigation.NavHostController
import androidx. compose. runtime. saveable. mapSaver
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun LibrosScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("favoritos", Context.MODE_PRIVATE) }
    var libros by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    val favoritos = remember { mutableStateMapOf<String, Boolean>() }


    val librosFiltrados = libros.filter {
        it["titulo"]?.contains(searchQuery, ignoreCase = true) == true
    }

    LaunchedEffect(libros) {
        withContext(Dispatchers.IO) {
            libros.forEach { libro ->
                val id = libro["id"]?.takeIf { it.isNotBlank() } ?: libro["titulo"]
                if (id != null) {
                    favoritos[id] = prefs.getBoolean(id, false)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            ApiService.obtenerLibros { lista ->
                libros = lista
            }
        }
    }


    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Mi Biblioteca",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Button(
                        onClick = { navController.navigate("favoritos") },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Favoritos")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val prefs = context.getSharedPreferences("bookcloud_prefs", Context.MODE_PRIVATE)
                            prefs.edit().remove("token").apply()
                            navController.navigate("login") {
                                popUpTo("libros") { inclusive = true }
                            }
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("Cerrar sesión")
                    }
                }
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar por título...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            items(librosFiltrados) { libro ->
                val id = libro["id"]?.takeIf { it.isNotBlank() } ?: libro["titulo"] ?: return@items
                val esFavorito = favoritos[id] ?: false

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(libro["portada"]),
                            contentDescription = "Portada del libro",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = libro["titulo"] ?: "Sin título",
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = libro["autor"] ?: "Autor desconocido",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = libro["descripcion"] ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(onClick = {
                            val nuevoValor = !esFavorito
                            favoritos[id] = nuevoValor
                            prefs.edit().putBoolean(id, nuevoValor).apply()
                        }) {
                            Icon(
                                imageVector = if (esFavorito) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Favorito",
                                tint = if (esFavorito) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}
