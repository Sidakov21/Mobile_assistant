package com.example.mobileassistant.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.mobileassistant.domain.model.SubGoal
import java.time.LocalDateTime

@Entity(
    tableName = "sub_goals",
    foreignKeys = [
        ForeignKey(
            entity = GoalEntity::class,
            parentColumns = ["goalId"],
            childColumns = ["goalId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("goalId")]
)
data class SubGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val subGoalId: Int = 0,

    val goalId: Int,

    val title: String,
    val description: String? = null,

    val isCompleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

fun SubGoalEntity.toDomain() = SubGoal(
    id = subGoalId,
    title = title
)
