package com.example.mixtapp.ui.screens.home.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.LogoText

// El encabezado del inicio: el logo a la izquierda y los accesos a Siguiendo y a las
// notificaciones. La busqueda no va aqui porque ya esta en la barra de abajo
@Composable
fun HomeHeader(
    onFollowingClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LogoText(
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            fontWeight = FontWeight.Bold,
            lineHeight = MaterialTheme.typography.headlineLarge.lineHeight,
            modifier = Modifier.weight(1f),
        )

        IconButton(onClick = onFollowingClick, modifier = Modifier.testTag("btnSiguiendo")) {
            Icon(
                imageVector = Icons.Outlined.People,
                contentDescription = stringResource(R.string.following_title),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }

        IconButton(onClick = onNotificationsClick) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = stringResource(R.string.notifications_title),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeHeaderPreview() {
    MixtappTheme(darkTheme = true) {
        HomeHeader(
            onFollowingClick = {},
            onNotificationsClick = {},
        )
    }
}
