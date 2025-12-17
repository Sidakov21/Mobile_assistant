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

@Database(
    entities = [
        GoalEntity::class,
        SubGoalEntity::class,
        TaskEntity::class
    ],
    version = 1,
    exportSchema = false
)
// 3. Подключаем конвертеры (создадим их на следующем шаге)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao
    abstract fun subGoalDao(): SubGoalDao
    abstract fun taskDao(): TaskDao
}
