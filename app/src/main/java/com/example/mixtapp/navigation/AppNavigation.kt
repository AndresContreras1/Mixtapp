package com.example.mixtapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mixtapp.ui.screens.home.HomeScreen
import com.example.mixtapp.ui.screens.login.LoginScreen
import com.example.mixtapp.ui.screens.myreviews.MyReviewsScreen
import com.example.mixtapp.ui.screens.profile.ProfileScreen
import com.example.mixtapp.ui.screens.review.WriteReviewScreen
import com.example.mixtapp.ui.screens.search.SearchScreen
import com.example.mixtapp.ui.screens.signup.SignUpScreen
import com.example.mixtapp.ui.screens.songreview.SongReviewsScreen

// Definicion de las rutas de la aplicacion
sealed class Screen(val route: String) {
    object Login : Screen(route = "login")
    object SignUp : Screen(route = "signUp")
    object Home : Screen(route = "home")
    object Search : Screen(route = "search")
    object WriteReview : Screen(route = "writeReview")
    object MyReviews : Screen(route = "myReviews")
    object Profile : Screen(route = "profile")
    object SongDetail : Screen(route = "songDetail")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen()
        }

        composable(route = Screen.SignUp.route) {
            SignUpScreen()
        }

        composable(route = Screen.Home.route) {
            HomeScreen()
        }

        composable(route = Screen.Search.route) {
            SearchScreen()
        }

        composable(route = Screen.WriteReview.route) {
            WriteReviewScreen()
        }

        composable(route = Screen.MyReviews.route) {
            MyReviewsScreen()
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen()
        }

        composable(route = Screen.SongDetail.route) {
            SongReviewsScreen()
        }
    }
}
