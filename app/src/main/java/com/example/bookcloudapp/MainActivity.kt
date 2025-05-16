package com.example.bookcloudapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.bookcloudapp.navigation.AppNavigation
import com.example.bookcloudapp.network.ApiService
import com.example.bookcloudapp.ui.theme.BookcloudAppTheme
import kotlinx.coroutines.delay

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
                    delay(300) // Simula carga o verifica si hay token
                    if (!tokenGuardado.isNullOrBlank()) {
                        startDestination = "libros"
                    }
                    isLoading = false
                }

                if (isLoading) {
                    androidx.compose.material3.Surface {
                        androidx.compose.material3.Text(
                            "Cargando...",
                            modifier = androidx.compose.ui.Modifier
                                .fillMaxSize()
                                .wrapContentSize()
                        )
                    }
                } else {
                    AppNavigation(navController = navController, startDestination = startDestination)
                }
            }
        }
    }
}
