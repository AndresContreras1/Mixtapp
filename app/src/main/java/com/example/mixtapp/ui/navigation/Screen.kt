package com.example.mixtapp.ui.navigation

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
    data object SelectAlbum : Screen(route = "selectAlbum")
    data object EditProfile : Screen(route = "editProfile")

    // Las pantallas de detalle reciben un id por la ruta
    data object AlbumDetail : Screen(route = "albumDetail/{albumId}") {
        fun createRoute(albumId: String) = "albumDetail/$albumId"
    }

    data object Discussion : Screen(route = "discussion/{reviewId}") {
        fun createRoute(reviewId: String) = "discussion/$reviewId"
    }

    data object WriteReview : Screen(route = "writeReview/{albumId}") {
        fun createRoute(albumId: String) = "writeReview/$albumId"
    }

    data object UserProfile : Screen(route = "userProfile/{userId}") {
        fun createRoute(userId: String) = "userProfile/$userId"
    }

    // Los seguidores o los seguidos de un usuario: el tipo dice cual de las dos listas
    data object FollowList : Screen(route = "followList/{userId}/{tipo}") {
        fun createRoute(userId: String, tipo: String) = "followList/$userId/$tipo"
    }
}
