package com.example.mixtapp.ui.screens.duelos

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.DueloUi
import com.example.mixtapp.data.model.OPCION_A
import com.example.mixtapp.data.model.OPCION_B
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.model.VotoUi
import com.example.mixtapp.data.model.toItemResumenUi
import com.example.mixtapp.ui.screens.duelos.components.BarraDeVotos
import com.example.mixtapp.ui.screens.duelos.components.DueloItemCard
import com.example.mixtapp.ui.screens.duelos.components.DuelosTabs
import com.example.mixtapp.ui.screens.duelos.components.OpinionRow
import com.example.mixtapp.ui.screens.duelos.components.VotoForm
import com.example.mixtapp.ui.screens.duelos.model.DueloTabUi
import com.example.mixtapp.ui.screens.duelos.model.dueloTabs
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ScreenTitle
import com.example.mixtapp.ui.utils.SectionTitle

@Composable
fun DuelosScreen(
    duelosViewModel: DuelosViewModel,
    onItemClick: (tipo: String, itemId: String) -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // El catalogo y los votos llegan en vivo desde el ViewModel: la pantalla solo pinta lo que ya
    // viene armado
    val state by duelosViewModel.uiState.collectAsState()

    DuelosScreenContent(
        tabs = state.tabs,
        tipoElegido = state.tipoElegido,
        duelo = state.duelos[state.tipoElegido],
        opcionElegida = state.opcionElegida,
        opinion = state.opinion,
        enviando = state.enviando,
        isLoading = state.isLoading,
        errorMessageRes = state.errorMessageRes,
        errorVotosRes = state.errorVotosRes,
        errorVotoRes = state.errorVotoRes,
        onTabSelected = { duelosViewModel.selectTipo(tipo = it) },
        onOpcionClick = { duelosViewModel.elegirOpcion(opcion = it) },
        onOpinionChange = { duelosViewModel.updateOpinion(texto = it) },
        onVotarClick = { duelosViewModel.votar() },
        onItemClick = onItemClick,
        onUserClick = onUserClick,
        modifier = modifier,
    )
}

@Composable
fun DuelosScreenContent(
    tabs: List<DueloTabUi>,
    tipoElegido: String,
    duelo: DueloUi?,
    opcionElegida: String?,
    opinion: String,
    enviando: Boolean,
    isLoading: Boolean,
    @StringRes errorMessageRes: Int?,
    @StringRes errorVotosRes: Int?,
    @StringRes errorVotoRes: Int?,
    onTabSelected: (String) -> Unit,
    onOpcionClick: (String) -> Unit,
    onOpinionChange: (String) -> Unit,
    onVotarClick: () -> Unit,
    onItemClick: (tipo: String, itemId: String) -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("duelosScreen")
    ) {
        AppBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            ScreenTitle(
                title = stringResource(R.string.duelos_title),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )

            // Las pestanas quedan fijas para pasar de un duelo a otro
            DuelosTabs(
                tabs = tabs,
                tipoElegido = tipoElegido,
                onTabSelected = onTabSelected,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )

            when {
                isLoading -> LoadingBox()
                // Sin catalogo no hay con que armar el duelo: solo se muestra el motivo del error
                duelo == null && errorMessageRes != null -> ErrorBox(messageRes = errorMessageRes)
                duelo == null -> EmptyMessage(
                    messageRes = R.string.sin_duelo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                )
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.duelo_del_dia_genero, duelo.genero),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }

                    item {
                        DueloCaraACara(
                            duelo = duelo,
                            opcionElegida = opcionElegida,
                            onOpcionClick = onOpcionClick,
                            onItemClick = onItemClick,
                        )
                    }

                    item {
                        BarraDeVotos(porcentajeA = duelo.porcentajeA, porcentajeB = duelo.porcentajeB)
                    }

                    item {
                        // El error de los votos no tapa el duelo: el par se sigue viendo
                        ErrorMessage(messageRes = errorVotosRes)
                        VotoForm(
                            opinion = opinion,
                            yaVote = duelo.miOpcion != null,
                            enviando = enviando,
                            errorVotoRes = errorVotoRes,
                            onOpinionChange = onOpinionChange,
                            onVotarClick = onVotarClick,
                        )
                    }

                    item {
                        SectionTitle(title = stringResource(R.string.opiniones_count, duelo.opiniones.size))
                    }

                    if (duelo.opiniones.isEmpty()) {
                        item {
                            EmptyMessage(
                                messageRes = R.string.sin_opiniones,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    items(duelo.opiniones, key = { it.usuarioId }) { voto ->
                        OpinionRow(
                            voto = voto,
                            tituloVotado = if (voto.opcion == OPCION_A) duelo.itemA.titulo else duelo.itemB.titulo,
                            onAuthorClick = { onUserClick(voto.usuarioId) },
                        )
                    }
                }
            }
        }
    }
}

// Los dos items lado a lado con el VS en el medio. El borde marca el que elegi para votar o, si no
// he elegido, el que ya vote
@Composable
private fun DueloCaraACara(
    duelo: DueloUi,
    opcionElegida: String?,
    onOpcionClick: (String) -> Unit,
    onItemClick: (tipo: String, itemId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val marcado = opcionElegida ?: duelo.miOpcion

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DueloItemCard(
            item = duelo.itemA,
            porcentaje = duelo.porcentajeA,
            votos = duelo.votosA,
            elegido = marcado == OPCION_A,
            esMiVoto = duelo.miOpcion == OPCION_A,
            vaGanando = duelo.ganador == OPCION_A,
            onClick = { onOpcionClick(OPCION_A) },
            onVerDetalle = { onItemClick(duelo.tipo, duelo.itemA.id) },
            modifier = Modifier
                .weight(1f)
                .testTag("dueloA"),
        )

        Text(
            text = stringResource(R.string.vs),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        DueloItemCard(
            item = duelo.itemB,
            porcentaje = duelo.porcentajeB,
            votos = duelo.votosB,
            elegido = marcado == OPCION_B,
            esMiVoto = duelo.miOpcion == OPCION_B,
            vaGanando = duelo.ganador == OPCION_B,
            onClick = { onOpcionClick(OPCION_B) },
            onVerDetalle = { onItemClick(duelo.tipo, duelo.itemB.id) },
            modifier = Modifier
                .weight(1f)
                .testTag("dueloB"),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun DuelosScreenPreview() {
    MixtappTheme(darkTheme = true) {
        DuelosScreenContent(
            tabs = dueloTabs,
            tipoElegido = TIPO_ALBUM,
            duelo = DueloUi(
                id = "dia_20397_album",
                dia = 20397,
                tipo = TIPO_ALBUM,
                genero = "Nu metal",
                itemA = LocalPreviewProvider.fromZero.toItemResumenUi(),
                itemB = LocalPreviewProvider.karolG.toItemResumenUi(),
                votosA = 16,
                votosB = 9,
                opiniones = listOf(
                    VotoUi(
                        usuarioId = "usuario_1",
                        autor = "Laura Gómez",
                        autorFoto = "",
                        opcion = OPCION_A,
                        opinion = "Volvieron con todo.",
                        timeAgo = "3 h",
                        esMio = false,
                    )
                ),
                miOpcion = OPCION_A,
            ),
            opcionElegida = null,
            opinion = "",
            enviando = false,
            isLoading = false,
            errorMessageRes = null,
            errorVotosRes = null,
            errorVotoRes = null,
            onTabSelected = {},
            onOpcionClick = {},
            onOpinionChange = {},
            onVotarClick = {},
            onItemClick = { _, _ -> },
            onUserClick = {},
        )
    }
}
