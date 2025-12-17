package com.example.mobileassistant.data.mapper

import com.example.mobileassistant.ui.main.model.WeekActivityUi
import com.example.mobileassistant.ui.main.model.WeekStatItem


fun WeekStatItem.toUi() = WeekActivityUi(
    day = day,
    completed = completed,
    color = color
)