package com.example.mixtapp.navigation

object NavigationLogic {

    // Pantallas que SI muestran la barra inferior: las que son destino de la barra
    private val bottomBarScreens = listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.WriteReview.route,
        Screen.MyReviews.route,
        Screen.Profile.route
    )

    fun shouldShowBottomBar(route: String?) = bottomBarScreens.contains(route)
}
