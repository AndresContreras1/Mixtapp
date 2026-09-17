package com.example.mixtapp.ui.screens.search.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.displayFontFamily

@Composable
fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val circuloGrande = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    val circuloPequeno = MaterialTheme.colorScheme.secondary.copy(alpha = 0.22f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(325.dp)
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.74f))
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = circuloGrande,
                radius = 150.dp.toPx(),
                center = Offset(x = 20.dp.toPx(), y = 48.dp.toPx()),
            )
            drawCircle(
                color = circuloPequeno,
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
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 31.sp,
                lineHeight = 36.sp,
                fontFamily = displayFontFamily,
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
