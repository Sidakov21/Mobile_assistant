package com.example.mobileassistant.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileassistant.databinding.MainDashboardBinding
import com.example.mobileassistant.domain.model.SubGoal
import com.example.mobileassistant.domain.model.TaskCard
import com.example.mobileassistant.domain.model.WeekStatItem
import com.example.mobileassistant.R
import com.example.mobileassistant.ui.subgoals.SubGoalsBottomSheetFragment
import android.view.GestureDetector.SimpleOnGestureListener
import androidx.core.view.GestureDetectorCompat

class MainActivity : AppCompatActivity(),
    SubGoalsBottomSheetFragment.OnSubGoalSelectedListener {

    private lateinit var binding: MainDashboardBinding
    private lateinit var gestureDetector: GestureDetectorCompat

    private lateinit var subGoals: List<SubGoal>
    private lateinit var bottomSheetFragment: SubGoalsBottomSheetFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGestureDetector()
        setupFakeData()
        setupWeekStats()
        setupBackPressedHandler()
    }

    private fun setupBackPressedHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val isFragmentVisible = supportFragmentManager
                    .findFragmentByTag("subgoals_bottom_sheet")
                    ?.isVisible ?: false

                if (isFragmentVisible) {
                    hideSubGoalsBottomSheet()
                } else {
                    finish()
                }
            }
        })
    }

    private fun setupGestureDetector() {
        gestureDetector = GestureDetectorCompat(this, object : SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null) return false

                val diffY = e1.y - e2.y
                val diffX = e1.x - e2.x

                // Проверяем фактическое состояние фрагмента
                val isFragmentVisible = supportFragmentManager
                    .findFragmentByTag("subgoals_bottom_sheet")
                    ?.isVisible ?: false

                // Если свайп вверх и достаточно большой
                if (Math.abs(diffY) > Math.abs(diffX) && diffY > 100) {
                    if (!isFragmentVisible) {
                        showSubGoalsBottomSheet()
                        return true
                    }
                }
                // Если свайп вниз - ничего не делаем с главным экраном
                // BottomSheet сам закроется при свайпе вниз внутри себя
                return false
            }
        })

        // Обработка касаний на корневом layout
        binding.root.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event) || false
        }
    }

    private fun setupFakeData() {
        val mainGoal = "Освоить гимнастику" // Главная цель

        subGoals = listOf(
            SubGoal(
                id = 1,
                name = "Стойка",
                tasks = listOf(
                    TaskCard(title = "Армейские отжимания", note = "Работать над стабильностью плеч, контроль корпуса. Выполнять 3 подхода по 15 раз."),
                    TaskCard(title ="Стойка у стены", note = "30–60 секунд, ровное дыхание. Спина прямая, ноги вместе."),
                    TaskCard(title = "Выход в стойку", note ="Работа над балансом. Пробовать отрывать ноги от стены на 1-2 секунды.")
                )
            ),
            SubGoal(
                id = 2,
                name = "Горизонт",
                tasks = listOf(
                    TaskCard(title = "Тяга в упоре", note ="Лопатки сведены, спина прямая. 4 подхода по 10 раз."),
                    TaskCard(title ="Негативы", note ="Контроль опускания, медленное движение. 3 подхода по 5 раз."),
                    TaskCard(title ="Стойка на локтях", note ="Баланс и контроль положения тела. 5 подходов по 20 секунд.")
                )
            ),
            SubGoal(
                id = 3,
                name = "Планш",
                tasks = listOf(
                    TaskCard(title ="Отжимания", note ="Сила трицепсов, полная амплитуда. 4 подхода по 12 раз."),
                    TaskCard(title ="Стойка на руках", note ="Баланс, работа с партнером. 5 подходов по 30 секунд."),
                    TaskCard(title ="Прогресс в планше", note ="Постепенное смещение центра тяжести. Тренировать ежедневно.")
                )
            )
        )

        // Передаем главную цель и подцели
        bottomSheetFragment = SubGoalsBottomSheetFragment.newInstance(subGoals, mainGoal)
        bottomSheetFragment.listener = this
    }

    private fun setupWeekStats() {
        val stats = listOf(
            WeekStatItem("Спорт", 3, Color.parseColor("#FF5252")),
            WeekStatItem("Интеллект", 1, Color.GRAY),
            WeekStatItem("Творчество", 1, Color.parseColor("#E040FB")),
            WeekStatItem("Харизма", 1, Color.parseColor("#FFD600")),
            WeekStatItem("Рутина", 0, Color.GRAY)
        )

        binding.statListContainer.removeAllViews()

        stats.forEach { stat ->
            val itemView = layoutInflater.inflate(
                R.layout.item_week_stat,
                binding.statListContainer,
                false
            )

            val textCategory = itemView.findViewById<android.widget.TextView>(R.id.tv_category)
            val progressBar = itemView.findViewById<android.widget.ProgressBar>(R.id.progress_bar)
            val textValue = itemView.findViewById<android.widget.TextView>(R.id.tv_value)

            textCategory.text = stat.name
            textValue.text = stat.value.toString()
            progressBar.progress = stat.value
            progressBar.max = 3

            progressBar.progressTintList = android.content.res.ColorStateList.valueOf(stat.color)

            binding.statListContainer.addView(itemView)
        }
    }

    private fun showSubGoalsBottomSheet() {
        // Проверяем, виден ли уже фрагмент
        val existingFragment = supportFragmentManager.findFragmentByTag("subgoals_bottom_sheet")
        if (existingFragment == null || !existingFragment.isVisible) {
            // БЕЗ АНИМАЦИИ - просто показываем
            bottomSheetFragment.show(supportFragmentManager, "subgoals_bottom_sheet")
        }
    }

    private fun hideSubGoalsBottomSheet() {
        val existingFragment = supportFragmentManager.findFragmentByTag("subgoals_bottom_sheet")
        if (existingFragment != null && existingFragment.isVisible) {
            // БЕЗ АНИМАЦИИ - просто закрываем
            (existingFragment as SubGoalsBottomSheetFragment).dismiss()
        }
    }

    override fun onSubGoalSelected(index: Int, subGoal: SubGoal) {
        // Можно обновить заголовок главной цели
        // Например: binding.tvMainGoal.text = subGoal.name
    }
}