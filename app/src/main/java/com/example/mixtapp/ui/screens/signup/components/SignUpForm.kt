package com.example.mixtapp.ui.screens.signup.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppButton

// El formulario completo del registro: los campos, la fila de los terminos y el boton de crear la
// cuenta, que muestra la carga mientras Firebase responde
@Composable
fun SignUpForm(
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
    terminos: Boolean,
    // Alternar la casilla lo hace el ViewModel; aqui solo se avisa del clic
    onTerminosClick: () -> Unit,
    cargando: Boolean,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SignUpFields(
            nombre = nombre,
            onNombreChange = onNombreChange,
            usuario = usuario,
            onUsuarioChange = onUsuarioChange,
            email = email,
            onEmailChange = onEmailChange,
            contrasena = contrasena,
            onContrasenaChange = onContrasenaChange,
            confirmarContrasena = confirmarContrasena,
            onConfirmarContrasenaChange = onConfirmarContrasenaChange,
            contrasenaVisible = contrasenaVisible,
            onContrasenaVisibleChange = onContrasenaVisibleChange,
            confirmarVisible = confirmarVisible,
            onConfirmarVisibleChange = onConfirmarVisibleChange
        )

        Spacer(modifier = Modifier.height(10.dp))

        TermsRow(
            checked = terminos,
            onCheckedChange = onTerminosClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppButton(
            texto = stringResource(R.string.sign_up_btn),
            onClick = onSignUpClick,
            cargando = cargando,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btnRegistrar"),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun SignUpFormPreview() {
    MixtappTheme(darkTheme = true) {
        SignUpForm(
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
            terminos = false,
            onTerminosClick = {},
            cargando = false,
            onSignUpClick = {}
        )
    }
}
