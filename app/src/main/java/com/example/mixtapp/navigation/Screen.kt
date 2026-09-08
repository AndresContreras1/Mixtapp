package com.example.mixtapp.navigation

// Definicion de las rutas de la aplicacion
sealed class Screen(val route: String) {
    data object Splash : Screen(route = "splash")
    data object Login : Screen(route = "login")
    data object SignUp : Screen(route = "signUp")
    data object Home : Screen(route = "home")
    data object Search : Screen(route = "search")
    data object MyReviews : Screen(route = "myReviews")
    data object Profile : Screen(route = "profile")
    data object Following : Screen(route = "following")
    data object Notifications : Screen(route = "notifications")

    // Las pantallas de detalle reciben un id por la ruta
    data object SongDetail : Screen(route = "songDetail/{songId}") {
        fun createRoute(songId: String) = "songDetail/$songId"
    }

    data object Discussion : Screen(route = "discussion/{reviewId}") {
        fun createRoute(reviewId: String) = "discussion/$reviewId"
    }

    data object WriteReview : Screen(route = "writeReview/{albumId}") {
        fun createRoute(albumId: String) = "writeReview/$albumId"

        // Mientras no exista un selector de album, el boton + abre siempre este
        const val DEFAULT_ALBUM_ID = "4"
    }
}
