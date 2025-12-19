package com.example.mobileassistant.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mobileassistant.data.local.entity.GoalEntity
import com.example.mobileassistant.data.local.entity.SubGoalEntity
import com.example.mobileassistant.data.local.entity.TaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.concurrent.Executors

object DatabaseInitializer {

    private val executor = Executors.newSingleThreadExecutor()

    object TimeUtils {
        // Возвращает timestamp для "N дней назад"
        fun daysAgo(days: Long): Long {
            return System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000)
        }
    }

    fun initialize(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mobile-assistant-db"
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Executors.newSingleThreadExecutor().execute {
                        prePopulateDatabase(context)
                    }
                }
            })
            .fallbackToDestructiveMigration()
            .build()
    }

    private fun prePopulateDatabase(context: Context) {
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mobile-assistant-db"
        ).allowMainThreadQueries().build()

        val goalDao = db.goalDao()
        val subGoalDao = db.subGoalDao()
        val taskDao = db.taskDao()

        // Очищаем существующие данные
        // (В реальном приложении этого не делаем, только для теста)

        // Создаем тестовые данные
        CoroutineScope(Dispatchers.IO).launch {
            // Цель 1: Физическое развитие
            val goalId1 = goalDao.insertGoal(
                GoalEntity(
                    userId = 1,
                    title = "Физическое развитие",
                    description = "Улучшить физическую форму и здоровье",
                    isCompleted = false,
                    createdAt = TimeUtils.daysAgo(30)
                )
            ).toInt()

            // Подцели для цели 1
            val subGoal1Id = subGoalDao.insertSubGoal(
                SubGoalEntity(
                    goalId = goalId1,
                    title = "Стойка на руках",
                    color = 0xFF4CAF50.toInt(),
                    isCompleted = false,
                    createdAt = TimeUtils.daysAgo(30)

                )
            ).toInt()

            val subGoal2Id = subGoalDao.insertSubGoal(
                SubGoalEntity(
                    goalId = goalId1,
                    title = "Подтягивания",
                    color = 0xFF2196F3.toInt(),
                    isCompleted = false,
                    createdAt = TimeUtils.daysAgo(25)

                )
            ).toInt()

            val subGoal3Id = subGoalDao.insertSubGoal(
                SubGoalEntity(
                    goalId = goalId1,
                    title = "Отжимания",
                    color = 0xFFFF9800.toInt(),
                    isCompleted = false,
                    createdAt = TimeUtils.daysAgo(20)
                )
            ).toInt()

            // Задачи для подцели 1
            taskDao.insertTask(
                TaskEntity(
                    subGoalId = subGoal1Id,
                    title = "Армейские отжимания",
                    note = "Работать над стабильностью плеч, контроль корпуса",
                    progress = 50,
                    isDone = false,
                    createdAt = TimeUtils.daysAgo(7)
                )
            )

            taskDao.insertTask(
                TaskEntity(
                    subGoalId = subGoal1Id,
                    title = "Стойка у стенки",
                    note = "Удерживать положение 30 секунд",
                    progress = 80,
                    isDone = true,
                    completedAt = TimeUtils.daysAgo(7),
                    createdAt = TimeUtils.daysAgo(1)
                )
            )

            taskDao.insertTask(
                TaskEntity(
                    subGoalId = subGoal1Id,
                    title = "Стойка на согнутых",
                    note = "Тренировка баланса",
                    progress = 30,
                    isDone = false,
                    createdAt = TimeUtils.daysAgo(5)
                )
            )

            // Задачи для подцели 2
            taskDao.insertTask(
                TaskEntity(
                    subGoalId = subGoal2Id,
                    title = "Подтягивания с резинкой",
                    note = "3 подхода по 8 повторений",
                    progress = 70,
                    isDone = true,
                    completedAt = TimeUtils.daysAgo(2),
                    createdAt = TimeUtils.daysAgo(10)
                )
            )

            taskDao.insertTask(
                TaskEntity(
                    subGoalId = subGoal2Id,
                    title = "Подтягивания широким хватом",
                    note = "Работа на ширину спины",
                    progress = 40,
                    isDone = false,
                    createdAt = TimeUtils.daysAgo(3)
                )
            )

            // Задачи для подцели 3
            taskDao.insertTask(
                TaskEntity(
                    subGoalId = subGoal3Id,
                    title = "Отжимания с упором",
                    note = "Тренировка техники",
                    progress = 90,
                    isDone = true,
                    completedAt = TimeUtils.daysAgo(5),
                    createdAt = TimeUtils.daysAgo(12)
                )
            )

            taskDao.insertTask(
                TaskEntity(
                    subGoalId = subGoal3Id,
                    title = "Алмазные отжимания",
                    note = "Для развития трицепса",
                    progress = 60,
                    isDone = false,
                    createdAt = TimeUtils.daysAgo(2)
                )
            )

            // Цель 2: Программирование
            val goalId2 = goalDao.insertGoal(
                GoalEntity(
                    userId = 1,
                    title = "Изучение Kotlin",
                    description = "Освоить разработку на Kotlin для Android",
                    isCompleted = false,
                    createdAt = TimeUtils.daysAgo(15)
                )
            ).toInt()

            // Подцели для цели 2
            val subGoal4Id = subGoalDao.insertSubGoal(
                SubGoalEntity(
                    goalId = goalId2,
                    title = "Основы Kotlin",
                    color = 0xFF9C27B0.toInt(),
                    isCompleted = true,
                    createdAt = TimeUtils.daysAgo(15)
                )
            ).toInt()

            val subGoal5Id = subGoalDao.insertSubGoal(
                SubGoalEntity(
                    goalId = goalId2,
                    title = "Android Development",
                    color = 0xFF607D8B.toInt(),
                    isCompleted = false,
                    createdAt = TimeUtils.daysAgo(8)
                )
            ).toInt()

            // Задачи для подцели 4
            taskDao.insertTask(
                TaskEntity(
                    subGoalId = subGoal4Id,
                    title = "Изучить основы синтаксиса",
                    note = "Переменные, функции, классы",
                    progress = 100,
                    isDone = true,
                    completedAt = TimeUtils.daysAgo(12),
                    createdAt = TimeUtils.daysAgo(15)
                )
            )

            taskDao.insertTask(
                TaskEntity(
                    subGoalId = subGoal4Id,
                    title = "Освоить корутины",
                    note = "Async/await, потоки, scope",
                    progress = 100,
                    isDone = true,
                    completedAt = TimeUtils.daysAgo(8),
                    createdAt = TimeUtils.daysAgo(12)
                )
            )

            // Задачи для подцели 5
            taskDao.insertTask(
                TaskEntity(
                    subGoalId = subGoal5Id,
                    title = "Изучить Compose",
                    note = "UI компоненты, состояние, навигация",
                    progress = 75,
                    isDone = false,
                    createdAt = TimeUtils.daysAgo(8)
                )
            )

            // В prePopulateDatabase добавляем задачи с недавними датами:

            // Задача, созданная сегодня
            taskDao.insertTask(
                TaskEntity(
                    subGoalId = subGoal1Id,
                    title = "Новая тренировка",
                    note = "Тренировка на баланс",
                    progress = 50,
                    isDone = false,
                    createdAt = System.currentTimeMillis() - (2L * 60 * 60 * 1000) // 2 часа назад
                )
            )

            // Задача, выполненная сегодня
            taskDao.insertTask(
                TaskEntity(
                    subGoalId = subGoal1Id,
                    title = "Утренняя разминка",
                    note = "Выполнено успешно",
                    progress = 100,
                    isDone = true,
                    createdAt = System.currentTimeMillis() - (5L * 60 * 60 * 1000), // 5 часов назад
                    completedAt = System.currentTimeMillis() - (4L * 60 * 60 * 1000) // 4 часа назад
                )
            )

            // Задача с прогрессом 75% (вчера)
            taskDao.insertTask(
                TaskEntity(
                    subGoalId = subGoal2Id,
                    title = "Силовая тренировка",
                    note = "Хороший результат",
                    progress = 75,
                    isDone = false,
                    createdAt = System.currentTimeMillis() - (25L * 60 * 60 * 1000) // 25 часов назад
                )
            )

            db.close()
        }

    }
}