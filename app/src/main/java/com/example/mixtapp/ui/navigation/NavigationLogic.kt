package com.example.mixtapp.ui.navigation

// Decide en que rutas se ven la barra de abajo y el boton +
object NavigationLogic {

    private val bottomBarScreens = listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Duelos.route,
        Screen.Mapa.route,
        Screen.Profile.route
    )

    // El + no va en el mapa: taparia la tarjeta de la resena que sale abajo al tocar un marcador
    private val fabScreens = listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Duelos.route,
        Screen.Profile.route
    )

    fun shouldShowBottomBar(route: String?) = bottomBarScreens.contains(route)

    fun shouldShowFab(route: String?) = fabScreens.contains(route)
}
