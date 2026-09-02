package com.example.mixtapp.ui.screens.signup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.mixtapp.ui.screens.signup.components.SignUpFooter
import com.example.mixtapp.ui.screens.signup.components.SignUpForm
import com.example.mixtapp.ui.screens.signup.components.SignUpHeader
import com.example.mixtapp.ui.theme.*

@Composable
fun SignUpScreen(
    signUpViewModel: SignUpViewModel,
    onSignUpSuccess: () -> Unit,
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
        onTerminosChange = { signUpViewModel.updateTerminos(terminos = it) },
        mostrarErrorContrasenas = state.mostrarErrorContrasenas,
        onSignUpClick = onSignUpSuccess,
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
    onTerminosChange: (Boolean) -> Unit,
    mostrarErrorContrasenas: Boolean,
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
                onTerminosChange = onTerminosChange,
                onSignUpClick = onSignUpClick
            )

            if (mostrarErrorContrasenas) {
                Text(
                    text = stringResource(R.string.passwords_no_coinciden),
                    color = PalePink,
                    fontSize = 14.sp
                )
            }

            SignUpFooter(onLoginClick = onLoginClick)

        }

    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "SignUp Light")
@Composable
fun SignUpScreenLightPreview() {
    MixtappTheme(darkTheme = false, dynamicColor = false) {
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
            onTerminosChange = {},
            mostrarErrorContrasenas = false,
            onSignUpClick = {},
            onLoginClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "SignUp Dark")
@Composable
fun SignUpScreenDarkPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
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
            onTerminosChange = {},
            mostrarErrorContrasenas = false,
            onSignUpClick = {},
            onLoginClick = {}
        )
    }
}
