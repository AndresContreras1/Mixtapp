package com.example.mixtapp.ui.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

// El campo de toda la app: ovalado, con el fondo translucido para que se vea el fondo con los
// circulos detras, y el borde claro que lo resalta. Sirve para el correo, la busqueda, los
// comentarios y la fecha de escucha; la contrasena tiene su propio campo, AppPasswordField
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    // Sin etiqueta en la busqueda y en los comentarios
    label: String? = null,
    // El icono es decorativo: la etiqueta o el placeholder ya dicen que va en el campo
    leadingIcon: ImageVector? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    // Lo que va al final del campo, como el boton de publicar un comentario
    trailingContent: (@Composable () -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = if (label != null) { { Text(text = label) } } else null,
        placeholder = { Text(text = placeholder) },
        leadingIcon = if (leadingIcon != null) {
            {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }
        } else {
            null
        },
        trailingIcon = trailingContent,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        // La mitad del alto en las esquinas: el campo queda ovalado
        shape = RoundedCornerShape(percent = 50),
        colors = coloresDelCampo(),
    )
}

// El campo de la contrasena: la misma apariencia, con los puntos y el ojo para verla. El ojo avisa
// con onContrasenaVisibleChange y el ViewModel decide si se ve (state hoisting)
@Composable
fun AppPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String,
    contrasenaVisible: Boolean,
    onContrasenaVisibleChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
        },
        trailingIcon = {
            IconButton(onClick = onContrasenaVisibleChange) {
                PasswordVisibilityIcon(contrasenaVisible = contrasenaVisible)
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (contrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
        shape = RoundedCornerShape(percent = 50),
        colors = coloresDelCampo(),
    )
}

// El area de texto de la resena: la misma apariencia, con varias lineas y el contador de caracteres
// debajo del campo. El modifier va al campo, asi el testTag queda donde se escribe
@Composable
fun AppTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    maxLength: Int,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        supportingText = {
            Text(
                text = stringResource(R.string.contador_caracteres, value.length, maxLength),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
        },
        minLines = 5,
        shape = RoundedCornerShape(28.dp),
        colors = coloresDelCampo(),
    )
}

// Los colores de la paleta para todos los campos: el fondo es el surfaceVariant transparente, mas
// visible cuando el campo tiene el foco
@Composable
private fun coloresDelCampo(): TextFieldColors {
    val colores = MaterialTheme.colorScheme

    return OutlinedTextFieldDefaults.colors(
        focusedContainerColor = colores.surfaceVariant.copy(alpha = 0.6f),
        unfocusedContainerColor = colores.surfaceVariant.copy(alpha = 0.35f),
        focusedBorderColor = colores.primary,
        unfocusedBorderColor = colores.outline.copy(alpha = 0.45f),
        focusedLabelColor = colores.primary,
        unfocusedLabelColor = colores.onSurfaceVariant,
        focusedTextColor = colores.onSurface,
        unfocusedTextColor = colores.onSurface,
        focusedPlaceholderColor = colores.onSurfaceVariant,
        unfocusedPlaceholderColor = colores.onSurfaceVariant,
        focusedLeadingIconColor = colores.primary,
        unfocusedLeadingIconColor = colores.onSurfaceVariant,
        cursorColor = colores.primary,
    )
}

@Composable
@Preview(showBackground = true)
fun AppTextFieldPreview() {
    MixtappTheme(darkTheme = true) {
        Column {
            AppTextField(
                value = "",
                onValueChange = {},
                placeholder = stringResource(R.string.enter_your_email),
                label = stringResource(R.string.email),
                leadingIcon = Icons.Outlined.Email,
            )
            AppPasswordField(
                value = "123456",
                onValueChange = {},
                placeholder = stringResource(R.string.enter_your_password),
                label = stringResource(R.string.contrasena),
                contrasenaVisible = false,
                onContrasenaVisibleChange = {},
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AppTextAreaPreview() {
    MixtappTheme(darkTheme = true) {
        AppTextArea(
            value = "",
            onValueChange = {},
            label = stringResource(R.string.label_resena),
            placeholder = stringResource(R.string.review_placeholder),
            maxLength = 500
        )
    }
}
