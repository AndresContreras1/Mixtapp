package com.example.mixtapp.ui.screens.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.Magenta
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.theme.Violeta
import com.example.mixtapp.ui.theme.White
import com.example.mixtapp.ui.utils.rebote

// El acceso a los datos curiosos del item, justo debajo de Calificar: una tarjeta llamativa con el
// degradado de la marca, el bombillo, el titulo y una linea que cuenta que trae la pantalla
@Composable
fun DatosCuriososButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val forma = RoundedCornerShape(22.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .rebote()
            .shadow(elevation = 8.dp, shape = forma)
            .clip(forma)
            .background(Brush.linearGradient(listOf(Magenta, Violeta)))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp)
            .testTag("btnDatosCuriosos"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Lightbulb,
            contentDescription = null,
            tint = White,
            modifier = Modifier.size(30.dp),
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.datos_curiosos),
                color = White,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.datos_curiosos_descripcion),
                color = White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = White,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun DatosCuriososButtonPreview() {
    MixtappTheme(darkTheme = true) {
        DatosCuriososButton(onClick = {})
    }
}
