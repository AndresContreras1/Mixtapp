package com.example.mixtapp.ui.screens.review.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R

@Composable
fun ReviewFormSection(
    reviewText: String,
    maxLength: Int,
    onReviewChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.review_section_title),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = reviewText,
            onValueChange = onReviewChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(126.dp),
            placeholder = { Text(text = stringResource(R.string.review_placeholder)) }
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.contador_caracteres, reviewText.length, maxLength),
            modifier = Modifier.align(Alignment.End),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
