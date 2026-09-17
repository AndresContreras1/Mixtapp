package com.example.mixtapp.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun BottomNav(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar(modifier = modifier) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.filledIcon else item.outlineIcon,
                        contentDescription = stringResource(item.label)
                    )
                },
                selected = false,
                onClick = { navController.navigate(item.route) }
            )
        }
    }
}

@Composable
@Preview
fun BottomNavPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        BottomNav(navController = rememberNavController())
    }
}
