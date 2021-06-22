package Application

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.newevent2.Functions.*
import com.example.newevent2.MVP.*
import com.example.newevent2.Model.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KClass

class Cache<T : Any> {

    private lateinit var taskPresenter: TaskPresenter
    private lateinit var paymentPresenter: PaymentPresenter
    private lateinit var guestPresenter: GuestPresenter
    private lateinit var eventPresenter: EventPresenter
    private lateinit var imagePresenter: ImagePresenter

    private lateinit var taskhelper: TaskDBHelper
    private lateinit var paymenthelper: PaymentDBHelper
    private lateinit var guesthelper: GuestDBHelper
    private lateinit var eventhelper: EventDBHelper

    private var contextCache: Context

    constructor(context: Context, presenter: TaskPresenter) {
        contextCache = context
        taskPresenter = presenter
        taskhelper = TaskDBHelper(context)
    }

    constructor(context: Context, presenter: PaymentPresenter) {
        contextCache = context
        paymentPresenter = presenter
        paymenthelper = PaymentDBHelper(context)
    }

    constructor(context: Context, presenter: GuestPresenter) {
        contextCache = context
        guestPresenter = presenter
        guesthelper = GuestDBHelper(context)
    }

    constructor(context: Context, presenter: EventPresenter) {
        contextCache = context
        eventPresenter = presenter
        eventhelper = EventDBHelper(context)
    }

    //    constructor(context: Context, presenter: PaymentPresenter) {
//        contextCache = context
//        paymentPresenter = presenter
//    }
//
    constructor(context: Context, presenter: ImagePresenter) {
        contextCache = context
        imagePresenter = presenter
    }

    fun save(list: ArrayList<*>) {
        when {
            list[0] is Task -> {
                for (task in list) {
                    taskhelper.insert(task as Task)
                }
            }
            list[0] is Payment -> {
                for (payment in list) {
                    paymenthelper.insert(payment as Payment)
                }
            }
            list[0] is Guest -> {
                for (guest in list) {
                    guesthelper.insert(guest as Guest)
                }
            }
        }
    }

    fun save(event: Event) {
        eventhelper.insert(event)
    }


//    fun save(cacheCategory: CacheCategory, arrayList: ArrayList<T>) {
//        val gson = Gson()
//        val json = gson.toJson(arrayList)
//        savetoStorage(cacheCategory, json)
//    }
//
//    fun save(cacheCategory: CacheCategory, singleitem: T) {
//        val gson = Gson()
//        val json = gson.toJson(singleitem)
//        savetoStorage(cacheCategory, json)
//    }
//
//    fun save(
//        cacheCategory: CacheCategory,
//        taskspending: Int,
//        taskscompleted: Int,
//        sumbudget: Float
//    ) {
//        val tasktoken = TaskStatsToken(taskspending, taskscompleted, sumbudget)
//        val gson = Gson()
//        val json = gson.toJson(tasktoken)
//        savetoStorage(cacheCategory, json)
//    }
//
//    fun save(
//        cacheCategory: CacheCategory,
//        countpayment: Int,
//        sumpayment: Float,
//        sumbudget: Float
//    ) {
//        val paymenttoken = PaymentStatsToken(countpayment, sumpayment, sumbudget)
//        val gson = Gson()
//        val json = gson.toJson(paymenttoken)
//        savetoStorage(cacheCategory, json)
//    }

    fun save(category: String, storageRef: StorageReference) {
        storageRef.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
            override fun onSuccess(p0: Uri?) {
                saveURLImgtoSD(category, p0, contextCache)
            }
        })
    }

