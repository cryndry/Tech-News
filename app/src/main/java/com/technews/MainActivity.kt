package com.technews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.technews.screens.NewsScreen
import com.technews.screens.ScreenManager
import com.technews.ui.theme.TechNewsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TechNewsTheme {
                TechNews()
            }
        }
    }
}

@Composable
fun TechNews() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ScreenManager.NewsScreen.route
    ) {
        composable(ScreenManager.NewsScreen.route) {
            NewsScreen()
        }
    }
}