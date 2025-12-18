package com.example.mobileassistant.ui.subgoals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileassistant.domain.model.SubGoal
import com.example.mobileassistant.domain.model.SubGoalButtonUi
import com.example.mobileassistant.domain.model.Task
import com.example.mobileassistant.domain.model.TaskCardUi
import com.example.mobileassistant.domain.model.repository.GoalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SubGoalsViewModel(
    private val repository: GoalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SubGoalsState())
    val state: StateFlow<SubGoalsState> = _state.asStateFlow()

    private var currentGoalId: Int = 1

    // Поток для обновления данных
    private val refreshTrigger = MutableSharedFlow<Unit>()

    init {
        setupDataObserver()
    }

    private fun setupDataObserver() {
        viewModelScope.launch {
            refreshTrigger
                .distinctUntilChanged()
                .collect {
                    loadSubGoals(currentGoalId)
                }
        }
    }

    fun loadGoalData(goalId: Int) {
        currentGoalId = goalId
        triggerRefresh()
    }

    private fun triggerRefresh() {
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
    }

    private fun loadSubGoals(goalId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val (subGoals, tasks) = repository.getGoalFull(goalId)

                // Загружаем данные для подцелей (цвета, прогресс)
                val subGoalButtons = mutableListOf<SubGoalButtonUi>()

                for (subGoal in subGoals) {
                    val progress = calculateSubGoalProgress(subGoal.id, tasks)
                    val taskCount = tasks.count { it.subGoalId == subGoal.id }
                    val color = repository.getSubGoalColor(subGoal.id) ?: 0xFF4CAF50.toInt()

                    subGoalButtons.add(
                        SubGoalButtonUi(
                            id = subGoal.id,
                            title = subGoal.title,
                            color = color,
                            progress = progress,
                            taskCount = taskCount
                        )
                    )
                }

                // Форматируем задачи для UI
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val allTasks = tasks.map { task ->
                    val subGoal = subGoals.find { it.id == task.subGoalId }
                    val color = repository.getSubGoalColor(task.subGoalId) ?: 0xFF4CAF50.toInt()

                    TaskCardUi(
                        id = task.id,
                        title = task.title,
                        progress = task.progress,
                        note = task.note,
                        subGoalTitle = subGoal?.title ?: "",
                        subGoalColor = color,
                        formattedDate = dateFormat.format(Date(task.createdAt)),
                        subGoalId = task.subGoalId
                    )
                }

                // Получаем название цели
                val goals = repository.getGoals(userId = 1)
                val goal = goals.find { it.id == goalId }
                val goalTitle = goal?.title ?: "Цель"

                _state.update {
                    it.copy(
                        goalTitle = goalTitle,
                        subGoals = subGoalButtons,
                        allTasks = allTasks,
                        filteredTasks = allTasks,
                        selectedSubGoal = null,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    private fun calculateSubGoalProgress(subGoalId: Int, tasks: List<Task>): Int {
        val subGoalTasks = tasks.filter { it.subGoalId == subGoalId }
        if (subGoalTasks.isEmpty()) return 0

        val completedTasks = subGoalTasks.count { it.isDone }
        return (completedTasks * 100) / subGoalTasks.size
    }

    fun selectSubGoal(subGoalId: Int?) {
        _state.update { currentState ->
            val updatedSubGoals = currentState.subGoals.map {
                it.copy(isSelected = it.id == subGoalId)
            }

            val filteredTasks = if (subGoalId == null) {
                currentState.allTasks
            } else {
                currentState.allTasks.filter { task ->
                    task.subGoalId == subGoalId
                }
            }

            currentState.copy(
                subGoals = updatedSubGoals,
                selectedSubGoal = updatedSubGoals.find { it.id == subGoalId },
                filteredTasks = filteredTasks
            )
        }
    }

    fun searchTasks(query: String) {
        _state.update { currentState ->
            val filtered = if (query.isEmpty()) {
                currentState.allTasks
            } else {
                currentState.allTasks.filter { task ->
                    task.title.contains(query, ignoreCase = true) ||
                            task.note.contains(query, ignoreCase = true) ||
                            task.subGoalTitle.contains(query, ignoreCase = true)
                }
            }

            currentState.copy(
                searchQuery = query,
                filteredTasks = filtered
            )
        }
    }

    fun showAddTaskDialog() {
        _state.update { it.copy(showAddTaskDialog = true) }
    }

    fun hideAddTaskDialog() {
        _state.update { it.copy(showAddTaskDialog = false) }
    }

    fun addTask(subGoalId: Int, title: String, note: String) {
        viewModelScope.launch {
            _state.update { it.copy(isAddingTask = true) }

            try {
                repository.addTask(subGoalId, title, note)
                triggerRefresh() // Обновляем данные
                hideAddTaskDialog()

                // Показываем уведомление об успехе
                _state.update { it.copy(
                    isAddingTask = false,
                    showSuccessMessage = "Задача '$title' добавлена"
                ) }

                // Автоматически скрываем сообщение через 2 секунды
                viewModelScope.launch {
                    kotlinx.coroutines.delay(2000)
                    _state.update { it.copy(showSuccessMessage = null) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isAddingTask = false,
                    error = "Ошибка добавления задачи: ${e.message}"
                ) }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun clearSuccessMessage() {
        _state.update { it.copy(showSuccessMessage = null) }
    }

    // Метод для принудительного обновления
    fun refreshData() {
        triggerRefresh()
    }
}