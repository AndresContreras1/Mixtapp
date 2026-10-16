package com.example.mixtapp.ui.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun AppTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardOptions: KeyboardOptions,
    esContrasena: Boolean,
    contrasenaVisible: Boolean,
    onContrasenaVisibleChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentTransformation =
        if (esContrasena && !contrasenaVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        // El icono es decorativo: la etiqueta ya dice que va en el campo
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingIcon = if (esContrasena) {
            {
                IconButton(onClick = onContrasenaVisibleChange) {
                    PasswordVisibilityIcon(contrasenaVisible = contrasenaVisible)
                }
            }
        } else {
            null
        },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        visualTransformation = currentTransformation,
        shape = RoundedCornerShape(16.dp),
        colors = appTextFieldColors(),
    )
}

@Composable
fun appTextFieldColors(): TextFieldColors {
    val scheme = MaterialTheme.colorScheme
    return OutlinedTextFieldDefaults.colors(
        focusedContainerColor = scheme.surfaceVariant.copy(alpha = 0.55f),
        unfocusedContainerColor = scheme.surfaceVariant.copy(alpha = 0.35f),
        focusedBorderColor = scheme.inversePrimary,
        unfocusedBorderColor = scheme.outline.copy(alpha = 0.55f),
        focusedTextColor = scheme.onSurface,
        unfocusedTextColor = scheme.onSurface,
        cursorColor = scheme.inversePrimary,
        focusedLabelColor = scheme.inversePrimary,
        unfocusedLabelColor = scheme.onSurfaceVariant.copy(alpha = 0.75f),
        focusedPlaceholderColor = scheme.onSurfaceVariant.copy(alpha = 0.5f),
        unfocusedPlaceholderColor = scheme.onSurfaceVariant.copy(alpha = 0.5f),
        focusedLeadingIconColor = scheme.inversePrimary,
        unfocusedLeadingIconColor = scheme.onSurfaceVariant,
        focusedTrailingIconColor = scheme.onSurfaceVariant,
        unfocusedTrailingIconColor = scheme.onSurfaceVariant,
    )
}

@Composable
@Preview(showBackground = true)
fun AppTextFieldPreview() {
    MixtappTheme(darkTheme = true) {
        AppTextField(
            label = "Correo",
            placeholder = "Escribe tu correo",
            value = "",
            onValueChange = {},
            icon = Icons.Outlined.Email,
            keyboardOptions = KeyboardOptions.Default,
            esContrasena = false,
            contrasenaVisible = false,
            onContrasenaVisibleChange = {}
        )
    }
}
