package com.example.mixtapp.ui.screens.signup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.components.*
import com.example.mixtapp.ui.screens.signup.components.SignUpErrorMessages
import com.example.mixtapp.ui.screens.signup.components.SignUpFooter
import com.example.mixtapp.ui.screens.signup.components.SignUpForm
import com.example.mixtapp.ui.screens.signup.components.SignUpHeader
import com.example.mixtapp.ui.theme.*

@Composable
fun SignUpScreen(
    signUpViewModel: SignUpViewModel,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by signUpViewModel.uiState.collectAsState()

    SignUpScreenContent(
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
        mostrarErrorContrasenas = state.mostrarErrorContrasenas,
        errorMessageRes = state.errorMessageRes,
        onSignUpClick = { signUpViewModel.signUpButtonPressed() },
        onLoginClick = onLoginClick,
        modifier = modifier
    )
}

@Composable
fun SignUpScreenContent(
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
    mostrarErrorContrasenas: Boolean,
    errorMessageRes: Int?,
    onSignUpClick: () -> Unit,
    onLoginClick: () -> Unit,
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 31.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SignUpHeader()

            SignUpForm(
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
                onSignUpClick = onSignUpClick
            )

            SignUpErrorMessages(
                mostrarErrorContrasenas = mostrarErrorContrasenas,
                errorMessageRes = errorMessageRes
            )

            SignUpFooter(onLoginClick = onLoginClick)

        }

    }
}

// Las dos previews solo cambian el tema, asi que el formulario vacio se arma una sola vez
@Composable
private fun SignUpScreenContentPreview() {
    SignUpScreenContent(
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
        mostrarErrorContrasenas = false,
        errorMessageRes = null,
        onSignUpClick = {},
        onLoginClick = {}
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "SignUp Light")
@Composable
fun SignUpScreenLightPreview() {
    MixtappTheme(darkTheme = false, dynamicColor = false) {
        SignUpScreenContentPreview()
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "SignUp Dark")
@Composable
fun SignUpScreenDarkPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        SignUpScreenContentPreview()
    }
}
