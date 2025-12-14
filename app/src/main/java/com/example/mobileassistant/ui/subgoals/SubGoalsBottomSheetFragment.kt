package com.example.mobileassistant.ui.subgoals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.mobileassistant.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.mobileassistant.databinding.SubgoalsBottomSheetBinding
import com.example.mobileassistant.domain.model.SubGoal
import com.example.mobileassistant.ui.tasks.TaskAdapter
import com.google.android.material.chip.Chip

class SubGoalsBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: SubgoalsBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var subGoals: List<SubGoal>
    private var selectedSubGoalIndex = 0

    interface OnSubGoalSelectedListener {
        fun onSubGoalSelected(index: Int, subGoal: SubGoal)
    }

    var listener: OnSubGoalSelectedListener? = null

    companion object {
        fun newInstance(subGoals: List<SubGoal>): SubGoalsBottomSheetFragment {
            val fragment = SubGoalsBottomSheetFragment()
            fragment.subGoals = subGoals
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        setupRecycler()
        setupSubGoalsChips()
        setupListeners()

        if (subGoals.isNotEmpty()) {
            showSubGoal(0)
        }
    }

    private fun setupRecycler() {
        taskAdapter = TaskAdapter()
        binding.tasksRecyclerView.adapter = taskAdapter

        taskAdapter.onItemClick = { task ->
            Toast.makeText(requireContext(), "Открываем: ${task.title}", Toast.LENGTH_SHORT).show()
            // TODO: Открыть экран редактирования заметки
        }
    }

    private fun setupSubGoalsChips() {
        binding.subgoalsContainer.removeAllViews()

        subGoals.forEachIndexed { index, subGoal ->
            val chip = Chip(requireContext()).apply {
                text = subGoal.name
                isCheckable = true
                isChecked = (index == selectedSubGoalIndex)
                chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                    if (index == selectedSubGoalIndex) 0xFF4CAF50.toInt() else 0xFF333333.toInt()
                )
                setTextColor(android.graphics.Color.WHITE)

                setOnClickListener {
                    selectedSubGoalIndex = index
                    showSubGoal(index)
                    listener?.onSubGoalSelected(index, subGoal)

                    // Снимаем выделение с других чипов
                    for (i in 0 until binding.subgoalsContainer.childCount) {
                        val otherChip = binding.subgoalsContainer.getChildAt(i) as Chip
                        otherChip.isChecked = (i == index)
                        otherChip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                            if (i == index) 0xFF4CAF50.toInt() else 0xFF333333.toInt()
                        )
                    }
                }
            }

            binding.subgoalsContainer.addView(chip)
        }
    }

    private fun setupListeners() {
        binding.fabAddTask.setOnClickListener {
            Toast.makeText(requireContext(), "Добавить задачу", Toast.LENGTH_SHORT).show()
        }

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            Toast.makeText(requireContext(), "Навигация: ${menuItem.title}", Toast.LENGTH_SHORT).show()
            true
        }

        binding.btnSearch.setOnClickListener {
            Toast.makeText(requireContext(), "Поиск", Toast.LENGTH_SHORT).show()
        }

        binding.btnSort.setOnClickListener {
            Toast.makeText(requireContext(), "Сортировка", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSubGoal(index: Int) {
        if (index in subGoals.indices) {
            val subGoal = subGoals[index]
            taskAdapter.submitList(subGoal.tasks)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}