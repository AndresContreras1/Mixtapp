package com.example.mixtapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.mixtapp.ui.screens.*
import com.example.mixtapp.ui.theme.DarkBackground

enum class Screen { LOGIN, HOME, TRACK_DETAIL, PROFILE }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentScreen by remember { mutableStateOf(Screen.LOGIN) }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = DarkBackground
            ) { innerPadding ->
                when (currentScreen) {
                    Screen.LOGIN -> LoginScreen(
                        onLoginSuccess = { currentScreen = Screen.HOME },
                        onNavigateToSignUp = { }
                    )
                    Screen.HOME -> HomeScreen(
                        onSelectTrack = { currentScreen = Screen.TRACK_DETAIL },
                        onOpenProfile = { currentScreen = Screen.PROFILE }
                    )
                    Screen.TRACK_DETAIL -> TrackDetailScreen(
                        onBack = { currentScreen = Screen.HOME }
                    )
                    Screen.PROFILE -> ProfileScreen(
                        onBackToHome = { currentScreen = Screen.HOME }
                    )
                }
            }
        }
    }
}