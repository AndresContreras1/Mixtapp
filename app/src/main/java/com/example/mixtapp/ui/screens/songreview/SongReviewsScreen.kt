package com.example.mixtapp.ui.screens.songreview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.data.local.LocalSongReviewProvider
import com.example.mixtapp.data.local.SongReviewUi
import com.example.mixtapp.ui.components.AppBackground
import com.example.mixtapp.ui.screens.songreview.components.*
import com.example.mixtapp.ui.theme.DeepBackground
import com.example.mixtapp.ui.theme.TextPink

@Composable
fun SongReviewsScreen(
    songReview: SongReviewUi,
    modifier: Modifier = Modifier
) {
    var userRating by rememberSaveable(songReview.title) { mutableStateOf(songReview.userRating) }
    var isSaved by rememberSaveable(songReview.title) { mutableStateOf(songReview.isSaved) }
    var isLiked by rememberSaveable(songReview.title) { mutableStateOf(songReview.isLiked) }

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
                ReleaseImage(coverRes = songReview.coverRes)

                Spacer(modifier = Modifier.height(24.dp))

                ReleaseTags(tags = songReview.tags)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = songReview.title,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = songReview.artist,
                    color = TextPink.copy(alpha = 0.7f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                ReleaseStats(
                    rating = songReview.rating,
                    ratingCount = songReview.ratingCount,
                    recommendRate = songReview.recommendRate
                )

                Spacer(modifier = Modifier.height(16.dp))

                UserRatingSection(
                    rating = userRating,
                    onRatingChange = { userRating = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                ActionButtons(
                    isSaved = isSaved,
                    isLiked = isLiked,
                    onSaveClick = { isSaved = !isSaved },
                    onLikeClick = { isLiked = !isLiked }
                )

                Spacer(modifier = Modifier.height(32.dp))

                ReviewsSection(reviews = songReview.reviews)
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SongReviewsScreenPreview() {
    SongReviewsScreen(songReview = LocalSongReviewProvider.songReview)
}
