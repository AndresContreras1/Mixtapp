package com.example.mixtapp.ui.screens.mixtapes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.model.MixtapeUi
import com.example.mixtapp.ui.utils.AppButton
import com.example.mixtapp.ui.utils.AppTextField

@Composable
fun CreateMixtapeDialog(
    onDismiss: () -> Unit,
    onCreate: (nombre: String, descripcion: String) -> Unit,
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.crear_mixtape)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                AppTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    placeholder = stringResource(R.string.nombre_mixtape),
                    label = stringResource(R.string.nombre_mixtape),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                AppTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    placeholder = stringResource(R.string.descripcion_mixtape),
                    label = stringResource(R.string.descripcion_mixtape),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            if (nombre.isNotBlank()) {
                AppButton(
                    texto = stringResource(R.string.crear_mixtape),
                    onClick = {
                        onCreate(nombre, descripcion)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar")
            }
        }
    )
}

@Composable
fun AddItemToMixtapeDialog(
    misMixtapes: List<MixtapeUi>,
    onDismiss: () -> Unit,
    onSelectMixtape: (mixtapeId: String) -> Unit,
    onCreateNewClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.agregar_a_mixtape)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (misMixtapes.isEmpty()) {
                    Text(
                        text = stringResource(R.string.sin_mixtapes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        items(misMixtapes) { mixtape ->
                            TextButton(
                                onClick = { onSelectMixtape(mixtape.id) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = mixtape.nombre,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                AppButton(
                    texto = stringResource(R.string.nueva_mixtape),
                    onClick = onCreateNewClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cerrar")
            }
        }
    )
}
