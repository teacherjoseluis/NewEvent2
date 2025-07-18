package com.bridesandgrooms.event.MVP

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.bridesandgrooms.event.UI.Fragments.DashboardEvent
import com.bridesandgrooms.event.MVP.PaymentPresenter.Companion.ERRCODEPAYMENTS
import com.bridesandgrooms.event.MVP.TaskPresenter.Companion.ERRCODETASKS
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.Model.DashboardImage.DashboardImageData
import com.bridesandgrooms.event.Model.DashboardImage.DashboardImageResult
import com.bridesandgrooms.event.Model.DashboardImage.DashboardRepository
import com.bridesandgrooms.event.Model.Event
import com.bridesandgrooms.event.Model.Guest
import com.bridesandgrooms.event.Model.Payment
import com.bridesandgrooms.event.Model.Task
import com.bridesandgrooms.event.Model.TaskDBHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardEventPresenter(val context: Context, val fragment: DashboardEvent, val view: View) :
    TaskPresenter.TaskList, PaymentPresenter.PaymentList, GuestPresenter.GuestList,
    EventPresenter.EventItem {

    private var presentertask: TaskPresenter = TaskPresenter(context, this)
    private var presenterpayment: PaymentPresenter = PaymentPresenter(context, this)
    private var presenterguest: GuestPresenter = GuestPresenter(context, this)
    private var presenterevent: EventPresenter = EventPresenter(context, this)

    private var paymentsumbudget = 0.0F
    private val mHandler = Handler(Looper.getMainLooper())

    private lateinit var repository: DashboardRepository

//    fun getEventchildrenflag(): Boolean {
//        //This function needs to return a boolean
//        val eventdbhelper = EventDBHelper(context)
//        val event = eventdbhelper.getEvent()
//        return presenterevent.getEventChildrenflag(event.key)
//    }

    fun setRepository(repository: DashboardRepository) {
        this.repository = repository
    }

//    fun fetchDashboardImages() {
//        CoroutineScope(Dispatchers.Main).launch {
//            val categories = repository.fetchCategories()
//            val dashboardImages = mutableListOf<List<Bitmap>>()
//            categories.forEach {
//                val image = getAllRecentThumbnails(it)
//                dashboardImages.add(image!!)
//            }
//            fragment.onDashboardImages(dashboardImages)
//        }
//    }

    fun fetchDashboardImages() {
        CoroutineScope(Dispatchers.Main).launch {
            val categories = repository.fetchCategories()
            val deferredImages = categories.map { category ->
                async {
                    val thumbnails = getAllRecentThumbnails(category.code)
                    CategoryThumbnails(thumbnails, category)
                }
            }
            val dashboardImages = deferredImages.awaitAll()
            fragment.onDashboardImages(dashboardImages)
        }
    }

    data class CategoryThumbnails(val thumbnails: List<Bitmap>?, val category: DashboardImageData)
    private suspend fun getAllRecentThumbnails(category: String): List<Bitmap>? {
        return withContext(Dispatchers.Main) {
            when (val result = repository.getAllRecentThumbnails(category, onlyFirst = true)) {
                is DashboardImageResult.Success -> {
                    result.images
                }

                else -> {
                    // Handle the error case
                    // You can notify the fragment about the error or log it
                    //fragment.onDashboardImagesError(result.toString())
                    Log.e("DashboardEventPresenter", "Error: $result")
                    null
                }
            }
        }
    }

    fun getTaskList() {
        Thread {
            presentertask.getTasksList()
        }.start()
    }

    fun getPaymentList() {
        Thread {
            presenterpayment.getPaymentsList()
        }.start()
    }

    fun getEvent() {
        Thread {
            presenterevent.getEventDetail()
        }.start()
    }

//    fun getEventChildrenflag(){
//        presenterevent.getEventDetail()
//    }

    fun getGuestList() {
        Thread {
            presenterguest.getGuestList()
        }.start()
    }

    override fun onTaskList(list: ArrayList<Task>) {
        mHandler.post {
            var countactive = 0 // Active/Open Tasks
            var countcompleted = 0 // Tasks Completed
            var sumbudget = 0.0f // Budget Amount for all Tasks

            val re = Regex("[^A-Za-z0-9 ]")
            for (task in list) {
                val budgetamount = re.replace(task.budget, "").dropLast(2)
                sumbudget += budgetamount.toFloat()
                if (task.status == "A") {
                    countactive += 1
                } else if (task.status == "C") {
                    countcompleted += 1
                }
            }
            paymentsumbudget = sumbudget

            list.sortWith(Comparator { o1, o2 ->
                if (o1.date == null || o2.date == null) 0 else o1.date
                    .compareTo(o2.date)
            })
            val nextactivetask = nextactive(list)
            fragment.onTasksStats(countactive, countcompleted, sumbudget, nextactivetask)
        }
    }

    private fun nextactive(list: ArrayList<Task>): Task {
        var nexttask = Task()
        for (task in list) {
            if (task.status == "A") {
                nexttask = task
                break
            }
        }
        return nexttask
    }

    override fun onTaskListError(errcode: String) {
        mHandler.post {
            fragment.onTaskStatsError(ERRCODETASKS)
        }
    }

    override fun onPaymentList(list: ArrayList<Payment>) {
//        mHandler.post {
//            var countpayment = 0 // Number of Payments made
//            var sumpayment = 0.0f // Amount of Payments
//
//            val re = Regex("[^A-Za-z0-9 ]")
//            for (payment in list) {
//
//                val paidamount = re.replace(payment.amount, "").dropLast(2)
//                sumpayment += paidamount.toFloat()
//                countpayment += 1
//            }
//            fragment.onPaymentsStats(countpayment, sumpayment, paymentsumbudget)
//        }
        Thread {
            var countpayment = 0 // Number of Payments made
            var sumpayment = 0.0f // Amount of Payments
            val re = Regex("[^A-Za-z0-9 ]")

            for (payment in list) {
                val paidamount = re.replace(payment.amount, "").dropLast(2)
                sumpayment += paidamount.toFloat()
                countpayment += 1
            }

            // Wait up to 500ms for paymentsumbudget to be populated
            var waitTime = 0L
            while (paymentsumbudget == 0.0F && waitTime < 500L) {
                Thread.sleep(50)
                waitTime += 50
            }

            mHandler.post {
                fragment.onPaymentsStats(countpayment, sumpayment, paymentsumbudget)
            }
        }.start()
    }

    override fun onPaymentListError(errcode: String) {
        mHandler.post {
            fragment.onPaymentsStatsError(ERRCODEPAYMENTS)
        }
    }

    override fun onGuestList(list: ArrayList<Guest>) {
        mHandler.post {
            var confirmed = 0
            var rejected = 0
            var pending = 0

            for (guestitem in list) {
                when (guestitem.rsvp) {
                    "y" -> confirmed += 1
                    "n" -> rejected += 1
                    "pending" -> pending += 1
                }
            }
            fragment.onGuestConfirmation(confirmed, rejected, pending)
            presenterevent.getEventDetail()
        }
    }

    override fun onGuestListError(errcode: String) {
        mHandler.post {
            fragment.onGuestConfirmationError(GuestPresenter.ERRCODEGUESTS)
            presenterevent.getEventDetail()
        }
    }

    override fun onEvent(event: Event) {
        mHandler.post {
            fragment.onEvent(context, event)
        }
    }

    override fun onEventError(errcode: String) {
        mHandler.post {
            fragment.onEventError(view, EventPresenter.ERRCODEEVENTS)
        }
    }

    //    interface DashboardCategories {
//        fun onDashboardCategories(categories: List<String>)
//        fun onDashboardCategoriesError(errcode: String)
//    }
//
    fun getActiveCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            val taskDBHelper = TaskDBHelper()
            try {
                // Perform database operation in the background
                val categoriesList = taskDBHelper.getActiveCategories()!!
                withContext(Dispatchers.Main) {
                    // Update UI on the main thread
                    fragment.onCategories(categoriesList)
                }
            } catch (e: Exception) {
                Log.e("DashboardEventPresenter", e.message.toString())
                withContext(Dispatchers.Main) {
                    // Handle errors back on the main thread
                    fragment.onCategoriesError(ERRCODETASKS)
                }
            }
        }
    }

    fun getUpcomingTasks() {
        CoroutineScope(Dispatchers.IO).launch {
            val taskDBHelper = TaskDBHelper()
            try {
                val taskList = taskDBHelper.getUpcomingTasks()
                withContext(Dispatchers.Main) {
                    fragment.onTaskNextList(taskList)
                }
            } catch (e: Exception) {
                Log.e("DashboardEventPresenter", e.message.toString())
                withContext(Dispatchers.Main) {
                    fragment.onTaskNextListError(ERRCODETASKS)
                }
            }
        }
    }

    interface TaskNextInterface {
        fun onTaskNextList (
            taskList: ArrayList<String>?
        )

        fun onTaskNextListError (
            errcode: String
        )

    }

    interface EventCategoryInterface {
        fun onCategories(
            list: List<Category>?
        )

        fun onCategoriesError(errcode: String)
    }

    interface DashboardImagesStats {
        fun onDashboardImages(
            images: List<CategoryThumbnails>
        )

        fun onDashboardImagesError(errcode: String)
    }

    interface TaskStats {
        fun onTasksStats(
            taskpending: Int,
            taskcompleted: Int,
            sumbudget: Float,
            task: Task
        )

        fun onTaskStatsError(errcode: String)
    }

    interface PaymentStats {
        fun onPaymentsStats(
            countpayment: Int,
            sumpayment: Float,
            sumbudget: Float
        )

        fun onPaymentsStatsError(errcode: String)
    }

    interface GuestStats {
        fun onGuestConfirmation(
            confirmed: Int,
            rejected: Int,
            pending: Int
        )

        fun onGuestConfirmationError(
            errcode: String
        )
    }

    interface EventInterface {
        fun onEvent(context: Context, event: Event)
        fun onEventError(inflatedview: View, errorcode: String)
    }
}

