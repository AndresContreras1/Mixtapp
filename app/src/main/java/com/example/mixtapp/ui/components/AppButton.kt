package com.example.mixtapp.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun AppButton(
    texto: String,
    onClick: () -> Unit,
    cargando: Boolean,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = !cargando,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onSurface,
                strokeWidth = 3.dp
            )
        } else {
            Text(
                text = texto,
                fontSize = fontSize,
                fontWeight = fontWeight
            )
        }
    }
}

@Composable
@Preview
fun AppButtonPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        AppButton(
            texto = "INGRESAR",
            onClick = {},
            cargando = false,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .fillMaxWidth()
                .height(82.dp)
        )
    }
}
