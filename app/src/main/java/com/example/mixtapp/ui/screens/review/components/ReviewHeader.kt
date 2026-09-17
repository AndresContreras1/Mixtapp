package com.example.mixtapp.ui.screens.review.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
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

@Composable
fun ReviewHeader(
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(46.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.cancel),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.64f),
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { onCancel() }
            )

            Text(
                text = stringResource(R.string.write_review),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                fontFamily = displayFontFamily
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.72f), thickness = 1.dp)
    }
}
