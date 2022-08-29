package com.example.newevent2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Category.Companion.getCategory
import com.example.newevent2.Functions.deleteTask
import com.example.newevent2.Functions.editTask
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskDBHelper
import com.example.newevent2.Model.TaskModel
import com.example.newevent2.Model.UserModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class Rv_TaskAdapter(val taskList: MutableList<Task>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchAdapterAction {

    lateinit var context: Context
    var taskmodel = TaskModel()
    lateinit var taskdbhelper: TaskDBHelper
    lateinit var usermodel: UserModel

    private val DEFAULT_VIEW_TYPE = 1
    private val NATIVE_AD_VIEW_TYPE = 2

    //--------------------------------------------------
    // For Native Ad Implementation
    override fun getItemViewType(position: Int): Int {
        if (position != 0 && position % 4 == 0) {
            return NATIVE_AD_VIEW_TYPE
        }
        return DEFAULT_VIEW_TYPE
    }
    //--------------------------------------------------

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        lateinit var genericViewHolder: RecyclerView.ViewHolder
        when (p1) {
            DEFAULT_VIEW_TYPE -> {
                val v = LayoutInflater.from(p0.context)
                    .inflate(R.layout.task_item_layout, p0, false)
                context = p0.context
                genericViewHolder = TaskViewHolder(v)
            }
            NATIVE_AD_VIEW_TYPE -> {
                val v = LayoutInflater.from(p0.context)
                    .inflate(R.layout.native_ad_layout, p0, false)
                context = p0.context
                genericViewHolder = NativeAdViewHolder(v)
            }
        }
        return genericViewHolder
    }

//    override fun onCreateViewHolder(p0: ViewGroup, p1: Int) : ViewHolder {
//        when(p1){
//            DEFAULT_VIEW_TYPE-> {
//                val v = LayoutInflater.from(p0?.context).inflate(R.layout.task_item_layout, p0, false)
//                context = p0.context
//                return ViewHolder(v)
//            }
//            NATIVE_AD_VIEW_TYPE-> {
//                val v = LayoutInflater.from(p0?.context).inflate(R.layout.task_item_layout, p0, false)
//                context = p0.context
//                return ViewHolder(v)
//            }
//            else -> {
//                val v = LayoutInflater.from(p0?.context).inflate(R.layout., p0, false)
//                context = p0.context
//                return ViewHolder(v)
//            }
//        }
//    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        when (p0) {
            is TaskViewHolder -> {
                p0.taskname?.text = taskList[p1].name
                p0.taskcategory?.text = taskList[p1].category
                p0.taskdate?.text = taskList[p1].date
                p0.taskbudget?.text = taskList[p1].budget
                val resourceId = context.resources.getIdentifier(
                    getCategory(taskList[p1].category).drawable, "drawable",
                    context.packageName
                )
                p0.categoryavatar?.setImageResource(resourceId)

                p0.itemView.setOnClickListener {
                    val taskdetail = Intent(context, TaskCreateEdit::class.java)
                    taskdetail.putExtra("task", taskList[p1])
                    context.startActivity(taskdetail)
                }
            }
            is NativeAdViewHolder -> {
                val adLoader = AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
                    .forNativeAd {
//                        NativeAd.OnNativeAdLoadedListener {
                        Log.d("AD1015", it.responseInfo.toString())
                        Log.d("AD1015", it.mediaContent.toString())
                        populateNativeAdView(it, p0.nativeAdView)
//                            p0.nativeAdView!!.visibility=View.VISIBLE
//                            p0.nativeAdView!!.setNativeAd(it)
//                        }
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            Log.d("AD1015", p0.message)
                        }
                    })
                    .build()
                adLoader.loadAd(AdRequest.Builder().build())
            }
        }
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
        adView.mediaView = adView.findViewById(R.id.ad_media)

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        //(adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)
        adView.mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP)

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskname: TextView? = itemView.findViewById(R.id.taskname)
        val taskcategory: TextView? = itemView.findViewById(R.id.taskcategory)
        val taskdate: TextView? = itemView.findViewById(R.id.taskdate)
        val taskbudget: TextView? = itemView.findViewById(R.id.taskbudgets)
        val categoryavatar = itemView.findViewById<ImageView>(R.id.categoryavatar)!!
    }

    class NativeAdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nativeAdView: NativeAdView = itemView.findViewById(R.id.nativeAd) as NativeAdView
    }


    override fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView, action: String) {
        if (action == CHECKACTION) {
            val taskswift = taskList[position]
            taskList.removeAt(position)
            notifyItemRemoved(position)
            taskswift.status = COMPLETETASK
            editTask(context, taskswift)

            val snackbar =
                Snackbar.make(recyclerView, "Task completed", Snackbar.LENGTH_LONG)
//                .setAction("UNDO") {
//                    taskList.add(taskswift)
//                    notifyItemInserted(taskList.lastIndex)
//                    taskswift.status = ACTIVETASK
//                    editTask(context, taskswift)
//                }
            snackbar.show()
        }
    }

    override fun onItemSwiftRight(position: Int, recyclerView: RecyclerView, action: String) {

        //val user = com.example.newevent2.Functions.getUserSession(context!!)
        val taskswift = taskList[position]
        val taskbackup = Task().apply {
            name = taskswift.name
            budget = taskswift.budget
            date = taskswift.date
            category = taskswift.category
            status = taskswift.status
            createdatetime = taskswift.createdatetime
        }

        //delNotification(taskswift)

        if (action == DELETEACTION) {
            taskList.removeAt(position)
            notifyItemRemoved(position)
                deleteTask(context, taskswift)

            val snackbar = Snackbar.make(recyclerView, "Task deleted", Snackbar.LENGTH_LONG)
//                .setAction("UNDO") {
//                    taskList.add(taskswift)
//                    notifyItemInserted(taskList.lastIndex)
//                    taskswift.status = ACTIVETASK
//                    addTask(context, taskbackup)
//                    //addNotification(taskbackup)
//                }
            snackbar.show()
        } else if (action == UNDOACTION) {
            taskList.add(taskswift)
            notifyItemInserted(taskList.lastIndex)
            taskswift.status = ACTIVETASK
            editTask(context, taskswift)
            //addNotification(taskbackup)
        }
    }

    //-------------------------------------------------------------------------------
    // Creating Notification for Tasks
    //-------------------------------------------------------------------------------
