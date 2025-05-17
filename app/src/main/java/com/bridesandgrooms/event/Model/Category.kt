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
    val fr_name: String,
    val pt_name: String,
    val drawable: String,
    val colorbackground: String,
    val colorforeground: String,
    val colorinactivebackground: String,
    val colorinactiveforeground: String,
    var searchQueryEn: String,
    var searchQueryEs: String,
    var searchQueryFr: String,
    var searchQueryPt: String
) {
    Venue(
        "venue", "Venue", "Lugar", "Lieu", "Local", "cream_venue_category", "#DB5ABA", "#FFFFFF", "#f4ceea", "#b3b3b3",
        "Wedding Venues", "Salones de Eventos para Bodas", "Lieux de réception mariage", "Locais para casamentos"
    ),
    Photo(
        "photo", "Photo & Video", "Foto & Video", "Photo & Vidéo", "Foto & Vídeo", "cream_photo_category", "#C455A8", "#FFFFFF", "#edcce5", "#b3b3b3",
        "Wedding Photo and Video", "Foto y Video para Bodas", "Photographie et vidéo de mariage", "Foto e vídeo para casamentos"
    ),
    Entertainment(
        "entertainment", "Entertainment", "Entretenimiento", "Animation", "Entretenimento", "cream_entertainment_category", "#CF8BA3", "#000000", "#ecd1da", "#b3b3b3",
        "Entertainment for Weddings", "Entretenimiento para Bodas", "Divertissement pour mariages", "Entretenimento para casamentos"
    ),
    Flowers(
        "flowers", "Flowers & Deco", "Flores & Decoracion", "Fleurs & Décoration", "Flores & Decoração", "cream_flowers_category", "#D7A6B3", "#000000", "#e7cad1", "#b3b3b3",
        "Wedding Flower Arrangements", "Arreglos Florales para Bodas", "Arrangements floraux pour mariages", "Arranjos florais para casamentos"
    ),
    Transportation(
        "transport", "Transportation", "Transportación", "Transport", "Transporte", "cream_transportation_category", "#E5CDC8", "#000000", "#eddcd9", "#b3b3b3",
        "Wedding Transportation and Limos", "Limousinas y Transportaciones para Novias", "Transport de mariage", "Transporte para casamentos"
    ),
    Ceremony(
        "ceremony", "Ceremony", "Ceremonia", "Cérémonie", "Cerimônia", "cream_ceremony_category", "#D94A98", "#FFFFFF", "#ead7d3", "#b3b3b3",
        "Wedding Ceremony Venues and Churches", "Iglesias, Templos para Bodas Religiosas", "Lieux de cérémonie de mariage", "Locais de cerimônia de casamento"
    ),
    Accesories(
        "accessories", "Attire & Accessories", "Vestidos & Accesorios", "Tenue & Accessoires", "Vestuário & Acessórios", "cream_accessories_category", "#F285C1", "#FFFFFF", "#f7d1e6", "#b3b3b3",
        "Brides Accessories", "Accesorios para Novias", "Accessoires de mariée", "Acessórios para noivas"
    ),
    Beauty(
        "beauty", "Health & Beauty", "Salud & Belleza", "Santé & Beauté", "Saúde & Beleza", "cream_beauty2_category", "#F2B3D6", "#000000", "#f7d1e6", "#b3b3b3",
        "Health and Beauty products for Brides", "Productos de Salud y Belleza para Novias", "Produits de beauté pour mariées", "Produtos de beleza para noivas"
    ),
    Food(
        "food", "Food & Drink", "Comida & Bebida", "Nourriture & Boissons", "Comida & Bebida", "cream_banquet_category", "#1D3973", "#FFFFFF", "#e8ebf1", "#b3b3b3",
        "Wedding Banquets", "Banquetes para Bodas", "Banquets de mariage", "Buffets de casamento"
    ),
    Guests(
        "guests", "Guests", "Invitados", "Invités", "Convidados", "cream_guests_category", "#F2D8D5", "#000000", "#f6e4e2", "#b3b3b3",
        "", "", "", ""
    );

    companion object {

        init {
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val myRef = database.getReference("/flamelink/environments/production/content/vendorSearch/en-US/")

            val searchVendorListenerActive = object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    for (snapshot in p0.children) {
                        val category = snapshot.child("category").value as String
                        val en = snapshot.child("searchQueryEn").value?.toString() ?: ""
                        val es = snapshot.child("searchQueryEs").value?.toString() ?: ""
                        val fr = snapshot.child("searchQueryFr").value?.toString() ?: ""
                        val pt = snapshot.child("searchQueryPt").value?.toString() ?: ""
                        when (category) {
                            "venue" -> {
                                Venue.searchQueryEn = en
                                Venue.searchQueryEs = es
                                Venue.searchQueryFr = fr
                                Venue.searchQueryPt = pt
                            }
                            "photo" -> {
                                Photo.searchQueryEn = en
                                Photo.searchQueryEs = es
                                Photo.searchQueryFr = fr
                                Photo.searchQueryPt = pt
                            }
                            "entertainment" -> {
                                Entertainment.searchQueryEn = en
                                Entertainment.searchQueryEs = es
                                Entertainment.searchQueryFr = fr
                                Entertainment.searchQueryPt = pt
                            }
                            "flowers" -> {
                                Flowers.searchQueryEn = en
                                Flowers.searchQueryEs = es
                                Flowers.searchQueryFr = fr
                                Flowers.searchQueryPt = pt
                            }
                            "transport" -> {
                                Transportation.searchQueryEn = en
                                Transportation.searchQueryEs = es
                                Transportation.searchQueryFr = fr
                                Transportation.searchQueryPt = pt
                            }
                            "ceremony" -> {
                                Ceremony.searchQueryEn = en
                                Ceremony.searchQueryEs = es
                                Ceremony.searchQueryFr = fr
                                Ceremony.searchQueryPt = pt
                            }
                            "accessories" -> {
                                Accesories.searchQueryEn = en
                                Accesories.searchQueryEs = es
                                Accesories.searchQueryFr = fr
                                Accesories.searchQueryPt = pt
                            }
                            "beauty" -> {
                                Beauty.searchQueryEn = en
                                Beauty.searchQueryEs = es
                                Beauty.searchQueryFr = fr
                                Beauty.searchQueryPt = pt
                            }
                            "food" -> {
                                Food.searchQueryEn = en
                                Food.searchQueryEs = es
                                Food.searchQueryFr = fr
                                Food.searchQueryPt = pt
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
            return values().firstOrNull { it.code == code } ?: Venue
        }

        fun getCategoryName(code: String): String {
            val category = getCategory(code)
            return when (getlocale().substring(0, 2)) {
                "es" -> category.es_name
                "fr" -> category.fr_name
                "pt" -> category.pt_name
                else -> category.en_name
            }
        }

        fun getCategoryCode(name: String): String {
            return values().firstOrNull {
                name in listOf(it.en_name, it.es_name, it.fr_name, it.pt_name)
            }?.code ?: "venue"
        }

        fun getAllCategories(language: String): ArrayList<String> {
            return values().map {
                when (language) {
                    "es" -> it.es_name
                    "fr" -> it.fr_name
                    "pt" -> it.pt_name
                    else -> it.en_name
                }
            }.toCollection(ArrayList())
        }
    }
}
