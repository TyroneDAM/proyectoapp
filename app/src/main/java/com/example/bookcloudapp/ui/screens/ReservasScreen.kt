package com.example.bookcloudapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.example.bookcloudapp.model.Reserva
import com.example.bookcloudapp.network.ApiService
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

@Composable
fun ReservasScreen() {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val reservasActivas = remember { mutableStateListOf<Reserva>() }
    var reservaAEliminar by remember { mutableStateOf<Reserva?>(null) }

    // Cargar reservas activas
    LaunchedEffect(Unit) {
        ApiService.obtenerReservas { todas ->
            reservasActivas.clear()
            reservasActivas.addAll(todas.filter { it.estado == "activa" })
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Tus Libros Reservados",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (reservasActivas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes libros reservados.")
                }
            } else {
                LazyColumn {
                    items(reservasActivas) { reserva ->

                        val imageLoader = ImageLoader.Builder(context)
                            .okHttpClient {
                                OkHttpClient.Builder()
                                    .addInterceptor { chain ->
                                        val newRequest = chain.request().newBuilder()
                                            .addHeader("User-Agent", "Mozilla/5.0")
                                            .build()
                                        chain.proceed(newRequest)
                                    }
                                    .build()
                            }
                            .build()

                        val painter = rememberAsyncImagePainter(
                            model = reserva.imagen,
                            imageLoader = imageLoader
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
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
                                    painter = painter,
                                    contentDescription = "Portada",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = reserva.titulo.ifBlank { "Sin título" },
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = reserva.autor.ifBlank { "Autor desconocido" },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = { reservaAEliminar = reserva },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                                    ) {
                                        Text("Cancelar reserva")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Diálogo de confirmación
        reservaAEliminar?.let { reserva ->
            AlertDialog(
                onDismissRequest = { reservaAEliminar = null },
                title = { Text("Cancelar reserva") },
                text = { Text("¿Estás seguro de que deseas cancelar esta reserva?") },
                confirmButton = {
                    TextButton(onClick = {
                        ApiService.cancelarReserva(reserva.id_reserva) { success, mensaje ->
                            if (success) {
                                reservasActivas.remove(reserva)
                            }
                            scope.launch {
                                snackbarHostState.showSnackbar(mensaje)
                            }
                        }
                        reservaAEliminar = null
                    }) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { reservaAEliminar = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
