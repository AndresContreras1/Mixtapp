package com.example.mixtapp.ui.screens.review.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.components.ErrorMessage

@Composable
fun ReviewActionsCard(
    listenedDate: String,
    isFavorite: Boolean,
    errorMessageRes: Int?,
    onDateChange: (String) -> Unit,
    onDatePickerClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                DateListenedRow(
                    listenedDate = listenedDate,
                    onDateChange = onDateChange,
                    onDatePickerClick = onDatePickerClick
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.42f))
                Spacer(modifier = Modifier.height(12.dp))

                FavoriteRow(
                    isFavorite = isFavorite,
                    onFavoriteClick = onFavoriteClick
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        PostButton(
            onClick = onPostClick
        )

        ErrorMessage(
            messageRes = errorMessageRes,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}
