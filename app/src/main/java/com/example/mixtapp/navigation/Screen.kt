package com.example.mixtapp.navigation

// Definicion de las rutas de la aplicacion
sealed class Screen(val route: String) {
    object Splash : Screen(route = "splash")
    object Login : Screen(route = "login")
    object SignUp : Screen(route = "signUp")
    object Home : Screen(route = "home")
    object Search : Screen(route = "search")
    object MyReviews : Screen(route = "myReviews")
    object Profile : Screen(route = "profile")
    object Following : Screen(route = "following")
    object Notifications : Screen(route = "notifications")

    // Las pantallas de detalle reciben un id por la ruta
    object SongDetail : Screen(route = "songDetail/{songId}") {
        fun createRoute(songId: String) = "songDetail/$songId"
    }

    object Discussion : Screen(route = "discussion/{reviewId}") {
        fun createRoute(reviewId: String) = "discussion/$reviewId"
    }

    object WriteReview : Screen(route = "writeReview/{albumId}") {
        fun createRoute(albumId: String) = "writeReview/$albumId"

        // Mientras no exista un selector de album, el boton + abre siempre este
        const val DEFAULT_ALBUM_ID = "4"
    }
}
