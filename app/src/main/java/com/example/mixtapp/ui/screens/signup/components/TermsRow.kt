package com.example.mixtapp.ui.screens.signup.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R

@Composable
fun TermsRow(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        // La casilla es un IconButton con el icono lleno o vacio, como la barra inferior
        IconButton(
            onClick = onCheckedChange,
            modifier = Modifier.size(29.dp)
        ) {
            Icon(
                imageVector = if (checked) Icons.Filled.CheckBox else Icons.Outlined.CheckBoxOutlineBlank,
                contentDescription = stringResource(if (checked) R.string.terminos_aceptados else R.string.aceptar_terminos),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(29.dp)
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                    append(stringResource(R.string.agree_terms))
                }
                append(" ")
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.inversePrimary,
                        fontWeight = FontWeight.Black
                    )
                ) {
                    append(stringResource(R.string.privacy))
                }
            },
            fontSize = 15.5.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.weight(1f)
        )
    }
}