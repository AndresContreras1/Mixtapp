package com.example.mixtapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun FieldIconView(
    icon: FieldIcon,
    modifier: Modifier = Modifier
) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(modifier = modifier) {
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
