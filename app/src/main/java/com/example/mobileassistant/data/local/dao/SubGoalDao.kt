package com.example.mobileassistant.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.mobileassistant.data.local.entity.SubGoalEntity
import com.example.mobileassistant.data.local.entity.SubGoalWithTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface SubGoalDao {

    @Transaction
    @Query("SELECT * FROM sub_goals WHERE goalId = :goalId")
    suspend fun getSubGoalsWithTasks(goalId: Int): List<SubGoalWithTasks>

    @Insert
    suspend fun insertSubGoal(subGoal: SubGoalEntity)

    // Получить подцели для цели
    @Query("SELECT * FROM sub_goals WHERE goalId = :goalId ORDER BY createdAt")
    suspend fun getSubGoalsByGoal(goalId: Int): List<SubGoalEntity>

    // Получить подцели с потоком
    @Query("SELECT * FROM sub_goals WHERE goalId = :goalId ORDER BY createdAt")
    fun observeSubGoalsByGoal(goalId: Int): Flow<List<SubGoalEntity>>

    // Обновить подцель
    @Update
    suspend fun updateSubGoal(subGoal: SubGoalEntity)

    // Удалить подцель
    @Delete
    suspend fun deleteSubGoal(subGoal: SubGoalEntity)
}
