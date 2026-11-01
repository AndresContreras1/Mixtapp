package com.example.mixtapp.ui.navigation

// Decide en que rutas se ven la barra de abajo y el boton +
object NavigationLogic {

    private val bottomBarScreens = listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Profile.route
    )

    fun shouldShowBottomBar(route: String?) = bottomBarScreens.contains(route)
}
