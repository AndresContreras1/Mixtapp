package com.example.mixtapp.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalProfileProvider
import com.example.mixtapp.ui.screens.profile.components.FavoriteSection
import com.example.mixtapp.ui.screens.profile.components.LogoutButton
import com.example.mixtapp.ui.screens.profile.components.ProfileAvatarSection
import com.example.mixtapp.ui.screens.profile.components.ProfileHeader
import com.example.mixtapp.ui.screens.profile.components.RatingsSection
import com.example.mixtapp.ui.screens.profile.components.RecentActivitySection
import com.example.mixtapp.ui.screens.profile.model.ProfileUi
import com.example.mixtapp.ui.theme.*

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    val state by profileViewModel.uiState.collectAsState()

    if (state.profile != null) {
        ProfileScreenContent(
            profile = state.profile!!,
            usuario = state.usuario,
            tabs = state.tabs,
            selectedTab = state.selectedTab,
            onTabSelected = { profileViewModel.updateSelectedTab(tab = it) },
            onLogoutClick = { profileViewModel.cerrarSesion() },
            modifier = modifier
        )
    }
}

@Composable
fun ProfileScreenContent(
    profile: ProfileUi,
    usuario: String,
    tabs: List<String>,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            ProfileHeader(
                usuario = usuario,
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
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
    MixtappTheme(dynamicColor = false) {
        ProfileScreenContent(
            profile = LocalProfileProvider.profile,
            usuario = "usuario",
            tabs = profileTabs,
            selectedTab = profileTabs.first(),
            onTabSelected = {},
            onLogoutClick = {}
        )
    }
}
