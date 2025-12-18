package com.example.mobileassistant.ui.taskdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskDetailViewModel : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state: StateFlow<TaskDetailState> = _state

    fun loadTask(taskId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // TODO: Загрузить задачу из репозитория по taskId
                // Пока используем заглушку
                _state.update {
                    it.copy(
                        taskId = taskId,
                        title = "Задача $taskId",
                        note = "Описание задачи $taskId",
                        progress = 50,
                        isLoading = false
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
                // TODO: Сохранить задачу в репозитории
                Thread.sleep(500) // Имитация сохранения

                _state.update {
                    it.copy(
                        isSaving = false,
                        isModified = false,
                        showSaveSuccess = true
                    )
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
                // TODO: Удалить задачу из репозитория
                Thread.sleep(500) // Имитация удаления

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
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isModified: Boolean = false,
    val error: String? = null,
    val showSaveSuccess: Boolean = false,
    val shouldClose: Boolean = false
)