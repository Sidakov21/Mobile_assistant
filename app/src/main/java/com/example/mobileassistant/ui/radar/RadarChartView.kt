package com.example.mobileassistant.ui.radar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class RadarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Данные из скрина
    private val categories = listOf("СПОРТ", "ИНТЕЛЛЕКТ", "ТВОРЧЕСТВО", "ХАРИЗМА", "РУТИНА")
    private val levels = listOf(2, 1, 3, 2, 1) // LVL из скрина
    private val maxLevel = 5

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 32f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val dataPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#4CAF50")
        style = Paint.Style.FILL
        alpha = 128
    }

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        radius = (w.coerceAtMost(h) / 2f) * 0.7f // 70% от минимального размера
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1. Рисуем концентрические окружности (уровни)
        paint.color = Color.parseColor("#333333")
        paint.style = Paint.Style.STROKE

        for (i in 1..maxLevel) {
            val levelRadius = radius * (i.toFloat() / maxLevel)
            canvas.drawCircle(centerX, centerY, levelRadius, paint)
        }

        // 2. Рисуем оси (линии к категориям)
        val angleStep = 360f / categories.size
        for (i in categories.indices) {
            val angle = Math.toRadians((i * angleStep - 90).toDouble()) // -90 чтобы начать сверху

            // Линия оси
            val x = centerX + radius * Math.cos(angle).toFloat()
            val y = centerY + radius * Math.sin(angle).toFloat()
            canvas.drawLine(centerX, centerY, x, y, paint)

            // Подпись категории
            val labelRadius = radius * 1.2f
            val labelX = centerX + labelRadius * Math.cos(angle).toFloat()
            val labelY = centerY + labelRadius * Math.sin(angle).toFloat()

            canvas.drawText(categories[i], labelX, labelY, textPaint)
        }

        // 3. Рисуем полигон данных
        val path = Path()
        for (i in levels.indices) {
            val angle = Math.toRadians((i * angleStep - 90).toDouble())
            val dataRadius = radius * (levels[i].toFloat() / maxLevel)
            val x = centerX + dataRadius * Math.cos(angle).toFloat()
            val y = centerY + dataRadius * Math.sin(angle).toFloat()

            // Точки вершины
            val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                style = Paint.Style.FILL
            }
            canvas.drawCircle(x, y, 10f, pointPaint)

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()

        canvas.drawPath(path, dataPaint)
    }
}