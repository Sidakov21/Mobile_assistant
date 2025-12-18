package com.example.mobileassistant.data.repository

import com.example.mobileassistant.domain.model.Goal
import com.example.mobileassistant.domain.model.SubGoal
import com.example.mobileassistant.domain.model.Task
import com.example.mobileassistant.domain.model.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime

class FakeGoalRepository : GoalRepository {

    override fun observeTasks(goalId: Int): Flow<List<Task>> =
        flowOf(
            listOf(
                Task(1, "Первая задача", 50, false, null, note = "Пример заметки 1"),
                Task(2, "Вторая задача", 80, true, LocalDateTime.now(), note = "Пример заметки 2")
            )
        )

    override suspend fun getGoals(userId: Int): List<Goal> =
        listOf(Goal(
            1, "Главная цель",
            description = null
        ))

    override suspend fun getGoalFull(goalId: Int): Pair<List<SubGoal>, List<Task>> {
        val subGoals = listOf(
            SubGoal(1, "Подцель 1"),
            SubGoal(2, "Подцель 2")
        )
        val tasks = listOf(
            Task(1, "Первая задача", 50, false, null, note = "Заметка 1"),
            Task(2, "Вторая задача", 80, true, LocalDateTime.now(), note = "Заметка 2")
        )
        return Pair(subGoals, tasks)
    }

    override suspend fun addTask(subGoalId: Int, title: String, note: String) {
        // Заглушка для тестирования
        println("Добавлена задача: $title с заметкой: $note")
    }

    override suspend fun addSubGoal(goalId: Int, title: String, color: Int) {
        // Заглушка для тестирования
        println("Добавлена подцель: $title с цветом: $color")
    }

    override suspend fun addGoal(userId: Int, title: String, description: String?) {
        // Заглушка для тестирования
        println("Добавлена цель: $title с описанием: $description")
    }

    override suspend fun updateTask(task: Task) {
        // Заглушка для тестирования
        println("Обновлена задача: ${task.title}")
    }

    override suspend fun deleteTask(taskId: Int) {
        // Заглушка для тестирования
        println("Удалена задача с ID: $taskId")
    }
}