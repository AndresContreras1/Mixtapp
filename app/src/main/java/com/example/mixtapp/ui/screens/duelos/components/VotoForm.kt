package com.example.mixtapp.ui.screens.duelos.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppButton
import com.example.mixtapp.ui.utils.AppTextField
import com.example.mixtapp.ui.utils.ErrorMessage

// La opinion y el boton de votar. El boton queda apagado hasta elegir uno de los dos (lo decide el
// ViewModel con puedeVotar). Si ya vote, dice por cual y el boton cambia el voto (o solo la opinion)
@Composable
fun VotoForm(
    opinion: String,
    // El titulo por el que vote; null si todavia no vote
    miVoto: String?,
    puedeVotar: Boolean,
    enviando: Boolean,
    @StringRes errorVotoRes: Int?,
    onOpinionChange: (String) -> Unit,
    onVotarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (miVoto != null) {
            Text(
                text = stringResource(R.string.votaste_por, miVoto),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .testTag("miVoto"),
            )
        }

        // Solo el recuadro con la pregunta: el ViewModel la corta en MAX_OPINION caracteres
        AppTextField(
            value = opinion,
            onValueChange = onOpinionChange,
            placeholder = stringResource(R.string.opinion_placeholder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("txtOpinion"),
        )

        ErrorMessage(messageRes = errorVotoRes)

        Spacer(modifier = Modifier.height(12.dp))

        AppButton(
            texto = stringResource(if (miVoto != null) R.string.cambiar_voto else R.string.votar),
            onClick = onVotarClick,
            cargando = enviando,
            habilitado = puedeVotar,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btnVotar"),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun VotoFormPreview() {
    MixtappTheme(darkTheme = true) {
        VotoForm(
            opinion = "",
            miVoto = null,
            puedeVotar = false,
            enviando = false,
            errorVotoRes = null,
            onOpinionChange = {},
            onVotarClick = {},
        )
    }
}
