package com.example.simplechat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.simplechat.core.designsystem.theme.SimpleChatTheme
import com.example.simplechat.presentation.app.AppState
import com.example.simplechat.presentation.app.AppViewModel
import com.example.simplechat.presentation.app.SimpleChatViewModelFactory
import com.example.simplechat.presentation.navigation.SimpleChatDestination
import com.example.simplechat.presentation.navigation.SimpleChatNavHost
@Composable
fun App() {
    SimpleChatTheme {
        val navController = rememberNavController()
        val appViewModel: AppViewModel = viewModel(factory = SimpleChatViewModelFactory.appViewModelFactory())
        val appState by appViewModel.state.collectAsState()

        LaunchedEffect(appState) {
            if (appState == AppState.Ready) {
                navController.navigate(SimpleChatDestination.Chat) {
                    popUpTo(SimpleChatDestination.ApiKey) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        when (appState) {
            AppState.Loading -> LoadingContent()
            else -> SimpleChatNavHost(
                navController = navController,
                startDestination = if (appState == AppState.Ready) {
                    SimpleChatDestination.Chat
                } else {
                    SimpleChatDestination.ApiKey
                }
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }
    }
}
