package com.example.mixtapp.ui.screens.profile.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.components.PickImageButton
import com.example.mixtapp.ui.components.ProfileAsyncImage

@Composable
fun ProfileAvatarSection(
    profileImageUrl: String,
    subiendoImagen: Boolean,
    errorImagenRes: Int?,
    onImagePicked: (Uri) -> Unit,
    reviewsCount: Int,
    albumsCount: Int,
    listsCount: Int,
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
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
        )
        Spacer(modifier = Modifier.height(12.dp))
        PickImageButton(
            action = onImagePicked,
            cargando = subiendoImagen
        )
        if (errorImagenRes != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(errorImagenRes),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.user_stats, reviewsCount, albumsCount, listsCount),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
    }

}