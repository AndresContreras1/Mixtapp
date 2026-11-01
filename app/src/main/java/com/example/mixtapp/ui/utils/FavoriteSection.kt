package com.example.mixtapp.ui.utils

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.ui.theme.MixtappTheme

// Los favoritos de un perfil (sus likes: albumes, canciones y artistas mezclados) en una fila con
// scroll. La usan mi perfil y el de otro usuario, cada uno con su texto de lista vacia
@Composable
fun FavoriteSection(
    favoritos: List<ItemResumenUi>,
    @StringRes errorMessageRes: Int?,
    @StringRes vaciaRes: Int,
    onItemClick: (ItemResumenUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionTitle(title = stringResource(R.string.favoritos))

        Spacer(modifier = Modifier.height(8.dp))

        when {
            errorMessageRes != null -> ErrorMessage(messageRes = errorMessageRes)
            favoritos.isEmpty() -> EmptyMessage(messageRes = vaciaRes, modifier = Modifier.fillMaxWidth())
            else -> ItemResumenRow(items = favoritos, onItemClick = onItemClick)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun FavoriteSectionPreview() {
    MixtappTheme(darkTheme = true) {
        FavoriteSection(
            favoritos = LocalPreviewProvider.favoritos,
            errorMessageRes = null,
            vaciaRes = R.string.sin_favoritos,
            onItemClick = {},
        )
    }
}
