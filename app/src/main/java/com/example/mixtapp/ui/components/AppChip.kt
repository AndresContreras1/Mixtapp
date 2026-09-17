package com.example.mixtapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun AppChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    unselectedContainerColor: Color,
    unselectedBorderColor: Color?,
    unselectedTextColor: Color,
    verticalPadding: Dp,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .then(
                when {
                    isSelected -> Modifier.background(MaterialTheme.colorScheme.primary)
                    unselectedBorderColor != null ->
                        Modifier.border(BorderStroke(1.dp, unselectedBorderColor), shape)

                    else -> Modifier.background(unselectedContainerColor)
                }
            )
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = verticalPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (isSelected) MaterialTheme.colorScheme.onSurface else unselectedTextColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}


@Composable
@Preview
fun AppChipPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AppChip(
                text = "Para ti",
                isSelected = true,
                onClick = {},
                unselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unselectedBorderColor = null,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                verticalPadding = 12.dp
            )
            AppChip(
                text = "Tendencias",
                isSelected = false,
                onClick = {},
                unselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unselectedBorderColor = null,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                verticalPadding = 12.dp
            )
        }
    }
}
