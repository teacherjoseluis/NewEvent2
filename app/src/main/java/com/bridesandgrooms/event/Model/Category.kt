package com.bridesandgrooms.event.Model

import com.bridesandgrooms.event.Functions.Firebase.BlogPost
import com.bridesandgrooms.event.Functions.getlocale
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

enum class Category(
    val code: String,
    val en_name: String,
    val es_name: String,
    val drawable: String,
    val colorbackground: String,
    val colorforeground: String,
    val colorinactivebackground: String,
    val colorinactiveforeground: String,
    var searchQueryEn: String,
    var searchQueryEs: String
) {
    Venue(
        "venue", "Venue", "Lugar", "ativo_3_dalle_removebg_preview", "#DB5ABA", "#FFFFFF", "#f4ceea", "#b3b3b3", "Wedding Venues", "Salones de Eventos para Bodas"
    ),
    Photo(
        "photo",
        "Photo & Video",
        "Foto & Video",
        "ativo_7_dalle_removebg_preview",
        "#C455A8",
        "#FFFFFF",
        "#edcce5",
        "#b3b3b3",
        "Wedding Photo and Video",
        "Foto y Video para Bodas"
    ),
    Entertainment(
        "entertainment",
        "Entertainment",
        "Entretenimiento",
        "ativo_2_dalle_removebg_preview",
        "#CF8BA3",
        "#000000",
        "#ecd1da",
        "#b3b3b3",
        "Entertainment for Weddings",
        "Entretenimiento para Bodas"
    ),
    Flowers(
        "flowers",
        "Flowers & Deco",
        "Flores & Decoracion",
        "ativo_8_dalle_removebg_preview",
        "#D7A6B3",
        "#000000",
        "#e7cad1",
        "#b3b3b3",
        "Wedding Flower Arrangements",
        "Arreglos Florales para Bodas"
    ),
    Transportation(
        "transport",
        "Transportation",
        "Transportacion",
        "ativo_9_dalle_removebg_preview",
        "#E5CDC8",
        "#000000",
        "#eddcd9",
        "#b3b3b3",
        "Wedding Transportation and Limos",
        "Limousinas y Transportaciones para Novias"
    ),
    Ceremony(
        "ceremony",
        "Ceremony",
        "Ceremonia",
        "ativo_5_dalle_removebg_preview",
        "#D94A98",
        "#FFFFFF",
        "#ead7d3",
        "#b3b3b3",
        "Wedding Ceremony Venues and Churches",
        "Iglesias, Templos para Bodas Religiosas"
    ),
    Accesories(
        "accessories",
        "Attire & Accessories",
        "Vestidos & Accessorios",
        "ativo6_dalle_removebg_preview",
        "#F285C1",
        "#FFFFFF",
        "#f7d1e6",
        "#b3b3b3",
        "Brides Accessories",
        "Accesorios para Novias"
    ),
    Beauty(
        "beauty",
        "Health & Beauty",
        "Salud & Belleza",
        "ativo_1_dalle_removebg_preview",
        "#F2B3D6",
        "#000000",
        "#f7d1e6",
        "#b3b3b3",
        "Health and Beauty products for Brides",
        "Productos de Salud y Belleza para Novias"
    ),
    Food(
        "food",
        "Food & Drink",
        "Comida & Bebida",
        "ativo_4_dalle",
        "#1D3973",
        "#FFFFFF",
        "#e8ebf1",
        "#b3b3b3",
        "Wedding Banquets",
        "Banquetes para Bodas"
    ),
    Guests("guests", "Guests", "Invitados", "ativo_10_dalle_removebg_preview", "#F2D8D5", "#000000", "#f6e4e2", "#b3b3b3", "", "");

    companion object {
        init {
            var database: FirebaseDatabase = FirebaseDatabase.getInstance()
            var myRef = database.getReference("/flamelink/environments/production/content/vendorSearch/en-US/")

            val searchVendorListenerActive = object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    for (snapshot in p0.children) {
                        val category = snapshot.child("category").value as String
                        when (category){
                            "venue" -> {
                                Venue.searchQueryEn = snapshot.child("searchQueryEn").value.toString()
                                Venue.searchQueryEs = snapshot.child("searchQueryEs").value.toString()
                            }
                            "photo" -> {
                                Photo.searchQueryEn = snapshot.child("searchQueryEn").value.toString()
                                Photo.searchQueryEs = snapshot.child("searchQueryEs").value.toString()
                            }
                            "entertainment" -> {
                                Entertainment.searchQueryEn = snapshot.child("searchQueryEn").value.toString()
                                Entertainment.searchQueryEs = snapshot.child("searchQueryEs").value.toString()
                            }
                            "flowers" -> {
                                Flowers.searchQueryEn = snapshot.child("searchQueryEn").value.toString()
                                Flowers.searchQueryEs = snapshot.child("searchQueryEs").value.toString()
                            }
                            "transport" -> {
                                Transportation.searchQueryEn = snapshot.child("searchQueryEn").value.toString()
                                Transportation.searchQueryEs = snapshot.child("searchQueryEs").value.toString()
                            }
                            "ceremony" -> {
                                Ceremony.searchQueryEn = snapshot.child("searchQueryEn").value.toString()
                                Ceremony.searchQueryEs = snapshot.child("searchQueryEs").value.toString()
                            }
                            "accessories" -> {
                                Accesories.searchQueryEn = snapshot.child("searchQueryEn").value.toString()
                                Accesories.searchQueryEs = snapshot.child("searchQueryEs").value.toString()
                            }
                            "beauty" -> {
                                Beauty.searchQueryEn = snapshot.child("searchQueryEn").value.toString()
                                Beauty.searchQueryEs = snapshot.child("searchQueryEs").value.toString()
                            }
                            "food" -> {
                                Food.searchQueryEn = snapshot.child("searchQueryEn").value.toString()
                                Food.searchQueryEs = snapshot.child("searchQueryEs").value.toString()
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("loadPost:onCancelled ${error.toException()}")
                }

            }
            myRef.addValueEventListener(searchVendorListenerActive)

        }

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

        fun getCategoryName(code: String): String {
            if (getlocale().substring(0, 2) == "es") {
                return when (code) {
                    "venue" -> Venue.es_name
                    "photo" -> Photo.es_name
                    "entertainment" -> Entertainment.es_name
                    "flowers" -> Flowers.es_name
                    "transport" -> Transportation.es_name
                    "ceremony" -> Ceremony.es_name
                    "accessories" -> Accesories.es_name
                    "beauty" -> Beauty.es_name
                    "food" -> Food.es_name
                    "guests" -> Guests.es_name
                    else -> Venue.es_name
                }
            } else {
                return when (code) {
                    "venue" -> Venue.en_name
                    "photo" -> Photo.en_name
                    "entertainment" -> Entertainment.en_name
                    "flowers" -> Flowers.en_name
                    "transport" -> Transportation.en_name
                    "ceremony" -> Ceremony.en_name
                    "accessories" -> Accesories.en_name
                    "beauty" -> Beauty.en_name
                    "food" -> Food.en_name
                    "guests" -> Guests.en_name
                    else -> Venue.en_name
                }
            }
        }

        fun getCategoryCode(CategoryName: String): String {
            if (getlocale().substring(0, 2) == "es") {
                return when (CategoryName) {
                    Venue.es_name -> "venue"
                    Photo.es_name -> "photo"
                    Entertainment.es_name -> "entertainment"
                    Flowers.es_name -> "flowers"
                    Transportation.es_name -> "transport"
                    Ceremony.es_name -> "ceremony"
                    Accesories.es_name -> "accessories"
                    Beauty.es_name -> "beauty"
                    Food.es_name -> "food"
                    Guests.es_name -> "guests"
                    else -> "venue"
                }
            } else {
                return when (CategoryName) {
                    Venue.en_name -> "venue"
                    Photo.en_name -> "photo"
                    Entertainment.en_name -> "entertainment"
                    Flowers.en_name -> "flowers"
                    Transportation.en_name -> "transport"
                    Ceremony.en_name -> "ceremony"
                    Accesories.en_name -> "accessories"
                    Beauty.en_name -> "beauty"
                    Food.en_name -> "food"
                    Guests.en_name -> "guests"
                    else -> "venue"
                }
            }
        }

        fun getAllCategories(language: String): ArrayList<String> {
            val categoryarray: ArrayList<String>
            if (language == "en") {
                categoryarray = arrayListOf(
                    Accesories.en_name,
                    Beauty.en_name,
                    Ceremony.en_name,
                    Entertainment.en_name,
                    Flowers.en_name,
                    Food.en_name,
                    Guests.en_name,
                    Photo.en_name,
                    Venue.en_name,
                    Transportation.en_name
                )
            } else {
                categoryarray = arrayListOf(
                    Accesories.es_name,
                    Beauty.es_name,
                    Ceremony.es_name,
                    Entertainment.es_name,
                    Flowers.es_name,
                    Food.es_name,
                    Guests.es_name,
                    Photo.es_name,
                    Venue.es_name,
                    Transportation.es_name
                )
            }
            return categoryarray
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

