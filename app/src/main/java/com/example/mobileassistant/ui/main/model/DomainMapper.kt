package com.example.mobileassistant.ui.main.model

import com.example.mobileassistant.data.local.entity.GoalEntity
import com.example.mobileassistant.data.local.entity.SubGoalEntity
import com.example.mobileassistant.data.local.entity.TaskEntity
import com.example.mobileassistant.domain.model.Goal
import com.example.mobileassistant.domain.model.SubGoal
import com.example.mobileassistant.domain.model.SubGoalButtonUi
import com.example.mobileassistant.domain.model.Task
import com.example.mobileassistant.domain.model.TaskCardUi

object DomainMapper {

    // Goal
    fun GoalEntity.toDomain(): Goal = GoalMapper.toDomain(this)
    fun Goal.toEntity(userId: Int): GoalEntity = GoalMapper.toEntity(this, userId)

    // SubGoal
    fun SubGoalEntity.toDomain(): SubGoal = SubGoalMapper.toDomain(this)
    fun SubGoal.toEntity(goalId: Int): SubGoalEntity = SubGoalMapper.toEntity(this, goalId)
    fun SubGoal.toUi(progress: Int, taskCount: Int, isSelected: Boolean = false): SubGoalButtonUi =
        SubGoalMapper.toUi(this, progress, taskCount, isSelected)

    // Task
    fun TaskEntity.toDomain(): Task = TaskMapper.toDomain(this)
    fun Task.toEntity(): TaskEntity = TaskMapper.toEntity(this)
    fun Task.toUi(subGoalTitle: String, subGoalColor: Int): TaskCardUi =
        TaskMapper.toUi(this, subGoalTitle, subGoalColor)

    // Быстрый доступ к мапперам
    val goal: GoalMapper get() = GoalMapper
    val subGoal: SubGoalMapper get() = SubGoalMapper
    val task: TaskMapper get() = TaskMapper
}
