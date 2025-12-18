package com.example.mobileassistant.ui.subgoals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileassistant.domain.model.SubGoalButtonUi
import com.example.mobileassistant.domain.model.TaskCardUi
import com.example.mobileassistant.domain.model.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SubGoalsViewModel(
    private val repository: GoalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SubGoalsState())
    val state: StateFlow<SubGoalsState> = _state

    private var currentGoalId: Int? = null

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Загружаем цели пользователя
                val goals = repository.getGoals(userId = 1)
                val selectedGoal = goals.firstOrNull()

                if (selectedGoal != null) {
                    currentGoalId = selectedGoal.id
                    loadSubGoals(selectedGoal.id)
                }

                _state.update {
                    it.copy(
                        goalTitle = selectedGoal?.title ?: "Цель",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun loadSubGoals(goalId: Int) {
        viewModelScope.launch {
            try {
                val (subGoals, tasks) = repository.getGoalFull(goalId)

                // Конвертируем подцели в UI модели
                val subGoalButtons = subGoals.map { subGoal ->
                    val subGoalTasks = tasks.filter { task -> task.id == subGoal.id }
                    val progress = if (subGoalTasks.isEmpty()) {
                        0
                    } else {
                        val completed = subGoalTasks.count { it.isDone }
                        (completed * 100) / subGoalTasks.size
                    }

                    SubGoalButtonUi(
                        id = subGoal.id,
                        title = subGoal.title,
                        color = 0xFF4CAF50.toInt(),
                        progress = progress,
                        taskCount = subGoalTasks.size
                    )
                }

                // Форматируем задачи для UI
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val allTasks = tasks.map { task ->
                    val subGoal = subGoals.find { it.id == task.id }
                    TaskCardUi(
                        id = task.id,
                        title = task.title,
                        progress = task.progress,
                        note = task.note,
                        subGoalTitle = subGoal?.title ?: "",
                        subGoalColor = 0xFF4CAF50.toInt(),
                        formattedDate = dateFormat.format(Date(task.createdAt))
                    )
                }

                _state.update {
                    it.copy(
                        subGoals = subGoalButtons,
                        allTasks = allTasks,
                        filteredTasks = allTasks,
                        selectedSubGoal = null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun selectSubGoal(subGoalId: Int?) {
        _state.update { current ->
            val updatedSubGoals = current.subGoals.map {
                it.copy(isSelected = it.id == subGoalId)
            }

            val filteredTasks = if (subGoalId == null) {
                current.allTasks
            } else {
                current.allTasks.filter { it.id == subGoalId }
            }

            current.copy(
                subGoals = updatedSubGoals,
                selectedSubGoal = updatedSubGoals.find { it.id == subGoalId },
                filteredTasks = filteredTasks
            )
        }
    }

    fun searchTasks(query: String) {
        _state.update { current ->
            val filtered = if (query.isEmpty()) {
                current.allTasks
            } else {
                current.allTasks.filter { task ->
                    task.title.contains(query, ignoreCase = true) ||
                            task.note.contains(query, ignoreCase = true) ||
                            task.subGoalTitle.contains(query, ignoreCase = true)
                }
            }

            current.copy(
                searchQuery = query,
                filteredTasks = filtered
            )
        }
    }

    // ДИАЛОГИ
    fun showAddTaskDialog() {
        _state.update { it.copy(showAddTaskDialog = true) }
    }

    fun hideAddTaskDialog() {
        _state.update { it.copy(showAddTaskDialog = false) }
    }

    // СОЗДАНИЕ ЗАДАЧИ
    fun addTask(subGoalId: Int, title: String, note: String) {
        viewModelScope.launch {
            try {
                repository.addTask(subGoalId, title, note)
                currentGoalId?.let { loadSubGoals(it) } // Перезагружаем данные
                hideAddTaskDialog()
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

// Обновляем SubGoalsState
data class SubGoalsState(
    val goalTitle: String = "",
    val subGoals: List<SubGoalButtonUi> = emptyList(),
    val selectedSubGoal: SubGoalButtonUi? = null,
    val allTasks: List<TaskCardUi> = emptyList(),
    val filteredTasks: List<TaskCardUi> = emptyList(),
    val searchQuery: String = "",
    val sortType: SortType = SortType.DATE_NEWEST,
    val isLoading: Boolean = false,
    val showAddTaskDialog: Boolean = false,
    val error: String? = null
)

enum class SortType {
    DATE_NEWEST,
    DATE_OLDEST,
    PROGRESS_HIGH,
    PROGRESS_LOW,
    ALPHABETICAL
}