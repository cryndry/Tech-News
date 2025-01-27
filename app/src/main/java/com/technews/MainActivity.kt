package com.technews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.technews.data.News
import com.technews.data.NewsViewModel
import com.technews.screens.NewsDetailScreen
import com.technews.screens.NewsScreen
import com.technews.screens.ScreenManager
import com.technews.ui.theme.TechNewsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val newsViewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        setContent {
            TechNewsTheme {
                TechNews(newsViewModel)
            }
        }
    }
}

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No NavHostController provided")
}

@Composable
fun TechNews(newsViewModel: NewsViewModel) {
    val navController = rememberNavController()

    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(
            navController = navController,
            startDestination = ScreenManager.NewsScreen.route
        ) {
            composable(ScreenManager.NewsScreen.route) {
                NewsScreen(newsViewModel)
            }
            composable(ScreenManager.NewsDetailScreen.route) {
                val currentNews = navController.previousBackStackEntry?.savedStateHandle?.get<News>("news")
                if (currentNews != null)
                    NewsDetailScreen(currentNews, newsViewModel)
            }
        }
    }
}