package com.example.mixtapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.components.AppBackground
import com.example.mixtapp.ui.components.BottomNav
import com.example.mixtapp.ui.theme.*

@Composable
fun HomeScreen(
    onSelectTrack: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
    ) {
        AppBackground()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                HomeHeader(onOpenProfile = onOpenProfile)

                Spacer(modifier = Modifier.height(24.dp))

                FilterChips()

                Spacer(modifier = Modifier.height(28.dp))

                TrendingCard(onClick = onSelectTrack)

                Spacer(modifier = Modifier.height(28.dp))

                SectionTitle(title = stringResource(R.string.popular_albums))

                Spacer(modifier = Modifier.height(14.dp))

                PopularAlbums(onSelectTrack = onSelectTrack)

                Spacer(modifier = Modifier.height(28.dp))

                SectionTitle(title = stringResource(R.string.friends_activity))

                Spacer(modifier = Modifier.height(14.dp))

                FriendsActivity()
            }

            BottomNav()
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen(
        onSelectTrack = {},
        onOpenProfile = {}
    )
}
