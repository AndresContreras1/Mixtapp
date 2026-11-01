package com.example.mixtapp.ui.screens.signup

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.screens.signup.components.SignUpFooter
import com.example.mixtapp.ui.screens.signup.components.SignUpForm
import com.example.mixtapp.ui.screens.signup.components.SignUpHeader
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.ErrorMessage

@Composable
fun SignUpScreen(
    signUpViewModel: SignUpViewModel,
    onSignUpSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by signUpViewModel.uiState.collectAsState()

    // El ViewModel valida y autoriza; cuando la cuenta queda creada, la pantalla avisa para navegar
    LaunchedEffect(state.navigate) {
        if (state.navigate) {
            onSignUpSuccess()
        }
    }

    SignUpScreenContent(
        nombre = state.nombre,
        onNombreChange = { signUpViewModel.updateNombre(nombre = it) },
        usuario = state.usuario,
        onUsuarioChange = { signUpViewModel.updateUsuario(usuario = it) },
        email = state.email,
        onEmailChange = { signUpViewModel.updateEmail(email = it) },
        contrasena = state.contrasena,
        onContrasenaChange = { signUpViewModel.updateContrasena(contrasena = it) },
        confirmarContrasena = state.confirmarContrasena,
        onConfirmarContrasenaChange = {
            signUpViewModel.updateConfirmarContrasena(confirmarContrasena = it)
        },
        contrasenaVisible = state.contrasenaVisible,
        onContrasenaVisibleChange = { signUpViewModel.mostrarEsconderContrasena() },
        confirmarVisible = state.confirmarVisible,
        onConfirmarVisibleChange = { signUpViewModel.mostrarEsconderConfirmar() },
        terminos = state.terminos,
        onTerminosClick = { signUpViewModel.alternarTerminos() },
        errorMessageRes = state.errorMessageRes,
        cargando = state.cargando,
        onSignUpClick = { signUpViewModel.signUpButtonPressed() },
        onLoginClick = onLoginClick,
        modifier = modifier
    )
}

@Composable
fun SignUpScreenContent(
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
    onTerminosClick: () -> Unit,
    @StringRes errorMessageRes: Int?,
    cargando: Boolean,
    onSignUpClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        // Son cinco campos: en pantallas pequenas o con el teclado abierto la columna se desplaza
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SignUpHeader()

            Spacer(modifier = Modifier.height(28.dp))

            SignUpForm(
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
                onConfirmarVisibleChange = onConfirmarVisibleChange,
                terminos = terminos,
                onTerminosClick = onTerminosClick,
                cargando = cargando,
                onSignUpClick = onSignUpClick,
            )

            // Error de las validaciones o del registro, calculado por el ViewModel
            ErrorMessage(
                messageRes = errorMessageRes,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            SignUpFooter(onLoginClick = onLoginClick)
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 393, heightDp = 852)
fun SignUpScreenPreview() {
    MixtappTheme(darkTheme = true) {
        SignUpScreenContent(
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
            errorMessageRes = null,
            cargando = false,
            onSignUpClick = {},
            onLoginClick = {}
        )
    }
}
