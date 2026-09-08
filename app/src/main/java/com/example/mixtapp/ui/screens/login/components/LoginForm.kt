package com.example.mixtapp.ui.screens.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.components.AppTextField
import com.example.mixtapp.ui.components.FieldIcon
import com.example.mixtapp.ui.theme.PalePink
import com.example.mixtapp.ui.theme.PrimaryPink

@Composable
fun LoginForm(
    email: String,
    onEmailChange: (String) -> Unit,
    contrasena: String,
    onContrasenaChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibleChange: () -> Unit,
    // La regla de la longitud minima la aplica el ViewModel; aqui solo se pinta el aviso
    mostrarErrorContrasena: Boolean,
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
            icon = FieldIcon.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            label = stringResource(R.string.contrasena),
            placeholder = stringResource(R.string.password),
            value = contrasena,
            onValueChange = onContrasenaChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            icon = FieldIcon.Lock,
            isPassword = true,
            passwordVisible = passwordVisible,
            onPasswordVisibleChange = onPasswordVisibleChange
        )

        if (mostrarErrorContrasena) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.password_corta),
                color = PalePink,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(82.dp),
            enabled = !cargando,
            shape = RoundedCornerShape(41.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryPink,
                contentColor = Color.White,
                disabledContainerColor = PrimaryPink,
                disabledContentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            } else {
                Text(
                    text = stringResource(R.string.ingresar).uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(19.dp))
    }

}
