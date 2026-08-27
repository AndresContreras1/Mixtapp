package com.example.mixtapp.ui.screens.following.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.theme.FieldBorder
import com.example.mixtapp.ui.theme.PalePink
import com.example.mixtapp.ui.theme.TextPink

@Composable
fun FollowingHeader(
    followingCount: Int,
    followersCount: Int,
    friendQuery: String,
    onFriendQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 18.dp)
    ) {
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
            FollowingStatusIcons()
        }

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Following",
                    color = Color.White,
                    fontSize = 27.sp,
                    lineHeight = 32.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = "$followingCount following • $followersCount followers",
                    modifier = Modifier.padding(top = 9.dp),
                    color = PalePink.copy(alpha = 0.78f),
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                )
            }

            FriendSearchField(
                value = friendQuery,
                onValueChange = onFriendQueryChange,
            )
        }
    }
}

@Composable
private fun FriendSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .size(width = 106.dp, height = 36.dp)
            .border(1.dp, FieldBorder, RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp),
        singleLine = true,
        textStyle = TextStyle(
            color = PalePink,
            fontSize = 15.sp,
            lineHeight = 18.sp,
        ),
        cursorBrush = SolidColor(PalePink),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = "Find friends",
                        color = TextPink.copy(alpha = 0.64f),
                        fontSize = 15.sp,
                        lineHeight = 18.sp,
                    )
                }
                innerTextField()
            }
        },
    )
}

@Composable
private fun FollowingStatusIcons(modifier: Modifier = Modifier) {
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
                Canvas(modifier = Modifier.size(width = 4.dp, height = barHeight)) {
                    drawRoundRect(color = Color.White, cornerRadius = CornerRadius(2.dp.toPx()))
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
