package com.example.mobileassistant.ui.subgoals

import com.example.mobileassistant.domain.model.SubGoalButtonUi
import com.example.mobileassistant.domain.model.TaskCardUi

data class SubGoalsState(
    val goalTitle: String = "",
    val subGoals: List<SubGoalButtonUi> = emptyList(),
    val selectedSubGoal: SubGoalButtonUi? = null,
    val allTasks: List<TaskCardUi> = emptyList(),
    val filteredTasks: List<TaskCardUi> = emptyList(),
    val searchQuery: String = "",
    val sortType: SortType = SortType.DATE_NEWEST,
    val isLoading: Boolean = false,
    val isAddingTask: Boolean = false,
    val showAddTaskDialog: Boolean = false,
    val error: String? = null,
    val showSuccessMessage: String? = null
)

enum class SortType {
    DATE_NEWEST,
    DATE_OLDEST,
    PROGRESS_HIGH,
    PROGRESS_LOW,
    ALPHABETICAL
}