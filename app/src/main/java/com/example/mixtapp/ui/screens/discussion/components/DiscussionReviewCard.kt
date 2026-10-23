package com.example.mixtapp.ui.screens.discussion.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.data.local.LocalDiscussionProvider
import com.example.mixtapp.data.model.DiscussionReviewUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ReviewActionsRow
import com.example.mixtapp.ui.utils.ReviewAlbumRow
import com.example.mixtapp.ui.utils.ReviewAuthorRow

@Composable
fun DiscussionReviewCard(
    review: DiscussionReviewUi,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.56f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.84f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            ReviewAuthorRow(
                reviewerName = review.reviewerName,
                avatarText = review.reviewerAvatarText,
                reviewedAt = review.reviewedAt,
                avatarSize = 40.dp,
                avatarBrush = SolidColor(MaterialTheme.colorScheme.secondary),
                avatarTextColor = MaterialTheme.colorScheme.primary,
                nameFontSize = 14.sp,
                dateFontSize = 11.sp,
            )

            Spacer(modifier = Modifier.height(14.dp))

            ReviewAlbumRow(
                cover = review.album.cover,
                albumTitle = review.album.title,
                artistName = review.album.artist,
                rating = review.rating,
                coverSize = 80.dp,
                coverCorner = 10.dp,
                starSize = 14.dp,
            )

            // Una resena que solo tiene calificacion no muestra la caja del texto
            if (review.reviewText.isNotEmpty()) {
                Text(
                    text = review.reviewText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(10.dp),
                        )
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.66f),
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            ReviewActionsRow(
                likes = review.likes,
                isLiked = review.isLiked,
                onLikeClick = onLikeClick,
                isShared = review.isShared,
                onShareClick = onShareClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 9.dp),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun DiscussionReviewCardPreview() {
    MixtappTheme(darkTheme = true) {
        DiscussionReviewCard(
            review = LocalDiscussionProvider.discussions.first().review,
            onLikeClick = {},
            onShareClick = {},
        )
    }
}
