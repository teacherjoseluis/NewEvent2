package com.example.newevent2

enum class Category(
    val code: String,
    val en_name: String,
    val drawable: String
) {
    Venue("venue", "Venue","venue"),
    Photo("photo", "Photo & Video","photo"),
    Entertainment("entertainment", "Entertainment", "entertainment"),
    Flowers("flowers", "Flowers & Deco","flowers"),
    Transportation("transport", "Transportation","transportation"),
    Ceremony("ceremony", "Ceremony","ceremony"),
    Accesories("accessories", "Attire & Accessories", "attire"),
    Beauty("beauty", "Health & Beauty","beauty"),
    Food("food", "Food & Drink","food"),
    Guests("guests", "Guests","guests")
}