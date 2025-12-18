package com.example.mobileassistant.ui.taskdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileassistant.domain.model.Task
import com.example.mobileassistant.domain.model.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TaskDetailViewModel(
    private val repository: GoalRepository?
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state: StateFlow<TaskDetailState> = _state

    private var originalTask: Task? = null

    fun loadTask(taskId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val task = if (repository is com.example.mobileassistant.data.repository.GoalRepositoryImpl) {
                    repository.getTask(taskId)
                } else {
                    // Fallback для тестирования
                    Task(
                        id = taskId,
                        subGoalId = 1,
                        title = "Задача $taskId",
                        progress = 50,
                        isDone = false,
                        completedAt = null,
                        note = "Описание задачи $taskId"
                    )
                }

                task?.let {
                    originalTask = it
                    _state.update {
                        it.copy(
                            taskId = taskId,
                            title = it.title,
                            note = it.note,
                            progress = it.progress,
                            isDone = it.isDone,
                            isLoading = false
                        )
                    }
                } ?: run {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Задача не найдена"
                        )
                    }
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

    fun updateTitle(title: String) {
        _state.update { it.copy(title = title, isModified = true) }
    }

    fun updateNote(note: String) {
        _state.update { it.copy(note = note, isModified = true) }
    }

    fun updateProgress(progress: Int) {
        _state.update { it.copy(progress = progress, isModified = true) }
    }

    fun saveTask() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            try {
                val currentState = _state.value

                if (repository is com.example.mobileassistant.data.repository.GoalRepositoryImpl) {
                    // Получаем текущую задачу
                    val original = originalTask
                    if (original != null) {
                        // Обновляем задачу
                        val updatedTask = Task(
                            id = original.id,
                            subGoalId = original.subGoalId,
                            title = currentState.title,
                            progress = currentState.progress,
                            isDone = currentState.isDone,
                            completedAt = if (currentState.progress == 100)
                                LocalDateTime.now()
                            else
                                original.completedAt,
                            note = currentState.note,
                            createdAt = original.createdAt
                        )

                        repository.updateTask(updatedTask)

                        // Если прогресс 100%, отмечаем как выполненную
                        if (currentState.progress == 100 && !original.isDone) {
                            repository.completeTask(original.id)
                        }
                    } else {
                        // Создаем новую задачу (если нужно)
                        // TODO: Реализовать создание новой задачи
                    }
                }

                _state.update {
                    it.copy(
                        isSaving = false,
                        isModified = false,
                        showSaveSuccess = true
                    )
                }

                // Автоматически скрываем успешное сообщение через 2 секунды
                viewModelScope.launch {
                    kotlinx.coroutines.delay(2000)
                    hideSaveSuccess()
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        error = "Ошибка сохранения: ${e.message}"
                    )
                }
            }
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }

            try {
                val taskId = _state.value.taskId

                if (repository is com.example.mobileassistant.data.repository.GoalRepositoryImpl) {
                    repository.deleteTask(taskId)
                }

                _state.update {
                    it.copy(
                        isDeleting = false,
                        shouldClose = true
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isDeleting = false,
                        error = "Ошибка удаления: ${e.message}"
                    )
                }
            }
        }
    }

    fun toggleDone() {
        val current = _state.value
        val newProgress = if (current.isDone) 0 else 100

        _state.update {
            it.copy(
                isDone = !current.isDone,
                progress = newProgress,
                isModified = true
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun hideSaveSuccess() {
        _state.update { it.copy(showSaveSuccess = false) }
    }
}

data class TaskDetailState(
    val taskId: Int = 0,
    val title: String = "",
    val note: String = "",
    val progress: Int = 0,
    val isDone: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isModified: Boolean = false,
    val error: String? = null,
    val showSaveSuccess: Boolean = false,
    val shouldClose: Boolean = false
)