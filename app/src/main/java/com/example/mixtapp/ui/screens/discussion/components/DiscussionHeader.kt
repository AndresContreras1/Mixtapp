package com.example.mixtapp.ui.screens.discussion.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.PalePink
import com.example.mixtapp.ui.theme.TextPink

@Composable
fun DiscussionHeader(
    commentsCount: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "9:41",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )

            StatusIcons()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.back),
                tint = TextPink.copy(alpha = 0.72f),
                modifier = Modifier
                    .size(30.dp)
                    .clickable { onBackClick() },
            )

            Text(
                text = stringResource(R.string.discussion),
                color = Color.White,
                fontSize = 25.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Black,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.comments_count, commentsCount),
                color = PalePink,
                fontSize = 13.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun StatusIcons(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            listOf(9.dp, 13.dp, 17.dp).forEach { barHeight ->
                Box(
                    modifier = Modifier
                        .size(width = 4.dp, height = barHeight)
                        .then(Modifier),
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawRoundRect(color = Color.White)
                    }
                }
            }
        }

        Canvas(modifier = Modifier.size(width = 39.dp, height = 18.dp)) {
            drawRoundRect(
                color = Color.White,
                topLeft = Offset.Zero,
                size = Size(35.dp.toPx(), 15.dp.toPx()),
                cornerRadius = CornerRadius(5.dp.toPx()),
                style = Stroke(width = 2.6.dp.toPx()),
            )
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(5.dp.toPx(), 4.dp.toPx()),
                size = Size(24.dp.toPx(), 7.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx()),
            )
        }
    }
}
