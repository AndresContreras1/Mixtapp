package com.example.mixtapp.ui.screens.myreviews.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.data.model.MyReviewUi

@Composable
fun MyReviewCard(
    review: MyReviewUi,
    onReviewClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)), RoundedCornerShape(14.dp))
            .clickable { onReviewClick(review.album.id) }
            .padding(16.dp)
    ) {
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
