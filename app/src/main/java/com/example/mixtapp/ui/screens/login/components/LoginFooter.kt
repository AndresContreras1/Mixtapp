package com.example.mixtapp.ui.screens.login.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

// El pie del login: lleva al registro a quien todavia no tiene cuenta. La accion va resaltada con
// el color primario para que se note que se puede tocar
@Composable
fun LoginFooter(
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pregunta = stringResource(R.string.no_tienes_cuenta)
    val accion = stringResource(R.string.create_account)

    TextButton(
        onClick = onSignUpClick,
        modifier = modifier.testTag("btnIrARegistro")
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                    append(pregunta)
                }
                append(" ")
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(accion)
                }
            },
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun LoginFooterPreview() {
    MixtappTheme(darkTheme = true) {
        LoginFooter(onSignUpClick = {})
    }
}
