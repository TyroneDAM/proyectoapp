package com.example.bookcloudapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.bookcloudapp.navigation.AppNavigation
import com.example.bookcloudapp.network.ApiService
import com.example.bookcloudapp.ui.theme.BookcloudAppTheme
import kotlinx.coroutines.delay
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("bookcloud_prefs", MODE_PRIVATE)
        val tokenGuardado = prefs.getString("token", null)
        if (!tokenGuardado.isNullOrBlank()) {
            ApiService.token = tokenGuardado
        }

        setContent {
            BookcloudAppTheme {
                val navController = rememberNavController()
                var isLoading by remember { mutableStateOf(true) }
                var startDestination by remember { mutableStateOf("login") }

                LaunchedEffect(Unit) {
                    delay(2000) // le pongo delay para que permita ver la pantalla de carga, no necesita tanto tiempo
                    if (!tokenGuardado.isNullOrBlank()) {
                        startDestination = "libros"
                    }
                    isLoading = false
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize()) {

                        Image(
                            painter = painterResource(id = R.drawable.fondo_bosque),
                            contentDescription = null,
                            contentScale = ContentScale.FillHeight,
                            modifier = Modifier.fillMaxSize()
                        )

                        Text(
                            text = "Cargando...",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.align(Alignment.Center)

                        )

                        Image(
                            painter = painterResource(id = R.drawable.zorro_bienvenida),
                            contentDescription = "Zorro saludo",
                            modifier = Modifier
                                .size(300.dp)
                                .align(Alignment.BottomStart)
                                .offset(x = (-47).dp, y = 16.dp)
                        )
                    }
                }
                else {
                    AppNavigation(navController = navController, startDestination = startDestination)
                }
            }
        }
    }
}
