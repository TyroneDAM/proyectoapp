package com.example.bookcloudapp.ui.screens

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import java.text.Normalizer
import androidx.compose.animation.core.*
import kotlinx.coroutines.delay

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

    var selectedGenero by remember { mutableStateOf("Todos") }
    val generos = listOf("Todos", "Economía", "Ficción", "Historia", "Infantil y Juvenil", "Novela Gráfica", "Poesía")
    var expanded by remember { mutableStateOf(false) }

    var cargandoSorpresa by remember { mutableStateOf(false) }
    var libroSorpresa by remember { mutableStateOf<Map<String, String>?>(null) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    val librosFiltrados = libros.filter {
        val coincideTitulo = it["titulo"]?.contains(searchQuery, ignoreCase = true) == true
        val generoLibro = it["genero"]
            ?.let { g -> Normalizer.normalize(g, Normalizer.Form.NFD).replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "").replace("\\s+".toRegex(), " ").lowercase().trim() } ?: ""
        val generoFiltro = Normalizer.normalize(selectedGenero, Normalizer.Form.NFD).replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "").replace("\\s+".toRegex(), " ").lowercase().trim()
        val coincideGenero = selectedGenero == "Todos" || generoLibro == generoFiltro
        coincideTitulo && coincideGenero
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) { ApiService.obtenerLibros { lista -> libros = lista } }
    }

    LaunchedEffect(Unit) {
        ApiService.obtenerReservas { lista ->
            reservas = lista
            val isbnsReservados = lista.filter { it.estado == "activa" }.map { it.isbn }
            reservados.clear()
            isbnsReservados.forEach { reservados[it] = true }
        }
    }

    LaunchedEffect(libros) {
        withContext(Dispatchers.IO) {
            libros.forEach { libro ->
                val id = libro["id"]?.takeIf { it.isNotBlank() } ?: libro["titulo"]
                if (id != null) favoritos[id] = prefs.getBoolean(id, false)
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

            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Mi Biblioteca", style = MaterialTheme.typography.headlineSmall)
                    IconButton(onClick = { navController.navigate("perfil") }) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Perfil",
                                tint = Color.White
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { navController.navigate("favoritos") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784))
                    ) { Text("Favoritos") }

                    Button(
                        onClick = { navController.navigate("reservas") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D))
                    ) { Text("Reservas") }

                    Button(
                        onClick = {
                            context.getSharedPreferences("bookcloud_prefs", Context.MODE_PRIVATE)
                                .edit().remove("token").apply()
                            navController.navigate("login") {
                                popUpTo("libros") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Cerrar sesión") }
                }

                Button(
                    onClick = {
                        if (librosFiltrados.isNotEmpty()) {
                            cargandoSorpresa = true
                            scope.launch {
                                delay(1000)
                                libroSorpresa = librosFiltrados.random()
                                cargandoSorpresa = false
                                mostrarDialogo = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6))
                ) {
                    Text("🎲 Sorpréndeme con un nuevo libro")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar por título...") },
                        modifier = Modifier.weight(1f)
                    )
                    Box {
                        OutlinedButton(onClick = { expanded = true }) {
                            Text(selectedGenero)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            generos.forEach { genero ->
                                DropdownMenuItem(
                                    text = { Text(genero) },
                                    onClick = {
                                        selectedGenero = genero
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

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
                                .padding(vertical = 8.dp)
                                .clickable {
                                    navController.navigate("detalleLibro?isbn=${Uri.encode(isbn)}")
                                },
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
                                            if (success) reservados[isbn] = true
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

            //  carga botón sorpresa
            if (cargandoSorpresa) {
                val rotation by rememberInfiniteTransition().animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing)
                    )
                )

                AlertDialog(
                    onDismissRequest = { cargandoSorpresa = false },
                    confirmButton = {},
                    title = { Text("El zorro está buscando...") },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.drawable.zorro_mareado),
                                contentDescription = "Zorro girando",
                                modifier = Modifier
                                    .size(120.dp)
                                    .graphicsLayer { rotationZ = rotation }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Espérame un segundo...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                )
            }

            if (mostrarDialogo && libroSorpresa != null) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogo = false },
                    confirmButton = {
                        TextButton(onClick = {
                            mostrarDialogo = false
                            navController.navigate("detalleLibro?isbn=" + Uri.encode(libroSorpresa!!["isbn"]))
                        }) {
                            Text("Ver más")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDialogo = false }) {
                            Text("Cerrar")
                        }
                    },
                    title = { Text("¡Tu libro sorpresa!") },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val imageLoader = ImageLoader.Builder(context)
                                .okHttpClient {
                                    OkHttpClient.Builder().addInterceptor { chain ->
                                        val newRequest = chain.request().newBuilder()
                                            .addHeader("User-Agent", "Mozilla/5.0").build()
                                        chain.proceed(newRequest)
                                    }.build()
                                }.build()
                            val painter = rememberAsyncImagePainter(
                                model = libroSorpresa!!["portada"],
                                imageLoader = imageLoader
                            )
                            Image(
                                painter = painter,
                                contentDescription = libroSorpresa!!["titulo"],
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(libroSorpresa!!["titulo"] ?: "", style = MaterialTheme.typography.titleMedium)
                            Text(libroSorpresa!!["autor"] ?: "", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                )
            }
        }
    }
}
