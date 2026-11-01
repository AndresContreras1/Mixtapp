package com.example.mixtapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mixtapp.ui.theme.MixtappTheme
import dagger.hilt.android.AndroidEntryPoint

// Donde inicia la aplicacion
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Sin darkTheme fijo: el tema sigue el modo claro u oscuro del celular
            MixtappTheme {
                Mixtapp()
            }
        }
    }
}
