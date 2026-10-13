package com.example.mixtapp.ui.screens.editprofile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.theme.displayFontFamily
import com.example.mixtapp.ui.utils.BackButton

@Composable
fun EditProfileHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        BackButton(onBackClick = onBackClick)

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

@Composable
@Preview(showBackground = true)
fun EditProfileHeaderPreview() {
    MixtappTheme(darkTheme = true) {
        EditProfileHeader(onBackClick = {})
    }
}
