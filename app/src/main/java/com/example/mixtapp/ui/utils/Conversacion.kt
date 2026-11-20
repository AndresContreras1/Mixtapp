package com.example.mixtapp.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.model.MensajeUi
import com.example.mixtapp.ui.theme.MixtappTheme

// Un mensaje de un foro o de un chat como burbuja: los mios a la derecha en rosado y los de los demas
// a la izquierda con su foto. En el foro se muestra el nombre de quien escribe; en el chat no hace
// falta, porque solo hay dos
@Composable
fun BurbujaDeMensaje(
    mensaje: MensajeUi,
    mostrarAutor: Boolean,
    // Tocar la foto o el nombre abre su perfil; null en el chat
    onAutorClick: ((String) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colores = MaterialTheme.colorScheme
    val forma = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = if (mensaje.esMio) 20.dp else 6.dp,
        bottomEnd = if (mensaje.esMio) 6.dp else 20.dp,
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (mensaje.esMio) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom,
    ) {
        if (!mensaje.esMio) {
            ProfileAsyncImage(
                profileImage = mensaje.autorFoto,
                contentDescription = stringResource(R.string.foto_perfil),
                size = 32,
                modifier = if (onAutorClick != null) Modifier.clickable { onAutorClick(mensaje.autorId) } else Modifier,
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(forma)
                .background(if (mensaje.esMio) colores.primary.copy(alpha = 0.9f) else colores.surfaceContainerHigh)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            if (mostrarAutor && !mensaje.esMio) {
                Text(
                    text = mensaje.autor,
                    color = colores.primary,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Text(
                text = mensaje.texto,
                color = if (mensaje.esMio) colores.onPrimary else colores.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = mensaje.timeAgo.ifEmpty { stringResource(R.string.ahora) },
                color = (if (mensaje.esMio) colores.onPrimary else colores.onSurfaceVariant).copy(alpha = 0.75f),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}

// El campo para escribir abajo del foro o del chat, con el boton de enviar. Mientras se envia no se
// puede mandar otro
@Composable
fun CampoDeMensaje(
    value: String,
    placeholder: String,
    enviando: Boolean,
    onValueChange: (String) -> Unit,
    onEnviarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier.testTag("txtMensaje"),
        trailingContent = {
            IconButton(
                onClick = onEnviarClick,
                enabled = !enviando,
                modifier = Modifier.testTag("btnEnviarMensaje"),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.enviar),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
    )
}

@Composable
@Preview(showBackground = true)
fun BurbujaDeMensajePreview() {
    MixtappTheme(darkTheme = true) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(12.dp)) {
            BurbujaDeMensaje(
                mensaje = MensajeUi("1", "u1", "Laura", "", "Este disco me cambió la vida", "3 h", esMio = false),
                mostrarAutor = true,
                onAutorClick = {},
            )
            BurbujaDeMensaje(
                mensaje = MensajeUi("2", "yo", "Yo", "", "¡A mí también! El final es brutal", "", esMio = true),
                mostrarAutor = true,
                onAutorClick = {},
            )
        }
    }
}
