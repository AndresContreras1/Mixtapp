package com.example.mixtapp.ui.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.mixtapp.data.local.LocalMyReviewsProvider
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.ui.theme.MixtappTheme

// La fecha en que se escucho el album
@Composable
fun ReviewCardFooter(
    review: MyReviewUi,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = review.date,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            fontSize = 11.sp
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ReviewCardFooterPreview() {
    MixtappTheme(darkTheme = true) {
        ReviewCardFooter(review = LocalMyReviewsProvider.reviews.first())
    }
}
