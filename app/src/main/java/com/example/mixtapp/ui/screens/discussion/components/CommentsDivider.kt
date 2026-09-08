package com.example.mixtapp.ui.screens.discussion.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.FieldBorder
import com.example.mixtapp.ui.theme.TextPink

@Composable
fun CommentsDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = FieldBorder.copy(alpha = 0.72f),
            thickness = 1.dp,
        )

        Text(
            text = stringResource(R.string.comments),
            color = TextPink.copy(alpha = 0.82f),
            fontSize = 15.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterVertically),
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = FieldBorder.copy(alpha = 0.72f),
            thickness = 1.dp,
        )
    }
}
