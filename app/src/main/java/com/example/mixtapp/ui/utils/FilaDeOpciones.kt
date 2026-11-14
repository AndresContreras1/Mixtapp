package com.example.mixtapp.ui.utils

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

// Una opcion de una fila de filtros o de pestanas: su id y el texto que se ve
data class OpcionUi(
    val id: String,
    @StringRes val label: Int,
)

// La fila de filtros ovalados de toda la app: los filtros del inicio y de Elegir que calificar, y
// las pestanas de notificaciones, de duelos y de mi perfil. Si no caben en el celular, se deslizan.
// La elegida llega del estado del ViewModel y cada chip lleva el testTag <prefijo>_<id>
@Composable
fun FilaDeOpciones(
    opciones: List<OpcionUi>,
    elegidaId: String,
    onOpcionClick: (String) -> Unit,
    prefijoTestTag: String,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(opciones) { opcion ->
            AppChip(
                text = stringResource(opcion.label),
                isSelected = opcion.id == elegidaId,
                onClick = { onOpcionClick(opcion.id) },
                modifier = Modifier.testTag("${prefijoTestTag}_${opcion.id}"),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun FilaDeOpcionesPreview() {
    MixtappTheme(darkTheme = true) {
        FilaDeOpciones(
            opciones = listOf(
                OpcionUi(id = "paraTi", label = R.string.for_you),
                OpcionUi(id = "tendencias", label = R.string.trending),
            ),
            elegidaId = "paraTi",
            onOpcionClick = {},
            prefijoTestTag = "filtro",
        )
    }
}
