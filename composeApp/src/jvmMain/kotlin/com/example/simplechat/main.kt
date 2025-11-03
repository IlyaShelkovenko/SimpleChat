package com.example.simplechat

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.simplechat.core.di.AppGraph
import com.example.simplechat.core.platform.PlatformConfiguration

fun main() = application {
    AppGraph.initialize(PlatformConfiguration())
    Window(onCloseRequest = ::exitApplication, title = "Simple Chat") {
        App()
    }
}
