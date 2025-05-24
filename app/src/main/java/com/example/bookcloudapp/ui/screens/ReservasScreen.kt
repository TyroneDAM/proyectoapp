package com.example.bookcloudapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.bookcloudapp.model.Reserva
import com.example.bookcloudapp.network.ApiService
import kotlinx.coroutines.launch

@Composable
fun ReservasScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val reservasActivas = remember { mutableStateListOf<Reserva>() }
    var reservaAEliminar by remember { mutableStateOf<Reserva?>(null) }

    // Cargar reservas activas al entrar
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
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp)) {
                                Image(
                                    painter = rememberAsyncImagePainter(reserva.imagen),
                                    contentDescription = "Portada",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .padding(end = 16.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(reserva.titulo, style = MaterialTheme.typography.titleMedium)
                                    Text(reserva.autor, style = MaterialTheme.typography.bodyMedium)
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
