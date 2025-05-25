package com.example.bookcloudapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bookcloudapp.ui.screens.LoginScreen
import com.example.bookcloudapp.ui.screens.RegisterScreen
import com.example.bookcloudapp.ui.screens.LibrosScreen
import com.example.bookcloudapp.ui.screens.FavoritosScreen
import com.example.bookcloudapp.ui.screens.ReservasScreen

@Composable
fun AppNavigation(navController: NavHostController, startDestination: String) {
    NavHost(navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("libros") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onGoToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable("libros") {
            LibrosScreen(navController)
        }

        composable("favoritos") {
            FavoritosScreen(navController)
        }

        composable("reservas") {
            ReservasScreen(navController)
        }
    }
}

