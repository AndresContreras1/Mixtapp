package com.example.mixtapp.ui.screens.review.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalAlbumProvider
import com.example.mixtapp.data.model.Album
import com.example.mixtapp.data.model.MAX_REVIEW_LENGTH
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun WriteReviewList(
    album: Album,
    rating: Int,
    reviewText: String,
    listenedDate: String,
    publicando: Boolean,
    errorMessageRes: Int?,
    onRatingChange: (Int) -> Unit,
    onReviewChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = 24.dp,
            top = 22.dp,
            end = 24.dp,
            bottom = 22.dp,
        )
    ) {
        item {
            AlbumReviewCard(album = album)
        }

        item {
            ReviewRatingCard(
                rating = rating,
                onRatingChange = onRatingChange,
                modifier = Modifier.padding(top = 22.dp)
            )
        }

        item {
            ReviewFormSection(
                reviewText = reviewText,
                maxLength = MAX_REVIEW_LENGTH,
                onReviewChange = onReviewChange,
                modifier = Modifier.padding(top = 22.dp)
            )
        }

        item {
            ReviewActionsCard(
                listenedDate = listenedDate,
                publicando = publicando,
                errorMessageRes = errorMessageRes,
                onDateChange = onDateChange,
                onPostClick = onPostClick,
                modifier = Modifier.padding(top = 22.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
fun WriteReviewListPreview() {
    MixtappTheme(darkTheme = true) {
        WriteReviewList(
            album = LocalAlbumProvider.toxicity,
            rating = 4,
            reviewText = "",
            listenedDate = "",
            publicando = false,
            errorMessageRes = null,
            onRatingChange = {},
            onReviewChange = {},
            onDateChange = {},
            onPostClick = {},
        )
    }
}
