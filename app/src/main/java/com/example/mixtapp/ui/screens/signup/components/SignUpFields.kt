package com.example.mixtapp.ui.screens.signup.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.model.CampoDeLaCuenta
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppPasswordField
import com.example.mixtapp.ui.utils.AppTextField

// Los cinco campos ovalados del registro, con el mismo estilo del login y la etiqueta siempre
// arriba. El que tiene el error se marca en rojo. Cada contrasena tiene su
// propio ojo, asi que cada una recibe su visibilidad. Los testTags son para las pruebas e2e
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
    // El campo que marco el ViewModel con el error; null si no hay
    campoConError: CampoDeLaCuenta?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AppTextField(
            value = nombre,
            onValueChange = onNombreChange,
            placeholder = stringResource(R.string.ingresa_tu_nombre),
            label = stringResource(R.string.nombre),
            leadingIcon = Icons.Outlined.Person,
            modifier = Modifier.testTag("txtNombre"),
            esError = campoConError == CampoDeLaCuenta.Nombre,
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = usuario,
            onValueChange = onUsuarioChange,
            placeholder = stringResource(R.string.choose_username),
            label = stringResource(R.string.username),
            leadingIcon = Icons.Outlined.AlternateEmail,
            modifier = Modifier.testTag("txtUsername"),
            esError = campoConError == CampoDeLaCuenta.Usuario,
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = stringResource(R.string.enter_your_email),
            label = stringResource(R.string.email),
            leadingIcon = Icons.Outlined.Email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.testTag("txtEmailRegistro"),
            esError = campoConError == CampoDeLaCuenta.Correo,
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppPasswordField(
            value = contrasena,
            onValueChange = onContrasenaChange,
            placeholder = stringResource(R.string.create_password),
            label = stringResource(R.string.contrasena),
            contrasenaVisible = contrasenaVisible,
            onContrasenaVisibleChange = onContrasenaVisibleChange,
            modifier = Modifier.testTag("txtPasswordRegistro"),
            esError = campoConError == CampoDeLaCuenta.Contrasena,
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppPasswordField(
            value = confirmarContrasena,
            onValueChange = onConfirmarContrasenaChange,
            placeholder = stringResource(R.string.repeat_password),
            label = stringResource(R.string.confirm_password),
            contrasenaVisible = confirmarVisible,
            onContrasenaVisibleChange = onConfirmarVisibleChange,
            modifier = Modifier.testTag("txtConfirmPassword"),
            esError = campoConError == CampoDeLaCuenta.Confirmar,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun SignUpFieldsPreview() {
    MixtappTheme(darkTheme = true) {
        SignUpFields(
            nombre = "",
            onNombreChange = {},
            usuario = "",
            onUsuarioChange = {},
            email = "",
            onEmailChange = {},
            contrasena = "",
            onContrasenaChange = {},
            confirmarContrasena = "",
            onConfirmarContrasenaChange = {},
            contrasenaVisible = false,
            onContrasenaVisibleChange = {},
            confirmarVisible = false,
            onConfirmarVisibleChange = {},
            campoConError = CampoDeLaCuenta.Confirmar,
        )
    }
}
