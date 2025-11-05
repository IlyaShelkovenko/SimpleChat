package com.example.simplechat.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.simplechat.presentation.apikey.ApiKeyRoute
import com.example.simplechat.presentation.chat.ChatRoute
import com.example.simplechat.presentation.settings.SettingsRoute

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
            ChatRoute(
                onOpenSettings = {
                    navController.navigate(SimpleChatDestination.Settings)
                }
            )
        }
        composable(SimpleChatDestination.Settings) {
            SettingsRoute(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
