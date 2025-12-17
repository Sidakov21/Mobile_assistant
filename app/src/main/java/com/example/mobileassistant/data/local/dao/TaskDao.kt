package com.example.mobileassistant.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mobileassistant.data.local.entity.TaskEntity
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

}
