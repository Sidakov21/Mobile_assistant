package com.example.mobileassistant.ui.subgoals

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileassistant.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.mobileassistant.databinding.SubgoalsBottomSheetBinding
import com.example.mobileassistant.domain.model.SubGoal
import com.example.mobileassistant.ui.taskdetail.TaskDetailActivity
import com.example.mobileassistant.ui.tasks.TaskAdapter
import com.google.android.material.chip.Chip

class SubGoalsBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: SubgoalsBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskAdapter: TaskAdapter // Изменяем на TaskAdapter, если используете его
    private lateinit var subGoals: List<SubGoal>
    private var selectedSubGoalIndex = 0

    // Главная цель (будет передаваться из MainActivity)
    private var mainGoal = "Основная цель"

    interface OnSubGoalSelectedListener {
        fun onSubGoalSelected(index: Int, subGoal: SubGoal)
    }

    var listener: OnSubGoalSelectedListener? = null

    companion object {
        fun newInstance(subGoals: List<SubGoal>, mainGoal: String): SubGoalsBottomSheetFragment {
            val fragment = SubGoalsBottomSheetFragment()
            fragment.subGoals = subGoals
            fragment.mainGoal = mainGoal
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SubgoalsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Устанавливаем заголовок главной цели
        binding.tvMainGoal.text = mainGoal

        setupRecycler()
        setupSubGoalsChips()
        setupListeners()

        if (subGoals.isNotEmpty()) {
            showSubGoal(0)
        }
    }

    private fun setupRecycler() {
        // Инициализируем taskAdapter (а не taskCardAdapter)
        taskAdapter = TaskAdapter()
        binding.tasksRecyclerView.adapter = taskAdapter

        // Настройка RecyclerView
        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        taskAdapter.onItemClick = { task, position ->
            // Закрываем BottomSheet
            dismiss()

            // Запускаем Activity с деталями задачи
            val intent = Intent(requireContext(), TaskDetailActivity::class.java).apply {
                putExtra("task_title", task.title)
                putExtra("task_note", task.note)
                putExtra("task_position", position)
                putExtra("subgoal_index", selectedSubGoalIndex)
            }

            // Добавляем анимацию перехода
            startActivity(intent)
            requireActivity().overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }
    }

    private fun setupSubGoalsChips() {
        binding.subgoalsContainer.removeAllViews()

        subGoals.forEachIndexed { index, subGoal ->
            val chip = Chip(requireContext()).apply {
                text = subGoal.name
                isCheckable = true
                isChecked = (index == selectedSubGoalIndex)
                setChipBackgroundColorResource(
                    if (index == selectedSubGoalIndex) R.color.chip_selected else R.color.chip_unselected
                )
                setTextColor(resources.getColor(android.R.color.white, null))

                // Стиль чипа
                chipStrokeWidth = 1f
                chipStrokeColor = resources.getColorStateList(R.color.chip_stroke, null)
                setChipIconResource(R.drawable.ic_target_24)
                chipIconTint = resources.getColorStateList(R.color.chip_icon_tint, null)

                setOnClickListener {
                    selectedSubGoalIndex = index
                    showSubGoal(index)
                    listener?.onSubGoalSelected(index, subGoal)

                    // Обновляем состояние всех чипов
                    updateChipsSelection()
                }
            }

            binding.subgoalsContainer.addView(chip)
        }
    }

    private fun updateChipsSelection() {
        for (i in 0 until binding.subgoalsContainer.childCount) {
            val chip = binding.subgoalsContainer.getChildAt(i) as Chip
            chip.isChecked = (i == selectedSubGoalIndex)
            chip.setChipBackgroundColorResource(
                if (i == selectedSubGoalIndex) R.color.chip_selected else R.color.chip_unselected
            )
        }
    }

    private fun setupListeners() {
        binding.fabAddTask.setOnClickListener {
            Toast.makeText(requireContext(), "Добавить новую задачу", Toast.LENGTH_SHORT).show()
            // TODO: В будущем - открыть диалог добавления задачи
        }

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_subgoals -> {
                    Toast.makeText(requireContext(), "Уже на экране подцелей", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_stats -> {
                    Toast.makeText(requireContext(), "Статистика (в разработке)", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(requireContext(), "Профиль (в разработке)", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        binding.btnSearch.setOnClickListener {
            Toast.makeText(requireContext(), "Поиск (в разработке)", Toast.LENGTH_SHORT).show()
        }

        binding.btnSort.setOnClickListener {
            Toast.makeText(requireContext(), "Сортировка (в разработке)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSubGoal(index: Int) {
        if (index in subGoals.indices) {
            val subGoal = subGoals[index]
            taskAdapter.submitList(subGoal.tasks) // Используем taskAdapter

            // Если нет задач, показываем сообщение
            if (subGoal.tasks.isEmpty()) {
                Toast.makeText(requireContext(), "Нет задач для этой подцели", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}