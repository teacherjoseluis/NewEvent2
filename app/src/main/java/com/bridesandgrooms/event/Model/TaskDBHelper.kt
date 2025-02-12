package com.bridesandgrooms.event.Model

import Application.FirebaseDataImportException
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import com.applandeo.materialcalendarview.CalendarDay
import com.bridesandgrooms.event.Model.Category.Companion.getCategory
import com.bridesandgrooms.event.Functions.CoRAddEditTask
import com.bridesandgrooms.event.Functions.CoRDeleteTask
import com.bridesandgrooms.event.Functions.convertToDBString
import com.bridesandgrooms.event.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaskDBHelper() : CoRAddEditTask, CoRDeleteTask {

    var nexthandler: CoRAddEditTask? = null
    var nexthandlerdel: CoRDeleteTask? = null

    @ExperimentalCoroutinesApi
    suspend fun firebaseImport(uid: String): Boolean {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val taskList: ArrayList<Task>
        val eventModel = EventModel()
        try {
            db.execSQL("DELETE FROM TASK")
            val taskModel = TaskModel()
            taskList = taskModel.getTasks()
            for (taskItem in taskList) {
                insert(taskItem)
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            throw FirebaseDataImportException("Error importing Task data: $e")
//        } finally {
//            db.close()
        }
        return true
    }

    fun insert(task: Task) {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
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
//        } finally {
//            db.close()
        }
    }

    private fun getTaskexists(key: String): Boolean {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
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
//        } finally {
//            db.close()
        }
    }

    @SuppressLint("Range")
    fun getTasks(): ArrayList<Task>? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
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
//        } finally {
//            db.close()
        }
    }

    fun getTasksNames(): ArrayList<String>? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val list = ArrayList<String>()
        try {
            val cursor: Cursor = db.rawQuery(
                "SELECT name FROM TASK WHERE taskid IS NOT NULL AND taskid !='' ORDER BY createdatetime DESC",
                null
            )
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    list.add(name)
                } while (cursor.moveToNext())
            }
            cursor.close()
            return list
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
//        } finally {
//            db.close()
        }
    }

    fun getTaskfromDate(date: Date): ArrayList<String>? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val dateString = convertToDBString(date)
        val nameList = arrayListOf<String>()
        try {
            val cursor: Cursor = db.rawQuery(
                "SELECT name FROM TASK WHERE taskid IS NOT NULL AND taskid !='' AND date='$dateString' AND status = 'A' ORDER BY createdatetime DESC",
                null
            )
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    nameList.add(name)
                } while (cursor.moveToNext())
            }
            cursor.close()
            return nameList
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
//        } finally {
//            db.close()
        }
    }

    fun getUpcomingTasks(): ArrayList<String>? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val currentDate = SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(Date())
        val taskDetailsList = arrayListOf<String>()
        try {
            // Fetch all tasks that are active, regardless of date
            val cursor: Cursor = db.rawQuery(
                "SELECT name, date FROM TASK WHERE taskid IS NOT NULL AND taskid != '' AND status = 'A'",
                null
            )
            if (cursor.count > 0) {
                val dbFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
                cursor.moveToFirst()
                do {
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val dateString = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                    val date = dbFormat.parse(dateString) ?: continue // Skip if parsing fails

                    // Compare dates within the app logic
                    if (date.after(Date())) {
                        val newFormat = SimpleDateFormat("MMM, dd", Locale.getDefault())
                        val formattedDate = newFormat.format(date)
                        taskDetailsList.add("$name - $formattedDate")
                    }
                } while (cursor.moveToNext())

                // Sort by date after fetching
                taskDetailsList.sortWith(Comparator { o1, o2 ->
                    val date1 = dbFormat.parse(o1.substringAfter(" - "))
                    val date2 = dbFormat.parse(o2.substringAfter(" - "))
                    date1.compareTo(date2)
                })
            }
            cursor.close()
            return taskDetailsList
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
//        } finally {
//            db.close()
        }
    }

    @SuppressLint("Range")
    fun getDateTaskArray(date: Date): ArrayList<Task>? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val dateString = convertToDBString(date)
        val list = ArrayList<Task>()
        try {
            val cursor: Cursor = db.rawQuery(
                "SELECT * FROM TASK WHERE taskid IS NOT NULL AND taskid !='' AND date='$dateString' AND status = 'A' ORDER BY createdatetime DESC",
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
                } while (cursor.moveToNext())
            }
            cursor.close()
            return list
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
//        } finally {
//            db.close()
        }
    }

    fun getTasksFromMonthYear(month: Int, year: Int): List<Date>? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().readableDatabase
        val calendarDayList = mutableListOf<Date>()

        try {
            // Calculate start and end dates for the given month and year
            val startDate = LocalDate.of(year, month + 1, 1) // month is zero-based, so we add 1
            val endDate = startDate.plusMonths(1).minusDays(1) // end of the month
            val dateFormat = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.getDefault())

            val cursor: Cursor = db.rawQuery(
                "SELECT DISTINCT date FROM TASK " +
                        "WHERE taskid IS NOT NULL AND taskid != '' " +
                        "AND date BETWEEN '${startDate.format(dateFormat)}' AND '${
                            endDate.format(
                                dateFormat
                            )
                        }' " +
                        "AND status = 'A' ORDER BY date ASC",
                null
            )

            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val dateString = cursor.getString(cursor.getColumnIndexOrThrow("date"))

                    // Define your date format matching "d/m/yyyy"
                    val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
                    val date = dateFormat.parse(dateString)
                    calendarDayList.add(date)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching tasks from month and year: ${e.message}")
            return null
