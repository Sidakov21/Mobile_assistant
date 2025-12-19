package com.example.mobileassistant.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mobileassistant.data.local.converter.Converters
import com.example.mobileassistant.data.local.dao.GoalDao
import com.example.mobileassistant.data.local.dao.SubGoalDao
import com.example.mobileassistant.data.local.dao.TaskDao
import com.example.mobileassistant.data.local.entity.GoalEntity
import com.example.mobileassistant.data.local.entity.SubGoalEntity
import com.example.mobileassistant.data.local.entity.TaskEntity
import com.example.mobileassistant.data.local.entity.UserEntity
import com.example.mobileassistant.data.local.entity.NoteEntity

@Database(
    entities = [
        GoalEntity::class,
        SubGoalEntity::class,
        TaskEntity::class,
        NoteEntity::class,
        UserEntity::class
    ],
    version = 3,
    exportSchema = false
)
// 3. Подключаем конвертеры (создадим их на следующем шаге)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao
    abstract fun subGoalDao(): SubGoalDao
    abstract fun taskDao(): TaskDao

    // TODO: Добавить при необходимости
    // abstract fun noteDao(): NoteDao
    // abstract fun userDao(): UserDaoA
}
