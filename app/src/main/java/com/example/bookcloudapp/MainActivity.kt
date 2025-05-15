package com.example.bookcloudapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.bookcloudapp.ui.screens.LibrosScreen
import com.example.bookcloudapp.ui.screens.LoginScreen
import com.example.bookcloudapp.ui.screens.RegisterScreen
import com.example.bookcloudapp.ui.theme.BookcloudAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BookcloudAppTheme {
                var showRegister by remember { mutableStateOf(false) }
                var loggedIn by remember { mutableStateOf(false) }

                when {
                    loggedIn -> LibrosScreen()
                    showRegister -> RegisterScreen(
                        onRegisterSuccess = { showRegister = false }
                    )
                    else -> LoginScreen(
                        onLoginSuccess = { loggedIn = true },
                        onGoToRegister = { showRegister = true }
                    )
                }
            }
        }
    }
}
