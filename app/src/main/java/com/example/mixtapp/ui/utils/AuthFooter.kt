package com.example.mixtapp.ui.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

// El pie del login y del registro: la pregunta y la accion resaltada con el color primario, para
// que se note que se puede tocar. El login lleva al registro y el registro devuelve al login
@Composable
fun AuthFooter(
    pregunta: String,
    accion: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
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
fun AuthFooterPreview() {
    MixtappTheme(darkTheme = true) {
        AuthFooter(
            pregunta = stringResource(R.string.no_tienes_cuenta),
            accion = stringResource(R.string.create_account),
            onClick = {},
        )
    }
}