//        } finally {
//            db.close()
        }
        return calendarDayList
    }

    fun getTaskBudget(context: Context): Float? {
        try {
            val taskDBHelper = TaskDBHelper()
            return taskDBHelper.getTasksBudget()!!
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
        }
    }

    fun getActiveCategories(): ArrayList<Category>? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
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
                    val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                    val taskcategory = getCategory(category)
                    list.add(taskcategory)
                } while (cursor.moveToNext())
            }
            cursor.close()
            return list
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
//        } finally {
//            db.close()
        }
    }

    fun getCategoryStats(category: String): TaskStatsToken? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
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
                    taskstats.taskpending =
                        cursor.getInt(cursor.getColumnIndexOrThrow("taskpending"))
                } while (cursor.moveToNext())
            }
            cursor = db.rawQuery(
                "SELECT COUNT(*) as taskcompleted FROM TASK WHERE category='$category' and status='C'",
                null
            )
            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    taskstats.taskcompleted =
                        cursor.getInt(cursor.getColumnIndexOrThrow("taskcompleted"))
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
                        val budget = cursor.getString(cursor.getColumnIndexOrThrow("budget"))
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
//        } finally {
//            db.close()
        }
    }

    fun getTaskPDFBudgetReport(): TaskPDFBudgetReport? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        val taskPdfBudget = TaskPDFBudgetReport()
        var sumActiveBudget = 0.0F
        var sumCompletebudget = 0.0F
        try {
            var cursor: Cursor = db.rawQuery(
                "SELECT budget FROM TASK WHERE status='A'",
                null
            )
            if (cursor != null) {
                if (cursor.count > 0) {
                    cursor.moveToFirst()
                    val re = Regex("[^A-Za-z0-9 ]")
                    do {
                        val budget = cursor.getString(cursor.getColumnIndexOrThrow("budget"))
                        val budgetamount = re.replace(budget, "").dropLast(2)
                        sumActiveBudget += budgetamount.toFloat()
                    } while (cursor.moveToNext())
                    val formatter = DecimalFormat("$#,###.00")
                    taskPdfBudget.budgetTasksActive = formatter.format(sumActiveBudget)
                } else {
                    taskPdfBudget.budgetTasksActive = "$0.00"
                }
            }
            cursor = db.rawQuery(
                "SELECT budget FROM TASK WHERE status='C'",
                null
            )
            if (cursor != null) {
                if (cursor.count > 0) {
                    cursor.moveToFirst()
                    val re = Regex("[^A-Za-z0-9 ]")
                    do {
                        val budget = cursor.getString(cursor.getColumnIndexOrThrow("budget"))
                        val budgetamount = re.replace(budget, "").dropLast(2)
                        sumCompletebudget += budgetamount.toFloat()
                    } while (cursor.moveToNext())
                    val formatter = DecimalFormat("$#,###.00")
                    taskPdfBudget.budgetTasksCompleted = formatter.format(sumActiveBudget)
                } else {
                    taskPdfBudget.budgetTasksCompleted = "$0.00"
                }
            }
            cursor.close()
            return taskPdfBudget
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
//        } finally {
//            db.close()
        }
    }

    fun getTasksBudget(): Float? {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        var sumBudget = 0.0F
        try {
            var cursor: Cursor = db.rawQuery(
                "SELECT budget FROM TASK WHERE status IN ('A', 'C')",
                null
            )
            if (cursor != null) {
                if (cursor.count > 0) {
                    cursor.moveToFirst()
                    val re = Regex("[^A-Za-z0-9 ]")
                    do {
                        val budget = cursor.getString(cursor.getColumnIndexOrThrow("budget"))
                        val budgetamount = re.replace(budget, "").dropLast(2)
                        sumBudget += budgetamount.toFloat()
                    } while (cursor.moveToNext())
                    //val formatter = DecimalFormat("$#,###.00")
                    //taskPdfBudget.budgetTasksActive = formatter.format(sumBudget)
                    //} else {
                    //    taskPdfBudget.budgetTasksActive = "$0.00"
                }
            }
            cursor.close()
            return sumBudget
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            return null
//        } finally {
//            db.close()
        }
    }

    fun update(task: Task) {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
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
//        } finally {
//            db.close()
        }
    }

    fun delete(taskId: String) {
        val db: SQLiteDatabase = DatabaseHelper.getInstance().writableDatabase
        try {
            val retVal = db.delete("TASK", "taskid = '$taskId'", null)
            if (retVal >= 1) {
                Log.d(TAG, "Task $taskId deleted")
            } else {
                Log.d(TAG, "Task $taskId not deleted")
            }
            //db.close()
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
//        } finally {
//            db.close()
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

    override fun onDeleteTask(taskId: String) {
        delete(taskId)
        nexthandlerdel?.onDeleteTask(taskId)
    }

    companion object {
        const val TAG = "TaskDBHelper"
    }
}
