package com.example.newevent2

enum class Category(
    val code: String,
    val en_name: String,
    val es_name: String,
    val drawable: String,
    val colorbackground: String,
    val colorforeground: String,
    val colorinactivebackground: String,
    val colorinactiveforeground: String
) {
    Venue("venue", "Venue", "Lugar","venue","#DB5ABA","#FFFFFF","#f4ceea", "#b3b3b3"),
    Photo("photo", "Photo & Video", "Foto & Video","photo","#C455A8", "#FFFFFF", "#edcce5", "#b3b3b3"),
    Entertainment("entertainment", "Entertainment", "Entretenimiento", "entertainment","#CF8BA3", "#000000","#ecd1da","#b3b3b3"),
    Flowers("flowers", "Flowers & Deco","Flores & Decoracion", "flowers","#D7A6B3","#000000", "#e7cad1","#b3b3b3"),
    Transportation("transport", "Transportation","Transportacion", "transportation","#E5CDC8","#000000", "#eddcd9","#b3b3b3"),
    Ceremony("ceremony", "Ceremony", "Ceremonia","ceremony","#D94A98","#FFFFFF", "#ead7d3","#b3b3b3"),
    Accesories("accessories", "Attire & Accessories", "Vestidos & Accessorios","attire","#F285C1", "#FFFFFF", "#f7d1e6","#b3b3b3"),
    Beauty("beauty", "Health & Beauty", "Salud & Belleza","beauty","#F2B3D6","#000000","#f7d1e6","#b3b3b3"),
    Food("food", "Food & Drink", "Comida & Bebida","food","#1D3973","#FFFFFF", "#e8ebf1","#b3b3b3"),
    Guests("guests", "Guests", "Invitados","guests","#F2D8D5","#000000", "#f6e4e2","#b3b3b3");

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

