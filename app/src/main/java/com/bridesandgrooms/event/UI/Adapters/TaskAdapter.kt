package com.bridesandgrooms.event.UI.Adapters

import Application.AnalyticsManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Model.Category.Companion.getCategory
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton
import com.bridesandgrooms.event.Functions.deleteTask
import com.bridesandgrooms.event.Functions.editTask
import com.bridesandgrooms.event.Model.*
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.Adapters.SearchVendorAdapter.Companion
import com.bridesandgrooms.event.UI.Fragments.TPT_TaskFragmentActionListener
import com.bridesandgrooms.event.UI.ItemTouchAdapterAction
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.AdChoicesView
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.snackbar.Snackbar

interface ItemSwipeListenerTask {
    fun onItemSwiped(taskList: MutableList<Task>)
}

class TaskAdapter(
    private val fragmentActionListener: TPT_TaskFragmentActionListener,
    private val taskList: ArrayList<Task>,
    private val swipeListener: ItemSwipeListenerTask,
    val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchAdapterAction {

    lateinit var usermodel: UserModel

    private val DEFAULT_VIEW_TYPE = 1
    private val NATIVE_AD_VIEW_TYPE = 2

    private val ADS_INTERVAL = 3
    private val showads = RemoteConfigSingleton.get_showads()

    override fun getItemViewType(position: Int): Int {
        return if ((position > 0 && (position + 1) % ADS_INTERVAL == 0) && showads) {
            NATIVE_AD_VIEW_TYPE
        } else {
            DEFAULT_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        lateinit var genericViewHolder: RecyclerView.ViewHolder
        when (p1) {
            DEFAULT_VIEW_TYPE -> {
                val v = LayoutInflater.from(p0.context)
                    .inflate(R.layout.task_cardview_expanded, p0, false)
                //context = p0.context
                genericViewHolder = TaskViewHolder(v)
            }

            NATIVE_AD_VIEW_TYPE -> {
                val v = LayoutInflater.from(p0.context)
                    .inflate(R.layout.native_ad_layout, p0, false)
                //context = p0.context
                genericViewHolder = NativeAdViewHolder(v)
            }
        }
        return genericViewHolder
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val adjustedPosition = p1 - (p1 / ADS_INTERVAL)
        when (p0) {
            is TaskViewHolder -> {
                if (adjustedPosition >= 0 && adjustedPosition < taskList.size) {
                    val task = taskList[p1]
                    p0.bind(task)
                }
            }

            is NativeAdViewHolder -> {
                val adLoader = AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
                    .forNativeAd {
                        Log.d("AD1015", it.responseInfo.toString())
                        Log.d("AD1015", it.mediaContent.toString())
                        populateNativeAdView(it, p0.nativeAdView)
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
        // Set Ad Attribution ("Ad")
        val attributionView = adView.findViewById<TextView>(R.id.ad_attribution)
        attributionView.visibility = View.VISIBLE

        // Set AdChoices Overlay
        val adChoicesContainer = adView.findViewById<FrameLayout>(R.id.ad_choices_container)
        val adChoicesView = AdChoicesView(adView.context)
        adChoicesContainer.removeAllViews()
        adChoicesContainer.addView(adChoicesView)

        // Set Media Content
        val mediaView = adView.findViewById<MediaView>(R.id.media_view)
        adView.mediaView = mediaView
        mediaView.setMediaContent(nativeAd.mediaContent)

        // Set Headline
        val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
        adView.headlineView = headlineView
        headlineView.text = nativeAd.headline

        // Set Call to Action
        val callToActionView = adView.findViewById<Button>(R.id.ad_call_to_action)
        adView.callToActionView = callToActionView
        callToActionView.text = nativeAd.callToAction
        callToActionView.visibility = if (nativeAd.callToAction != null) View.VISIBLE else View.INVISIBLE

        // Register the native ad with the NativeAdView
        adView.setNativeAd(nativeAd)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskCardView: ConstraintLayout = itemView.findViewById(R.id.taskcardview)
        private val taskname: TextView? = itemView.findViewById(R.id.taskName)
        private val taskcategory: TextView? = itemView.findViewById(R.id.taskCategory)
        private val taskbudget: TextView? = itemView.findViewById(R.id.taskBudget)
        private val categoryavatar = itemView.findViewById<ImageView>(R.id.taskImageView)!!

        init {
            taskCardView.setOnClickListener {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "taskCardView", "click")
                handleClick()
            }
        }

        fun bind(task: Task){
            val resourceId = context.resources.getIdentifier(
                getCategory(task.category).drawable, "drawable",
                context.packageName
            )

            taskname?.text = task.name
            taskcategory?.text = task.category
            taskbudget?.text = task.budget
            categoryavatar.setImageResource(resourceId)
        }

        private fun handleClick() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val task = taskList[position]
                fragmentActionListener.onTaskFragmentWithData(task)
            }
        }
    }

    class NativeAdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nativeAdView: NativeAdView = itemView.findViewById(R.id.nativeAd) as NativeAdView
    }

    override fun onItemSwiftLeft(
        context: Context,
        position: Int,
        recyclerView: RecyclerView,
        action: String
    ) {
        AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Complete Task", null)
        //val user = User().getUser()
        if (action == CHECKACTION) {
            val taskswift = taskList[position]
            taskList.removeAt(position)
            notifyItemRemoved(position)
            taskswift.status = COMPLETETASK
            try {
                editTask(taskswift)
            } catch (e: Exception) {
                Log.e(TAG,e.message.toString())
            }

            val snackbar =
                Snackbar.make(recyclerView, "Task completed", Snackbar.LENGTH_LONG)
            snackbar.show()
            swipeListener.onItemSwiped(taskList)
        }
    }

    override fun onItemSwiftRight(
        context: Context,
        position: Int,
        recyclerView: RecyclerView,
        action: String
    ) {
        AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Delete Task", null)
        //val user = User().getUser()
        val taskswift = taskList[position]

        if (action == DELETEACTION) {
            taskList.removeAt(position)
            notifyItemRemoved(position)
            try {
            deleteTask(taskswift.key)
            } catch (e: Exception) {
                Log.e(TAG,e.message.toString())
            }
            val snackbar = Snackbar.make(recyclerView, "Task deleted", Snackbar.LENGTH_LONG)
            snackbar.show()
            swipeListener.onItemSwiped(taskList)
        }
    }

    companion object {
        const val ACTIVETASK = "A"
        const val COMPLETETASK = "C"
        const val CHECKACTION = "check"
        const val DELETEACTION = "delete"
        const val UNDOACTION = "undo"
        const val TAG = "TaskAdapter"
        const val SCREEN_NAME = "TaskAdapter"
    }
}



