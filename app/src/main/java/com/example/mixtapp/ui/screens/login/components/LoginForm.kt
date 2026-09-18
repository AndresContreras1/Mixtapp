package com.example.mixtapp.ui.screens.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.components.AppButton
import com.example.mixtapp.ui.components.AppTextField
import com.example.mixtapp.ui.components.FieldIcon

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
){

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppTextField(
            label = stringResource(R.string.email),
            placeholder = stringResource(R.string.enter_your_email),
            value = email,
            onValueChange = onEmailChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            icon = FieldIcon.Email,
            esContrasena = false,
            contrasenaVisible = false,
            onContrasenaVisibleChange = {}
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            label = stringResource(R.string.contrasena),
            placeholder = stringResource(R.string.contrasena),
            value = contrasena,
            onValueChange = onContrasenaChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            icon = FieldIcon.Lock,
            esContrasena = true,
            contrasenaVisible = contrasenaVisible,
            onContrasenaVisibleChange = onContrasenaVisibleChange
        )

        Spacer(modifier = Modifier.height(28.dp))

        AppButton(
            texto = stringResource(R.string.ingresar).uppercase(),
            onClick = onLoginClick,
            cargando = cargando,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .fillMaxWidth()
                .height(82.dp)
        )
    }
}
