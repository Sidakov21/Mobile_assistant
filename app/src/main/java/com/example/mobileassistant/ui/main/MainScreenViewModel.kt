package com.example.mobileassistant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileassistant.domain.model.*
import com.example.mobileassistant.domain.model.repository.GoalRepository
import com.example.mobileassistant.ui.main.model.DomainMapper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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

                    selectedGoal?.let { loadGoalData(it.id) }
                }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            refreshTrigger.emit(Unit)
            _state.update { it.copy(isLoading = false) }
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
                val radarData = RadarUi(
                    points = subGoals.mapIndexed { index, subGoal ->
                        val progress = subGoalProgresses[subGoal.id] ?: 0
                        val angle = (360f / subGoals.size) * index
                        RadarPointUi(
                            label = subGoal.title,
                            value = progress,
                            color = subGoal.color,
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
                triggerRefresh()
                hideAddGoalDialog()

                _state.update {
                    it.copy(
                        isCreatingGoal = false,
                        showSuccessMessage = "Цель '$title' создана"
                    )
                }

                viewModelScope.launch {
                    kotlinx.coroutines.delay(2000)
                    _state.update { it.copy(showSuccessMessage = null) }
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
                    loadGoalData(goalId)
                    hideAddSubGoalDialog()

                    _state.update {
                        it.copy(
                            isCreatingSubGoal = false,
                            showSuccessMessage = "Подцель '$title' создана"
                        )
                    }

                    viewModelScope.launch {
                        kotlinx.coroutines.delay(2000)
                        _state.update { it.copy(showSuccessMessage = null) }
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

    fun refreshData() {
        viewModelScope.launch {
            triggerRefresh()
        }
    }
}

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