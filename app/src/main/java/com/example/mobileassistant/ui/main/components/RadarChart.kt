package com.example.mobileassistant.ui.main.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.mobileassistant.ui.radar.RadarData
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarChart(
    data: RadarData,
    modifier: Modifier = Modifier
) {
    if (data.points.isEmpty()) {
        Text("Нет данных")
        return
    }

    Canvas(modifier = modifier) {

        val count = data.points.size
        val angleStep = 360f / count

        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 * 0.8f

        data.points.forEachIndexed { index, point ->
            val angle = Math.toRadians(
                (angleStep * index - 90).toDouble()
            )

            val valueRadius = radius * (point.value / 100f)

            val x = center.x + valueRadius * cos(angle).toFloat()
            val y = center.y + valueRadius * sin(angle).toFloat()

            drawLine(
                color = Color.Gray,
                start = center,
                end = Offset(
                    center.x + radius * cos(angle).toFloat(),
                    center.y + radius * sin(angle).toFloat()
                )
            )

            drawCircle(
                color = Color.White,
                radius = 6f,
                center = Offset(x, y)
            )
        }
    }
}
