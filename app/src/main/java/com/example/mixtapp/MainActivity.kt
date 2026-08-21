package com.example.mixtapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.mixtapp.ui.screens.login.LoginScreen
import com.example.mixtapp.ui.screens.signup.SignUpScreen
import com.example.mixtapp.ui.theme.MixtappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MixtappTheme(dynamicColor = false) {
                Scaffold() {
                    LoginScreen(
                        modifier = Modifier.padding(it)
                    )
                }

            }
        }
    }
}
