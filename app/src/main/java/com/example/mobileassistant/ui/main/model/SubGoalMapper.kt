package com.example.mobileassistant.ui.main.model

import com.example.mobileassistant.data.local.entity.SubGoalEntity
import com.example.mobileassistant.domain.model.SubGoal
import com.example.mobileassistant.domain.model.SubGoalButtonUi

object SubGoalMapper {

    fun toDomain(entity: SubGoalEntity): SubGoal {
        return SubGoal(
            id = entity.subGoalId,
            title = entity.title,
            color = entity.color
        )
    }

    fun toEntity(domain: SubGoal, goalId: Int): SubGoalEntity {
        return SubGoalEntity(
            subGoalId = domain.id,
            goalId = goalId,
            title = domain.title,
            color = domain.color,
            isCompleted = false,
            createdAt = System.currentTimeMillis()
        )
    }

    fun toUi(
        subGoal: SubGoal,
        progress: Int,
        taskCount: Int,
        isSelected: Boolean = false
    ): SubGoalButtonUi {
        return SubGoalButtonUi(
            id = subGoal.id,
            title = subGoal.title,
            color = subGoal.color,
            progress = progress,
            taskCount = taskCount,
            isSelected = isSelected
        )
    }

    fun toUi(
        entity: SubGoalEntity,
        progress: Int,
        taskCount: Int,
        isSelected: Boolean = false
    ): SubGoalButtonUi {
        return SubGoalButtonUi(
            id = entity.subGoalId,
            title = entity.title,
            color = entity.color,
            progress = progress,
            taskCount = taskCount,
            isSelected = isSelected
        )
    }
}