package com.example.mixtapp.ui.screens.review.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.model.MAX_REVIEW_LENGTH
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppTextArea

@Composable
fun ReviewFormSection(
    reviewText: String,
    maxLength: Int,
    onReviewChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.label_resena),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 18.sp,
            letterSpacing = 6.sp,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(10.dp))

        AppTextArea(
            value = reviewText,
            onValueChange = onReviewChange,
            label = stringResource(R.string.label_resena),
            placeholder = stringResource(R.string.review_placeholder),
            maxLength = maxLength
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ReviewFormSectionPreview() {
    MixtappTheme(darkTheme = true) {
        ReviewFormSection(
            reviewText = "",
            maxLength = MAX_REVIEW_LENGTH,
            onReviewChange = {},
        )
    }
}
