package com.example.mobileassistant.ui.main

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileassistant.databinding.MainDashboardBinding
import com.example.mobileassistant.domain.model.WeekStatItem
import com.example.mobileassistant.R


class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWeekStats()
    }

    private fun setupWeekStats() {
        // Создаем список данных как в скрине
        val stats = listOf(
            WeekStatItem("Спорт", 3, Color.parseColor("#FF5252")),
            WeekStatItem("Интеллект", 1, Color.GRAY),
            WeekStatItem("Творчество", 1, Color.parseColor("#E040FB")),
            WeekStatItem("Харизма", 1, Color.parseColor("#FFD600")),
            WeekStatItem("Рутина", 0, Color.GRAY)
        )

        // Очищаем контейнер
        binding.statListContainer.removeAllViews()

        // Добавляем каждый элемент
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
            progressBar.max = 3 // Максимум 3 как в скрине

            // Устанавливаем цвет прогресс-бара
            progressBar.progressTintList = android.content.res.ColorStateList.valueOf(stat.color)

            binding.statListContainer.addView(itemView)
        }
    }
}