package com.example.mixtapp.ui.screens.signup.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

// La fila de los terminos: la casilla es un IconButton con el icono lleno o vacio, como la barra
// inferior. Alternarla lo decide el ViewModel; aqui solo se avisa del clic
@Composable
fun TermsRow(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onCheckedChange,
            modifier = Modifier.testTag("chkTerminos")
        ) {
            Icon(
                imageVector = if (checked) Icons.Filled.CheckBox else Icons.Outlined.CheckBoxOutlineBlank,
                contentDescription = stringResource(if (checked) R.string.terminos_aceptados else R.string.aceptar_terminos),
                // Marcada toma el color primario para que se note el cambio
                tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = stringResource(R.string.agree_terms),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun TermsRowPreview() {
    MixtappTheme(darkTheme = true) {
        TermsRow(
            checked = true,
            onCheckedChange = {},
        )
    }
}
