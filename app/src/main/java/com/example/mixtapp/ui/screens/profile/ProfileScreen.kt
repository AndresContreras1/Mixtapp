package com.example.mixtapp.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.*
import com.example.mixtapp.ui.screens.profile.components.FavoriteSection
import com.example.mixtapp.ui.screens.profile.components.ProfileAvatarSection
import com.example.mixtapp.ui.screens.profile.components.ProfileHeader
import com.example.mixtapp.ui.screens.profile.components.RatingsSection
import com.example.mixtapp.ui.screens.profile.components.RecentActivitySection

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("Profile") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            ProfileHeader(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ProfileAvatarSection()

                FavoriteSection()

                RecentActivitySection()

                RatingsSection()

            }
            
        }
    }
}

@Composable
@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
fun ProfileScreenPreview() {
    MixtappTheme(dynamicColor = false) {
        ProfileScreen()
    }
}
