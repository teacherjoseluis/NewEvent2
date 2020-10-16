package com.example.newevent2

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.database.*

class EventEntity : Event() {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.reference
    val postRef = myRef.child("User").child("Event")

    fun getEventKey(eventname: String, dataFetched: FirebaseSuccessListener) {
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

    fun getFirstEventKey(dataFetched: FirebaseSuccessListener) {
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


    fun getEventNames(dataFetched: FirebaseSuccessListener) {
        var eventList = ArrayList<String>()
        //var eventNameList = ArrayList<String>()
        val eventListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                eventList.clear()
                Log.i("ListDataStart",eventList.toString())
                for (snapshot in p0.children) {
                    val eventitem = snapshot.getValue(Event::class.java)!!
                    eventList.add(eventitem.name)
                }
                Log.i("ListDataEnd",eventList.toString())
                dataFetched.onListCreated(eventList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }

        }
        postRef.addValueEventListener(eventListenerActive)
    }
}