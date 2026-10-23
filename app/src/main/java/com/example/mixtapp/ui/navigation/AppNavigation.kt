package com.example.mixtapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mixtapp.ui.screens.discussion.DiscussionScreen
import com.example.mixtapp.ui.screens.discussion.DiscussionViewModel
import com.example.mixtapp.ui.screens.editprofile.EditProfileScreen
import com.example.mixtapp.ui.screens.editprofile.EditProfileViewModel
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
import com.example.mixtapp.ui.screens.selectalbum.SelectAlbumScreen
import com.example.mixtapp.ui.screens.selectalbum.SelectAlbumViewModel
import com.example.mixtapp.ui.screens.signup.SignUpScreen
import com.example.mixtapp.ui.screens.signup.SignUpViewModel
import com.example.mixtapp.ui.screens.songreview.SongReviewsScreen
import com.example.mixtapp.ui.screens.songreview.SongReviewsViewModel
import com.example.mixtapp.ui.screens.splash.SplashScreen
import com.example.mixtapp.ui.screens.splash.SplashViewModel
import com.example.mixtapp.ui.screens.userprofile.UserProfileScreen
import com.example.mixtapp.ui.screens.userprofile.UserProfileViewModel

private fun NavHostController.navegarLimpiandoLaPila(ruta: String) {
    navigate(ruta) {
        popUpTo(0) { inclusive = true }
    }
}

// Abrir un album, el perfil de un usuario y escribir una resena se repite en varias pantallas
private fun NavHostController.abrirAlbum(songId: String) {
    navigate(Screen.SongDetail.createRoute(songId = songId))
}

private fun NavHostController.abrirPerfil(userId: String) {
    navigate(Screen.UserProfile.createRoute(userId = userId))
}

private fun NavHostController.abrirEscribirResena(albumId: String) {
    navigate(Screen.WriteReview.createRoute(albumId = albumId))
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

            LoginScreen(
                loginViewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navegarLimpiandoLaPila(Screen.Home.route)
                },
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(route = Screen.SignUp.route) {
            val signUpViewModel: SignUpViewModel = hiltViewModel()

            SignUpScreen(
                signUpViewModel = signUpViewModel,
                onSignUpSuccess = {
                    navController.navegarLimpiandoLaPila(Screen.Home.route)
                },
                // Vuelve al login que ya estaba en la pila, para que atras no repita pantallas
                onLoginClick = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()

            HomeScreen(
                homeViewModel = homeViewModel,
                onAlbumClick = { songId ->
                    navController.abrirAlbum(songId = songId)
                },
                onAuthorClick = { userId ->
                    navController.abrirPerfil(userId = userId)
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
                    navController.abrirAlbum(songId = songId)
                }
            )
        }

        composable(route = Screen.SelectAlbum.route) {
            val selectAlbumViewModel: SelectAlbumViewModel = hiltViewModel()

            SelectAlbumScreen(
                selectAlbumViewModel = selectAlbumViewModel,
                // Esta pantalla sale de la pila: al publicar se vuelve a donde se toco el +
                onAlbumClick = { albumId ->
                    navController.navigate(Screen.WriteReview.createRoute(albumId = albumId)) {
                        popUpTo(Screen.SelectAlbum.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
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
                // Al volver, la pantalla anterior pide sus datos de nuevo y la resena ya aparece
                onPublicada = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(route = Screen.MyReviews.route) {
            val myReviewsViewModel: MyReviewsViewModel = hiltViewModel()

            MyReviewsScreen(
                myReviewsViewModel = myReviewsViewModel,
                onReviewClick = { songId ->
                    navController.abrirAlbum(songId = songId)
                },
                onEditClick = { albumId ->
                    navController.abrirEscribirResena(albumId = albumId)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = hiltViewModel()

            ProfileScreen(
                profileViewModel = profileViewModel,
                onSettingsClick = { navController.navigate(Screen.EditProfile.route) },
                onMoreClick = {},
                logoutButtonPressed = {
                    navController.navegarLimpiandoLaPila(Screen.Login.route)
                },
                onReviewClick = { songId ->
                    navController.abrirAlbum(songId = songId)
                },
                onUserClick = { userId ->
                    navController.abrirPerfil(userId = userId)
                },
                onSeeAllReviewsClick = { navController.navigate(Screen.MyReviews.route) }
            )
        }

        composable(route = Screen.EditProfile.route) {
            val editProfileViewModel: EditProfileViewModel = hiltViewModel()

            EditProfileScreen(
                editProfileViewModel = editProfileViewModel,
                // El perfil vuelve a pedir sus datos al mostrarse, asi que ya sale con los cambios
                onGuardado = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SongDetail.route,
            arguments = listOf(navArgument(name = "songId") { type = NavType.StringType })
        ) {
            // Solo se obtiene el id; buscar el album es tarea del ViewModel
            val songId = it.arguments?.getString("songId") ?: ""
            val songReviewsViewModel: SongReviewsViewModel = hiltViewModel()

            SongReviewsScreen(
                songId = songId,
                songReviewsViewModel = songReviewsViewModel,
                onWriteReviewClick = {
                    navController.abrirEscribirResena(albumId = songId)
                },
                onBackClick = { navController.popBackStack() },
                onAuthorClick = { userId ->
                    navController.abrirPerfil(userId = userId)
                }
            )
        }

        composable(
            route = Screen.UserProfile.route,
            arguments = listOf(navArgument(name = "userId") { type = NavType.StringType })
        ) {
            val userId = it.arguments?.getString("userId") ?: ""
            val userProfileViewModel: UserProfileViewModel = hiltViewModel()

            UserProfileScreen(
                userId = userId,
                userProfileViewModel = userProfileViewModel,
                onReviewClick = { songId ->
                    navController.abrirAlbum(songId = songId)
                },
                onUserClick = { otherUserId ->
                    navController.abrirPerfil(userId = otherUserId)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Following.route) {
            val followingViewModel: FollowingViewModel = hiltViewModel()

            FollowingScreen(
                followingViewModel = followingViewModel
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
