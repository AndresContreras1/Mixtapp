package com.example.mixtapp.ui.screens.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(325.dp)
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.74f))
    ) {
        // Los circulos se hacen como en AppBackground: un Box redondo con fondo
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-130).dp, y = (-102).dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
        )

        Box(
            modifier = Modifier
                .size(164.dp)
                .align(Alignment.BottomCenter)
                .offset(x = 20.dp, y = 156.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.22f))
        )

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
