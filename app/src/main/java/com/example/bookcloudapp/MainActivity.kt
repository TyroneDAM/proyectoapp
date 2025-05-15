package com.example.bookcloudapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.bookcloudapp.navigation.AppNavigation
import com.example.bookcloudapp.ui.theme.BookcloudAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookcloudAppTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}
