package com.example.mixtapp.ui.screens.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R

@Composable
fun LogoutButton(
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.cerrar_sesion),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clickable { onLogoutClick() }
            .padding(vertical = 24.dp)
    )
}
