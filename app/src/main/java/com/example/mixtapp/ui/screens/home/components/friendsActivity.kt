package com.example.mixtapp.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.theme.FieldBackground
import com.example.mixtapp.ui.theme.FieldBorder
import com.example.mixtapp.ui.theme.PalePink
import com.example.mixtapp.ui.theme.TextPink

@Composable
fun FriendsActivity(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(FieldBackground.copy(alpha = 0.45f))
            .border(1.dp, FieldBorder.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row {
            repeat(5) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = TextPink,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Random Access Memories - Daft Punk",
            color = PalePink,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "\"A masterpiece from start to finish.\"",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 12.sp
        )
    }
}