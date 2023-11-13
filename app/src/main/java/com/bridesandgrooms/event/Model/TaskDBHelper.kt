package com.bridesandgrooms.event.Model

import Application.FirebaseDataImportException
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.bridesandgrooms.event.Model.Category.Companion.getCategory
import com.bridesandgrooms.event.Functions.CoRAddEditTask
import com.bridesandgrooms.event.Functions.CoRDeleteTask
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.DecimalFormat

class TaskDBHelper(val context: Context) : CoRAddEditTask, CoRDeleteTask{

    lateinit var task: Task
    var key = ""
    var nexthandler: CoRAddEditTask? = null
    var nexthandlerdel: CoRDeleteTask? = null

    @ExperimentalCoroutinesApi
    suspend fun firebaseImport(userid: String): Boolean {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
        val taskList: ArrayList<Task>
        val eventModel = EventModel()
        try {
            db.execSQL("DELETE FROM TASK")
            val eventKey = eventModel.getEventKey(userid)
            val taskModel = TaskModel()
            taskList = taskModel.getTasks(userid, eventKey)
            for (taskItem in taskList) {
                insert(taskItem)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw FirebaseDataImportException("Error importing Task data: $e")
        }
        finally {
            db.close()
        }
        return true
    }

    fun insert(task: Task) {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
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
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        } finally {
            db.close()
        }
    }

    private fun getTaskexists(key: String): Boolean {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
        var existsflag = false
        try {
            val cursor: Cursor = db.rawQuery("SELECT * FROM TASK WHERE taskid = '$key'", null)
            if (cursor.count > 0) {
                existsflag = true
            }
            cursor.close()
            return existsflag
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return false
        } finally {
            db.close()
        }
    }

    @SuppressLint("Range")
    fun getTasks(): ArrayList<Task>? {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
        val list = ArrayList<Task>()
        try {
            val cursor: Cursor = db.rawQuery(
                "SELECT * FROM TASK WHERE taskid IS NOT NULL AND taskid !='' ORDER BY createdatetime DESC",
                null
            )
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
                    val createdatetime =
                        cursor.getString(cursor.getColumnIndex("createdatetime"))
                    val task =
                        Task(
                            taskid,
                            name,
                            date,
                            category,
                            budget,
                            status,
                            eventid,
                            createdatetime
                        )
                    list.add(task)
                    Log.d(TAG, "Task $taskid record obtained from local DB")
                } while (cursor.moveToNext())
            }
            cursor.close()
            return list
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
        } finally {
            db.close()
        }
    }

    fun getTasksNames(): ArrayList<String>? {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
        val list = ArrayList<String>()
        try {
            val cursor: Cursor = db.rawQuery(
                "SELECT name FROM TASK WHERE taskid IS NOT NULL AND taskid !='' ORDER BY createdatetime DESC",
                null
            )
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    list.add(name)
                } while (cursor.moveToNext())
            }
            cursor.close()
            return list
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
        } finally {
            db.close()
        }
    }

    fun getActiveCategories(): ArrayList<Category>? {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
        val list = ArrayList<Category>()
        try {
            val cursor: Cursor =
                db.rawQuery(
                    "SELECT DISTINCT category FROM (SELECT category FROM TASK WHERE status = 'A' UNION ALL SELECT category FROM PAYMENT)",
                    null
                )
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val category = cursor.getString(cursor.getColumnIndex("category"))
                    val taskcategory = getCategory(category)
                    list.add(taskcategory)
                } while (cursor.moveToNext())
            }
            cursor.close()
            return list
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
        } finally {
            db.close()
        }
    }

    fun getCategoryStats(category: String): TaskStatsToken? {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
        val taskstats = TaskStatsToken()
        var sumbudget = 0.0F
        try {
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
                db.rawQuery(
                    "SELECT budget FROM TASK WHERE category='$category' and status='A'",
                    null
                )
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
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
        } finally {
            db.close()
        }
    }

    fun update(task: Task) {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
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
            val retVal = db.update("TASK", values, "taskid = '${task.key}'", null)
            if (retVal >= 1) {
                Log.d(TAG, "Task ${task.key} updated")
            } else {
                Log.d(TAG, "Task ${task.key} not updated")
            }
            //db.close()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        } finally {
            db.close()
        }
    }

    fun delete(task: Task) {
        val db: SQLiteDatabase = DatabaseHelper(context).writableDatabase
        try {
            val retVal = db.delete("TASK", "taskid = '${task.key}'", null)
            if (retVal >= 1) {
                Log.d(TAG, "Task ${task.key} deleted")
            } else {
                Log.d(TAG, "Task ${task.key} not deleted")
            }
            //db.close()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        } finally {
            db.close()
        }
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
