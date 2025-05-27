package com.example.bookcloudapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.example.bookcloudapp.R
import com.example.bookcloudapp.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DetalleLibroScreen(isbn: String?) {
    var libro by remember { mutableStateOf<Map<String, String>?>(null) }

    // Cargar libros y filtrar por ISBN
    LaunchedEffect(isbn) {
        if (!isbn.isNullOrBlank()) {
            withContext(Dispatchers.IO) {
                ApiService.obtenerLibros { lista ->
                    libro = lista.find { it["isbn"] == isbn }
                }
            }
        }
    }

    libro?.let { datos ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.fondo_bosque),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.7f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val imageLoader = ImageLoader.Builder(LocalContext.current).build()

                val painter = rememberAsyncImagePainter(model = datos["portada"], imageLoader = imageLoader)

                Image(
                    painter = painter,
                    contentDescription = "Portada del libro",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = datos["titulo"] ?: "Sin título",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Autor: ${datos["autor"] ?: "Desconocido"}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = "Editorial: ${datos["editorial"] ?: "No disponible"}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = "Género: ${datos["genero"] ?: "No especificado"}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = "Publicado: ${datos["fecha_publicacion"] ?: "No disponible"}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = datos["descripcion"] ?: "Sin descripción",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Justify,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    } ?: run {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Libro no encontrado", color = Color.Gray)
        }
    }
}
