package com.example.mixtapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.theme.FieldBackground
import com.example.mixtapp.ui.theme.FieldBorder
import com.example.mixtapp.ui.theme.PalePink
import com.example.mixtapp.ui.theme.TextPink

@Composable
fun FieldIconView(
    icon: FieldIcon,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val color = PalePink
        val strokeWidth = 2.6f
        when (icon) {
            FieldIcon.User -> {
                drawCircle(
                    color = color,
                    radius = size.minDimension * 0.19f,
                    center = Offset(size.width * 0.5f, size.height * 0.30f),
                    style = Stroke(width = strokeWidth)
                )
                drawArc(
                    color = color,
                    startAngle = 205f,
                    sweepAngle = 130f,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.12f, size.height * 0.50f),
                    size = Size(size.width * 0.76f, size.height * 0.58f),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            FieldIcon.Email -> {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(size.width * 0.08f, size.height * 0.20f),
                    size = Size(size.width * 0.84f, size.height * 0.60f),
                    cornerRadius = CornerRadius(1.5.dp.toPx()),
                    style = Stroke(width = strokeWidth)
                )
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.10f, size.height * 0.25f),
                    end = Offset(size.width * 0.50f, size.height * 0.56f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.90f, size.height * 0.25f),
                    end = Offset(size.width * 0.50f, size.height * 0.56f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }

            FieldIcon.Lock -> {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(size.width * 0.25f, size.height * 0.43f),
                    size = Size(size.width * 0.50f, size.height * 0.39f),
                    cornerRadius = CornerRadius(2.dp.toPx()),
                    style = Stroke(width = strokeWidth)
                )
                val shackle = Path().apply {
                    moveTo(size.width * 0.32f, size.height * 0.45f)
                    cubicTo(
                        size.width * 0.32f,
                        size.height * 0.19f,
                        size.width * 0.68f,
                        size.height * 0.19f,
                        size.width * 0.68f,
                        size.height * 0.45f
                    )
                }
                drawPath(
                    path = shackle,
                    color = color,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                drawCircle(
                    color = color,
                    radius = size.minDimension * 0.045f,
                    center = Offset(size.width * 0.50f, size.height * 0.61f)
                )
            }
        }
    }
}

@Composable
fun AppTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: FieldIcon,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(73.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(FieldBackground.copy(alpha = 0.91f))
                .border(
                    width = 1.2.dp,
                    color = FieldBorder,
                    shape = RoundedCornerShape(13.dp)
                )
                .padding(horizontal = 30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FieldIconView(
                icon = icon,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.size(24.dp))

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = TextStyle(
                    color = PalePink,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Normal
                ),
                cursorBrush = SolidColor(PalePink),
                keyboardOptions = keyboardOptions,
                visualTransformation = visualTransformation,
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = TextPink.copy(alpha = 0.88f),
                                fontSize = 20.sp,
                                lineHeight = 24.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}