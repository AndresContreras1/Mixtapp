package com.example.mixtapp.ui.screens.search.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
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
            Spacer(modifier = Modifier.height(118.dp))

            Text(
                text = stringResource(R.string.search_title),
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
