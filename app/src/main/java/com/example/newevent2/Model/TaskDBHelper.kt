package com.example.newevent2.Model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.newevent2.Category
import com.example.newevent2.Category.Companion.getCategory
import com.example.newevent2.CoRAddEditTask
import com.example.newevent2.CoRDeleteTask
import com.example.newevent2.Functions.removeDuplicates
import java.text.DecimalFormat

class TaskDBHelper(val context: Context) : CoRAddEditTask, CoRDeleteTask {

    private val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
    lateinit var task: Task
    var key = ""
    var nexthandler: CoRAddEditTask? = null
    var nexthandlerdel: CoRDeleteTask? = null

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

    fun getTaskexists(key: String): Boolean {
        var existsflag = false
        val cursor: Cursor = db.rawQuery("SELECT * FROM TASK WHERE taskid = '$key'", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                existsflag = true
            }
        }
        return existsflag
    }

    fun getTasks(): ArrayList<Task> {
        val list = ArrayList<Task>()
        val cursor: Cursor = db.rawQuery("SELECT * FROM TASK WHERE taskid IS NOT NULL AND taskid !='' ORDER BY createdatetime DESC", null)
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

    fun getActiveCategories(): ArrayList<Category> {
        val list = ArrayList<Category>()
        val cursor: Cursor =
            db.rawQuery("SELECT DISTINCT category FROM TASK WHERE status = 'A'", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val category = cursor.getString(cursor.getColumnIndex("category"))
                    val taskcategory = getCategory(category)
                    list.add(taskcategory)
                } while (cursor.moveToNext())
            }
        }
        return list
    }

    fun getCategoryStats(category: String): TaskStatsToken {
        var taskstats = TaskStatsToken()
        var sumbudget = 0.0F
        var cursor: Cursor = db.rawQuery(
            "SELECT COUNT(*) as taskpending FROM TASK WHERE category='$category' and status='A'",
            null
        )
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    taskstats.taskpending = cursor.getInt(cursor.getColumnIndex("taskpending"))
                } while (cursor.moveToNext())
            }
        }
        cursor = db.rawQuery(
            "SELECT COUNT(*) as taskcompleted FROM TASK WHERE category='$category' and status='C'",
            null
        )
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    taskstats.taskcompleted = cursor.getInt(cursor.getColumnIndex("taskcompleted"))
                } while (cursor.moveToNext())
            }
        }
        cursor =
            db.rawQuery("SELECT budget FROM TASK WHERE category='$category' and status='A'", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                val re = Regex("[^A-Za-z0-9 ]")
                do {
                    val budget = cursor.getString(cursor.getColumnIndex("budget"))
                    val budgetamount = re.replace(budget, "").dropLast(2)
                    sumbudget += budgetamount.toFloat()
                } while (cursor.moveToNext())
                val formatter = DecimalFormat("$#,###.00")
                taskstats.sumbudget = formatter.format(sumbudget)
            }
        }
        return taskstats
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

        val retVal = db.update("TASK", values, "taskid = '${task.key}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Task ${task.key} updated")
        } else {
            Log.d(TAG, "Task ${task.key} not updated")
        }
        db.close()
    }

    fun delete(task: Task) {
        val retVal = db.delete("TASK", "taskid = '${task.key}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Task ${task.key} deleted")
        } else {
            Log.d(TAG, "Task ${task.key} not deleted")
        }
        db.close()
    }

    override fun onAddEditTask(task: Task) {
        if (!getTaskexists(task.key)) {
            insert(task)
        } else {
            update(task)
        }
        nexthandler?.onAddEditTask(task)
    }

    override fun onDeleteTask(task: Task) {
        delete(task)
        nexthandlerdel?.onDeleteTask(task)
    }

    companion object {
        const val TAG = "TaskDBHelper"
    }
}