//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//    fun loadsingleitem(cacheCategory: CacheCategory, kClass: KClass<T>) {
//        val itemjson = loadfromStorage(cacheCategory)
//        val gson = Gson()
//        if (kClass == Task::class) {
//            val type = object : TypeToken<Task>() {}.type
//            var item: T? = try {
//                gson.fromJson(itemjson, type)
//            } catch (e: Exception) {
//                contextCache.getSharedPreferences(cacheCategory.cachename, Context.MODE_PRIVATE)
//                    .edit()
//                    .clear().apply()
//                null
//            }
//            if (item == null) {
//                // Nothing was obtained from the cache
//                taskPresenter.onEmptyItem(cacheCategory.classtype)
//            } else {
//                taskPresenter.onSingleItem(item)
//            }
//        } else if (kClass == TaskStatsToken::class) {
//            val type = object : TypeToken<TaskStatsToken>() {}.type
//            var item: T? = try {
//                gson.fromJson(itemjson, type)
//            } catch (e: Exception) {
//                contextCache.getSharedPreferences(cacheCategory.cachename, Context.MODE_PRIVATE)
//                    .edit()
//                    .clear().apply()
//                null
//            }
//            if (item == null) {
//                // Nothing was obtained from the cache
//                taskPresenter.onEmptyItem(cacheCategory.classtype)
//            } else {
//                taskPresenter.onSingleItem(item)
//            }
//        } else if (kClass == PaymentStatsToken::class) {
//            val type = object : TypeToken<PaymentStatsToken>() {}.type
//            var item: T? = try {
//                gson.fromJson(itemjson, type)
//            } catch (e: Exception) {
//                contextCache.getSharedPreferences(cacheCategory.cachename, Context.MODE_PRIVATE)
//                    .edit()
//                    .clear().apply()
//                null
//            }
//            if (item == null) {
//                // Nothing was obtained from the cache
//                paymentPresenter.onEmptyItem(cacheCategory.classtype)
//            } else {
//                paymentPresenter.onSingleItem(item)
//            }
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadarraylist(kClass: KClass<T>) {
        when (kClass) {
            Task::class -> {
                val arrayList = taskhelper.getTasks()
                if (arrayList.size != 0) {
                    taskPresenter.onArrayListT(arrayList)
                } else {
                    taskPresenter.onEmptyListT()
                }
            }

            Payment::class -> {
                val arrayList = paymenthelper.getPayments()
                if (arrayList.size != 0) {
                    paymentPresenter.onArrayListP(arrayList)
                } else {
                    paymentPresenter.onEmptyListP()
                }
            }

            Guest::class -> {
                val arrayList = guesthelper.getGuest()
                if (arrayList.size != 0) {
                    guestPresenter.onArrayListG(arrayList)
                } else {
                    guestPresenter.onEmptyListG()
                }
            }

            Event::class -> {
                val event = eventhelper.getEvent()
                if (event.key != "") {
                    eventPresenter.onEvent(event)
                } else {
                    eventPresenter.onEventError()
                }
            }
        }
    }


    fun insertItem(item: Any) {
        if (item is Task) taskhelper.insert(item)
        if (item is Payment) paymenthelper.insert(item)
        if (item is Guest) guesthelper.insert(item)
    }

    fun updateItem(item: Any) {
        if (item is Task) taskhelper.update(item)
        if (item is Payment) paymenthelper.update(item)
        if (item is Guest) guesthelper.update(item)
        if (item is Event) eventhelper.update(item)
    }

    fun deleteItem(item: Any) {
        if (item is Task) taskhelper.delete(item)
        if (item is Payment) paymenthelper.delete(item)
        if (item is Guest) guesthelper.delete(item)
    }

//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//    fun loadarraylist(cacheCategory: CacheCategory, kClass: KClass<T>) {
//        val itemjson = loadfromStorage(cacheCategory)
//        val gson = Gson()
//
//        when (kClass) {
//            TaskJournal::class -> {
//                val type = object : TypeToken<ArrayList<TaskJournal>>() {}.type
//                try {
//                    val arrayList: ArrayList<TaskJournal>? = gson.fromJson(itemjson, type)
//                    if (arrayList == null) {
//                        // Nothing was obtained from the cache
//                        taskPresenter.onEmptyList(cacheCategory.classtype)
//                    } else {
//                        taskPresenter.onArrayList(arrayList, cacheCategory.classtype)
//                    }
//                } catch (e: Exception) {
//                    contextCache.getSharedPreferences(cacheCategory.cachename, Context.MODE_PRIVATE)
//                        .edit()
//                        .clear().apply()
//                    taskPresenter.onEmptyList(cacheCategory.classtype)
//                }
//            }
//            Task::class -> {
//                val type = object : TypeToken<ArrayList<Task>>() {}.type
//                val arrayList: ArrayList<Task>? = gson.fromJson(itemjson, type)
//                // Getting the category and status is important at this point as we need to pass them to the Presenter
//                // in order to filter the data obtained by the cache
//                if (arrayList == null) {
//                    taskPresenter.onEmptyList(cacheCategory.classtype)
//                } else {
//                    taskPresenter.onArrayList(arrayList, cacheCategory.classtype)
//                }
//            }
//            Payment::class -> {
//                val type = object : TypeToken<ArrayList<Payment>>() {}.type
//                val arrayList: ArrayList<Payment>? = gson.fromJson(itemjson, type)
//                // Getting the category and status is important at this point as we need to pass them to the Presenter
//                // in order to filter the data obtained by the cache
//                if (arrayList == null) {
//                    paymentPresenter.onEmptyList(cacheCategory.classtype)
//                } else {
//                    paymentPresenter.onArrayList(arrayList, cacheCategory.classtype)
//                }
//            }
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadimage(cacheCategory: String) {
        val imagebitmap = getImgfromSD(cacheCategory, contextCache)
        val emptyBitmap = createemptyBitmap(250, 250)
        if (imagebitmap.sameAs(emptyBitmap)) {
            imagePresenter.onEmptyEventImage("")
        } else {
            imagePresenter.onEventImage(imagebitmap)
        }
    }

