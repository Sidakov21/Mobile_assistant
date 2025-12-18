package com.example.mobileassistant.ui.main.model

import com.example.mobileassistant.data.local.entity.GoalEntity
import com.example.mobileassistant.domain.model.Goal

object GoalMapper {

    fun toDomain(entity: GoalEntity): Goal {
        return Goal(
            id = entity.goalId,
            title = entity.title,
            description = entity.description
        )
    }

    fun toEntity(domain: Goal, userId: Int): GoalEntity {
        return GoalEntity(
            goalId = domain.id,
            userId = userId,
            title = domain.title,
            description = domain.description,
            isCompleted = false,
            createdAt = System.currentTimeMillis()
        )
    }
}