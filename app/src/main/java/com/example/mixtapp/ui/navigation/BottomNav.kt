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
import androidx.navigation.compose.rememberNavController
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.rutaActual

// La barra de abajo: Inicio, Buscar, Duelos, Mapa y Perfil. Solo se ve en esas cinco rutas (NavigationLogic)
@Composable
fun BottomNav(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val currentRoute = rutaActual(navController)

    NavigationBar(modifier = modifier) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.filledIcon else item.outlineIcon,
                        // La ruta, como en clase: una prueba e2e encuentra la pestana por ella
                        contentDescription = item.route
                    )
                },
                label = { Text(text = stringResource(item.label)) },
                // Como lo deja el profesor: el icono lleno o vacio ya marca la pestana actual
                selected = false,
                // Las pestanas no se apilan: se vuelve al inicio antes de abrir la otra, asi en la
                // pila solo queda el inicio debajo y atras desde el inicio sale de la app. Al salir
                // de una pestana su ViewModel se destruye y sus listeners se quitan
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) { inclusive = item.route == Screen.Home.route }
                        }
                    }
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
