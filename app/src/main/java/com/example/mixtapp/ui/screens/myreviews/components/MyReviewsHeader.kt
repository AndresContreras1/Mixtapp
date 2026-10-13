package com.example.mixtapp.ui.screens.myreviews.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalMyReviewsProvider
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.theme.displayFontFamily
import com.example.mixtapp.ui.utils.BackButton

@Composable
fun MyReviewsHeader(
    username: String,
    iniciales: String,
    reviewsCount: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        BackButton(onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciales,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = displayFontFamily
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = username,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = displayFontFamily
                )
                Text(
                    text = if (reviewsCount == 1) {
                        stringResource(R.string.una_resena)
                    } else {
                        stringResource(R.string.n_resenas, reviewsCount)
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.my_reviews),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            fontFamily = displayFontFamily
        )
    }
}

@Composable
@Preview(showBackground = true)
fun MyReviewsHeaderPreview() {
    MixtappTheme(darkTheme = true) {
        MyReviewsHeader(
            username = "andres",
            iniciales = "AN",
            reviewsCount = LocalMyReviewsProvider.reviews.size,
            onBackClick = {},
        )
    }
}
