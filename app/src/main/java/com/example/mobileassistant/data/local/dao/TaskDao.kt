package com.example.mobileassistant.data.local.dao

import androidx.room.*
import com.example.mobileassistant.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // Получить задачи по подцели
    @Query("SELECT * FROM tasks WHERE subGoalId = :subGoalId ORDER BY createdAt DESC")
    fun observeTasksBySubGoal(subGoalId: Int): Flow<List<TaskEntity>>

    // Получить задачи по цели (через подцели)
    @Query("""
        SELECT tasks.* FROM tasks
        INNER JOIN sub_goals ON tasks.subGoalId = sub_goals.subGoalId
        WHERE sub_goals.goalId = :goalId
        ORDER BY tasks.createdAt DESC
    """)
    fun observeTasksByGoal(goalId: Int): Flow<List<TaskEntity>>

    // Получить все задачи
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun observeAllTasks(): Flow<List<TaskEntity>>

    // Получить задачу по ID
    @Query("SELECT * FROM tasks WHERE taskId = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    // Получить задачи по ID подцели
    @Query("SELECT * FROM tasks WHERE subGoalId = :subGoalId ORDER BY createdAt DESC")
    suspend fun getTasksBySubGoal(subGoalId: Int): List<TaskEntity>

    // Получить прогресс подцели
    @Query("""
        SELECT 
            CASE 
                WHEN COUNT(*) = 0 THEN 0
                ELSE (SUM(CASE WHEN isDone THEN 1 ELSE 0 END) * 100.0 / COUNT(*))
            END
        FROM tasks 
        WHERE subGoalId = :subGoalId
    """)
    suspend fun getSubGoalProgress(subGoalId: Int): Int

    // Получить количество выполненных задач за последние 7 дней
    @Query("""
        SELECT COUNT(*) 
        FROM tasks 
        WHERE subGoalId = :subGoalId 
        AND completedAt >= :sevenDaysAgo
        AND isDone = 1
    """)
    suspend fun getWeeklyActivity(subGoalId: Int, sevenDaysAgo: Long): Int

    // Вставить задачу
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    // Обновить задачу
    @Update
    suspend fun updateTask(task: TaskEntity)

    // Удалить задачу
    @Delete
    suspend fun deleteTask(task: TaskEntity)

    // Отметить задачу как выполненную
    @Query("UPDATE tasks SET isDone = 1, progress = 100, completedAt = :completedAt WHERE taskId = :taskId")
    suspend fun completeTask(taskId: Int, completedAt: Long)

    // Обновить прогресс задачи
    @Query("UPDATE tasks SET progress = :progress WHERE taskId = :taskId")
    suspend fun updateTaskProgress(taskId: Int, progress: Int)

    // Получить количество задач в подцели
    @Query("SELECT COUNT(*) FROM tasks WHERE subGoalId = :subGoalId")
    suspend fun getTaskCount(subGoalId: Int): Int

    // Получить количество выполненных задач в подцели
    @Query("SELECT COUNT(*) FROM tasks WHERE subGoalId = :subGoalId AND isDone = 1")
    suspend fun getCompletedTaskCount(subGoalId: Int): Int
}