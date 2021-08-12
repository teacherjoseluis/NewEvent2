package com.example.newevent2

import android.content.Context
import com.example.newevent2.Model.TaskModel
import java.util.*

enum class Category(
    val code: String,
    val en_name: String,
    val drawable: String,
    val colorbackground: String,
    val colorforeground: String
) {
    Venue("venue", "Venue", "venue","#FF9AA2","#9AFFF7"),
    Photo("photo", "Photo & Video", "photo","#FFB7B2", "#FFFFFF"),
    Entertainment("entertainment", "Entertainment", "entertainment","#FFDAC1", "#C1E6FF"),
    Flowers("flowers", "Flowers & Deco", "flowers","#E2F0CB","#000000"),
    Transportation("transport", "Transportation", "transportation","#B5EAD7","#FFFFFF"),
    Ceremony("ceremony", "Ceremony", "ceremony","#C7CEEA","#000000"),
    Accesories("accessories", "Attire & Accessories", "attire","#B1B1B1", "#000000"),
    Beauty("beauty", "Health & Beauty", "beauty","#ACD0C0","#D0ACBC"),
    Food("food", "Food & Drink", "food","#C5CADC","#FFFFFF"),
    Guests("guests", "Guests", "guests","#AFB4C3","#C3BEAF");

    companion object {
        fun getCategory(code: String): Category {
            return when (code) {
                "venue" -> Venue
                "photo" -> Photo
                "entertainment" -> Entertainment
                "flowers" -> Flowers
                "transport" -> Transportation
                "ceremony" -> Ceremony
                "accessories" -> Accesories
                "beauty" -> Beauty
                "food" -> Food
                "guests" -> Guests
                else -> Venue
            }
        }
    }

//    companion object {
//        fun getTaskCount(context: Context, category: Category, status: String): Int {
//            var tasks = 0
//            val sharedPreference =
//                context.getSharedPreferences(category.code, Context.MODE_PRIVATE)
//            if (status == TaskModel.ACTIVESTATUS) {
//                tasks = sharedPreference.getInt("tasksactive_${category.code}", 0)
//            } else if (status == TaskModel.COMPLETESTATUS) {
//                tasks = sharedPreference.getInt("taskscompleted_${category.code}", 0)
//            }
//            return tasks
//        }
//
//        fun setTaskCount(context: Context, category: Category, status: String, count: Int) {
//            val sharedPreference =
//                context.getSharedPreferences(category.code, Context.MODE_PRIVATE)
//            val editor = sharedPreference.edit()
//            if (status == TaskModel.ACTIVESTATUS) {
//                editor.putInt("tasksactive_${category.code}", count)
//            } else if (status == TaskModel.COMPLETESTATUS) {
//                editor.putInt("taskscompleted_${category.code}", count)
//            }
//            editor.apply()
//        }
//
//        fun getPaymentCount(context: Context, category: Category): Int {
//            val sharedPreference =
//                context.getSharedPreferences(category.code, Context.MODE_PRIVATE)
//            return sharedPreference.getInt("payments_${category.code}", 0)
//        }
//
//        fun setPaymentCount(context: Context, category: Category, count: Int) {
//            val sharedPreference =
//                context.getSharedPreferences(category.code, Context.MODE_PRIVATE)
//            val editor = sharedPreference.edit()
//            editor.putInt("payments_${category.code}", count)
//        }
//    }
}

