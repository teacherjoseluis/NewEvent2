package com.example.newevent2

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*

class EventEntity : Event() {

    private val database = FirebaseDatabase.getInstance()
    private val storage = Firebase.storage

    private val myRef = database.reference
    private val storageRef = storage.reference

    private val postRef = myRef.child("User").child("Event")

    fun getEventKey(eventname: String, dataFetched: FirebaseSuccessListenerSingleValue) {
        var eventkey = ""

        val eventListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {

                for (snapshot in p0.children) {
                    val eventitem = snapshot.getValue(Event::class.java)!!
                    if (eventitem.name == eventname) {
                        eventkey = snapshot.key.toString()
                    }
                }
                dataFetched.onDatafound(eventkey)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(eventListenerActive)
    }

    fun getFirstEventKey(dataFetched: FirebaseSuccessListenerSingleValue) {
        val eventListenerActive = object : ValueEventListener {
            var eventkey = ""

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                loop@
                for (snapshot in p0.children) {
                    val eventitem = snapshot.getValue(Event::class.java)!!
                    eventkey = snapshot.key.toString()
                    break@loop
                }
                dataFetched.onDatafound(eventkey)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(eventListenerActive)
    }

    fun getEventNames(dataFetched: FirebaseSuccessListenerList) {
        var eventlist = ArrayList<String>()
        //var eventNameList = ArrayList<String>()
        val eventListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                eventlist.clear()
                Log.i("ListDataStart", eventlist.toString())
                for (snapshot in p0.children) {
                    val eventitem = snapshot.getValue(Event::class.java)!!
                    eventlist.add(eventitem.name)
                }
                Log.i("ListDataEnd", eventlist.toString())
                val listevent = eventlist as ArrayList<Any>
                dataFetched.onListCreated(eventlist)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }

        }
        postRef.addValueEventListener(eventListenerActive)
    }

    fun getEvents(dataFetched: FirebaseSuccessListenerEvent) {

        var eventList = ArrayList<Event>()
        val eventListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                eventList.clear()
                for (snapshot in p0.children) {
                    val eventitem = snapshot.getValue(Event::class.java)!!
                    eventitem!!.key = snapshot.key.toString()
                    eventList.add(eventitem!!)
                }
                dataFetched.onEventList(eventList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(eventListenerActive)
    }

    fun getEventdetail(context: Context, dataFetched: FirebaseSuccessListenerEvent) {
        val usersessionlist = getUserSession(context)
        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
        val postRef =
            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])

        val eventListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                val eventitem = p0.getValue(Event::class.java)!!
                eventitem!!.key = p0.key.toString()
                dataFetched.onEvent(eventitem)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(eventListenerActive)
    }

    fun addEvent(uri: Uri?) {
        // Save Event detail in DB
        val myRef = postRef.push()
        val events = hashMapOf(
            "name" to name,
            "date" to date,
            "time" to time,
            "location" to location,
            "placeid" to placeid,
            "latitude" to latitude,
            "longitude" to longitude,
            "address" to address,
            "about" to about,
            "imageurl" to imageurl
        )

        myRef.setValue(events as Map<String, Any>)
            .addOnFailureListener {
                return@addOnFailureListener
            }
            .addOnSuccessListener {
                //Save Event image in Storage
                if (uri != null) {
                    key = myRef.key.toString()
                    val imageRef = storageRef.child("images/$key/${uri.lastPathSegment}")
                    val uploadTask = imageRef.putFile(uri)

                    uploadTask.addOnFailureListener {
                        return@addOnFailureListener
                    }.addOnSuccessListener {
                        return@addOnSuccessListener
                    }
                }
            }
    }

    fun editEvent(context: Context, uri: Uri?) {
        // Save Event detail in DB
        val usersessionlist = getUserSession(context)
        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Task")
        val postRef =
            myRef.child("User").child(usersessionlist[0]).child("Event").child(usersessionlist[3])

        val events = hashMapOf(
            "name" to name,
            "date" to date,
            "time" to time,
            "location" to location,
            "placeid" to placeid,
            "latitude" to latitude,
            "longitude" to longitude,
            "address" to address,
            "about" to about,
            "imageurl" to uri!!.lastPathSegment
        )

        postRef.setValue(events as Map<String, Any>)
            .addOnFailureListener {
                return@addOnFailureListener
            }
            .addOnSuccessListener {
                //Save Event image in Storage
                if (uri != null && uri.lastPathSegment != imageurl) {
                    val eventkey = postRef.key.toString()
                    val imageRef = storageRef.child("images/$eventkey/${uri.lastPathSegment}")
                    val uploadTask = imageRef.putFile(uri)

                    uploadTask.addOnFailureListener {
                        return@addOnFailureListener
                    }.addOnSuccessListener {
                        return@addOnSuccessListener
                    }
                }
            }
    }
}
