package com.zchat.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ZLogo(
    size: Dp = 80.dp,
    tint: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.size(size)
    ) {
        val canvasSize = size.toPx()
        val center = Offset(canvasSize / 2f, canvasSize / 2f)
        val radius = canvasSize / 2f - 4.dp.toPx()
        val strokeWidth = canvasSize * 0.12f

        // Бubble shape (доира)
        drawRoundRect(
            color = tint,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius * 0.3f),
            style = Stroke(width = strokeWidth)
        )

        // Ҳарфи Z дар дохили доира
        drawLetterZ(
            color = tint,
            center = center,
            size = radius * 0.8f,
            strokeWidth = strokeWidth * 0.8f
        )
    }
}

private fun DrawScope.drawLetterZ(
    color: Color,
    center: Offset,
    size: Float,
    strokeWidth: Float
) {
    val startX = center.x - size / 2f
    val startY = center.y - size / 2f
    val endX = center.x + size / 2f
    val endY = center.y + size / 2f

    // Хати болоии Z
    drawLine(
        color = color,
        start = Offset(startX, startY),
        end = Offset(endX, startY),
        strokeWidth = strokeWidth,
        cap = androidx.compose.ui.graphics.StrokeCap.Round
    )

    // Хати диагоналии Z
    drawLine(
        color = color,
        start = Offset(endX, startY),
        end = Offset(startX, endY),
        strokeWidth = strokeWidth,
        cap = androidx.compose.ui.graphics.StrokeCap.Round
    )

    // Хати поёнии Z
    drawLine(
        color = color,
        start = Offset(startX, endY),
        end = Offset(endX, endY),
        strokeWidth = strokeWidth,
        cap = androidx.compose.ui.graphics.StrokeCap.Round
    )
}
