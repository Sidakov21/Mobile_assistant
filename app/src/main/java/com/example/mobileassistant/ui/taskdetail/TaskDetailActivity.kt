package com.example.mobileassistant.ui.taskdetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileassistant.databinding.ActivityTaskDetailBinding
import com.example.mobileassistant.domain.model.TaskCard

class TaskDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskDetailBinding
    private var taskTitle: String = ""
    private var taskNote: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем данные из Intent
        taskTitle = intent.getStringExtra("task_title") ?: "Новая задача"
        taskNote = intent.getStringExtra("task_note") ?: ""

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        // Устанавливаем заголовок и заметку
        binding.etTaskTitle.setText(taskTitle)
        binding.etTaskNote.setText(taskNote)

        // Настраиваем Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Задача"
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Кнопка сохранения
        binding.btnSave.setOnClickListener {
            saveTask()
        }

        // Кнопка удаления
        binding.btnDelete.setOnClickListener {
            deleteTask()
        }
    }

    private fun saveTask() {
        val newTitle = binding.etTaskTitle.text.toString().trim()
        val newNote = binding.etTaskNote.text.toString().trim()

        if (newTitle.isEmpty()) {
            binding.etTaskTitle.error = "Введите название задачи"
            return
        }

        // TODO: Здесь будет логика сохранения в базу данных
        // Создаем новую задачу с обновленными данными
        val updatedTask = TaskCard(title = newTitle, note = newNote)

        // Возвращаем результат
        intent.putExtra("updated_task_title", newTitle)
        intent.putExtra("updated_task_note", newNote)
        setResult(RESULT_OK, intent)

        finish()
    }

    private fun deleteTask() {
        // TODO: Здесь будет логика удаления
        setResult(RESULT_CANCELED)
        finish()
    }
}