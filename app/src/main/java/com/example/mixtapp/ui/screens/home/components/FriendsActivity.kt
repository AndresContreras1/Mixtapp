package com.example.mixtapp.ui.screens.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.model.FriendActivityUi
import com.example.mixtapp.ui.components.StarRating

@Composable
fun FriendsActivity(
    activity: FriendActivityUi,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            StarRating(
                rating = activity.rating,
                starCount = activity.rating,
                starSize = 18.dp,
                spacing = 0.dp,
                filledTint = MaterialTheme.colorScheme.onBackground,
                emptyTint = MaterialTheme.colorScheme.onBackground,
                emptyIcon = Icons.Filled.Star,
                onRatingChange = null
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.titulo_guion_subtitulo, activity.album.title, activity.album.artist),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.cita_entre_comillas, activity.quote),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                fontSize = 12.sp
            )
        }
    }
}
