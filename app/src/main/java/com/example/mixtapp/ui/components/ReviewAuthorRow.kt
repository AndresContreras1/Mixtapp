package com.example.mixtapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.PalePink
import com.example.mixtapp.ui.theme.TextPink

@Composable
fun ReviewAuthorRow(
    reviewerName: String,
    avatarText: String,
    reviewedAt: String,
    avatarSize: Dp,
    avatarBrush: Brush,
    avatarTextColor: Color,
    nameFontSize: TextUnit,
    dateFontSize: TextUnit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .background(avatarBrush),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = avatarText,
                color = avatarTextColor,
                fontSize = nameFontSize,
                fontWeight = FontWeight.Black,
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = reviewerName,
            color = PalePink,
            fontSize = nameFontSize,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.reviewed),
            color = TextPink.copy(alpha = 0.56f),
            fontSize = nameFontSize,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = reviewedAt,
            color = TextPink.copy(alpha = 0.58f),
            fontSize = dateFontSize,
            fontWeight = FontWeight.Bold,
        )
    }
}
