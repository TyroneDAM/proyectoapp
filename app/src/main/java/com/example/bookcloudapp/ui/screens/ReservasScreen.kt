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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.bookcloudapp.R
import com.example.bookcloudapp.model.Reserva
import com.example.bookcloudapp.network.ApiService
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController

@Composable
fun ReservasScreen(navController: NavHostController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val reservasActivas = remember { mutableStateListOf<Reserva>() }
    var reservaAEliminar by remember { mutableStateOf<Reserva?>(null) }

    LaunchedEffect(Unit) {
        ApiService.obtenerReservas { todas ->
            reservasActivas.clear()
            reservasActivas.addAll(todas.filter { it.estado == "activa" })
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Fondo de bosque
            Image(
                painter = painterResource(id = R.drawable.fondo_bosque),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.8f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Tus Libros Reservados",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (reservasActivas.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.drawable.zorro_molesto),
                                contentDescription = "Zorro molesto",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "¿Es que no vamos a coger ningún libro?",
                                fontSize = 18.sp,
                                color = Color(0xFF2E7D32),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        items(reservasActivas) { reserva ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(modifier = Modifier.padding(16.dp)) {
                                    Image(
                                        painter = rememberAsyncImagePainter(reserva.imagen),
                                        contentDescription = "Portada",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = reserva.titulo.ifBlank { "Sin título" },
                                            style = MaterialTheme.typography.titleMedium
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

                Spacer(modifier = Modifier.height(12.dp))

                // Botón siempre visible abajo
                Button(
                    onClick = {
                        navController.navigate("libros") {
                            popUpTo("reservas") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA5D6A7))
                ) {
                    Text("Volver a tus libros")
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
}
