package com.example.mixtapp.ui.screens.review.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.FieldBorder
import com.example.mixtapp.ui.theme.PalePink

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
            color = PalePink,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(126.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF171314))
                .border(BorderStroke(1.dp, FieldBorder), RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            BasicTextField(
                value = reviewText,
                onValueChange = onReviewChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp),
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 17.sp
                ),
                decorationBox = { innerTextField ->
                    if (reviewText.isBlank()) {
                        Text(
                            text = stringResource(R.string.review_placeholder),
                            color = PalePink.copy(alpha = 0.68f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                    innerTextField()
                }
            )

            Text(
                text = stringResource(R.string.contador_caracteres, reviewText.length, maxLength),
                color = PalePink.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}
