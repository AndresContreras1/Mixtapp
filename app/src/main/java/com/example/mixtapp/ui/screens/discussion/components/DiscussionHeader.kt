package com.example.mixtapp.ui.screens.discussion.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.displayFontFamily
import com.example.mixtapp.ui.utils.BackButton

@Composable
fun DiscussionHeader(
    commentsCount: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(52.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BackButton(onBackClick = onBackClick)

            Text(
                text = stringResource(R.string.discussion),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 25.sp,
                fontFamily = displayFontFamily,
                fontWeight = FontWeight.Black,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = if (commentsCount == 1) {
                    stringResource(R.string.un_comentario)
                } else {
                    stringResource(R.string.comments_count, commentsCount)
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                fontFamily = displayFontFamily,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
