package com.example.mixtapp.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalFriendActivityProvider
import com.example.mixtapp.data.local.LocalAlbumReviewsProvider
import com.example.mixtapp.data.model.FriendActivityUi
import com.example.mixtapp.data.model.AlbumReviewsUi
import com.example.mixtapp.ui.screens.home.components.HomeSections
import com.example.mixtapp.ui.screens.home.model.HomeFilterUi
import com.example.mixtapp.ui.screens.home.model.homeFilters
import com.example.mixtapp.ui.theme.*
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.LoadingBox

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onAlbumClick: (String) -> Unit,
    onAuthorClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by homeViewModel.uiState.collectAsState()

    // Los albumes y los usuarios que sigo llegan en vivo desde el ViewModel. Al mostrarse solo se
    // pide mi foto, que puede haber cambiado en otra pantalla
    LaunchedEffect(Unit) {
        homeViewModel.refrescarFotoDePerfil()
    }

    when {
        state.isLoading -> {
            LoadingBox(modifier = modifier)
        }
        // Sin albumes no hay nada que mostrar: solo el motivo del error
        state.errorMessageRes != null && state.todos.isEmpty() -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(state.errorMessageRes ?: R.string.error_cargar_contenido))
            }
        }
        else -> HomeScreenContent(
            albums = state.albums,
            todos = state.todos,
            trending = state.trending,
            resenasDeSeguidos = state.resenasDeSeguidos,
            filters = state.filters,
            selectedFilterId = state.selectedFilterId,
            soloSeguidos = state.soloSeguidos,
            profileImageUrl = state.profileImageUrl,
            errorMessageRes = state.errorMessageRes ?: state.errorSeguidosRes,
            onFilterSelected = { homeViewModel.updateSelectedFilter(filtroId = it) },
            onAlbumClick = onAlbumClick,
            onAuthorClick = onAuthorClick,
            onCommentsClick = onCommentsClick,
            onSearchClick = onSearchClick,
            onProfileClick = onProfileClick,
            onFollowingClick = onFollowingClick,
            onNotificationsClick = onNotificationsClick,
            modifier = modifier
        )
    }
}

@Composable
fun HomeScreenContent(
    albums: List<AlbumReviewsUi>,
    todos: List<AlbumReviewsUi>,
    trending: AlbumReviewsUi?,
    resenasDeSeguidos: List<FriendActivityUi>,
    filters: List<HomeFilterUi>,
    selectedFilterId: String,
    soloSeguidos: Boolean,
    profileImageUrl: String,
    errorMessageRes: Int?,
    onFilterSelected: (String) -> Unit,
    onAlbumClick: (String) -> Unit,
    onAuthorClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        HomeSections(
            albums = albums,
            todos = todos,
            trending = trending,
            resenasDeSeguidos = resenasDeSeguidos,
            filters = filters,
            selectedFilterId = selectedFilterId,
            soloSeguidos = soloSeguidos,
            profileImageUrl = profileImageUrl,
            errorMessageRes = errorMessageRes,
            onFilterSelected = onFilterSelected,
            onAlbumClick = onAlbumClick,
            onAuthorClick = onAuthorClick,
            onCommentsClick = onCommentsClick,
            onSearchClick = onSearchClick,
            onProfileClick = onProfileClick,
            onFollowingClick = onFollowingClick,
            onNotificationsClick = onNotificationsClick,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    MixtappTheme(darkTheme = true) {
        HomeScreenContent(
            albums = LocalAlbumReviewsProvider.popularAlbums,
            todos = LocalAlbumReviewsProvider.albumReviews,
            trending = LocalAlbumReviewsProvider.trendingAlbum,
            resenasDeSeguidos = LocalFriendActivityProvider.friendActivities,
            filters = homeFilters,
            selectedFilterId = homeFilters.first().id,
            soloSeguidos = false,
            profileImageUrl = "",
            errorMessageRes = null,
            onFilterSelected = {},
            onAlbumClick = {},
            onAuthorClick = {},
            onCommentsClick = {},
            onSearchClick = {},
            onProfileClick = {},
            onFollowingClick = {},
            onNotificationsClick = {}
        )
    }
}
