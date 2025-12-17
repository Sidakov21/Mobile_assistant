package com.example.mobileassistant.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["taskId"],
            childColumns = ["taskId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("taskId")]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val noteId: Int = 0,

    val taskId: Int,
    val note: String?,

    val createdAt: LocalDateTime = LocalDateTime.now()
)
