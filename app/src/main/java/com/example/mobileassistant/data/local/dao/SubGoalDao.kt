package com.example.mobileassistant.data.local.dao

import androidx.room.*
import com.example.mobileassistant.data.local.entity.GoalEntity
import com.example.mobileassistant.data.local.entity.SubGoalEntity
import com.example.mobileassistant.data.local.entity.SubGoalWithTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface SubGoalDao {
    // Получить подцели по цели (реактивно)
    @Query("SELECT * FROM sub_goals WHERE goalId = :goalId ORDER BY createdAt")
    fun observeSubGoalsByGoal(goalId: Int): Flow<List<SubGoalEntity>>

    // Получить подцели по цели (синхронно)
    @Query("SELECT * FROM sub_goals WHERE goalId = :goalId ORDER BY createdAt")
    suspend fun getSubGoalsByGoal(goalId: Int): List<SubGoalEntity>

    // Получить подцель по ID
    @Query("SELECT * FROM sub_goals WHERE subGoalId = :subGoalId")
    suspend fun getSubGoalById(subGoalId: Int): SubGoalEntity?

    // Получить подцели с задачами (используем транзакцию)
    @Transaction
    @Query("SELECT * FROM sub_goals WHERE goalId = :goalId")
    suspend fun getSubGoalsWithTasks(goalId: Int): List<SubGoalWithTasks>

    // Вставить подцель
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubGoal(subGoal: SubGoalEntity): Long

    // Обновить подцель
    @Update
    suspend fun updateSubGoal(subGoal: SubGoalEntity)

    // Удалить подцель
    @Delete
    suspend fun deleteSubGoal(subGoal: SubGoalEntity)

    // Получить количество подцелей в цели
    @Query("SELECT COUNT(*) FROM sub_goals WHERE goalId = :goalId")
    suspend fun getSubGoalCount(goalId: Int): Int

    // Получить цвет подцели
    @Query("SELECT color FROM sub_goals WHERE subGoalId = :subGoalId")
    suspend fun getSubGoalColor(subGoalId: Int): Int?

    @Query("SELECT * FROM goals WHERE goalId = :goalId")
    suspend fun getGoalById(goalId: Int): GoalEntity?
}