package com.example.mixtapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mixtapp.data.local.LocalDiscussionProvider
import com.example.mixtapp.data.local.LocalFollowingProvider
import com.example.mixtapp.data.local.LocalSongReviewProvider
import com.example.mixtapp.ui.screens.discussion.DiscussionScreen
import com.example.mixtapp.ui.screens.following.FollowingScreen
import com.example.mixtapp.ui.screens.home.HomeScreen
import com.example.mixtapp.ui.screens.login.LoginScreen
import com.example.mixtapp.ui.screens.myreviews.MyReviewsScreen
import com.example.mixtapp.ui.screens.profile.ProfileScreen
import com.example.mixtapp.ui.screens.review.WriteReviewScreen
import com.example.mixtapp.ui.screens.review.model.fromZeroAlbum
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
    object Following : Screen(route = "following")
    object Discussion : Screen(route = "discussion")
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
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(route = Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(route = Screen.Home.route) {
            HomeScreen(
                onAlbumClick = { navController.navigate(Screen.SongDetail.route) },
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onFollowingClick = { navController.navigate(Screen.Following.route) }
            )
        }

        composable(route = Screen.Search.route) {
            SearchScreen()
        }

        composable(route = Screen.WriteReview.route) {
            WriteReviewScreen(
                album = fromZeroAlbum,
                onCancel = { navController.popBackStack() },
                onPostReview = { navController.navigate(Screen.MyReviews.route) }
            )
        }

        composable(route = Screen.MyReviews.route) {
            MyReviewsScreen(
                onReviewClick = { navController.navigate(Screen.SongDetail.route) }
            )
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen()
        }

        composable(route = Screen.SongDetail.route) {
            SongReviewsScreen(
                songReview = LocalSongReviewProvider.songReview
            )
        }

        composable(route = Screen.Following.route) {
            FollowingScreen(
                following = LocalFollowingProvider.following,
                onCommentsClick = { navController.navigate(Screen.Discussion.route) }
            )
        }

        composable(route = Screen.Discussion.route) {
            DiscussionScreen(
                discussion = LocalDiscussionProvider.discussion,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
