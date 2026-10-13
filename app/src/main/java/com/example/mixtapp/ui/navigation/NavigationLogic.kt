package com.example.mixtapp.ui.navigation

object NavigationLogic {

    private val bottomBarScreens = listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Profile.route
    )

    fun shouldShowBottomBar(route: String?) = bottomBarScreens.contains(route)
}
