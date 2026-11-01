package com.example.mixtapp.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mixtapp.ui.theme.MixtappTheme

// La barra de abajo: Inicio, Buscar y Perfil. Solo se ve en esas tres rutas (NavigationLogic)
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
                        // La ruta, como en clase: las pruebas e2e encuentran la pestana por ella
                        contentDescription = item.route
                    )
                },
                label = { Text(text = stringResource(item.label)) },
                // Como lo deja el profesor: el icono lleno o vacio ya marca la pestana actual
                selected = false,
                // Tocar la pestana en la que ya se esta no la vuelve a apilar
                onClick = {
                    if (!isSelected) navController.navigate(item.route)
                }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun BottomNavPreview() {
    MixtappTheme(darkTheme = true) {
        BottomNav(navController = rememberNavController())
    }
}
