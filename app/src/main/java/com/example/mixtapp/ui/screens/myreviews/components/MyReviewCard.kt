package com.example.mixtapp.ui.screens.myreviews.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.data.model.MyReviewUi

@Composable
fun MyReviewCard(
    review: MyReviewUi,
    onReviewClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onReviewClick(review.album.id) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            ReviewRatingBadge(
                rating = review.rating,
                modifier = Modifier.align(Alignment.TopEnd)
            )

            Column(modifier = Modifier.padding(end = 44.dp)) {
                ReviewCardHeader(review = review)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = review.excerpt,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(12.dp))

                ReviewCardFooter(review = review)
            }
        }
    }
}
