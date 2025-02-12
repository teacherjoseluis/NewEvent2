package com.bridesandgrooms.event.Functions

import android.content.Context
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.Model.User

interface CoRAddEditTask {
    fun onAddEditTask(task: Task)
}

interface CoRDeleteTask {
    fun onDeleteTask(taskId: String)
}