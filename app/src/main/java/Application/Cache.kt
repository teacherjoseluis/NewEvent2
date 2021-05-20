package Application

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.newevent2.MVP.TaskPresenter
import com.example.newevent2.Model.Task
import com.example.newevent2.Model.TaskJournal
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KClass

class Cache<T : Any> {

    private var taskPresenter: TaskPresenter
    private var contextCache: Context

    constructor(context: Context, presenter: TaskPresenter) {
        contextCache = context
        taskPresenter = presenter
    }

    fun save(cacheCategory: CacheCategory, arrayList: ArrayList<T>) {
        val gson = Gson()
        val json = gson.toJson(arrayList)
        savetoStorage(cacheCategory, json)
    }

    fun save(cacheCategory: CacheCategory, singleitem: T) {
        val gson = Gson()
        val json = gson.toJson(singleitem)
        savetoStorage(cacheCategory, json)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun loadsingleitem(cacheCategory: CacheCategory, kClass: KClass<T>) {
        val itemjson = loadfromStorage(cacheCategory)
        val gson = Gson()
        if (kClass == Task::class) {
            val type = object : TypeToken<Task>() {}.type
            val item: T? = gson.fromJson(itemjson, type)
            if (item == null) {
                taskPresenter.onEmptyItem(cacheCategory.classtype)
            } else {
                taskPresenter.onSingleItem(item)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun loadarraylist(cacheCategory: CacheCategory, kClass: KClass<T>) {
        val itemjson = loadfromStorage(cacheCategory)
        val gson = Gson()

        if (kClass == TaskJournal::class) {
            val type = object : TypeToken<ArrayList<TaskJournal>>() {}.type
            val arrayList: ArrayList<TaskJournal>? = gson.fromJson(itemjson, type)
            if (arrayList == null) {
                taskPresenter.onEmptyList(cacheCategory.classtype)
            } else {
                taskPresenter.onArrayList(arrayList, cacheCategory.classtype)
            }
        } else if (kClass == Task::class) {
            val type = object : TypeToken<ArrayList<Task>>() {}.type
            val arrayList: ArrayList<Task>? = gson.fromJson(itemjson, type)
            // Getting the category and status is important at this point as we need to pass them to the Presenter
            // in order to filter the data obtained by the cache
            if (arrayList == null) {
                taskPresenter.onEmptyList(cacheCategory.classtype)
            } else {
                taskPresenter.onArrayList(arrayList, cacheCategory.classtype)
            }
        }
    }

    private fun savetoStorage(cacheCategory: CacheCategory, json: String) {
        val sharedPreference =
            contextCache.getSharedPreferences(cacheCategory.cachename, Context.MODE_PRIVATE)
        val date = Date(System.currentTimeMillis())
        sharedPreference.edit().putLong("timestamp", date.time).apply()
        val editor = sharedPreference.edit()
        editor.putString(cacheCategory.cachename, json)
        editor.apply()
    }

    private fun loadfromStorage(cacheCategory: CacheCategory): String? {
        val sharedPreference =
            contextCache.getSharedPreferences(cacheCategory.cachename, Context.MODE_PRIVATE)
        val cachedate = Date(sharedPreference.getLong("timestamp", 0))
        val expirationdate =
            Date(cachedate.time + cacheCategory.expiration * ONE_MINUTE_IN_MILLIS)
        if (cachedate >= expirationdate) {
            contextCache.getSharedPreferences(cacheCategory.cachename, Context.MODE_PRIVATE).edit()
                .clear().apply()
        }
        return sharedPreference.getString(cacheCategory.cachename, null)
    }

    fun deletefromStorage(entity: String) {
        val list = ArrayList<CacheCategory>(EnumSet.allOf(CacheCategory::class.java))
        for (category in list) {
            if (category.entity == entity) {
                contextCache.getSharedPreferences(category.cachename, Context.MODE_PRIVATE).edit()
                    .clear().apply()
            }
        }
    }

    interface ArrayListCacheData {
        fun onArrayList(arrayList: ArrayList<*>, classtype: String)
        fun onEmptyList(classtype: String)
    }

    interface SingleItemCacheData {
        fun onSingleItem(item: Any)
        fun onEmptyItem(classtype: String)
    }

    companion object {
        const val ONE_MINUTE_IN_MILLIS: Long = 60000
    }
}