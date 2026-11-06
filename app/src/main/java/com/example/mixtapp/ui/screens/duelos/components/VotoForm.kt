package com.example.mixtapp.ui.screens.duelos.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.screens.duelos.MAX_OPINION
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppButton
import com.example.mixtapp.ui.utils.AppTextField
import com.example.mixtapp.ui.utils.ErrorMessage

// La opinion y el boton de votar. Si ya vote, el boton cambia el voto (o solo la opinion)
@Composable
fun VotoForm(
    opinion: String,
    yaVote: Boolean,
    enviando: Boolean,
    @StringRes errorVotoRes: Int?,
    onOpinionChange: (String) -> Unit,
    onVotarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Una sola linea: el ViewModel la corta en MAX_OPINION caracteres
        AppTextField(
            value = opinion,
            onValueChange = onOpinionChange,
            label = stringResource(R.string.label_opinion, MAX_OPINION),
            placeholder = stringResource(R.string.opinion_placeholder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("txtOpinion"),
        )

        ErrorMessage(messageRes = errorVotoRes)

        Spacer(modifier = Modifier.height(12.dp))

        AppButton(
            texto = stringResource(if (yaVote) R.string.cambiar_voto else R.string.votar),
            onClick = onVotarClick,
            cargando = enviando,
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
            yaVote = false,
            enviando = false,
            errorVotoRes = null,
            onOpinionChange = {},
            onVotarClick = {},
        )
    }
}
