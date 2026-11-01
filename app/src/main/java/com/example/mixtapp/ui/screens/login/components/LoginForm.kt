package com.example.mixtapp.ui.screens.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppButton
import com.example.mixtapp.ui.utils.AppTextField

// El formulario del login: los campos ovalados de correo y contrasena (con el ojo para verla) y el
// boton de ingresar. Los testTags son para las pruebas e2e
@Composable
fun LoginForm(
    email: String,
    onEmailChange: (String) -> Unit,
    contrasena: String,
    onContrasenaChange: (String) -> Unit,
    contrasenaVisible: Boolean,
    onContrasenaVisibleChange: () -> Unit,
    cargando: Boolean,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AppTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = stringResource(R.string.enter_your_email),
            label = stringResource(R.string.email),
            leadingIcon = Icons.Outlined.Email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.testTag("txtEmail"),
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = contrasena,
            onValueChange = onContrasenaChange,
            placeholder = stringResource(R.string.enter_your_password),
            label = stringResource(R.string.contrasena),
            leadingIcon = Icons.Outlined.Lock,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            esContrasena = true,
            contrasenaVisible = contrasenaVisible,
            onContrasenaVisibleChange = onContrasenaVisibleChange,
            modifier = Modifier.testTag("txtPassword"),
        )

        Spacer(modifier = Modifier.height(28.dp))

        AppButton(
            texto = stringResource(R.string.ingresar),
            onClick = onLoginClick,
            cargando = cargando,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btnLogin"),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun LoginFormPreview() {
    MixtappTheme(darkTheme = true) {
        LoginForm(
            email = "",
            onEmailChange = {},
            contrasena = "",
            onContrasenaChange = {},
            contrasenaVisible = false,
            onContrasenaVisibleChange = {},
            cargando = false,
            onLoginClick = {},
        )
    }
}
