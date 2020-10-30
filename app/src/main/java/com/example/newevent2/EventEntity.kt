package com.example.newevent2

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.database.*

class EventEntity : Event() {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.reference
    val postRef = myRef.child("User").child("Event")

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


//    fun getEventNamesHash(dataFetched: FirebaseSuccessListenerHashMap) {
//        var eventmap = HashMap<String, String>()
//        //var eventNameList = ArrayList<String>()
//        val eventListenerActive = object : ValueEventListener {
//            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//            override fun onDataChange(p0: DataSnapshot) {
//                eventmap.clear()
//                Log.i("ListDataStart",eventmap.toString())
//                for (snapshot in p0.children) {
//                    val eventitem = snapshot.getValue(Event::class.java)!!
//                    eventmap[snapshot.key.toString()] = eventitem.name
//                }
//                Log.i("ListDataEnd",eventmap.toString())
//                dataFetched.onHashMapCreated(eventmap)
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                println("loadPost:onCancelled ${databaseError.toException()}")
//            }
//
//        }
//        postRef.addValueEventListener(eventListenerActive)
//    }

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
                    eventlist.add( eventitem.name)
                }
                Log.i("ListDataEnd", eventlist.toString())
                dataFetched.onListCreated(eventlist)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }

        }
        postRef.addValueEventListener(eventListenerActive)
    }

}