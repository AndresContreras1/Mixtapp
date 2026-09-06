package com.example.mixtapp.navigation

object NavigationLogic {

    // Pantallas que NO deben mostrar la barra inferior
    private val noBottomBarScreens = listOf(
        Screen.Splash.route,
        Screen.Login.route,
        Screen.SignUp.route
    )

    fun shouldShowBottomBar(route: String?) = !noBottomBarScreens.contains(route)
}
