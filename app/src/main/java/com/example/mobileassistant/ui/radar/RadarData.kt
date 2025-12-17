package com.example.mobileassistant.ui.radar

data class RadarData(
    val points: List<RadarPoint> = emptyList()
)

data class RadarPoint(
    val label: String,
    val value: Int // 0–100
)