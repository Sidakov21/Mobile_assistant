package com.example.mobileassistant.data.local.dao

import androidx.room.*
import com.example.mobileassistant.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    // Получить цели пользователя
    @Query("SELECT * FROM goals WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeGoalsByUser(userId: Int): Flow<List<GoalEntity>>

    // Получить цели пользователя (синхронно)
    @Query("SELECT * FROM goals WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getGoalsByUser(userId: Int): List<GoalEntity>

    // Получить цель по ID
    @Query("SELECT * FROM goals WHERE goalId = :goalId")
    suspend fun getGoalById(goalId: Int): GoalEntity?

    // Получить цель с подцелями
    @Transaction
    @Query("SELECT * FROM goals WHERE goalId = :goalId")
    suspend fun getGoalWithSubGoals(goalId: Int): com.example.mobileassistant.data.local.entity.GoalWithSubGoals

    // Вставить цель
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    // Обновить цель
    @Update
    suspend fun updateGoal(goal: GoalEntity)

    // Удалить цель
    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    // Получить последнюю цель пользователя
    @Query("SELECT * FROM goals WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastGoal(userId: Int): GoalEntity?
}