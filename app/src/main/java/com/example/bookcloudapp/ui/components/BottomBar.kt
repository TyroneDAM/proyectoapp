package com.example.bookcloudapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
        BottomBarItem("libros", Icons.Default.MenuBook, "Libros"),
        BottomBarItem("favoritos", Icons.Default.Star, "Favoritos"),
        BottomBarItem("reservas", Icons.Default.Bookmark, "Reservas"),
        BottomBarItem("perfil", Icons.Default.Person, "Perfil")
    )
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        tonalElevation = 8.dp,
        containerColor = Color(0xFFFFE0B2), // naranja claro más visible
        contentColor = Color(0xFFEF6C00) // naranja zorro
    )
    {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFEF6C00), // Naranja oscuro zorro
                    selectedTextColor = Color(0xFFEF6C00),
                    indicatorColor = Color(0xFFFFCC80)
                )
            )
        }
    }
}

data class BottomBarItem(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String)
