package com.technews.screens

sealed class ScreenManager(val route: String) {
    object NewsScreen: ScreenManager("NewsScreen")
    object NewsDetailScreen: ScreenManager("NewsDetailScreen")
}