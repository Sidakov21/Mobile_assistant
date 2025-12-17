package com.example.mobileassistant.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.mobileassistant.data.local.entity.SubGoalEntity
import com.example.mobileassistant.data.local.entity.SubGoalWithTasks

@Dao
interface SubGoalDao {

    @Transaction
    @Query("SELECT * FROM sub_goals WHERE goalId = :goalId")
    suspend fun getSubGoalsWithTasks(goalId: Int): List<SubGoalWithTasks>

    @Insert
    suspend fun insertSubGoal(subGoal: SubGoalEntity)
}
