package com.example.newevent2.Functions

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
private var myRef = database.reference

internal fun getBlog(
    language: String,
    dataFetched: FirebaseGetBlogSuccess
) {
    val postRef =
        myRef.child("Blog").child(language)
    var bloglist = ArrayList<Blog>()
    val blogListenerActive = object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot) {
            for (snapshot in p0.children) {
                val blogitem = snapshot.getValue(Blog::class.java)!!
                bloglist.add(blogitem)
            }
            dataFetched.onGetBlogSuccess(bloglist)
        }

        override fun onCancelled(error: DatabaseError) {
            println("loadPost:onCancelled ${error.toException()}")
        }
    }
    postRef.addValueEventListener(blogListenerActive)
}

class Blog {
    var blogtitle: String = ""
    var author: String = ""
    var blogdate: String = ""
    var readingtime: String = ""
    var imageurl: String = ""
    var link: String = ""
}

interface FirebaseGetBlogSuccess {
    fun onGetBlogSuccess(loglist: ArrayList<Blog>)
}