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
    onSignUpSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var usuario by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var terminos by remember { mutableStateOf(false) }
    var contrasenaVisible by remember { mutableStateOf(false) }
    var confirmarVisible by remember { mutableStateOf(false) }

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
                onUsuarioChange = { usuario = it },
                email = email,
                onEmailChange = { email = it },
                contrasena = contrasena,
                onContrasenaChange = { contrasena = it },
                confirmarContrasena = confirmarContrasena,
                onConfirmarContrasenaChange = { confirmarContrasena = it },
                contrasenaVisible = contrasenaVisible,
                onContrasenaVisibleChange = { contrasenaVisible = !contrasenaVisible },
                confirmarVisible = confirmarVisible,
                onConfirmarVisibleChange = { confirmarVisible = !confirmarVisible },
                terminos = terminos,
                onTerminosChange = { terminos = it },
                onSignUpClick = onSignUpSuccess
            )

            if (confirmarContrasena.isNotEmpty() && contrasena != confirmarContrasena) {
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
        SignUpScreen(onSignUpSuccess = {}, onLoginClick = {})
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "SignUp Dark")
@Composable
fun SignUpScreenDarkPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        SignUpScreen(onSignUpSuccess = {}, onLoginClick = {})
    }
}
