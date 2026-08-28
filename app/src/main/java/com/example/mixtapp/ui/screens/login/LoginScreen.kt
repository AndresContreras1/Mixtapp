package com.example.mixtapp.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.components.*
import com.example.mixtapp.ui.screens.login.components.LoginFooter
import com.example.mixtapp.ui.screens.login.components.LoginForm
import com.example.mixtapp.ui.screens.login.components.LoginHeader
import com.example.mixtapp.ui.theme.*

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

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

            LoginHeader()

            LoginForm(
                email = email,
                onEmailChange = { email = it },
                contrasena = contrasena,
                onContrasenaChange = { contrasena = it },
                passwordVisible = passwordVisible,
                onPasswordVisibleChange = { passwordVisible = !passwordVisible },
                onLoginClick = onLoginClick
            )

            LoginFooter(onSignUpClick = onSignUpClick)
        }

    }
}

@Composable
@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "Login Light")
fun LoginScreenLightPreview() {
    MixtappTheme(darkTheme = false, dynamicColor = false) {
        LoginScreen(onLoginClick = {}, onSignUpClick = {})
    }
}

@Composable
@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "Login Dark")
fun LoginScreenDarkPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        LoginScreen(onLoginClick = {}, onSignUpClick = {})
    }
}
