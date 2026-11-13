package com.example.mixtapp.ui.navigation

// Definicion de las rutas de la aplicacion
sealed class Screen(val route: String) {
    data object Splash : Screen(route = "splash")
    data object Login : Screen(route = "login")
    data object SignUp : Screen(route = "signUp")
    data object Home : Screen(route = "home")
    data object Search : Screen(route = "search")
    data object Duelos : Screen(route = "duelos")
    data object Mapa : Screen(route = "mapa")
    data object Resumen : Screen(route = "resumen")
    data object MyReviews : Screen(route = "myReviews")
    data object Profile : Screen(route = "profile")
    data object Following : Screen(route = "following")
    data object Notifications : Screen(route = "notifications")
    data object SelectItem : Screen(route = "selectItem")
    data object EditProfile : Screen(route = "editProfile")

    // El detalle sirve para albumes, canciones y artistas: el tipo dice de que coleccion es el id
    data object Detail : Screen(route = "detail/{tipo}/{itemId}") {
        fun createRoute(tipo: String, itemId: String) = "detail/$tipo/$itemId"
    }

    // Los datos curiosos del mismo item, en su propia pantalla
    data object DatosCuriosos : Screen(route = "datosCuriosos/{tipo}/{itemId}") {
        fun createRoute(tipo: String, itemId: String) = "datosCuriosos/$tipo/$itemId"
    }

    data object WriteReview : Screen(route = "writeReview/{tipo}/{itemId}") {
        fun createRoute(tipo: String, itemId: String) = "writeReview/$tipo/$itemId"
    }

    data object Discussion : Screen(route = "discussion/{reviewId}") {
        fun createRoute(reviewId: String) = "discussion/$reviewId"
    }

    data object UserProfile : Screen(route = "userProfile/{userId}") {
        fun createRoute(userId: String) = "userProfile/$userId"
    }

    // Los seguidores o los seguidos de un usuario: el tipo dice cual de las dos listas
    data object FollowList : Screen(route = "followList/{userId}/{tipo}") {
        fun createRoute(userId: String, tipo: String) = "followList/$userId/$tipo"
    }

    // Una mixtape con sus items
    data object MixtapeDetail : Screen(route = "mixtapeDetail/{mixtapeId}") {
        fun createRoute(mixtapeId: String) = "mixtapeDetail/$mixtapeId"
    }

    data object CrearMixtape : Screen(route = "crearMixtape")

    // Elegir en cual de mis mixtapes va el item del detalle
    data object AgregarAMixtape : Screen(route = "agregarAMixtape/{tipo}/{itemId}") {
        fun createRoute(tipo: String, itemId: String) = "agregarAMixtape/$tipo/$itemId"
    }
}
