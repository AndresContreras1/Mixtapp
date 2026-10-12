package com.example.mixtapp.ui.screens.signup.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.utils.AppTextField

@Composable
fun SignUpFields(
    nombre: String,
    onNombreChange: (String) -> Unit,
    usuario: String,
    onUsuarioChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    contrasena: String,
    onContrasenaChange: (String) -> Unit,
    confirmarContrasena: String,
    onConfirmarContrasenaChange: (String) -> Unit,
    contrasenaVisible: Boolean,
    onContrasenaVisibleChange: () -> Unit,
    confirmarVisible: Boolean,
    onConfirmarVisibleChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AppTextField(
            label = stringResource(R.string.nombre),
            placeholder = stringResource(R.string.ingresa_tu_nombre),
            value = nombre,
            onValueChange = onNombreChange,
            keyboardOptions = KeyboardOptions.Default,
            icon = Icons.Outlined.Person,
            esContrasena = false,
            contrasenaVisible = false,
            onContrasenaVisibleChange = {}
        )

        Spacer(modifier = Modifier.height(14.dp))

        AppTextField(
            label = stringResource(R.string.username),
            placeholder = stringResource(R.string.choose_username),
            value = usuario,
            onValueChange = onUsuarioChange,
            keyboardOptions = KeyboardOptions.Default,
            icon = Icons.Outlined.AlternateEmail,
            esContrasena = false,
            contrasenaVisible = false,
            onContrasenaVisibleChange = {}
        )

        Spacer(modifier = Modifier.height(14.dp))

        AppTextField(
            label = stringResource(R.string.email),
            placeholder = stringResource(R.string.enter_your_email),
            value = email,
            onValueChange = onEmailChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            icon = Icons.Outlined.Email,
            esContrasena = false,
            contrasenaVisible = false,
            onContrasenaVisibleChange = {}
        )

        Spacer(modifier = Modifier.height(14.dp))

        AppTextField(
            label = stringResource(R.string.contrasena),
            placeholder = stringResource(R.string.create_password),
            value = contrasena,
            onValueChange = onContrasenaChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            icon = Icons.Outlined.Lock,
            esContrasena = true,
            contrasenaVisible = contrasenaVisible,
            onContrasenaVisibleChange = onContrasenaVisibleChange
        )

        Spacer(modifier = Modifier.height(14.dp))

        AppTextField(
            label = stringResource(R.string.confirm_password),
            placeholder = stringResource(R.string.repeat_password),
            value = confirmarContrasena,
            onValueChange = onConfirmarContrasenaChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            icon = Icons.Outlined.Lock,
            esContrasena = true,
            contrasenaVisible = confirmarVisible,
            onContrasenaVisibleChange = onConfirmarVisibleChange
        )
    }
}
