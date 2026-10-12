package com.example.mixtapp.ui.screens.songreview.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun ReviewsHeader(
    // Solo hay una resena por album: si ya existe, el enlace la edita
    tieneResena: Boolean,
    onWriteReviewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.reviews),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 24.sp,
                fontFamily = displayFontFamily,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onWriteReviewClick, contentPadding = PaddingValues(0.dp)) {
                Text(
                    text = stringResource(if (tieneResena) R.string.editar_la_tuya else R.string.write_one),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
