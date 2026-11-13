package com.example.mixtapp.ui.screens.crearmixtape

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.AppButton
import com.example.mixtapp.ui.utils.AppTextArea
import com.example.mixtapp.ui.utils.AppTextField
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.ScreenTitle

@Composable
fun CrearMixtapeScreen(
    crearMixtapeViewModel: CrearMixtapeViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by crearMixtapeViewModel.uiState.collectAsState()

    // Cuando la mixtape queda creada se vuelve a la pantalla anterior
    LaunchedEffect(state.navigate) {
        if (state.navigate) onBackClick()
    }

    CrearMixtapeScreenContent(
        nombre = state.nombre,
        descripcion = state.descripcion,
        guardando = state.guardando,
        errorMessageRes = state.errorMessageRes,
        onNombreChange = { crearMixtapeViewModel.updateNombre(nombre = it) },
        onDescripcionChange = { crearMixtapeViewModel.updateDescripcion(descripcion = it) },
        onCrearClick = { crearMixtapeViewModel.crearMixtape() },
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun CrearMixtapeScreenContent(
    nombre: String,
    descripcion: String,
    guardando: Boolean,
    @StringRes errorMessageRes: Int?,
    onNombreChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onCrearClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("crearMixtapeScreen")
    ) {
        AppBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            ScreenTitle(
                title = stringResource(R.string.nueva_mixtape),
                onBackClick = onBackClick,
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = nombre,
                onValueChange = onNombreChange,
                label = stringResource(R.string.label_nombre_mixtape),
                placeholder = stringResource(R.string.nombre_mixtape_placeholder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("txtNombreMixtape"),
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppTextArea(
                value = descripcion,
                onValueChange = onDescripcionChange,
                label = stringResource(R.string.label_descripcion_mixtape),
                placeholder = stringResource(R.string.descripcion_mixtape_placeholder),
                maxLength = MAX_DESCRIPCION_MIXTAPE,
                modifier = Modifier.testTag("txtDescripcionMixtape"),
            )

            ErrorMessage(messageRes = errorMessageRes)

            Spacer(modifier = Modifier.height(16.dp))

            AppButton(
                texto = stringResource(R.string.crear_mixtape),
                onClick = onCrearClick,
                cargando = guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btnCrearMixtape"),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun CrearMixtapeScreenPreview() {
    MixtappTheme(darkTheme = true) {
        CrearMixtapeScreenContent(
            nombre = "Para entrenar",
            descripcion = "",
            guardando = false,
            errorMessageRes = null,
            onNombreChange = {},
            onDescripcionChange = {},
            onCrearClick = {},
            onBackClick = {},
        )
    }
}
