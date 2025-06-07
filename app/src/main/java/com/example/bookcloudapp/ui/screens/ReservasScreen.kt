package com.example.bookcloudapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.bookcloudapp.R
import com.example.bookcloudapp.model.Reserva
import com.example.bookcloudapp.network.ApiService
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController
import androidx.compose.ui.text.style.TextAlign

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
                    "Tus libros reservados",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = Color(0xFFBF5F17),
                    modifier = Modifier
                        .background(Color(0xFFFFE0B2), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
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
                                "¿No vamos a reservar ningún libro? 🦊",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF6D4C41),
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(25.dp)
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

                // Botón para volver
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
