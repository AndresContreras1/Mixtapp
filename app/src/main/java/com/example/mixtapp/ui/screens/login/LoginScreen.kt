package com.example.mixtapp.ui.screens.login

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
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
import com.example.mixtapp.ui.screens.login.components.LoginFooter
import com.example.mixtapp.ui.screens.login.components.LoginForm
import com.example.mixtapp.ui.screens.login.components.LoginHeader
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.ErrorMessage

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by loginViewModel.uiState.collectAsState()

    // El ViewModel valida y autoriza; cuando entra, la pantalla avisa para navegar
    LaunchedEffect(state.navigate) {
        if (state.navigate) {
            onLoginSuccess()
        }
    }

    LoginScreenContent(
        email = state.email,
        onEmailChange = { loginViewModel.updateEmail(email = it) },
        contrasena = state.contrasena,
        onContrasenaChange = { loginViewModel.updateContrasena(contrasena = it) },
        contrasenaVisible = state.contrasenaVisible,
        onContrasenaVisibleChange = { loginViewModel.mostrarEsconderContrasena() },
        errorMessageRes = state.errorMessageRes,
        cargando = state.cargando,
        onLoginClick = { loginViewModel.loginButtonPressed() },
        onSignUpClick = onSignUpClick,
        modifier = modifier
    )
}

@Composable
fun LoginScreenContent(
    email: String,
    onEmailChange: (String) -> Unit,
    contrasena: String,
    onContrasenaChange: (String) -> Unit,
    contrasenaVisible: Boolean,
    onContrasenaVisibleChange: () -> Unit,
    @StringRes errorMessageRes: Int?,
    cargando: Boolean,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        // Con el teclado abierto el formulario no cabe: la columna se desplaza. Si cabe, queda
        // centrada en la pantalla
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoginHeader()

            Spacer(modifier = Modifier.height(40.dp))

            LoginForm(
                email = email,
                onEmailChange = onEmailChange,
                contrasena = contrasena,
                onContrasenaChange = onContrasenaChange,
                contrasenaVisible = contrasenaVisible,
                onContrasenaVisibleChange = onContrasenaVisibleChange,
                cargando = cargando,
                onLoginClick = onLoginClick,
            )

            // Error del intento de entrar, calculado por el ViewModel
            ErrorMessage(
                messageRes = errorMessageRes,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            LoginFooter(onSignUpClick = onSignUpClick)
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 393, heightDp = 852)
fun LoginScreenPreview() {
    MixtappTheme(darkTheme = true) {
        LoginScreenContent(
            email = "",
            onEmailChange = {},
            contrasena = "",
            onContrasenaChange = {},
            contrasenaVisible = false,
            onContrasenaVisibleChange = {},
            errorMessageRes = null,
            cargando = false,
            onLoginClick = {},
            onSignUpClick = {}
        )
    }
}
