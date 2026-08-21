package com.example.mixtapp.ui.screens.myreviews.components

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.theme.CircleWine
import com.example.mixtapp.ui.theme.PalePink
import com.example.mixtapp.ui.theme.PrimaryPink

@Composable
fun MyReviewsHeader(
    username: String,
    joinDate: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(CircleWine.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = username.take(2).uppercase(),
                    color = PalePink,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = username,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "Join $joinDate",
                    color = PalePink.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "My reviews",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Serif
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}