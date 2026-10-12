package com.example.mixtapp.ui.screens.editprofile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.model.MAX_BIO_LENGTH
import com.example.mixtapp.ui.utils.AppTextField

@Composable
fun EditProfileForm(
    nombre: String,
    onNombreChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    bio: String,
    onBioChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AppTextField(
            label = stringResource(R.string.nombre),
            placeholder = stringResource(R.string.ingresa_tu_nombre),
            value = nombre,
            onValueChange = onNombreChange,
            icon = Icons.Outlined.Person,
            keyboardOptions = KeyboardOptions.Default,
            esContrasena = false,
            contrasenaVisible = false,
            onContrasenaVisibleChange = {}
        )

        Spacer(modifier = Modifier.height(14.dp))

        AppTextField(
            label = stringResource(R.string.username),
            placeholder = stringResource(R.string.choose_username),
            value = username,
            onValueChange = onUsernameChange,
            icon = Icons.Outlined.AlternateEmail,
            keyboardOptions = KeyboardOptions.Default,
            esContrasena = false,
            contrasenaVisible = false,
            onContrasenaVisibleChange = {}
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = bio,
            onValueChange = onBioChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(126.dp),
            label = { Text(text = stringResource(R.string.biografia)) },
            placeholder = { Text(text = stringResource(R.string.biografia_placeholder)) }
        )

        Text(
            text = stringResource(R.string.contador_caracteres, bio.length, MAX_BIO_LENGTH),
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 6.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
