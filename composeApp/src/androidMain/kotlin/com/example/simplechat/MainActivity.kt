package com.example.simplechat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.simplechat.core.di.AppGraph
import com.example.simplechat.core.platform.PlatformConfiguration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppGraph.initialize(PlatformConfiguration(applicationContext))

        setContent {
            App()
        }
    }
}
