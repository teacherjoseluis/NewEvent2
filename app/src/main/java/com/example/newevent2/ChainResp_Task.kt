package com.example.newevent2

import com.example.newevent2.Model.Task

interface CoRAddEditTask {
    fun onAddEditTask(task: Task)
}

interface CoRDeleteTask {
    fun onDeleteTask(task: Task)
}