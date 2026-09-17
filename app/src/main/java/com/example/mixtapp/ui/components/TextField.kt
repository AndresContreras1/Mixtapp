package com.example.mixtapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    icon: FieldIcon,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    esContrasena: Boolean = false,
    contrasenaVisible: Boolean = false,
    onContrasenaVisibleChange: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val currentTransformation =
        if (esContrasena && !contrasenaVisible) {
            PasswordVisualTransformation()
        } else {
            visualTransformation
        }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        leadingIcon = {
            FieldIconView(
                icon = icon,
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
        visualTransformation = currentTransformation
    )
}


@Composable
@Preview
fun AppTextFieldPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        AppTextField(
            label = "Correo",
            placeholder = "Escribe tu correo",
            value = "",
            onValueChange = {},
            icon = FieldIcon.Email
        )
    }
}
