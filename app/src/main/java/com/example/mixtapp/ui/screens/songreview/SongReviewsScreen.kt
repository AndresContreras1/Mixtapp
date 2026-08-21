package com.example.mixtapp.ui.screens.songreview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.components.AppBackground
import com.example.mixtapp.ui.components.BottomNav
import com.example.mixtapp.ui.screens.songreview.components.*
import com.example.mixtapp.ui.theme.DeepBackground
import com.example.mixtapp.ui.theme.TextPink

@Composable
fun SongReviewsScreen(
    modifier: Modifier = Modifier
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
                ReleaseImage()

                Spacer(modifier = Modifier.height(24.dp))

                ReleaseTags(tags = listOf("2006", "emo", "Rock Alternativo"))

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "The Sharpest Lives",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "My Chemical Romance",
                    color = TextPink.copy(alpha = 0.7f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                ReleaseStats(
                    rating = "4.5",
                    ratingCount = "28.4k",
                    recommendRate = "94%"
                )

                Spacer(modifier = Modifier.height(16.dp))

                UserRatingSection()

                Spacer(modifier = Modifier.height(24.dp))

                ActionButtons()

                Spacer(modifier = Modifier.height(32.dp))

                ReviewsSection()
            }

            BottomNav()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SongReviewsScreenPreview() {
    SongReviewsScreen()
}
