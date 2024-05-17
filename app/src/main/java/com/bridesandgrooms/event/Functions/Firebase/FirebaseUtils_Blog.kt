package com.bridesandgrooms.event.Functions.Firebase

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
private var myRef = database.getReference("/flamelink/environments/production/content/")
private var mediaRef = database.getReference("/flamelink/media/files/")

internal fun getBlog(
    language: String,
    dataFetched: FirebaseGetBlogSuccess
) {
    val postRef = when (language) {
        "en" -> myRef.child("blogPost").child("en-US")
        else -> {
            myRef.child("blogPost").child(language)
        } //it needs to fail here, I don't know how Spanish will look like
    }
    val bloglist = java.util.ArrayList<BlogPost>()
    val blogListenerActive = object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot) {
            for (snapshot in p0.children) {
                //val blogitem = snapshot.getValue(BlogPost::class.java)!!
                val blogItem = BlogPost()
                blogItem.author = snapshot.child("author").value as String
                blogItem.publicationDate = snapshot.child("publicationDate").value as String
                blogItem.summary = snapshot.child("summary").value as String
                blogItem.title = snapshot.child("title").value as String
                blogItem.url = snapshot.child("url").value as String
                blogItem.image = snapshot.child("imageLink").value as String
                blogItem.readingTime = snapshot.child("readingTime").value as String
                bloglist.add(blogItem)
            }
            dataFetched.onGetBlogSuccess(bloglist)
        }

        override fun onCancelled(error: DatabaseError) {
            println("loadPost:onCancelled ${error.toException()}")
        }

//        suspend fun getImageValue(mediaRef: DatabaseReference, imagePath: String): String {
//            return suspendCancellableCoroutine { continuation ->
//                val query = mediaRef.child(imagePath)
//                query.addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        val imageValue = snapshot.child("file").value as String
//                        continuation.resume(imageValue)
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        continuation.resumeWithException(error.toException())
//                    }
//                })
//            }
//        }
    }
    postRef.addValueEventListener(blogListenerActive)
}

//    val blogListenerActive = object : ValueEventListener {
//        override fun onDataChange(p0: DataSnapshot) {
//            for (snapshot in p0.children) {
//                val blogitem = snapshot.getValue(BlogPost::class.java)!!
//                bloglist.add(blogitem)
//            }
//            dataFetched.onGetBlogSuccess(bloglist)
//        }
//
//        override fun onCancelled(error: DatabaseError) {
//            println("loadPost:onCancelled ${error.toException()}")
//        }
//    }
// postRef.addValueEventListener(blogListenerActive)


open class BlogPost (
    var author: String = "",
    var image: String = "",
    var publicationDate: String = "",
    var summary: String = "",
    var title: String = "",
    var url: String = "",
    var readingTime: String = ""
    ) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeString(author)
        p0.writeString(image)
        p0.writeString(publicationDate)
        p0.writeString(summary)
        p0.writeString(title)
        p0.writeString(url)
        p0.writeString(readingTime)
    }

    companion object CREATOR : Parcelable.Creator<BlogPost> {
        override fun createFromParcel(parcel: Parcel): BlogPost {
            return BlogPost(parcel)
        }

        override fun newArray(size: Int): Array<BlogPost?> {
            return arrayOfNulls(size)
        }
    }

}

interface FirebaseGetBlogSuccess {
    fun onGetBlogSuccess(bloglist: ArrayList<BlogPost>)
}