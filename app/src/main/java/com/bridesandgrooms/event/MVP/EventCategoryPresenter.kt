package com.bridesandgrooms.event.MVP

import android.content.Context
import android.util.Log
import com.bridesandgrooms.event.MVP.TaskPresenter.Companion.ERRCODETASKS
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.Model.TaskDBHelper
import com.bridesandgrooms.event.UI.Fragments.EventCategories

class EventCategoryPresenter(
    val context: Context,
    val fragment: EventCategories
) {

    fun getActiveCategories() {
        val taskDBHelper = TaskDBHelper(context)
        try {
            val categoriesList = taskDBHelper.getActiveCategories()!!
            fragment.onCategories(categoriesList)
        } catch (e: Exception) {
            Log.e("EventCategoryPresenter", e.message.toString())
            fragment.onCategoriesError(ERRCODETASKS)
        }
    }

    interface EventCategoryInterface {
        fun onCategories(
            list: List<Category>?
        )

        fun onCategoriesError(errcode: String)
    }
}

