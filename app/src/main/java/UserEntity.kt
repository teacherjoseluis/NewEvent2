//package com.example.newevent2
//
//import android.content.Context
//import android.net.Uri
//import android.os.Build
//import androidx.annotation.RequiresApi
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.google.firebase.ktx.Firebase
//import com.google.firebase.storage.ktx.storage
//import java.sql.Time
//import java.text.SimpleDateFormat
//import java.util.*
//
//class UserEntity /*: User()*/ {
//
//    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
//    private var myRef = database.reference
//    private val storage = Firebase.storage
//    private val storageRef = storage.reference
//
//    // Change for getUser data not only check if the user exists
//    fun getUser(dataFetched: FirebaseSuccessListenerUser) {
//
//        // Query should be applied to the node level, just taking the User record that matches with the key
//        // Let's try with the parcelable version of User otherwise we'll need to remove the Parceleable implementation
//        // and make attribute assignment in User Account class
//   //     val postRef = myRef.child("User").child(this.key)
//    //    var userexists = User()
//        val userListenerActive = object : ValueEventListener {
//            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//            override fun onDataChange(p0: DataSnapshot) {
//                // Extracting each of the elements of the node and assigning them to a local instance of User
//                //for (snapshot in p0.children) {
//                if (p0.exists()) {
////                    userexists.shortname = p0.child("shortname").getValue(String::class.java)!!
////                    userexists.email = p0.child("email").getValue(String::class.java)!!
////                    userexists.language = p0.child("language").getValue(String::class.java)!!
////                    userexists.country = p0.child("country").getValue(String::class.java)!!
////                    userexists.hasevent = p0.child("hasevent").getValue(String::class.java)!!
////                    userexists.eventid = p0.child("eventid").getValue(String::class.java)!!
////
////                    userexists.imageurl = p0.child("imageurl").getValue(String::class.java)!!
////                    userexists.role = p0.child("role").getValue(String::class.java)!!
//                    //val useritem = snapshot.getValue(User::class.java)
//                    //userexists = useritem
//                    //}
//                }
//                //dataFetched.onUserexists(userexists)
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                println("loadPost:onCancelled ${databaseError.toException()}")
//            }
//        }
// //       postRef.addValueEventListener(userListenerActive)
//    }
//
////    fun getProfileprogress(context: Context, dataFetched: FirebaseSuccessListenerUser) {
////        val usersessionlist = getUserSession(context)
////
////    }
//
//    fun editUser(context: Context, uri: Uri?) {
//        val usersessionlist = getUserSession(context)
//        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
//        val postRef =
//            myRef.child("User").child(usersessionlist[0])
//
//        val userkey = usersessionlist[0]
//
////        postRef.child("shortname").setValue(shortname)
////        postRef.child("country").setValue(country)
////        postRef.child("language").setValue(language)
////        postRef.child("role").setValue(role)
//
//        var userlocalsession =
//            context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
//        val sessionEditor = userlocalsession!!.edit()
////        sessionEditor.putString("Shortname", shortname) // UID from Firebase
////        sessionEditor.putString("Role", role)
//
//        //Save Event image in Storage
////        if (uri != null && uri.lastPathSegment != imageurl) {
////            val imageRef = storageRef.child("images/User/$userkey/${uri.lastPathSegment}")
////            val uploadTask = imageRef.putFile(uri)
//
////            uploadTask.addOnFailureListener {
////                return@addOnFailureListener
////            }.addOnSuccessListener {
////                sessionEditor.putString("Imageurl", uri.lastPathSegment)
////                return@addOnSuccessListener
////            }
//        }
////        sessionEditor.apply()
//
//
//    fun editUserEvent() {
////        val postRef = myRef.child("User").child(key)
////        postRef.child("eventid").setValue(eventid)
//    }
//
//    fun addUser() {
////        val postRef = myRef.child("User").child(this.key)
//
//        //---------------------------------------
//        // Getting the time and date to record in the recently created task
//        val timestamp = Time(System.currentTimeMillis())
//        val taskdatetime = Date(timestamp.time)
//        val sdf = SimpleDateFormat("MM/dd/yyyy h:mm:ss a")
//        //---------------------------------------
//
////        val user = hashMapOf(
////            "eventid" to eventid,
////            "shortname" to shortname,
////            "email" to email,
////            "country" to country,
////            "language" to language,
////            "createdatetime" to sdf.format(taskdatetime),
////            "status" to "A"
////        )
//
////        postRef.setValue(user as Map<String, Any>)
////            .addOnFailureListener {
////            }
////            .addOnSuccessListener {
////            }
//    }
//}
