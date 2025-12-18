package com.example.mobileassistant.ui.main.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobileassistant.domain.model.RadarPointUi
import com.example.mobileassistant.domain.model.RadarUi
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarChart(
    radarData: RadarUi,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок - название цели
        Text(
            text = radarData.centerText,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Общий прогресс
        Text(
            text = "Общий прогресс: ${radarData.totalProgress}%",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Контейнер для радар-диаграммы
        Box(
            modifier = Modifier
                .size(250.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            RadarChartCanvas(radarData = radarData)
        }
    }
}

@Composable
fun RadarChartCanvas(
    radarData: RadarUi,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 * 0.8f

        // Очищаем фон
        drawRect(
            color = Color.Transparent,
            size = size
        )

        // Рисуем сетку
        drawRadarGrid(center, radius, radarData.points.size)

        // Рисуем полигон данных
        if (radarData.points.isNotEmpty()) {
            drawRadarPolygon(center, radius, radarData.points)

            // Рисуем точки и подписи
            drawPointsAndLabels(center, radius, radarData.points, textMeasurer)
        }
    }
}

private fun DrawScope.drawRadarGrid(
    center: Offset,
    radius: Float,
    sides: Int
) {
    // Окружность сетки
    drawCircle(
        color = Color.Gray.copy(alpha = 0.3f),
        radius = radius,
        center = center,
        style = Stroke(width = 1f)
    )

    // Линии сетки
    val angleStep = 360f / sides

    repeat(sides) { i ->
        val angle = Math.toRadians((angleStep * i - 90).toDouble())
        val x = center.x + radius * cos(angle).toFloat()
        val y = center.y + radius * sin(angle).toFloat()

        // Линия к центру
        drawLine(
            color = Color.Gray.copy(alpha = 0.3f),
            start = center,
            end = Offset(x, y),
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawRadarPolygon(
    center: Offset,
    radius: Float,
    points: List<RadarPointUi>
) {
    if (points.isEmpty()) return

    val pathPoints = mutableListOf<Offset>()

    // Собираем точки полигона
    points.forEach { point ->
        val angle = Math.toRadians((point.angle - 90).toDouble())
        val valueRadius = radius * (point.value / 100f)
        val x = center.x + valueRadius * cos(angle).toFloat()
        val y = center.y + valueRadius * sin(angle).toFloat()

        pathPoints.add(Offset(x, y))
    }

    // Рисуем полигон
    if (pathPoints.size >= 3) {
        for (i in 0 until pathPoints.size) {
            val start = pathPoints[i]
            val end = pathPoints[(i + 1) % pathPoints.size]

            drawLine(
                color = Color(0xFF4CAF50).copy(alpha = 0.6f),
                start = start,
                end = end,
                strokeWidth = 2f
            )
        }

        // Заливаем полигон
        drawCircle(
            color = Color(0xFF4CAF50).copy(alpha = 0.1f),
            radius = 4f,
            center = center
        )
    }
}

private fun DrawScope.drawPointsAndLabels(
    center: Offset,
    radius: Float,
    points: List<RadarPointUi>,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    points.forEach { point ->
        val angle = Math.toRadians((point.angle - 90).toDouble())
        val valueRadius = radius * (point.value / 100f)

        // Координаты точки
        val x = center.x + valueRadius * cos(angle).toFloat()
        val y = center.y + valueRadius * sin(angle).toFloat()

        // Рисуем точку
        drawCircle(
            color = Color(point.color),
            radius = 8f,
            center = Offset(x, y)
        )

        // Рисуем подпись
        val labelRadius = radius + 25f
        val labelX = center.x + labelRadius * cos(angle).toFloat()
        val labelY = center.y + labelRadius * sin(angle).toFloat()

        val textLayoutResult = textMeasurer.measure(
            text = point.label,
            style = TextStyle(fontSize = 12.sp)
        )

        rotate(
            degrees = point.angle,
            pivot = Offset(labelX, labelY)
        ) {
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    labelX - textLayoutResult.size.width / 2,
                    labelY - textLayoutResult.size.height / 2
                )
            )
        }
    }
}