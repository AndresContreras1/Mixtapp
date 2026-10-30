package com.example.mixtapp.debug

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// Pantalla solo de debug para cargar los datos falsos. Se abre desde el icono "Mixtapp Seed"
class DebugSeedActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { PantallaSeed() } }
    }

    @Composable
    private fun PantallaSeed() {
        val scope = rememberCoroutineScope()
        var estado by remember { mutableStateOf("Sin ejecutar") }
        var cargando by remember { mutableStateOf(false) }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var entiendoRiesgo by remember { mutableStateOf(false) }

        fun ejecutar(accion: suspend () -> String) {
            scope.launch {
                cargando = true
                estado = try {
                    accion()
                } catch (e: Exception) {
                    "Error: ${e.message}"
                }
                cargando = false
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Datos falsos (Faker)", style = MaterialTheme.typography.titleLarge)
            Text(estado)

            Button(onClick = { ejecutar { SeedRunner.alEmulador() } }, enabled = !cargando) {
                Text("Subir al emulador")
            }

            Text("Firebase real (produccion)", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo (opcional)") })
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contrasena") },
                visualTransformation = PasswordVisualTransformation()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Switch(checked = entiendoRiesgo, onCheckedChange = { entiendoRiesgo = it })
                Text("Entiendo que esto escribe en el Firebase REAL")
            }
            Button(
                onClick = { ejecutar { SeedRunner.aFirebaseReal(this@DebugSeedActivity, email, password) } },
                enabled = !cargando && entiendoRiesgo
            ) {
                Text("Subir a Firebase real")
            }
        }
    }
}
