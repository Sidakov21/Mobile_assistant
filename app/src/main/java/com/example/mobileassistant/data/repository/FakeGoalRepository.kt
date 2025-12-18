//package com.example.mobileassistant.data.repository
//
//import com.example.mobileassistant.domain.model.Goal
//import com.example.mobileassistant.domain.model.SubGoal
//import com.example.mobileassistant.domain.model.Task
//import com.example.mobileassistant.domain.model.repository.GoalRepository
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.flowOf
//import java.time.LocalDateTime
//
//class FakeGoalRepository : GoalRepository {
//
//    private val goalsFlow = MutableStateFlow(
//        listOf(
//            Goal(1, "Физическое развитие", description = null),
//            Goal(2, "Изучение Kotlin", description = "Освоить разработку на Kotlin")
//        )
//    )
//
//    override fun observeGoals(userId: Int): Flow<List<Goal>> = goalsFlow
//
//    override fun observeTasks(goalId: Int): Flow<List<Task>> =
//        flowOf(
//            listOf(
//                Task(1, "Армейские отжимания", 1, 50, false, null,
//                    "Работать над стабильностью плеч", 1),
//                Task(2, "Стойка у стенки", 2, 80, true, LocalDateTime.now(),
//                    "Удерживать положение 30 секунд", 1)
//            )
//        )
//
//    override suspend fun getGoals(userId: Int): List<Goal> = goalsFlow.value
//
//    override suspend fun getGoalFull(goalId: Int): Pair<List<SubGoal>, List<Task>> {
//        val subGoals = listOf(
//            SubGoal(1, "Стойка на руках"),
//            SubGoal(2, "Подтягивания")
//        )
//        val tasks = listOf(
//            Task(1, "Армейские отжимания", 1, 50, false, null,
//                "Заметка 1", 1),
//            Task(2, "Подтягивания с резинкой", 2, 80, true,
//                LocalDateTime.now(), "Заметка 2", 2)
//        )
//        return Pair(subGoals, tasks)
//    }
//
//    override suspend fun addTask(subGoalId: Int, title: String, note: String) {
//        println("Добавлена задача: $title с заметкой: $note")
//        // Обновляем Flow если нужно
//    }
//
//    override suspend fun addSubGoal(goalId: Int, title: String, color: Int) {
//        println("Добавлена подцель: $title с цветом: $color")
//    }
//
//    override suspend fun addGoal(userId: Int, title: String, description: String?) {
//        val newGoal = Goal(
//            id = goalsFlow.value.size + 1,
//            title = title,
//            description = description
//        )
//        goalsFlow.value = goalsFlow.value + newGoal
//        println("Добавлена цель: $title")
//    }
//
//    override suspend fun updateTask(task: Task) {
//        println("Обновлена задача: ${task.title}")
//    }
//
//    override suspend fun deleteTask(taskId: Int) {
//        println("Удалена задача с ID: $taskId")
//    }
//
//    override suspend fun getSubGoalColor(subGoalId: Int): Int? {
//        return when (subGoalId) {
//            1 -> 0xFF4CAF50.toInt()
//            2 -> 0xFF2196F3.toInt()
//            else -> 0xFF4CAF50.toInt()
//        }
//    }
//
//    override suspend fun getTask(taskId: Int): Task? {
//        return when (taskId) {
//            1 -> Task(1, "Армейские отжимания", 1,50, false, null,
//                "Работать над стабильностью плеч", 1)
//            2 -> Task(2, "Стойка у стенки", 1, 80, true, LocalDateTime.now(),
//                "Удерживать положение 30 секунд", 1)
//            else -> null
//        }
//    }
//
//    override suspend fun getSubGoal(subGoalId: Int): SubGoal? {
//        return when (subGoalId) {
//            1 -> SubGoal(1, "Стойка на руках")
//            2 -> SubGoal(2, "Подтягивания")
//            else -> null
//        }
//    }
//}