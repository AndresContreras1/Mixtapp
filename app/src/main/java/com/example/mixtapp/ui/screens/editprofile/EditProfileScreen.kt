package com.example.mixtapp.ui.screens.editprofile

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.screens.editprofile.components.EditProfileForm
import com.example.mixtapp.ui.screens.editprofile.components.EditProfileHeader
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.AppButton
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.LoadingBox

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

    when {
        state.isLoading -> {
            LoadingBox(modifier = modifier)
        }
        state.errorCargaRes != null -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(state.errorCargaRes ?: R.string.error_cargar_contenido))
            }
        }
        else -> EditProfileScreenContent(
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
}

@Composable
fun EditProfileScreenContent(
    nombre: String,
    onNombreChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    bio: String,
    onBioChange: (String) -> Unit,
    guardando: Boolean,
    errorMessageRes: Int?,
    onGuardarClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            EditProfileHeader(onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(28.dp))

            EditProfileForm(
                nombre = nombre,
                onNombreChange = onNombreChange,
                username = username,
                onUsernameChange = onUsernameChange,
                bio = bio,
                onBioChange = onBioChange
            )

            Spacer(modifier = Modifier.height(28.dp))

            AppButton(
                texto = stringResource(R.string.guardar_cambios),
                onClick = onGuardarClick,
                cargando = guardando,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            ErrorMessage(
                messageRes = errorMessageRes,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun EditProfileScreenPreview() {
    MixtappTheme(darkTheme = true) {
        EditProfileScreenContent(
            nombre = "Usuario de Mixtapp",
            onNombreChange = {},
            username = "usuario",
            onUsernameChange = {},
            bio = "",
            onBioChange = {},
            guardando = false,
            errorMessageRes = null,
            onGuardarClick = {},
            onBackClick = {}
        )
    }
}
