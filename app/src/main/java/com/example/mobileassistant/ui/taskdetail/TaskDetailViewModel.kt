package com.example.mobileassistant.ui.taskdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileassistant.domain.model.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val repository: GoalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state: StateFlow<TaskDetailState> = _state

    private var originalTaskId: Int? = null
    private var hasLoadedTask: Boolean = false

    fun loadTask(taskId: Int) {
        // Если уже загружали эту задачу, не загружаем снова
        if (hasLoadedTask && originalTaskId == taskId) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val task = repository.getTask(taskId)
                task?.let { loadedTask ->
                    originalTaskId = taskId
                    hasLoadedTask = true
                    _state.update {
                        it.copy(
                            taskId = taskId,
                            title = loadedTask.title,
                            note = loadedTask.note ?: "",
                            progress = loadedTask.progress,
                            isDone = loadedTask.isDone,
                            isLoading = false,
                            error = null,
                            isModified = false
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

    // Сбрасываем состояние при закрытии экрана
    fun resetState() {
        originalTaskId = null
        hasLoadedTask = false
        _state.update { TaskDetailState() }
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
                val taskId = currentState.taskId

                // Получаем текущую задачу
                val originalTask = repository.getTask(taskId)

                if (originalTask != null) {
                    // Обновляем задачу
                    val updatedTask = originalTask.copy(
                        title = currentState.title,
                        note = currentState.note,
                        progress = currentState.progress,
                        isDone = currentState.progress == 100,
                        completedAt = if (currentState.progress == 100 && !originalTask.isDone) {
                            System.currentTimeMillis()
                        } else {
                            originalTask.completedAt
                        }
                    )

                    repository.updateTask(updatedTask)

                    _state.update {
                        it.copy(
                            isSaving = false,
                            isModified = false,
                            showSaveSuccess = true
                        )
                    }

                    // Автоматически скрываем сообщение
                    viewModelScope.launch {
                        kotlinx.coroutines.delay(2000)
                        hideSaveSuccess()
                    }
                } else {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            error = "Задача не найдена для сохранения"
                        )
                    }
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
                repository.deleteTask(taskId)

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