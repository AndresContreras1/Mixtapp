package com.example.mixtapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mixtapp.ui.screens.discussion.DiscussionScreen
import com.example.mixtapp.ui.screens.discussion.DiscussionViewModel
import com.example.mixtapp.ui.screens.following.FollowingScreen
import com.example.mixtapp.ui.screens.following.FollowingViewModel
import com.example.mixtapp.ui.screens.home.HomeScreen
import com.example.mixtapp.ui.screens.home.HomeViewModel
import com.example.mixtapp.ui.screens.login.LoginScreen
import com.example.mixtapp.ui.screens.login.LoginViewModel
import com.example.mixtapp.ui.screens.myreviews.MyReviewsScreen
import com.example.mixtapp.ui.screens.myreviews.MyReviewsViewModel
import com.example.mixtapp.ui.screens.notifications.NotificationsScreen
import com.example.mixtapp.ui.screens.notifications.NotificationsViewModel
import com.example.mixtapp.ui.screens.profile.ProfileScreen
import com.example.mixtapp.ui.screens.profile.ProfileViewModel
import com.example.mixtapp.ui.screens.review.WriteReviewScreen
import com.example.mixtapp.ui.screens.review.WriteReviewViewModel
import com.example.mixtapp.ui.screens.search.SearchScreen
import com.example.mixtapp.ui.screens.search.SearchViewModel
import com.example.mixtapp.ui.screens.signup.SignUpScreen
import com.example.mixtapp.ui.screens.signup.SignUpViewModel
import com.example.mixtapp.ui.screens.songreview.SongReviewsScreen
import com.example.mixtapp.ui.screens.songreview.SongReviewsViewModel
import com.example.mixtapp.ui.screens.splash.SplashScreen
import com.example.mixtapp.ui.screens.splash.SplashViewModel

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

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(route = Screen.Splash.route) {
            val splashViewModel: SplashViewModel = hiltViewModel()

            SplashScreen(
                splashViewModel = splashViewModel,
                navigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                navigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Login.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            val state by loginViewModel.uiState.collectAsState()

            // El ViewModel valida el formulario y autoriza; la navegacion solo ejecuta
            if (state.navigate) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(0) { inclusive = true }
                }
            }

            LoginScreen(
                loginViewModel = loginViewModel,
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(route = Screen.SignUp.route) {
            val signUpViewModel: SignUpViewModel = hiltViewModel()
            val state by signUpViewModel.uiState.collectAsState()

            // El ViewModel valida el formulario y autoriza; la navegacion solo ejecuta
            if (state.navigate) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(0) { inclusive = true }
                }
            }

            SignUpScreen(
                signUpViewModel = signUpViewModel,
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(route = Screen.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()

            HomeScreen(
                homeViewModel = homeViewModel,
                onAlbumClick = { songId ->
                    navController.navigate(Screen.SongDetail.createRoute(songId = songId))
                },
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onFollowingClick = { navController.navigate(Screen.Following.route) },
                onNotificationsClick = { navController.navigate(Screen.Notifications.route) }
            )
        }

        composable(route = Screen.Search.route) {
            val searchViewModel: SearchViewModel = hiltViewModel()

            SearchScreen(searchViewModel = searchViewModel)
        }

        composable(
            route = Screen.WriteReview.route,
            arguments = listOf(navArgument(name = "albumId") { type = NavType.StringType })
        ) {
            // Solo se obtiene el id; buscar el album es tarea del ViewModel
            val albumId = it.arguments?.getString("albumId") ?: ""
            val writeReviewViewModel: WriteReviewViewModel = hiltViewModel()

            WriteReviewScreen(
                albumId = albumId,
                writeReviewViewModel = writeReviewViewModel,
                onCancel = { navController.popBackStack() },
                onPostReview = { navController.navigate(Screen.MyReviews.route) }
            )
        }

        composable(route = Screen.MyReviews.route) {
            val myReviewsViewModel: MyReviewsViewModel = hiltViewModel()

            MyReviewsScreen(
                myReviewsViewModel = myReviewsViewModel,
                onReviewClick = { songId ->
                    navController.navigate(Screen.SongDetail.createRoute(songId = songId))
                }
            )
        }

        composable(route = Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = hiltViewModel()

            ProfileScreen(profileViewModel = profileViewModel)
        }

        composable(
            route = Screen.SongDetail.route,
            arguments = listOf(navArgument(name = "songId") { type = NavType.StringType })
        ) {
            // Solo se obtiene el id; buscar la cancion es tarea del ViewModel
            val songId = it.arguments?.getString("songId") ?: ""
            val songReviewsViewModel: SongReviewsViewModel = hiltViewModel()

            SongReviewsScreen(
                songId = songId,
                songReviewsViewModel = songReviewsViewModel
            )
        }

        composable(route = Screen.Following.route) {
            val followingViewModel: FollowingViewModel = hiltViewModel()

            FollowingScreen(
                followingViewModel = followingViewModel,
                onCommentsClick = { reviewId ->
                    navController.navigate(Screen.Discussion.createRoute(reviewId = reviewId))
                }
            )
        }

        composable(route = Screen.Notifications.route) {
            val notificationsViewModel: NotificationsViewModel = hiltViewModel()

            NotificationsScreen(
                notificationsViewModel = notificationsViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Discussion.route,
            arguments = listOf(navArgument(name = "reviewId") { type = NavType.StringType })
        ) {
            // Solo se obtiene el id; buscar la discusion es tarea del ViewModel
            val reviewId = it.arguments?.getString("reviewId") ?: ""
            val discussionViewModel: DiscussionViewModel = hiltViewModel()

            DiscussionScreen(
                reviewId = reviewId,
                discussionViewModel = discussionViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}