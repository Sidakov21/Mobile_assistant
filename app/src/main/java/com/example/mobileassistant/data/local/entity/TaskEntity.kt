package com.example.mobileassistant.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.mobileassistant.domain.model.Task
import java.time.LocalDateTime

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = SubGoalEntity::class,
            parentColumns = ["subGoalId"],
            childColumns = ["subGoalId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("subGoalId")]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val taskId: Int = 0,

    val subGoalId: Int,

    val title: String,

    val isDone: Boolean = false,

    // вычисляемый прогресс (для радара)
    val progress: Int = 0, // 0–100

    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null
)

fun TaskEntity.toDomain() = Task(
    id = taskId,
    title = title,
    progress = progress,
    isDone = isDone,
    completedAt = completedAt
)
