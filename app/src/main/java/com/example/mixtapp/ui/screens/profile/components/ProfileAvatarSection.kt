package com.example.mixtapp.ui.screens.profile.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalMyReviewsProvider
import com.example.mixtapp.data.model.FAVORITOS_EN_PERFIL
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.ProfileAsyncImage

@Composable
fun ProfileAvatarSection(
    profileImageUrl: String,
    subiendoImagen: Boolean,
    errorImagenRes: Int?,
    onImagePicked: (Uri) -> Unit,
    reviewsCount: Int,
    favoritosCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        ProfileAsyncImage(
            profileImage = profileImageUrl,
            contentDescription = stringResource(R.string.foto_perfil),
            modifier = Modifier
                .size(100.dp)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), CircleShape)
        )
        Spacer(modifier = Modifier.height(12.dp))
        PickImageButton(
            action = onImagePicked,
            cargando = subiendoImagen
        )
        ErrorMessage(
            messageRes = errorImagenRes,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        val resenas = if (reviewsCount == 1) {
            stringResource(R.string.una_resena)
        } else {
            stringResource(R.string.n_resenas, reviewsCount)
        }
        val favoritos = if (favoritosCount == 1) {
            stringResource(R.string.un_favorito)
        } else {
            stringResource(R.string.n_favoritos, favoritosCount)
        }

        Text(
            text = stringResource(R.string.user_stats, resenas, favoritos),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ProfileAvatarSectionPreview() {
    MixtappTheme(darkTheme = true) {
        ProfileAvatarSection(
            profileImageUrl = "",
            subiendoImagen = false,
            errorImagenRes = null,
            onImagePicked = {},
            reviewsCount = LocalMyReviewsProvider.reviews.size,
            favoritosCount = FAVORITOS_EN_PERFIL,
        )
    }
}
