package com.example.mixtapp.ui.screens.signup.components

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

// El pie del registro: devuelve al login a quien ya tiene cuenta, con la accion resaltada igual
// que en el pie del login
@Composable
fun SignUpFooter(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pregunta = stringResource(R.string.already_have_account)
    val accion = stringResource(R.string.log_in)

    TextButton(
        onClick = onLoginClick,
        modifier = modifier.testTag("btnIrALogin")
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
fun SignUpFooterPreview() {
    MixtappTheme(darkTheme = true) {
        SignUpFooter(onLoginClick = {})
    }
}
