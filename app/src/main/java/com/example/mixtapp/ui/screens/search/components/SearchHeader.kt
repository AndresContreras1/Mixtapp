package com.example.mixtapp.ui.screens.search.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.theme.CircleWine

@Composable
fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(325.dp)
            .background(Color.Black.copy(alpha = 0.74f))
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = CircleWine.copy(alpha = 0.5f),
                radius = 150.dp.toPx(),
                center = Offset(x = 20.dp.toPx(), y = 48.dp.toPx()),
            )
            drawCircle(
                color = CircleWine.copy(alpha = 0.22f),
                radius = 82.dp.toPx(),
                center = Offset(x = size.width * 0.55f, y = size.height + 74.dp.toPx()),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            SearchStatusBar(modifier = Modifier.padding(top = 20.dp))

            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Search",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color.White,
                fontSize = 31.sp,
                lineHeight = 36.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Black,
            )

            Spacer(modifier = Modifier.height(30.dp))

            SearchTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun SearchStatusBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "9:41",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SignalBars()
            BatteryIcon()
        }
    }
}

@Composable
private fun SignalBars(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.height(18.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        listOf(9.dp, 13.dp, 17.dp).forEach { barHeight ->
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = barHeight)
                    .background(Color.White.copy(alpha = 0.9f)),
            )
        }
    }
}

@Composable
private fun BatteryIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(width = 39.dp, height = 18.dp)) {
        val stroke = 2.6.dp.toPx()
        drawRoundRect(
            color = Color.White,
            topLeft = Offset.Zero,
            size = Size(
                width = 35.dp.toPx(),
                height = 15.dp.toPx(),
            ),
            cornerRadius = CornerRadius(5.dp.toPx()),
            style = Stroke(width = stroke),
        )
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(5.dp.toPx(), 4.dp.toPx()),
            size = Size(
                width = 24.dp.toPx(),
                height = 7.dp.toPx(),
            ),
            cornerRadius = CornerRadius(2.dp.toPx()),
        )
    }
}
