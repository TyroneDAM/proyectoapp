package com.example.bookcloudapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bookcloudapp.ui.screens.*

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

        composable("perfil") {
            PerfilScreen(navController)
        }

        composable(
            "detalleLibro?isbn={isbn}",
            arguments = listOf(navArgument("isbn") { type = NavType.StringType })
        ) { backStackEntry ->
            val isbn = backStackEntry.arguments?.getString("isbn")
            if (isbn != null) {
                DetalleLibroScreen(isbn = isbn)
            }
        }
    }
}
