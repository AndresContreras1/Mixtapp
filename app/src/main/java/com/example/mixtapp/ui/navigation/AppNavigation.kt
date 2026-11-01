package com.example.mixtapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.ui.screens.datoscuriosos.DatosCuriososScreen
import com.example.mixtapp.ui.screens.datoscuriosos.DatosCuriososViewModel
import com.example.mixtapp.ui.screens.detail.DetailScreen
import com.example.mixtapp.ui.screens.detail.DetailViewModel
import com.example.mixtapp.ui.screens.discussion.DiscussionScreen
import com.example.mixtapp.ui.screens.discussion.DiscussionViewModel
import com.example.mixtapp.ui.screens.editprofile.EditProfileScreen
import com.example.mixtapp.ui.screens.editprofile.EditProfileViewModel
import com.example.mixtapp.ui.screens.followlist.FollowListScreen
import com.example.mixtapp.ui.screens.followlist.FollowListViewModel
import com.example.mixtapp.ui.screens.followlist.LISTA_SEGUIDORES
import com.example.mixtapp.ui.screens.followlist.LISTA_SEGUIDOS
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
import com.example.mixtapp.ui.screens.selectitem.SelectItemScreen
import com.example.mixtapp.ui.screens.selectitem.SelectItemViewModel
import com.example.mixtapp.ui.screens.signup.SignUpScreen
import com.example.mixtapp.ui.screens.signup.SignUpViewModel
import com.example.mixtapp.ui.screens.splash.SplashScreen
import com.example.mixtapp.ui.screens.splash.SplashViewModel
import com.example.mixtapp.ui.screens.userprofile.UserProfileScreen
import com.example.mixtapp.ui.screens.userprofile.UserProfileViewModel

private fun NavHostController.navegarLimpiandoLaPila(ruta: String) {
    navigate(ruta) {
        popUpTo(0) { inclusive = true }
    }
}

// Abrir un item, el perfil de un usuario, escribir una resena y abrir la discusion se repite en
// varias pantallas
private fun NavHostController.abrirItem(tipo: String, itemId: String) {
    navigate(Screen.Detail.createRoute(tipo = tipo, itemId = itemId))
}

private fun NavHostController.abrirPerfil(userId: String) {
    navigate(Screen.UserProfile.createRoute(userId = userId))
}

private fun NavHostController.abrirEscribirResena(tipo: String, itemId: String) {
    navigate(Screen.WriteReview.createRoute(tipo = tipo, itemId = itemId))
}

private fun NavHostController.abrirLista(userId: String, tipo: String) {
    navigate(Screen.FollowList.createRoute(userId = userId, tipo = tipo))
}

