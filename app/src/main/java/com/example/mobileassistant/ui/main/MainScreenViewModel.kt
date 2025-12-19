package com.example.mobileassistant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileassistant.domain.model.*
import com.example.mobileassistant.domain.model.repository.GoalRepository
import com.example.mobileassistant.ui.main.model.DomainMapper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val repository: GoalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state: StateFlow<MainScreenState> = _state.asStateFlow()

    private val refreshTrigger = MutableSharedFlow<Unit>()
    private var currentGoalId: Int? = null

    init {
        setupDataObservers()
        loadInitialData()
    }

    private fun setupDataObservers() {
        viewModelScope.launch {
            // Комбинируем observeGoals с обновлениями данных
            repository.observeGoals(userId = 1)
                .combine(repository.dataUpdates) { goals, _ -> goals }
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
            try {
                // Загружаем цели пользователя
                val goals = repository.getGoals(userId = 1)
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
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun triggerRefresh() {
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
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
                    DomainMapper.subGoal.toUi(subGoal, progress, taskCount)
                }

                // Создаем радар-диаграмму
                val radarData = createRadarData(subGoals, subGoalProgresses)

                // Рассчитываем активность за неделю
                val weeklyActivity = calculateWeeklyActivity(subGoals, tasks)

                // Форматируем задачи для UI
                val taskCards = tasks.map { task ->
                    val subGoal = subGoals.find { it.id == task.subGoalId }
                    DomainMapper.task.toUi(
                        task = task,
                        subGoalTitle = subGoal?.title ?: "",
                        subGoalColor = subGoal?.color ?: 0xFF4CAF50.toInt()
                    )
                }

                _state.update {
                    it.copy(
                        subGoals = subGoalButtons,
                        tasks = taskCards,
                        filteredTasks = taskCards,
                        radarData = radarData,
                        weeklyActivity = weeklyActivity,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Ошибка загрузки данных",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun createRadarData(
        subGoals: List<SubGoal>,
        progresses: Map<Int, Int>
    ): RadarUi {
        if (subGoals.isEmpty()) {
            return RadarUi(emptyList(), "Нет данных", 0)
        }

        val points = subGoals.mapIndexed { index, subGoal ->
            val progress = progresses[subGoal.id] ?: 0
            val angle = (360f / subGoals.size) * index
            RadarPointUi(
                label = subGoal.title,
                value = progress,
                color = subGoal.color,
                angle = angle
            )
        }

        val totalProgress = if (points.isNotEmpty()) {
            points.sumOf { it.value } / points.size
        } else {
            0
        }

        return RadarUi(
            points = points,
            centerText = _state.value.selectedGoal?.title ?: "Цель",
            totalProgress = totalProgress
        )
    }

    private fun calculateSubGoalProgress(subGoalId: Int, tasks: List<Task>): Int {
        val subGoalTasks = tasks.filter { it.subGoalId == subGoalId }
        if (subGoalTasks.isEmpty()) return 0

        val completedTasks = subGoalTasks.count { it.isDone }
        return (completedTasks * 100) / subGoalTasks.size
    }

    private fun calculateWeeklyActivity(
        subGoals: List<SubGoal>,
        tasks: List<Task>
    ): List<WeeklyActivityUi> {
        val oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)

        return subGoals.map { subGoal ->
            val subGoalTasks = tasks.filter { it.subGoalId == subGoal.id }
            val completedThisWeek = subGoalTasks.count {
                it.isDone && it.completedAt != null && it.completedAt!! > oneWeekAgo
            }

            WeeklyActivityUi(
                subGoalTitle = subGoal.title,
                progress = calculateSubGoalProgress(subGoal.id, subGoalTasks),
                color = subGoal.color,
                activityCount = completedThisWeek
            )
        }
    }

    // Добавляем в MainScreenViewModel.kt
    fun selectGoal(goalId: Int) {
        viewModelScope.launch {
            val goal = repository.getGoal(goalId)
            if (goal != null) {
                _state.update { it.copy(selectedGoal = goal) }
                loadGoalData(goalId)
            }
        }
    }

    // Добавляем в MainScreenViewModel.kt
    fun markGoalAsCompleted(goalId: Int) {
        viewModelScope.launch {
            try {
                // Получаем цель из репозитория
                val goal = repository.getGoal(goalId)
                if (goal != null) {
                    // Помечаем цель как завершенную
                    // Здесь нужно будет реализовать метод updateGoal в репозитории
                    // repository.updateGoal(goal.copy(isCompleted = true))

                    // Временно просто обновляем данные
                    triggerRefresh()

                    _state.update {
                        it.copy(
                            showSuccessMessage = "Цель '${goal.title}' отмечена как выполненная"
                        )
                    }

                    viewModelScope.launch {
                        kotlinx.coroutines.delay(2000)
                        clearSuccessMessage()
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Ошибка: ${e.message}"
                    )
                }
            }
        }
    }

    fun deleteGoal(goalId: Int) {
        viewModelScope.launch {
            try {
                // Получаем цель для получения названия
                val goal = repository.getGoal(goalId)

                // Удаляем цель
                // Здесь нужно будет реализовать метод deleteGoal в репозитории
                // repository.deleteGoal(goalId)

                // Временно просто обновляем данные
                triggerRefresh()

                goal?.let {
                    _state.update {
                        it.copy(
                            showSuccessMessage = "Цель удалена"
                        )
                    }
                }

                viewModelScope.launch {
                    kotlinx.coroutines.delay(2000)
                    clearSuccessMessage()
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Ошибка удаления: ${e.message}"
                    )
                }
            }
        }
    }

    // Диалоги
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

    // Создание целей
    fun createNewGoal(title: String, description: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isCreatingGoal = true) }

            try {
                repository.addGoal(userId = 1, title = title, description = description)

                // Показываем сообщение и закрываем диалог
                _state.update {
                    it.copy(
                        isCreatingGoal = false,
                        showSuccessMessage = "Цель '$title' создана"
                    )
                }

                // Закрываем диалог и обновляем данные
                hideAddGoalDialog()
                loadInitialData()

                viewModelScope.launch {
                    kotlinx.coroutines.delay(2000)
                    clearSuccessMessage()
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isCreatingGoal = false,
                        error = "Ошибка создания цели: ${e.message}"
                    )
                }
            }
        }
    }

    fun createNewSubGoal(title: String, color: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isCreatingSubGoal = true) }

            try {
                currentGoalId?.let { goalId ->
                    repository.addSubGoal(goalId, title, color)

                    // Показываем сообщение и закрываем диалог
                    _state.update {
                        it.copy(
                            isCreatingSubGoal = false,
                            showSuccessMessage = "Подцель '$title' создана"
                        )
                    }

                    // Закрываем диалог и обновляем данные
                    hideAddSubGoalDialog()
                    loadGoalData(goalId)

                    viewModelScope.launch {
                        kotlinx.coroutines.delay(2000)
                        clearSuccessMessage()
                    }
                } ?: run {
                    _state.update {
                        it.copy(
                            isCreatingSubGoal = false,
                            error = "Не выбрана цель для создания подцели"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isCreatingSubGoal = false,
                        error = "Ошибка создания подцели: ${e.message}"
                    )
                }
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
            _state.update { it.copy(isLoading = true) }
            loadInitialData()
        }
    }
}

// Обновляем MainScreenState
data class MainScreenState(
    val goals: List<Goal> = emptyList(),
    val selectedGoal: Goal? = null,
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