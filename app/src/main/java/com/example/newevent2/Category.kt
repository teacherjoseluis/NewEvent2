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
    Venue("venue", "Venue", "venue","#DB5ABA","#FFFFFF"),
    Photo("photo", "Photo & Video", "photo","#C455A8", "#FFFFFF"),
    Entertainment("entertainment", "Entertainment", "entertainment","#CF8BA3", "#000000"),
    Flowers("flowers", "Flowers & Deco", "flowers","#D7A6B3","#000000"),
    Transportation("transport", "Transportation", "transportation","#E5CDC8","#000000"),
    Ceremony("ceremony", "Ceremony", "ceremony","#D94A98","#FFFFFF"),
    Accesories("accessories", "Attire & Accessories", "attire","#F285C1", "#FFFFFF"),
    Beauty("beauty", "Health & Beauty", "beauty","#F2B3D6","#000000"),
    Food("food", "Food & Drink", "food","#1D3973","#FFFFFF"),
    Guests("guests", "Guests", "guests","#F2D8D5","#000000");

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

