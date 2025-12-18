package com.example.mobileassistant.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.mobileassistant.domain.model.Goal
import java.time.LocalDateTime

@Entity(
    tableName = "goals",
//    foreignKeys = [
//        ForeignKey(
//            entity = UserEntity::class,
//            parentColumns = ["userId"],
//            childColumns = ["userId"],
//            onDelete = CASCADE
//        )
//    ],
//    indices = [Index("userId")]
)
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val goalId: Int = 0,

    val userId: Int,

    val title: String,
    val description: String? = null,

    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

fun GoalEntity.toDomain() = Goal(
    id = goalId,
    title = title,
    description = description
)