//    private fun addNotification(task: Task) {
//        // Job ID must be unique if you have multiple jobs scheduled
//        var jobID = NotificationID.getID()
//
//        var gson = Gson()
//        var json = gson.toJson(task)
//        var bundle = PersistableBundle()
//        bundle.putString("task", json)
//
//        // Get fake user set time (a future time 1 min from current time)
//        val (userSetHourOfDay, userSetMinute) = getMockUserSetTime()
//        val timeToWaitBeforeExecuteJob = calculateTimeDifferenceMs(userSetHourOfDay, userSetMinute)
//        (context.applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).run {
//            schedule(
//                JobInfo.Builder(
//                    jobID,
//                    ComponentName(context, NotificationJobService::class.java)
//                )
//                    // job execution will be delayed by this amount of time
//                    .setMinimumLatency(timeToWaitBeforeExecuteJob)
//                    // job will be run by this deadline
//                    .setOverrideDeadline(timeToWaitBeforeExecuteJob)
//                    .setExtras(bundle)
//                    .build()
//            )
//        }
//    }

    //-------------------------------------------------------------------------------
//    private fun delNotification(task: Task) {
//        val notificationManager =
//            context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.cancel(task.key, 0)
//    }
    //-------------------------------------------------------------------------------

    companion object {
        const val ACTIVETASK = "A"
        const val COMPLETETASK = "C"
        const val CHECKACTION = "check"
        const val DELETEACTION = "delete"
        const val UNDOACTION = "undo"
        const val TAG = "Rv_TaskAdapter"
    }
}



