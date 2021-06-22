package com.example.newevent2.Model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.newevent2.rvCategoryAdapter
import java.lang.Exception

class TaskDBHelper(val context: Context) {

    private val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase

    fun insert(task: Task) {
        var values = ContentValues()
        values.put("taskid", task.key)
        values.put("name", task.name)
        values.put("date", task.date)
        values.put("category", task.category)
        values.put("budget", task.budget)
        values.put("status", task.status)
        values.put("eventid", task.eventid)
        values.put("createdatetime", task.createdatetime)
        db.insert("TASK", null, values)
        Log.d(TAG, "Task record inserted")
    }

    fun getTasks(): ArrayList<Task> {
        val list = ArrayList<Task>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM TASK ORDER BY createdatetime DESC", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val taskid = cursor.getString(cursor.getColumnIndex("taskid"))
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    val date = cursor.getString(cursor.getColumnIndex("date"))
                    val category = cursor.getString(cursor.getColumnIndex("category"))
                    val budget = cursor.getString(cursor.getColumnIndex("budget"))
                    val status = cursor.getString(cursor.getColumnIndex("status"))
                    val eventid = cursor.getString(cursor.getColumnIndex("eventid"))
                    val createdatetime = cursor.getString(cursor.getColumnIndex("createdatetime"))
                    val task =
                        Task(taskid, name, date, category, budget, status, eventid, createdatetime)
                    list.add(task)
                    Log.d(TAG, "Task $taskid record obtained from local DB")
                } while (cursor.moveToNext())
            }
        }
        return list
    }

    fun update(task: Task) {
        var values = ContentValues()
        values.put("taskid", task.key)
        values.put("name", task.name)
        values.put("date", task.date)
        values.put("category", task.category)
        values.put("budget", task.budget)
        values.put("status", task.status)
        values.put("eventid", task.eventid)
        values.put("createdatetime", task.createdatetime)

        val retVal = db.update("TASK", values, "taskid = " + task.key, null)
        if (retVal >= 1) {
            Log.d(TAG, "Task ${task.key} updated")
        } else {
            Log.d(TAG, "Task ${task.key} not updated")
        }
        db.close()
    }

    fun delete(task: Task) {
        val retVal = db.delete("TASK", "taskid = " + task.key, null)
        if (retVal >= 1) {
            Log.d(TAG, "Task ${task.key} deleted")
        } else {
            Log.d(TAG, "Task ${task.key} not deleted")
        }
        db.close()
    }

    companion object {
        const val TAG = "TaskDBHelper"
    }
}
