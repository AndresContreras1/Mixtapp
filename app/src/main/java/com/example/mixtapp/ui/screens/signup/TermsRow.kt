package com.example.mixtapp.ui.screens.signup

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.PalePink

@Composable
private fun TermsRow(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(29.dp)
                .clip(RoundedCornerShape(5.dp))
                .border(
                    width = 2.2.dp,
                    color = PalePink,
                    shape = RoundedCornerShape(5.dp)
                )
                .clickable(onClick = onCheckedChange),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Canvas(modifier = Modifier.size(17.dp)) {
                    val stroke = Stroke(width = 3.4f, cap = StrokeCap.Round)
                    drawLine(
                        color = PalePink,
                        start = Offset(size.width * 0.08f, size.height * 0.55f),
                        end = Offset(size.width * 0.40f, size.height * 0.86f),
                        strokeWidth = stroke.width,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = PalePink,
                        start = Offset(size.width * 0.40f, size.height * 0.86f),
                        end = Offset(size.width * 0.94f, size.height * 0.16f),
                        strokeWidth = stroke.width,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(12.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White)) {
                    append(stringResource(R.string.agree_terms))
                }
                append(" ")
                withStyle(
                    SpanStyle(
                        color = Color(0xFFFF8CAA),
                        fontWeight = FontWeight.Black
                    )
                ) {
                    append(stringResource(R.string.privacy))
                }
            },
            fontSize = 15.5.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.weight(1f)
        )
    }
}