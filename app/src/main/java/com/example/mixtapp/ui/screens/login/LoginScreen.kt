package com.example.mixtapp.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.components.*
import com.example.mixtapp.ui.screens.login.components.LoginFooter
import com.example.mixtapp.ui.screens.login.components.LoginForm
import com.example.mixtapp.ui.screens.login.components.LoginHeader
import com.example.mixtapp.ui.theme.*

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by loginViewModel.uiState.collectAsState()

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
    errorMessageRes: Int?,
    cargando: Boolean,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {

        AppBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 31.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LoginHeader()

            LoginForm(
                email = email,
                onEmailChange = onEmailChange,
                contrasena = contrasena,
                onContrasenaChange = onContrasenaChange,
                contrasenaVisible = contrasenaVisible,
                onContrasenaVisibleChange = onContrasenaVisibleChange,
                cargando = cargando,
                onLoginClick = onLoginClick
            )

            // Error del intento de entrar, calculado por el ViewModel
            AnimatedVisibility(
                visible = errorMessageRes != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = stringResource(errorMessageRes ?: R.string.error_inicio_sesion),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }

            LoginFooter(onSignUpClick = onSignUpClick)
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "Login Light")
fun LoginScreenLightPreview() {
    MixtappTheme(darkTheme = false, dynamicColor = false) {
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

@Composable
@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "Login Dark")
fun LoginScreenDarkPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
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
