package com.example.mixtapp.ui.screens.profile.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun LogoutButton(
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onLogoutClick,
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.cerrar_sesion),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
@Preview(showBackground = true)
fun LogoutButtonPreview() {
    MixtappTheme(darkTheme = true) {
        LogoutButton(onLogoutClick = {})
    }
}
