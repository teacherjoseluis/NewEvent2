package com.example.newevent2

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
private var myRef = database.reference

internal fun saveLog(
    context: Context,
    action: String,
    entity: String,
    entityid: String,
    entityname: String,
    userid: String,
    eventid: String
) {
    val usersessionlist = getUserSession(context)

    val postRef =
        myRef.child("User").child(userid).child("Event").child(eventid)
            .child("Log").push()

    //---------------------------------------
    // Getting the time and date to record in the recently created task
    val timestamp = Time(System.currentTimeMillis())
    val taskdatetime = Date(timestamp.time)
    val sdf = SimpleDateFormat("MM/dd/yyyy h:mm:ss a")
    val nodedate = SimpleDateFormat("yyyyMMdd")
    //---------------------------------------

    val loginfo = hashMapOf(
        "userid" to userid,
        //"eventid" to usersessionlist[3],
        "action" to action,
        "entity" to entity,
        "entityid" to entityid,
        "entityname" to entityname,
        "date" to nodedate.format(taskdatetime),
        "createdatetime" to sdf.format(taskdatetime)
    )

    postRef.setValue(loginfo as Map<String, Any>)
        .addOnFailureListener {
        }
        .addOnSuccessListener {
        }
}

internal fun getLog(
    context: Context,
    //daysback: Long,
    dataFetched: FirebaseSuccessListenerLogWelcome
) {
    val usersessionlist = getUserSession(context)
    val postRef =
        myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])
            .child("Log")
            .orderByChild("date")
    var loginfolist = ArrayList<Loginfo>()
    val logListenerActive = object : ValueEventListener {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onDataChange(p0: DataSnapshot) {
            val todaydate = LocalDate.now()
            for (snapshot in p0.children) {
                val loginfoitem = snapshot.getValue(Loginfo::class.java)!!
//                val beforedate = todaydate.minusDays(daysback)
                //Check in detail that the formatting works. Most likely the today and before dates will need to be formatted
                // to match whatever is read from Firebase
//                if(loginfoitem!!.date >= beforedate.toString() && loginfoitem.date <= todaydate.toString()){
                loginfolist.add(loginfoitem)
                //               }
            }
            dataFetched.onLogList(loginfolist)
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }
    postRef.addValueEventListener(logListenerActive)
}

class Loginfo {
    var userid: String = ""

    //ar eventid: String = ""
    var action: String = ""
    var entity: String = ""
    var entityid: String = ""
    var entityname: String = ""
    var date: String = ""
    var createdatetime: String = ""
}

internal fun getBlog(
    dataFetched: FirebaseSuccessListenerBlogWelcome
) {
    val postRef =
        myRef.child("Blog")
    var bloglist = ArrayList<Blog>()
    val blogListenerActive = object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot) {
            for (snapshot in p0.children) {
                val blogitem = snapshot.getValue(Blog::class.java)!!
                bloglist.add(blogitem)
            }
            dataFetched.onBlogList(bloglist)
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
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