package com.example.mixtapp.ui.screens.editprofile

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.ui.screens.editprofile.components.EditProfileForm
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.AppButton
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ScreenTitle

// Modificar mis datos de Firestore: nombre, nombre de usuario y biografia
@Composable
fun EditProfileScreen(
    editProfileViewModel: EditProfileViewModel,
    onGuardado: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by editProfileViewModel.uiState.collectAsState()

    // Se vuelve al perfil solo cuando Firestore confirma que quedo guardado
    LaunchedEffect(state.guardado) {
        if (state.guardado) {
            onGuardado()
        }
    }

    EditProfileScreenContent(
        isLoading = state.isLoading,
        errorCargaRes = state.errorCargaRes,
        nombre = state.nombre,
        onNombreChange = { editProfileViewModel.updateNombre(nombre = it) },
        username = state.username,
        onUsernameChange = { editProfileViewModel.updateUsername(username = it) },
        bio = state.bio,
        onBioChange = { editProfileViewModel.updateBio(bio = it) },
        guardando = state.guardando,
        errorMessageRes = state.errorMessageRes,
        onGuardarClick = { editProfileViewModel.guardarCambios() },
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Composable
fun EditProfileScreenContent(
    isLoading: Boolean,
    @StringRes errorCargaRes: Int?,
    nombre: String,
    onNombreChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    bio: String,
    onBioChange: (String) -> Unit,
    guardando: Boolean,
    @StringRes errorMessageRes: Int?,
    onGuardarClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // El titulo queda fijo arriba: aun cargando o con error se puede volver al perfil
            ScreenTitle(
                title = stringResource(R.string.editar_perfil),
                onBackClick = onBackClick
            )

            when {
                isLoading -> LoadingBox()

                // Sin mis datos no se muestra el formulario: guardar reemplazaria el documento
                errorCargaRes != null -> ErrorBox(messageRes = errorCargaRes)

                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = stringResource(R.string.editar_perfil_descripcion),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    EditProfileForm(
                        nombre = nombre,
                        onNombreChange = onNombreChange,
                        username = username,
                        onUsernameChange = onUsernameChange,
                        bio = bio,
                        onBioChange = onBioChange
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    AppButton(
                        texto = stringResource(R.string.guardar_cambios),
                        onClick = onGuardarClick,
                        cargando = guardando,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Error de las validaciones o del guardado, calculado por el ViewModel
                    ErrorMessage(
                        messageRes = errorMessageRes,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 393, heightDp = 852)
fun EditProfileScreenPreview() {
    val usuario = LocalPreviewProvider.usuario

    MixtappTheme(darkTheme = true) {
        EditProfileScreenContent(
            isLoading = false,
            errorCargaRes = null,
            nombre = usuario.nombre,
            onNombreChange = {},
            username = usuario.username,
            onUsernameChange = {},
            bio = usuario.bio,
            onBioChange = {},
            guardando = false,
            errorMessageRes = null,
            onGuardarClick = {},
            onBackClick = {}
        )
    }
}
