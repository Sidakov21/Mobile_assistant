package com.example.mobileassistant.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.mobileassistant.data.local.entity.GoalEntity
import com.example.mobileassistant.data.local.entity.GoalWithSubGoals

@Dao
interface GoalDao {

    @Query("SELECT * FROM goals WHERE userId = :userId")
    suspend fun getGoalsByUser(userId: Int): List<GoalEntity>

    @Transaction
    @Query("SELECT * FROM goals WHERE goalId = :goalId")
    suspend fun getGoalWithSubGoals(goalId: Int): GoalWithSubGoals

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)
}
