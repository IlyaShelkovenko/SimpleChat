package com.example.simplechat

import androidx.compose.ui.window.ComposeUIViewController
import com.example.simplechat.core.di.AppGraph
import com.example.simplechat.core.platform.PlatformConfiguration

fun MainViewController() = ComposeUIViewController {
    AppGraph.initialize(PlatformConfiguration())
    App()
}