//    fun loadtaskstats(cacheCategory: CacheCategory) {
//        val itemjson = loadfromStorage(cacheCategory)
//        val gson = Gson()
//
//        val type = object : TypeToken<TaskStatsToken>() {}.type
//        val item: TaskStatsToken = gson.fromJson(itemjson, type)
//        if (item == null) {
//            // Nothing was obtained from the cache
//            taskPresenter.onEmptyStats()
//        } else {
//            taskPresenter.onTaskStats(item)
//        }
//    }

//    private fun savetoStorage(cacheCategory: CacheCategory, json: String) {
//        val sharedPreference =
//            contextCache.getSharedPreferences(cacheCategory.cachename, Context.MODE_PRIVATE)
//        val date = Date(System.currentTimeMillis())
//        val editor = sharedPreference.edit()
//        editor.putLong("timestamp", date.time)
//        editor.putString(cacheCategory.cachename, json)
//        editor.apply()
//        Log.d(TAG, "Save ${cacheCategory.cachename} cache to Local Storage")
//    }
//
//    private fun loadfromStorage(cacheCategory: CacheCategory): String? {
//        val sharedPreference =
//            contextCache.getSharedPreferences(cacheCategory.cachename, Context.MODE_PRIVATE)
//        val currentdate = Date(System.currentTimeMillis())
//        val cachedate = Date(sharedPreference.getLong("timestamp", 0))
//        //val expiredcache = sharedPreference.getString("expired", "N")
//        val expirationdate =
//            Date(cachedate.time + cacheCategory.expiration * ONE_MINUTE_IN_MILLIS)
//        if ((currentdate >= expirationdate)) {
//            contextCache.getSharedPreferences(cacheCategory.cachename, Context.MODE_PRIVATE).edit()
//                .clear().apply()
//            Log.d(TAG, "Load ${cacheCategory.cachename} cache from Local Storage")
//        }
//        return sharedPreference.getString(cacheCategory.cachename, null)
//    }


    interface TaskArrayListCacheData {
        fun onArrayListT(arrayList: ArrayList<Task>)
        fun onEmptyListT()
    }

    interface PaymentArrayListCacheData {
        fun onArrayListP(arrayList: ArrayList<Payment>)
        fun onEmptyListP()
    }

    interface GuestArrayListCacheData {
        fun onArrayListG(arrayList: ArrayList<Guest>)
        fun onEmptyListG()
    }

    interface EventItemCacheData {
        fun onEvent(item: Event)
        fun onEventError()
    }

    interface EventImageCacheData {
        fun onEventImage(image: Bitmap)
        fun onEmptyEventImage(errorcode: String)
    }

    /*
        interface TaskStats {
            fun onEmptyStats()
            fun onTaskStats(taskStats: TaskStatsToken)
        }

        interface PaymentStats {
            fun onEmptyStats()
            fun onPaymentStats(taskStats: TaskStatsToken)
        }
    */
    companion object {
        const val EMPTYRESULTS = "NODATA"
        const val ONE_MINUTE_IN_MILLIS: Long = 60000
        private const val TAG = "Cache"

        fun deletefromStorage(entity: String, contextCache: Context) {
            val list = ArrayList<CacheCategory>(EnumSet.allOf(CacheCategory::class.java))
            for (category in list) {
                if (category.entity == entity) {
                    contextCache.getSharedPreferences(category.cachename, Context.MODE_PRIVATE)
                        .edit()
                        .clear().apply()
                    Log.d(TAG, "Clear cache information for ${category.cachename}")
                }
            }
        }
    }

}