// La discusion se abre desde cualquier tarjeta de resena: el detalle, el inicio, los perfiles,
// Mis resenas, Siguiendo y las notificaciones
private fun NavHostController.abrirDiscusion(reviewId: String) {
    navigate(Screen.Discussion.createRoute(reviewId = reviewId))
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
                onItemClick = { tipo, itemId ->
                    navController.abrirItem(tipo = tipo, itemId = itemId)
                },
                onAuthorClick = { userId ->
                    navController.abrirPerfil(userId = userId)
                },
                onCommentsClick = { reviewId ->
                    navController.abrirDiscusion(reviewId = reviewId)
                },
                onFollowingClick = { navController.navigate(Screen.Following.route) },
                onNotificationsClick = { navController.navigate(Screen.Notifications.route) }
            )
        }

        composable(route = Screen.Search.route) {
            val searchViewModel: SearchViewModel = hiltViewModel()

            SearchScreen(
                searchViewModel = searchViewModel,
                onItemClick = { tipo, itemId ->
                    navController.abrirItem(tipo = tipo, itemId = itemId)
                },
                onUserClick = { userId ->
                    navController.abrirPerfil(userId = userId)
                }
            )
        }

        composable(route = Screen.SelectItem.route) {
            val selectItemViewModel: SelectItemViewModel = hiltViewModel()

            SelectItemScreen(
                selectItemViewModel = selectItemViewModel,
                // Esta pantalla sale de la pila: al publicar se vuelve a donde se toco el +
                onItemClick = { tipo, itemId ->
                    navController.navigate(Screen.WriteReview.createRoute(tipo = tipo, itemId = itemId)) {
                        popUpTo(Screen.SelectItem.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.WriteReview.route,
            arguments = listOf(
                navArgument(name = "tipo") { type = NavType.StringType },
                navArgument(name = "itemId") { type = NavType.StringType }
            )
        ) {
            // Solo se obtienen el tipo y el id; buscar el item es tarea del ViewModel
            val tipo = it.arguments?.getString("tipo") ?: TIPO_ALBUM
            val itemId = it.arguments?.getString("itemId") ?: ""
            val writeReviewViewModel: WriteReviewViewModel = hiltViewModel()

            WriteReviewScreen(
                tipo = tipo,
                itemId = itemId,
                writeReviewViewModel = writeReviewViewModel,
                // Al volver, la pantalla anterior ya tiene la resena: le llega por su listener
                onPublicada = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(route = Screen.MyReviews.route) {
            val myReviewsViewModel: MyReviewsViewModel = hiltViewModel()

            MyReviewsScreen(
                myReviewsViewModel = myReviewsViewModel,
                onItemClick = { tipo, itemId ->
                    navController.abrirItem(tipo = tipo, itemId = itemId)
                },
                onCommentsClick = { reviewId ->
                    navController.abrirDiscusion(reviewId = reviewId)
                },
                onEditClick = { tipo, itemId ->
                    navController.abrirEscribirResena(tipo = tipo, itemId = itemId)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = hiltViewModel()

            ProfileScreen(
                profileViewModel = profileViewModel,
                onSettingsClick = { navController.navigate(Screen.EditProfile.route) },
                logoutButtonPressed = {
                    navController.navegarLimpiandoLaPila(Screen.Login.route)
                },
                onItemClick = { tipo, itemId ->
                    navController.abrirItem(tipo = tipo, itemId = itemId)
                },
                onCommentsClick = { reviewId ->
                    navController.abrirDiscusion(reviewId = reviewId)
                },
                onEditReviewClick = { tipo, itemId ->
                    navController.abrirEscribirResena(tipo = tipo, itemId = itemId)
                },
                // Mis seguidores y mis seguidos se abren desde los contadores del encabezado
                onFollowListClick = { userId, tipo ->
                    navController.abrirLista(userId = userId, tipo = tipo)
                },
                onSeeAllReviewsClick = { navController.navigate(Screen.MyReviews.route) }
            )
        }

        composable(route = Screen.EditProfile.route) {
            val editProfileViewModel: EditProfileViewModel = hiltViewModel()

            EditProfileScreen(
                editProfileViewModel = editProfileViewModel,
                // El perfil escucha su documento en vivo, asi que ya sale con los cambios
                onGuardado = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument(name = "tipo") { type = NavType.StringType },
                navArgument(name = "itemId") { type = NavType.StringType }
            )
        ) {
            // Solo se obtienen el tipo y el id; buscar el item es tarea del ViewModel
            val tipo = it.arguments?.getString("tipo") ?: TIPO_ALBUM
            val itemId = it.arguments?.getString("itemId") ?: ""
            val detailViewModel: DetailViewModel = hiltViewModel()

            DetailScreen(
                tipo = tipo,
                itemId = itemId,
                detailViewModel = detailViewModel,
                onRateClick = {
                    navController.abrirEscribirResena(tipo = tipo, itemId = itemId)
                },
                onDatosCuriososClick = {
                    navController.navigate(Screen.DatosCuriosos.createRoute(tipo = tipo, itemId = itemId))
                },
                onBackClick = { navController.popBackStack() },
                onAuthorClick = { userId ->
                    navController.abrirPerfil(userId = userId)
                },
                onCommentsClick = { reviewId ->
                    navController.abrirDiscusion(reviewId = reviewId)
                }
            )
        }

        composable(
            route = Screen.DatosCuriosos.route,
            arguments = listOf(
                navArgument(name = "tipo") { type = NavType.StringType },
                navArgument(name = "itemId") { type = NavType.StringType }
            )
        ) {
            val tipo = it.arguments?.getString("tipo") ?: TIPO_ALBUM
            val itemId = it.arguments?.getString("itemId") ?: ""
            val datosCuriososViewModel: DatosCuriososViewModel = hiltViewModel()

            DatosCuriososScreen(
                tipo = tipo,
                itemId = itemId,
                datosCuriososViewModel = datosCuriososViewModel,
                onBackClick = { navController.popBackStack() }
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
                onItemClick = { tipo, itemId ->
                    navController.abrirItem(tipo = tipo, itemId = itemId)
                },
                onCommentsClick = { reviewId ->
                    navController.abrirDiscusion(reviewId = reviewId)
                },
                onSeguidoresClick = { otherUserId ->
                    navController.abrirLista(userId = otherUserId, tipo = LISTA_SEGUIDORES)
                },
                onSeguidosClick = { otherUserId ->
                    navController.abrirLista(userId = otherUserId, tipo = LISTA_SEGUIDOS)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.FollowList.route,
            arguments = listOf(
                navArgument(name = "userId") { type = NavType.StringType },
                navArgument(name = "tipo") { type = NavType.StringType }
            )
        ) {
            // Solo se obtienen el id y el tipo; buscar la lista es tarea del ViewModel
            val userId = it.arguments?.getString("userId") ?: ""
            val tipo = it.arguments?.getString("tipo") ?: LISTA_SEGUIDORES
            val followListViewModel: FollowListViewModel = hiltViewModel()

            FollowListScreen(
                userId = userId,
                tipo = tipo,
                followListViewModel = followListViewModel,
                onUserClick = { otherUserId ->
                    navController.abrirPerfil(userId = otherUserId)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Following.route) {
            val followingViewModel: FollowingViewModel = hiltViewModel()

            FollowingScreen(
                followingViewModel = followingViewModel,
                onAuthorClick = { userId ->
                    navController.abrirPerfil(userId = userId)
                },
                onItemClick = { tipo, itemId ->
                    navController.abrirItem(tipo = tipo, itemId = itemId)
                },
                onCommentsClick = { reviewId ->
                    navController.abrirDiscusion(reviewId = reviewId)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Notifications.route) {
            val notificationsViewModel: NotificationsViewModel = hiltViewModel()

            NotificationsScreen(
                notificationsViewModel = notificationsViewModel,
                // Un like, un comentario o una discusion abren la resena; un seguidor, su perfil;
                // un lanzamiento, su detalle
                onReviewClick = { reviewId ->
                    navController.abrirDiscusion(reviewId = reviewId)
                },
                onUserClick = { userId ->
                    navController.abrirPerfil(userId = userId)
                },
                onItemClick = { tipo, itemId ->
                    navController.abrirItem(tipo = tipo, itemId = itemId)
                },
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
                onAuthorClick = { userId ->
                    navController.abrirPerfil(userId = userId)
                },
                onItemClick = { tipo, itemId ->
                    navController.abrirItem(tipo = tipo, itemId = itemId)
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
