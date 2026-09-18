package com.example.mixtapp.ui.screens.signup.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.components.AppButton
import com.example.mixtapp.ui.theme.MixtappTheme

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
    // Alternar la casilla lo hace el ViewModel; aqui solo se avisa del clic
    onTerminosClick: () -> Unit,
    cargando: Boolean,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
    ) {
        SignUpFields(
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
            onConfirmarVisibleChange = onConfirmarVisibleChange
        )

        Spacer(modifier = Modifier.height(14.dp))

        TermsRow(
            checked = terminos,
            onCheckedChange = onTerminosClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppButton(
            texto = stringResource(R.string.sign_up_btn),
            onClick = onSignUpClick,
            cargando = cargando,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )
    }
}

@Preview
@Composable
fun SignUpFormPreview(){
    MixtappTheme(darkTheme = true, dynamicColor = false) {
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
            onTerminosClick = {},
            cargando = false,
            onSignUpClick = {}
        )
    }
}
