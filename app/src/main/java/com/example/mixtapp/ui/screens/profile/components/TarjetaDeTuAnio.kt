package com.example.mixtapp.ui.screens.profile.components

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
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.Coral
import com.example.mixtapp.ui.theme.Magenta
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.theme.Violeta
import com.example.mixtapp.ui.theme.White
import com.example.mixtapp.ui.utils.rebote

// El acceso a Tu ano en el perfil: no es una opcion mas, asi que va con el degradado vivo del resumen
// (coral, magenta y violeta), el destello y una linea que cuenta que hay adentro
@Composable
fun TarjetaDeTuAnio(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .rebote()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(Coral, Magenta, Violeta)))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .testTag("btnResumen"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.AutoAwesome,
            contentDescription = null,
            tint = White,
            modifier = Modifier.size(34.dp),
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.resumen_titulo),
                color = White,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = stringResource(R.string.resumen_tarjeta_detalle),
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
fun TarjetaDeTuAnioPreview() {
    MixtappTheme(darkTheme = true) {
        TarjetaDeTuAnio(onClick = {})
    }
}
