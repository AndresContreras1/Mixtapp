package com.example.mixtapp.ui.screens.profile

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalProfileProvider
import com.example.mixtapp.data.model.ProfileUi
import com.example.mixtapp.ui.screens.profile.components.FavoriteSection
import com.example.mixtapp.ui.screens.profile.components.LogoutButton
import com.example.mixtapp.ui.screens.profile.components.ProfileAvatarSection
import com.example.mixtapp.ui.screens.profile.components.ProfileHeader
import com.example.mixtapp.ui.screens.profile.components.RatingsSection
import com.example.mixtapp.ui.screens.profile.components.RecentActivitySection
import com.example.mixtapp.ui.screens.profile.model.ProfileTabUi
import com.example.mixtapp.ui.screens.profile.model.profileTabs
import com.example.mixtapp.ui.theme.*

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    onSettingsClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by profileViewModel.uiState.collectAsState()

    if (state.profile == null) {
        Text(text = stringResource(R.string.perfil_no_encontrado))
    } else {
        ProfileScreenContent(
            profile = state.profile!!,
            usuario = state.usuario,
            profileImageUrl = state.profileImageUrl,
            subiendoImagen = state.subiendoImagen,
            errorImagenRes = state.errorImagenRes,
            onImagePicked = { profileViewModel.subirFotoDePerfil(uri = it) },
            tabs = state.tabs,
            selectedTabId = state.selectedTabId,
            onTabSelected = { profileViewModel.updateSelectedTab(tabId = it) },
            onSettingsClick = onSettingsClick,
            onMoreClick = onMoreClick,
            onLogoutClick = { profileViewModel.cerrarSesion() },
            modifier = modifier
        )
    }
}

@Composable
fun ProfileScreenContent(
    profile: ProfileUi,
    usuario: String,
    profileImageUrl: String,
    subiendoImagen: Boolean,
    errorImagenRes: Int?,
    onImagePicked: (Uri) -> Unit,
    tabs: List<ProfileTabUi>,
    selectedTabId: String,
    onTabSelected: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onMoreClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            ProfileHeader(
                usuario = usuario,
                tabs = tabs,
                selectedTabId = selectedTabId,
                onTabSelected = onTabSelected,
                onSettingsClick = onSettingsClick,
                onMoreClick = onMoreClick
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ProfileAvatarSection(
                    profileImageUrl = profileImageUrl,
                    subiendoImagen = subiendoImagen,
                    errorImagenRes = errorImagenRes,
                    onImagePicked = onImagePicked,
                    reviewsCount = profile.reviewsCount,
                    albumsCount = profile.albumsCount,
                    listsCount = profile.listsCount
                )

                FavoriteSection(favoritesCount = profile.favoritesCount)

                RecentActivitySection(activity = profile.recentActivity)

                RatingsSection(ratingBars = profile.ratingBars)

                LogoutButton(onLogoutClick = onLogoutClick)
            }
        }
    }
}

@Composable
@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
fun ProfileScreenPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        ProfileScreenContent(
            profile = LocalProfileProvider.profile,
            usuario = "usuario",
            profileImageUrl = "",
            subiendoImagen = false,
            errorImagenRes = null,
            onImagePicked = {},
            tabs = profileTabs,
            selectedTabId = profileTabs.first().id,
            onTabSelected = {},
            onSettingsClick = {},
            onMoreClick = {},
            onLogoutClick = {}
        )
    }
}
