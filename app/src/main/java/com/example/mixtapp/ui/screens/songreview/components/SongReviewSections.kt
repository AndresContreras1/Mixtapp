package com.example.mixtapp.ui.screens.songreview.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.screens.songreview.model.SongReviewUi
import com.example.mixtapp.ui.theme.TextPink

@Composable
fun SongReviewSections(
    songReview: SongReviewUi,
    userRating: Int,
    isSaved: Boolean,
    isLiked: Boolean,
    onRatingChange: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
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
            onRatingChange = onRatingChange
        )

        Spacer(modifier = Modifier.height(24.dp))

        ActionButtons(
            isSaved = isSaved,
            isLiked = isLiked,
            onSaveClick = onSaveClick,
            onLikeClick = onLikeClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        ReviewsSection(reviews = songReview.reviews)
    }
}
