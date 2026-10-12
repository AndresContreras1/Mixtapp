package com.example.mixtapp.ui.screens.editprofile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.displayFontFamily

@Composable
fun EditProfileHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(30.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.editar_perfil),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            fontFamily = displayFontFamily
        )

        Text(
            text = stringResource(R.string.editar_perfil_descripcion),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
    }
}
