package com.example.mixtapp.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mixtapp.R
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
    object SongDetail : Screen(route = "songDetail") {
        fun createRoute(songId: String) = "songDetail/$songId"
    }
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
                albums = LocalSongReviewProvider.popularSongs,
                trending = LocalSongReviewProvider.trendingSong,
                onAlbumClick = { songId ->
                    navController.navigate(Screen.SongDetail.createRoute(songId = songId))
                },
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
                onReviewClick = { songId ->
                    navController.navigate(Screen.SongDetail.createRoute(songId = songId))
                }
            )
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen()
        }

        composable(
            route = "songDetail/{songId}",
            arguments = listOf(navArgument(name = "songId") { type = NavType.StringType })
        ) {
            // Obtener los parametros
            val songId = it.arguments?.getString("songId") ?: ""

            // Buscar la cancion
            val song = LocalSongReviewProvider.songs.find { cancion -> cancion.id == songId }

            if (song == null) {
                Text(text = stringResource(R.string.cancion_no_encontrada))
            } else {
                SongReviewsScreen(songReview = song)
            }
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
