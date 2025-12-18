package com.example.mobileassistant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileassistant.domain.model.RadarPointUi
import com.example.mobileassistant.domain.model.RadarUi
import com.example.mobileassistant.domain.model.SubGoal
import com.example.mobileassistant.domain.model.SubGoalButtonUi
import com.example.mobileassistant.domain.model.Task
import com.example.mobileassistant.domain.model.TaskCardUi
import com.example.mobileassistant.domain.model.WeeklyActivityUi
import com.example.mobileassistant.domain.model.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainScreenViewModel(
    private val repository: GoalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState> = _state

    private var currentGoalId: Int? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Загружаем цели пользователя
                val goals = repository.getGoals(userId = 1)
                val selectedGoal = goals.firstOrNull()

                if (selectedGoal != null) {
                    currentGoalId = selectedGoal.id
                    loadGoalData(selectedGoal.id)
                }

                _state.update {
                    it.copy(
                        goals = goals,
                        selectedGoal = selectedGoal,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun loadGoalData(goalId: Int) {
        viewModelScope.launch {
            // Загружаем полные данные по цели (подцели и задачи)
            val (subGoals, tasks) = repository.getGoalFull(goalId)

            // Рассчитываем прогресс для каждой подцели
            val subGoalProgresses = subGoals.associate { subGoal ->
                subGoal.id to calculateSubGoalProgress(subGoal.id, tasks)
            }

            // Создаем UI модели
            val subGoalButtons = subGoals.map { subGoal ->
                val progress = subGoalProgresses[subGoal.id] ?: 0
                val taskCount = tasks.count { it.id == subGoal.id }
                SubGoalButtonUi(
                    id = subGoal.id,
                    title = subGoal.title,
                    color = 0xFF4CAF50.toInt(), // TODO: брать из базы
                    progress = progress,
                    taskCount = taskCount
                )
            }

            // Создаем радар-диаграмму (заглушка, пока не получаем цвета)
            val radarData = RadarUi(
                points = subGoals.mapIndexed { index, subGoal ->
                    val progress = subGoalProgresses[subGoal.id] ?: 0
                    val angle = (360f / subGoals.size) * index
                    RadarPointUi(
                        label = subGoal.title,
                        value = progress,
                        color = 0xFF4CAF50.toInt(),
                        angle = angle
                    )
                },
                centerText = _state.value.selectedGoal?.title ?: "Цель",
                totalProgress = if (subGoalProgresses.isNotEmpty()) {
                    subGoalProgresses.values.sum() / subGoalProgresses.size
                } else {
                    0
                }
            )

            // Рассчитываем активность за неделю
            val weeklyActivity = calculateWeeklyActivity(subGoals, tasks)

            // Форматируем задачи для UI
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val taskCards = tasks.map { task ->
                TaskCardUi(
                    id = task.id,
                    title = task.title,
                    progress = task.progress,
                    note = task.note,
                    subGoalTitle = subGoals.find { it.id == task.id }?.title ?: "",
                    subGoalColor = 0xFF4CAF50.toInt(),
                    formattedDate = dateFormat.format(Date(task.createdAt))
                )
            }

            _state.update {
                it.copy(
                    subGoals = subGoalButtons,
                    tasks = taskCards,
                    filteredTasks = taskCards,
                    radarData = radarData,
                    weeklyActivity = weeklyActivity
                )
            }
        }
    }

    private fun calculateSubGoalProgress(subGoalId: Int, tasks: List<Task>): Int {
        val subGoalTasks = tasks.filter { it.id == subGoalId }
        if (subGoalTasks.isEmpty()) return 0

        val completedTasks = subGoalTasks.count { it.isDone }
        return (completedTasks * 100) / subGoalTasks.size
    }

    private fun calculateWeeklyActivity(subGoals: List<SubGoal>, tasks: List<Task>): List<WeeklyActivityUi> {
        val oneWeekAgo = java.time.LocalDateTime.now().minusDays(7)

        return subGoals.map { subGoal ->
            val subGoalTasks = tasks.filter { it.id == subGoal.id }
            val completedThisWeek = subGoalTasks.count {
                it.isDone && it.completedAt?.isAfter(oneWeekAgo) == true
            }

            WeeklyActivityUi(
                subGoalTitle = subGoal.title,
                progress = calculateSubGoalProgress(subGoal.id, subGoalTasks),
                color = 0xFF4CAF50.toInt(),
                activityCount = completedThisWeek
            )
        }
    }

    // ДИАЛОГИ
    fun showAddGoalDialog() {
        _state.update { it.copy(showAddGoalDialog = true) }
    }

    fun hideAddGoalDialog() {
        _state.update { it.copy(showAddGoalDialog = false) }
    }

    fun showAddSubGoalDialog() {
        _state.update { it.copy(showAddSubGoalDialog = true) }
    }

    fun hideAddSubGoalDialog() {
        _state.update { it.copy(showAddSubGoalDialog = false) }
    }

    // СОЗДАНИЕ ЦЕЛИ
    fun createNewGoal(title: String, description: String?) {
        viewModelScope.launch {
            try {
                repository.addGoal(userId = 1, title = title, description = description)
                loadInitialData() // Перезагружаем данные
                hideAddGoalDialog()
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    // СОЗДАНИЕ ПОДЦЕЛИ
    fun createNewSubGoal(title: String, color: Int) {
        viewModelScope.launch {
            try {
                currentGoalId?.let { goalId ->
                    repository.addSubGoal(goalId, title, color)
                    loadGoalData(goalId) // Перезагружаем данные для текущей цели
                    hideAddSubGoalDialog()
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    // Методы для навигации и выбора
    fun selectSubGoal(subGoalId: Int?) {
        _state.update { current ->
            val updatedSubGoals = current.subGoals.map {
                it.copy(isSelected = it.id == subGoalId)
            }

            val filteredTasks = if (subGoalId == null) {
                current.tasks
            } else {
                current.tasks.filter { it.id == subGoalId }
            }

            current.copy(
                subGoals = updatedSubGoals,
                selectedSubGoal = updatedSubGoals.find { it.id == subGoalId },
                filteredTasks = filteredTasks
            )
        }
    }
}

// Обновляем MainScreenState
data class MainScreenState(
    val goals: List<com.example.mobileassistant.domain.model.Goal> = emptyList(),
    val selectedGoal: com.example.mobileassistant.domain.model.Goal? = null,
    val subGoals: List<SubGoalButtonUi> = emptyList(),
    val selectedSubGoal: SubGoalButtonUi? = null,
    val tasks: List<TaskCardUi> = emptyList(),
    val filteredTasks: List<TaskCardUi> = emptyList(),
    val radarData: RadarUi = RadarUi(emptyList(), "", 0),
    val weeklyActivity: List<WeeklyActivityUi> = emptyList(),
    val isLoading: Boolean = false,
    val showAddGoalDialog: Boolean = false,
    val showAddSubGoalDialog: Boolean = false,
    val error: String? = null
)