package com.example.newevent2.Model

import Application.FirebaseDataImportException
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.newevent2.Category
import com.example.newevent2.Category.Companion.getCategory
import com.example.newevent2.CoRAddEditTask
import com.example.newevent2.CoRDeleteTask
import com.google.android.gms.common.stats.StatsUtils.getEventKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.DecimalFormat

class TaskDBHelper(val context: Context) : CoRAddEditTask, CoRDeleteTask {

    private val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
    lateinit var task: Task
    var key = ""
    var nexthandler: CoRAddEditTask? = null
    var nexthandlerdel: CoRDeleteTask? = null

    @ExperimentalCoroutinesApi
    suspend fun firebaseImport(userid: String) : Boolean {
        val taskList: ArrayList<Task>
        val eventModel = EventModel()
        try {
            db.execSQL("DELETE FROM TASK")
            val eventKey = eventModel.getEventKey(userid)
            val taskModel = TaskModel()
            taskList = taskModel.getTasks(userid, eventKey)
            for (taskItem in taskList){
                insert(taskItem)
            }
        } catch (e: Exception){
            println(e.message)
            throw FirebaseDataImportException("Error importing Task data: $e")
        }
        return true
    }

    fun insert(task: Task) {
        val values = ContentValues()
        values.put("taskid", task.key)
        values.put("name", task.name)
        values.put("date", task.date)
        values.put("category", task.category)
        values.put("budget", task.budget)
        values.put("status", task.status)
        values.put("eventid", task.eventid)
        values.put("createdatetime", task.createdatetime)
        try {
            db.insert("TASK", null, values)
            Log.d(TAG, "Task record inserted ${task.name}")
        }
        catch (e: Exception)
        {
            println(e.message)
        }
    }

    private fun getTaskexists(key: String): Boolean {
        var existsflag = false
        val cursor: Cursor = db.rawQuery("SELECT * FROM TASK WHERE taskid = '$key'", null)
        if (cursor != null) {
            if (cursor.count > 0) {
                existsflag = true
            }
        }
        cursor.close()
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
        cursor.close()
        return list
    }

    fun getActiveCategories(): ArrayList<Category> {
        val list = ArrayList<Category>()
        val cursor: Cursor =
            db.rawQuery("SELECT DISTINCT category FROM (SELECT category FROM TASK WHERE status = 'A' UNION ALL SELECT category FROM PAYMENT)", null)
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
        cursor.close()
        return list
    }

    fun getCategoryStats(category: String): TaskStatsToken {
        val taskstats = TaskStatsToken()
        var sumbudget = 0.0F
        var cursor: Cursor = db.rawQuery(
            "SELECT COUNT(*) as taskpending FROM TASK WHERE category='$category' and status='A'",
            null
        )
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                taskstats.taskpending = cursor.getInt(cursor.getColumnIndex("taskpending"))
            } while (cursor.moveToNext())
        }
        cursor = db.rawQuery(
            "SELECT COUNT(*) as taskcompleted FROM TASK WHERE category='$category' and status='C'",
            null
        )
        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            do {
                taskstats.taskcompleted = cursor.getInt(cursor.getColumnIndex("taskcompleted"))
            } while (cursor.moveToNext())
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
        cursor.close()
        return taskstats
    }

    fun update(task: Task) {
        val values = ContentValues()
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
        //db.close()
    }

    fun delete(task: Task) {
        val retVal = db.delete("TASK", "taskid = '${task.key}'", null)
        if (retVal >= 1) {
            Log.d(TAG, "Task ${task.key} deleted")
        } else {
            Log.d(TAG, "Task ${task.key} not deleted")
        }
        //db.close()
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
