package com.example.mobileassistant.ui.subgoals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileassistant.domain.model.Task
import com.example.mobileassistant.domain.model.repository.GoalRepository
import com.example.mobileassistant.ui.main.model.DomainMapper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SubGoalsViewModel(
    private val repository: GoalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SubGoalsState())
    val state: StateFlow<SubGoalsState> = _state.asStateFlow()

    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 0)
    private var currentGoalId: Int = 1

    init {
        setupDataObservers()
    }

    private fun setupDataObservers() {
        viewModelScope.launch {
            merge(refreshTrigger)
                .collect { loadSubGoals(currentGoalId) }
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

                // Получаем название цели
                val goals = repository.getGoals(userId = 1)
                val goal = goals.find { it.id == goalId }
                val goalTitle = goal?.title ?: "Цель"

                // Создаем UI модели для подцелей
                val subGoalButtons = subGoals.map { subGoal ->
                    val subGoalTasks = tasks.filter { it.subGoalId == subGoal.id }
                    val progress = calculateSubGoalProgress(subGoal.id, tasks)
                    DomainMapper.subGoal.toUi(
                        subGoal = subGoal,
                        progress = progress,
                        taskCount = subGoalTasks.size,
                        isSelected = _state.value.selectedSubGoal?.id == subGoal.id
                    )
                }

                // Создаем UI модели для задач
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val allTasks = tasks.map { task ->
                    val subGoal = subGoals.find { it.id == task.subGoalId }
                    DomainMapper.task.toUi(
                        task = task,
                        subGoalTitle = subGoal?.title ?: "",
                        subGoalColor = subGoal?.color ?: 0xFF4CAF50.toInt()
                    )
                }

                // Применяем текущий фильтр
                val filteredTasks = applyFilter(
                    allTasks = allTasks,
                    selectedSubGoalId = _state.value.selectedSubGoal?.id,
                    searchQuery = _state.value.searchQuery
                )

                _state.update {
                    it.copy(
                        goalTitle = goalTitle,
                        subGoals = subGoalButtons,
                        allTasks = allTasks,
                        filteredTasks = filteredTasks,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка загрузки: ${e.message}"
                    )
                }
            }
        }
    }

    private fun applyFilter(
        allTasks: List<com.example.mobileassistant.domain.model.TaskCardUi>,
        selectedSubGoalId: Int?,
        searchQuery: String
    ): List<com.example.mobileassistant.domain.model.TaskCardUi> {
        var filtered = allTasks

        if (selectedSubGoalId != null) {
            filtered = filtered.filter { it.subGoalId == selectedSubGoalId }
        }

        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter { task ->
                task.title.contains(searchQuery, ignoreCase = true) ||
                        task.note.contains(searchQuery, ignoreCase = true) ||
                        task.subGoalTitle.contains(searchQuery, ignoreCase = true)
            }
        }

        return filtered
    }

    private fun calculateSubGoalProgress(subGoalId: Int, tasks: List<Task>): Int {
        val subGoalTasks = tasks.filter { it.subGoalId == subGoalId }
        if (subGoalTasks.isEmpty()) return 0

        val completedTasks = subGoalTasks.count { it.isDone }
        return (completedTasks * 100) / subGoalTasks.size
    }

    fun selectSubGoal(subGoalId: Int?) {
        _state.update { current ->
            val updatedSubGoals = current.subGoals.map {
                it.copy(isSelected = it.id == subGoalId)
            }

            val filteredTasks = applyFilter(
                allTasks = current.allTasks,
                selectedSubGoalId = subGoalId,
                searchQuery = current.searchQuery
            )

            current.copy(
                subGoals = updatedSubGoals,
                selectedSubGoal = updatedSubGoals.find { it.id == subGoalId },
                filteredTasks = filteredTasks
            )
        }
    }

    fun searchTasks(query: String) {
        _state.update { current ->
            val filtered = applyFilter(
                allTasks = current.allTasks,
                selectedSubGoalId = current.selectedSubGoal?.id,
                searchQuery = query
            )

            current.copy(
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
                triggerRefresh()

                _state.update {
                    it.copy(
                        isAddingTask = false,
                        showSuccessMessage = "Задача '$title' добавлена",
                        showAddTaskDialog = false
                    )
                }

                viewModelScope.launch {
                    kotlinx.coroutines.delay(2000)
                    _state.update { it.copy(showSuccessMessage = null) }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isAddingTask = false,
                        error = "Ошибка: ${e.message}"
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
        triggerRefresh()
    }
}

data class SubGoalsState(
    val goalTitle: String = "",
    val subGoals: List<com.example.mobileassistant.domain.model.SubGoalButtonUi> = emptyList(),
    val selectedSubGoal: com.example.mobileassistant.domain.model.SubGoalButtonUi? = null,
    val allTasks: List<com.example.mobileassistant.domain.model.TaskCardUi> = emptyList(),
    val filteredTasks: List<com.example.mobileassistant.domain.model.TaskCardUi> = emptyList(),
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