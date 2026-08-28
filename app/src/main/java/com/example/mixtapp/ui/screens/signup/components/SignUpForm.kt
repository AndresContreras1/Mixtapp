package com.example.mixtapp.ui.screens.signup.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.components.AppTextField
import com.example.mixtapp.ui.components.FieldIcon
import com.example.mixtapp.ui.theme.PrimaryPink


@Composable
fun SignUpForm(
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
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
    ) {
        AppTextField(
            label = stringResource(R.string.username),
            placeholder = stringResource(R.string.choose_username),
            value = usuario,
            onValueChange = onUsuarioChange,
            icon = FieldIcon.User
        )

        Spacer(modifier = Modifier.height(14.dp))

        AppTextField(
            label = stringResource(R.string.email),
            placeholder = stringResource(R.string.enter_your_email),
            value = email,
            onValueChange = onEmailChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            icon = FieldIcon.Email
        )

        Spacer(modifier = Modifier.height(14.dp))

        AppTextField(
            label = stringResource(R.string.password),
            placeholder = stringResource(R.string.create_password),
            value = contrasena,
            onValueChange = onContrasenaChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            icon = FieldIcon.Lock,
            isPassword = true,
            passwordVisible = contrasenaVisible,
            onPasswordVisibleChange = onContrasenaVisibleChange
        )

        Spacer(modifier = Modifier.height(14.dp))

        AppTextField(
            label = stringResource(R.string.confirm_password),
            placeholder = stringResource(R.string.repeat_password),
            value = confirmarContrasena,
            onValueChange = onConfirmarContrasenaChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            icon = FieldIcon.Lock,
            isPassword = true,
            passwordVisible = confirmarVisible,
            onPasswordVisibleChange = onConfirmarVisibleChange
        )

        Spacer(modifier = Modifier.height(14.dp))


        TermsRow(
            checked = terminos,
            onCheckedChange = { onTerminosChange(!terminos) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSignUpClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryPink,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = stringResource(R.string.sign_up_btn),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Preview
@Composable
fun SignUpFormPreview(){
    SignUpForm(
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
        onSignUpClick = {}
    )
}