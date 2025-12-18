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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainScreenViewModel(
    private val repository: GoalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState> = _state.asStateFlow()

    private var currentGoalId: Int? = null

    // Поток для обновления данных
    private val refreshTrigger = MutableSharedFlow<Unit>()

    init {
        setupDataObservers()
        loadInitialData()
    }

    private fun setupDataObservers() {
        viewModelScope.launch {
            // Реактивно обновляем цели при их изменении
            repository.observeGoals(userId = 1)
                .combine(refreshTrigger) { goals, _ -> goals }
                .distinctUntilChanged()
                .collect { goals ->
                    val selectedGoal = goals.firstOrNull()
                    currentGoalId = selectedGoal?.id

                    _state.update { currentState ->
                        currentState.copy(
                            goals = goals,
                            selectedGoal = selectedGoal
                        )
                    }

                    if (selectedGoal != null) {
                        loadGoalData(selectedGoal.id)
                    }
                }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            triggerRefresh() // Инициируем обновление
            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun triggerRefresh() {
        refreshTrigger.emit(Unit)
    }

    private fun loadGoalData(goalId: Int) {
        viewModelScope.launch {
            try {
                val (subGoals, tasks) = repository.getGoalFull(goalId)

                // Рассчитываем прогресс для каждой подцели
                val subGoalProgresses = subGoals.associate { subGoal ->
                    subGoal.id to calculateSubGoalProgress(subGoal.id, tasks)
                }

                // Создаем UI модели для подцелей
                val subGoalButtons = subGoals.map { subGoal ->
                    val progress = subGoalProgresses[subGoal.id] ?: 0
                    val taskCount = tasks.count { it.subGoalId == subGoal.id }
                    SubGoalButtonUi(
                        id = subGoal.id,
                        title = subGoal.title,
                        color = 0xFF4CAF50.toInt(),
                        progress = progress,
                        taskCount = taskCount
                    )
                }

                // Создаем радар-диаграмму
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
                    val subGoal = subGoals.find { it.id == task.subGoalId }
                    TaskCardUi(
                        id = task.id,
                        title = task.title,
                        progress = task.progress,
                        note = task.note,
                        subGoalTitle = subGoal?.title ?: "",
                        subGoalColor = 0xFF4CAF50.toInt(),
                        formattedDate = dateFormat.format(Date(task.createdAt)),
                        subGoalId = task.subGoalId
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
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun calculateSubGoalProgress(subGoalId: Int, tasks: List<Task>): Int {
        val subGoalTasks = tasks.filter { it.subGoalId == subGoalId }
        if (subGoalTasks.isEmpty()) return 0

        val completedTasks = subGoalTasks.count { it.isDone }
        return (completedTasks * 100) / subGoalTasks.size
    }

    private fun calculateWeeklyActivity(subGoals: List<SubGoal>, tasks: List<Task>): List<WeeklyActivityUi> {
        val oneWeekAgo = java.time.LocalDateTime.now().minusDays(7)

        return subGoals.map { subGoal ->
            val subGoalTasks = tasks.filter { it.subGoalId == subGoal.id }
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

    // СОЗДАНИЕ ЦЕЛИ - УЛУЧШЕННАЯ ВЕРСИЯ
    fun createNewGoal(title: String, description: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isCreatingGoal = true) }

            try {
                repository.addGoal(userId = 1, title = title, description = description)
                triggerRefresh() // Запускаем обновление данных
                hideAddGoalDialog()

                // Показываем уведомление об успехе
                _state.update { it.copy(
                    isCreatingGoal = false,
                    showSuccessMessage = "Цель '$title' создана"
                ) }

                // Автоматически скрываем сообщение через 2 секунды
                viewModelScope.launch {
                    kotlinx.coroutines.delay(2000)
                    _state.update { it.copy(showSuccessMessage = null) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isCreatingGoal = false,
                    error = "Ошибка создания цели: ${e.message}"
                ) }
            }
        }
    }

    // СОЗДАНИЕ ПОДЦЕЛИ - УЛУЧШЕННАЯ ВЕРСИЯ
    fun createNewSubGoal(title: String, color: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isCreatingSubGoal = true) }

            try {
                currentGoalId?.let { goalId ->
                    repository.addSubGoal(goalId, title, color)
                    loadGoalData(goalId) // Обновляем данные конкретной цели
                    hideAddSubGoalDialog()

                    // Показываем уведомление об успехе
                    _state.update { it.copy(
                        isCreatingSubGoal = false,
                        showSuccessMessage = "Подцель '$title' создана"
                    ) }

                    // Автоматически скрываем сообщение через 2 секунды
                    viewModelScope.launch {
                        kotlinx.coroutines.delay(2000)
                        _state.update { it.copy(showSuccessMessage = null) }
                    }
                } ?: run {
                    _state.update { it.copy(
                        isCreatingSubGoal = false,
                        error = "Не выбрана цель для создания подцели"
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isCreatingSubGoal = false,
                    error = "Ошибка создания подцели: ${e.message}"
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

    // Метод для принудительного обновления данных
    fun refreshData() {
        viewModelScope.launch {
            triggerRefresh()
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
    val isCreatingGoal: Boolean = false,
    val isCreatingSubGoal: Boolean = false,
    val showAddGoalDialog: Boolean = false,
    val showAddSubGoalDialog: Boolean = false,
    val error: String? = null,
    val showSuccessMessage: String? = null
)