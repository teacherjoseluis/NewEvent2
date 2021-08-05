//package com.example.newevent2
//
//import android.os.Build
//import androidx.annotation.RequiresApi
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//
//class VendorEntity : Vendor() {
//
//    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
//    private val myRef = database.reference
//
//    fun getVendorsContacts(dataFetched: FirebaseSuccessListener_Vendor) {
//        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Vendor")
//        val postRef = myRef.child("User").child("Vendor")
//        var vendorList = ArrayList<Vendor>()
//        val vendorListenerActive = object : ValueEventListener {
//            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//            override fun onDataChange(p0: DataSnapshot) {
//                vendorList.clear()
//                for (snapshot in p0.children) {
//                    val vendoritem = snapshot.getValue(Vendor::class.java)!!
//                    vendoritem.key = snapshot.key.toString()
//                    vendorList.add(vendoritem)
//                }
//                dataFetched.onVendorList(vendorList)
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                println("loadPost:onCancelled ${databaseError.toException()}")
//            }
//        }
//        postRef.addValueEventListener(vendorListenerActive)
//    }
//
//    fun addVendor() {
//        //val postRef = myRef.child("User").child("Event").child(this.eventid).child("Vendor").push()
//        val postRef = myRef.child("User").child("Vendor").push()
//        val vendor = hashMapOf(
//            "name" to name,
//            //"eventid" to eventid,
//            "contactid" to contactid,
//            "phone" to phone,
//            "email" to email,
//            "placeid" to placeid,
//            "latitude" to latitude,
//            "longitude" to longitude
//        )
//
//        postRef.setValue(vendor as Map<String, Any>)
//            .addOnFailureListener {
//            }
//            .addOnSuccessListener {
//            }
//    }
//
//    fun deleteVendor() {
//        //myRef.child("User").child("Event").child(this.eventid).child("Vendor").child(this.key)
//        myRef.child("User").child("Vendor").child(this.key)
//            .removeValue()
//    }
//}