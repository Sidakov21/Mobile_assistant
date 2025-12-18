package com.example.mobileassistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,

    val username: String,
    val passwordHash: String,

    val createdAt: Long = System.currentTimeMillis()
)
