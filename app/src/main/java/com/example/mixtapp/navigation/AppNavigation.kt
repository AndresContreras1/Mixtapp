package com.example.mixtapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

private fun NavHostController.navegarLimpiandoLaPila(ruta: String) {
    navigate(ruta) {
        popUpTo(0) { inclusive = true }
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
                    navController.navegarLimpiandoLaPila(Screen.Home.route)
                },
                navigateToLogin = {
                    navController.navegarLimpiandoLaPila(Screen.Login.route)
                }
            )
        }

        composable(route = Screen.Login.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            val state by loginViewModel.uiState.collectAsState()

            // El ViewModel valida el formulario y autoriza; la navegacion solo ejecuta
            LaunchedEffect(state.navigate) {
                if (state.navigate) {
                    navController.navegarLimpiandoLaPila(Screen.Home.route)
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
            LaunchedEffect(state.navigate) {
                if (state.navigate) {
                    navController.navegarLimpiandoLaPila(Screen.Home.route)
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

            SearchScreen(
                searchViewModel = searchViewModel,
                onAlbumClick = { songId ->
                    navController.navigate(Screen.SongDetail.createRoute(songId = songId))
                }
            )
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
            val state by profileViewModel.uiState.collectAsState()

            // El ViewModel cierra la sesion y autoriza; la navegacion solo ejecuta
            LaunchedEffect(state.sesionCerrada) {
                if (state.sesionCerrada) {
                    navController.navegarLimpiandoLaPila(Screen.Login.route)
                }
            }

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
                songReviewsViewModel = songReviewsViewModel,
                onWriteReviewClick = {
                    navController.navigate(Screen.WriteReview.createRoute(albumId = songId))
                },
                onBackClick = { navController.popBackStack() }
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