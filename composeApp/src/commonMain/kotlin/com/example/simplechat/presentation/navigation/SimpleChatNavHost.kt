package com.example.simplechat.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.simplechat.presentation.apikey.ApiKeyRoute
import com.example.simplechat.presentation.chat.ChatRoute

@Composable
fun SimpleChatNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(SimpleChatDestination.ApiKey) {
            ApiKeyRoute(
                onNavigateToChat = {
                    navController.navigate(SimpleChatDestination.Chat) {
                        popUpTo(SimpleChatDestination.ApiKey) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(SimpleChatDestination.Chat) {
            ChatRoute()
        }
    }
}
