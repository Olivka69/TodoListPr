package com.example.todolistpr

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.todolistpr.databinding.FragmentNewTaskSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDate
import java.time.LocalTime

class NewTaskSheet(var taskItem: TaskItem?) : BottomSheetDialogFragment()
{
    private lateinit var binding: FragmentNewTaskSheetBinding
    private lateinit var taskViewModel: TaskViewModel
    private var dueTime: LocalTime? = null
    private var dueDate: LocalDate? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        if (taskItem != null)
        {
            binding.taskTitle.text = "Редактировать"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(taskItem!!.name)
            binding.desc.text = editable.newEditable(taskItem!!.desc)
            binding.empl.text = editable.newEditable(taskItem!!.empl)
            if(taskItem!!.dueTime() != null)
            {
                dueTime = taskItem!!.dueTime()!!
                updateTimeButtonText()
            }
            if(taskItem!!.dueDate() != null)
            {
                dueDate = taskItem!!.dueDate()!!
                updateDateButtonText()
            }
        }
        else
        {
            binding.taskTitle.text = "Новая задача"
        }

        taskViewModel = ViewModelProvider(activity).get(TaskViewModel::class.java)
        binding.saveButton.setOnClickListener {
            saveAction()
        }
        binding.timePickerButton.setOnClickListener {
            openTimePicker()
        }
        binding.datePickerButton.setOnClickListener {
            openDatePicker()
        }
    }

    private fun openDatePicker() {
        if(dueDate == null)
            dueDate = LocalDate.now()
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            dueDate = LocalDate.of(year, month+1, dayOfMonth)
            updateDateButtonText() }
        val dialog = DatePickerDialog( requireContext(), dateListener, dueDate!!.year, dueDate!!.monthValue - 1, dueDate!!.dayOfMonth)
        dialog.setTitle("Установить дату")
        dialog.show()
    }

    private fun updateDateButtonText() {
        binding.datePickerButton.text = String.format("%02d.%02d.%04d", dueDate!!.dayOfMonth, dueDate!!.monthValue, dueDate!!.year)
    }

    private fun openTimePicker() {
        if(dueTime == null)
            dueTime = LocalTime.now()
        val listener = TimePickerDialog.OnTimeSetListener{ _, selectedHour, selectedMinute ->
            dueTime = LocalTime.of(selectedHour, selectedMinute)
            updateTimeButtonText()
        }
        val dialog = TimePickerDialog(activity, listener, dueTime!!.hour, dueTime!!.minute, true)
        dialog.setTitle("Установить время")
        dialog.show()

    }

    private fun updateTimeButtonText() {
        binding.timePickerButton.text = String.format("%02d:%02d",dueTime!!.hour,dueTime!!.minute)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewTaskSheetBinding.inflate(inflater,container,false)
        return binding.root
    }


    private fun saveAction()
    {
        val name = binding.name.text.toString()
        val desc = binding.desc.text.toString()
        val empl = binding.empl.text.toString()
        val dueTimeString = if(dueTime == null) null else TaskItem.timeFormatter.format(dueTime)
        val dueDateString = if(dueDate == null) null else TaskItem.dateFormatter.format(dueDate)
        if(taskItem == null)
        {
            val newTask = TaskItem(name,desc, empl, dueTimeString,dueDateString, null)
            taskViewModel.addTaskItem(newTask)
        }
        else
        {
            taskItem!!.name = name
            taskItem!!.desc = name
            taskItem!!.empl = name
            taskItem!!.dueTimeString = dueTimeString
            taskItem!!.dueDateString = dueDateString

            taskViewModel.updateTaskItem(taskItem!!)
        }
        binding.name.setText("")
        binding.desc.setText("")
        binding.empl.setText("")
        dismiss()
    }

}








