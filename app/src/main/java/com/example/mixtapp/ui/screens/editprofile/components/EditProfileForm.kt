package com.example.mixtapp.ui.screens.editprofile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.model.MAX_BIO_LENGTH
import com.example.mixtapp.ui.utils.AppTextArea
import com.example.mixtapp.ui.utils.AppTextField

@Composable
fun EditProfileForm(
    nombre: String,
    onNombreChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    bio: String,
    onBioChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AppTextField(
            label = stringResource(R.string.nombre),
            placeholder = stringResource(R.string.ingresa_tu_nombre),
            value = nombre,
            onValueChange = onNombreChange,
            icon = Icons.Outlined.Person,
            keyboardOptions = KeyboardOptions.Default,
            esContrasena = false,
            contrasenaVisible = false,
            onContrasenaVisibleChange = {}
        )

        Spacer(modifier = Modifier.height(14.dp))

        AppTextField(
            label = stringResource(R.string.username),
            placeholder = stringResource(R.string.choose_username),
            value = username,
            onValueChange = onUsernameChange,
            icon = Icons.Outlined.AlternateEmail,
            keyboardOptions = KeyboardOptions.Default,
            esContrasena = false,
            contrasenaVisible = false,
            onContrasenaVisibleChange = {}
        )

        Spacer(modifier = Modifier.height(14.dp))

        AppTextArea(
            value = bio,
            onValueChange = onBioChange,
            label = stringResource(R.string.biografia),
            placeholder = stringResource(R.string.biografia_placeholder),
            maxLength = MAX_BIO_LENGTH
        )
    }
}
