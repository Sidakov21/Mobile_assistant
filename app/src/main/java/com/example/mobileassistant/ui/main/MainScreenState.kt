package com.example.mobileassistant.ui.main

import com.example.mobileassistant.domain.model.Goal
import com.example.mobileassistant.domain.model.SubGoal
import com.example.mobileassistant.domain.model.Task
import com.example.mobileassistant.ui.DayActivity
import com.example.mobileassistant.ui.main.model.TaskUi
import com.example.mobileassistant.ui.main.model.WeekActivityUi
import com.example.mobileassistant.ui.radar.RadarData

data class MainScreenState(
    val weekActivity: List<WeekActivityUi> = emptyList(),
    val goals: List<Goal> = emptyList(),
    val selectedGoal: Goal? = null,
    val subGoals: List<SubGoal> = emptyList(),
    val tasks: List<TaskUi> = emptyList(),
    val radarData: RadarData = RadarData(),
    val isLoading: Boolean = false,
    val isBottomSheetOpen: Boolean = false,
    val selectedSubGoal: SubGoal? = null
)
