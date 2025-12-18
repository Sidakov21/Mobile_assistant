package com.example.mobileassistant.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mobileassistant.data.local.entity.TaskEntity
import com.example.mobileassistant.data.local.entity.TaskWithSubGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE subGoalId IN (:subGoalIds)")
    suspend fun getTasksBySubGoals(subGoalIds: List<Int>): List<TaskEntity>

    @Insert
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("""
    SELECT tasks.* FROM tasks
    INNER JOIN sub_goals ON tasks.subGoalId = sub_goals.subGoalId
    WHERE sub_goals.goalId = :goalId
""")
    fun observeTasksByGoal(goalId: Int): Flow<List<TaskEntity>>

    // Получить задачи для конкретной подцели
    @Query("SELECT * FROM tasks WHERE subGoalId = :subGoalId ORDER BY createdAt DESC")
    suspend fun getTasksBySubGoal(subGoalId: Int): List<TaskEntity>

    // Получить задачи с информацией о подцели
    @Query("""
        SELECT t.*, sg.title as subGoalTitle, sg.color as subGoalColor
        FROM tasks t
        INNER JOIN sub_goals sg ON t.subGoalId = sg.subGoalId
        WHERE t.subGoalId = :subGoalId
        ORDER BY t.createdAt DESC
    """)
    suspend fun getTasksWithSubGoal(subGoalId: Int): List<TaskWithSubGoal>

    // Получить прогресс подцели (процент выполненных задач)
    @Query("""
        SELECT 
            CASE 
                WHEN COUNT(*) = 0 THEN 0
                ELSE (SUM(CASE WHEN isDone THEN 1 ELSE 0 END) * 100.0 / COUNT(*))
            END as progress
        FROM tasks 
        WHERE subGoalId = :subGoalId
    """)
    suspend fun getSubGoalProgress(subGoalId: Int): Int

    // Получить количество активных задач за последние 7 дней для подцели
    @Query("""
        SELECT COUNT(*) 
        FROM tasks 
        WHERE subGoalId = :subGoalId 
        AND completedAt >= :sevenDaysAgo
        AND isDone = 1
    """)
    suspend fun getWeeklyActivity(subGoalId: Int, sevenDaysAgo: Long): Int

}
