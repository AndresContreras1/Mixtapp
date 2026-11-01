package com.example.mixtapp.ui.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

// El boton principal de la app: ovalado, con el color primario. Mientras carga muestra el circulo
// y no se puede tocar otra vez
@Composable
fun AppButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cargando: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        enabled = !cargando,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 3.dp
            )
        } else {
            Text(
                text = texto,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AppButtonPreview() {
    MixtappTheme(darkTheme = true) {
        AppButton(
            texto = stringResource(R.string.ingresar),
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
