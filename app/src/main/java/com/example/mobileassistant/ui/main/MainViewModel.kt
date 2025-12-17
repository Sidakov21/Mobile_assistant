package com.example.mobileassistant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileassistant.data.mapper.toUi
import com.example.mobileassistant.domain.model.SubGoal
import com.example.mobileassistant.domain.model.repository.GoalRepository
import com.example.mobileassistant.domain.model.buildRadar
import com.example.mobileassistant.domain.model.buildWeekActivity
import com.example.mobileassistant.domain.model.toUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: GoalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState> = _state

    private var currentGoalId: Int? = null

    init {
        loadGoals()
    }


    private fun loadGoals() = viewModelScope.launch {
        val goals = repository.getGoals(userId = 1)

        _state.update {
            it.copy(
                goals = goals,
                selectedGoal = goals.firstOrNull()
            )
        }

        goals.firstOrNull()?.let {
            onGoalSelected(it.id)
        }
    }

    fun onGoalSelected(goalId: Int) {
        currentGoalId = goalId

        viewModelScope.launch {
            repository.observeTasks(goalId)
                .collect { tasks ->
                    _state.update {
                        it.copy(
                            tasks = tasks.map { it.toUi() },
                            radarData = buildRadar(tasks),
                            weekActivity = buildWeekActivity(tasks).map { item -> item.toUi() }
                        )
                    }
                }
        }
    }

    fun openBottomSheet(subGoalId: Long) {
        viewModelScope.launch {
            repository.observeTasks(subGoalId.toInt())
                .collect { tasks ->
                    _state.update {
                        it.copy(
                            isBottomSheetOpen = true,
                            tasks = tasks.map { it.toUi() }
                        )
                    }
                }
        }
    }

    fun openSubGoal(subGoal: SubGoal) {
        _state.update {
            it.copy(
                isBottomSheetOpen = true,
                selectedSubGoal = subGoal
            )
        }
    }

    fun closeBottomSheet() {
        _state.update { it.copy(isBottomSheetOpen = false) }
    }

    fun openBottomSheet() {
        _state.update { it.copy(isBottomSheetOpen = true) }
    }

    fun addTask(title: String) {
        // временно — потом подключим БД
    }
}
