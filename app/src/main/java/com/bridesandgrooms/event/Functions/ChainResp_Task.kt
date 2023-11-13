package com.bridesandgrooms.event.Functions

import com.bridesandgrooms.event.Model.Task

interface CoRAddEditTask {
    fun onAddEditTask(task: Task)
}

interface CoRDeleteTask {
    fun onDeleteTask(task: Task)
}