package com.example.mixtapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mixtapp.navigation.Screen
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun BottomNav(
    navController: NavHostController,
    albumParaResenar: String,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Surface(
        color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.9f),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = currentRoute == item.route
                val icon = if (isSelected) item.filledIcon else item.outlineIcon
                val destino = if (item.route == Screen.WriteReview.route) {
                    Screen.WriteReview.createRoute(albumId = albumParaResenar)
                } else {
                    item.route
                }

                when (item.route) {
                    Screen.WriteReview.route -> CircledNavIcon(
                        icon = icon,
                        contentDescription = stringResource(item.label),
                        circleSize = 44.dp,
                        circleColor = MaterialTheme.colorScheme.primary,
                        iconSize = 30.dp,
                        iconTint = MaterialTheme.colorScheme.onSurface,
                        onClick = { navController.navigate(destino) }
                    )

                    Screen.Profile.route -> CircledNavIcon(
                        icon = icon,
                        contentDescription = stringResource(item.label),
                        circleSize = 36.dp,
                        circleColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                        iconSize = 28.dp,
                        iconTint = MaterialTheme.colorScheme.primary,
                        onClick = { navController.navigate(destino) }
                    )

                    else -> Icon(
                        imageVector = icon,
                        contentDescription = stringResource(item.label),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { navController.navigate(destino) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CircledNavIcon(
    icon: ImageVector,
    contentDescription: String,
    circleSize: Dp,
    circleColor: Color,
    iconSize: Dp,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(circleSize)
            .clip(CircleShape)
            .background(circleColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
@Preview
fun BottomNavPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        BottomNav(navController = rememberNavController(), albumParaResenar = "4")
    }
}
