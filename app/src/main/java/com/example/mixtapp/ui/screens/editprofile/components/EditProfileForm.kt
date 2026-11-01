package com.example.mixtapp.ui.screens.editprofile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.MAX_BIO_LENGTH
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppTextArea
import com.example.mixtapp.ui.utils.AppTextField

// Los campos del perfil: nombre y usuario ovalados como en el registro, la biografia en el area
// de texto con el contador, porque puede ocupar varias lineas, y el Instagram ovalado al final
@Composable
fun EditProfileForm(
    nombre: String,
    onNombreChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    bio: String,
    onBioChange: (String) -> Unit,
    instagram: String,
    onInstagramChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AppTextField(
            value = nombre,
            onValueChange = onNombreChange,
            placeholder = stringResource(R.string.ingresa_tu_nombre),
            label = stringResource(R.string.nombre),
            leadingIcon = Icons.Outlined.Person,
        )

        Spacer(modifier = Modifier.height(14.dp))

        AppTextField(
            value = username,
            onValueChange = onUsernameChange,
            placeholder = stringResource(R.string.choose_username),
            label = stringResource(R.string.username),
            leadingIcon = Icons.Outlined.AlternateEmail,
        )

        Spacer(modifier = Modifier.height(14.dp))

        AppTextArea(
            value = bio,
            onValueChange = onBioChange,
            label = stringResource(R.string.biografia),
            placeholder = stringResource(R.string.biografia_placeholder),
            maxLength = MAX_BIO_LENGTH
        )

        Spacer(modifier = Modifier.height(14.dp))

        AppTextField(
            value = instagram,
            onValueChange = onInstagramChange,
            placeholder = stringResource(R.string.instagram_placeholder),
            label = stringResource(R.string.label_instagram),
            leadingIcon = Icons.Outlined.PhotoCamera,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun EditProfileFormPreview() {
    val usuario = LocalPreviewProvider.usuario

    MixtappTheme(darkTheme = true) {
        EditProfileForm(
            nombre = usuario.nombre,
            onNombreChange = {},
            username = usuario.username,
            onUsernameChange = {},
            bio = usuario.bio,
            onBioChange = {},
            instagram = usuario.instagram,
            onInstagramChange = {},
        )
    }
}
