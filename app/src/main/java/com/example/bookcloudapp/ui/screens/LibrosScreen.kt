package com.example.bookcloudapp.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.example.bookcloudapp.R
import com.example.bookcloudapp.model.Reserva
import com.example.bookcloudapp.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient

@Composable
fun LibrosScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("favoritos", Context.MODE_PRIVATE) }

    var libros by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var reservas by remember { mutableStateOf<List<Reserva>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    val favoritos = remember { mutableStateMapOf<String, Boolean>() }
    val reservados = remember { mutableStateMapOf<String, Boolean>() }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val librosFiltrados = libros.filter {
        it["titulo"]?.contains(searchQuery, ignoreCase = true) == true
    }

    // Cargar libros
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            ApiService.obtenerLibros { lista ->
                libros = lista
            }
        }
    }

    // Cargar reservas reales desde el servidor
    LaunchedEffect(Unit) {
        ApiService.obtenerReservas { lista ->
            reservas = lista
            val isbnsReservados = lista.filter { it.estado == "activa" }.map { it.isbn }
            reservados.clear()
            isbnsReservados.forEach { reservados[it] = true }
        }
    }

    // Cargar favoritos del usuario
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

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            Image(
                painter = painterResource(id = R.drawable.fondo_bosque),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.fillMaxSize().alpha(0.8f)
            )

            Image(
                painter = painterResource(id = R.drawable.zorro_leyendo),
                contentDescription = "Zorro leyendo",
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
            )

            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Mi Biblioteca", style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                            Button(onClick = { navController.navigate("favoritos") }) {
                                Text("Favoritos")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { navController.navigate("reservas") }) {
                                Text("Reservas")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                context.getSharedPreferences("bookcloud_prefs", Context.MODE_PRIVATE)
                                    .edit().remove("token").apply()
                                navController.navigate("login") {
                                    popUpTo("libros") { inclusive = true }
                                }
                            }) {
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
                        val isbn = libro["isbn"] ?: return@items
                        val esFavorito = favoritos[id] ?: false
                        val yaReservado = reservados[isbn] ?: false

                        val imageLoader = ImageLoader.Builder(context)
                            .okHttpClient {
                                OkHttpClient.Builder().addInterceptor { chain ->
                                    val newRequest = chain.request().newBuilder()
                                        .addHeader("User-Agent", "Mozilla/5.0").build()
                                    chain.proceed(newRequest)
                                }.build()
                            }.build()

                        val painter = rememberAsyncImagePainter(model = libro["portada"], imageLoader = imageLoader)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painter,
                                        contentDescription = "Portada del libro",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(libro["titulo"] ?: "", style = MaterialTheme.typography.titleMedium)
                                        Text(libro["autor"] ?: "", style = MaterialTheme.typography.bodyMedium)
                                        Text(libro["descripcion"] ?: "", maxLines = 3, overflow = TextOverflow.Ellipsis)
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
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        ApiService.reservarLibro(isbn) { success, mensaje ->
                                            if (success) {
                                                reservados[isbn] = true
                                            }
                                            scope.launch {
                                                snackbarHostState.showSnackbar(mensaje)
                                            }
                                        }
                                    },
                                    enabled = !yaReservado,
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(if (yaReservado) "Reservado" else "📚 Reservar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
