package com.bridesandgrooms.event.Model

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.bridesandgrooms.event.Functions.CoRAddEditVendor
import com.bridesandgrooms.event.Functions.CoRDeleteVendor
import com.bridesandgrooms.event.Functions.UserSessionHelper
import com.google.firebase.FirebaseException
import com.google.firebase.database.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class VendorModel : CoRAddEditVendor, CoRDeleteVendor {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef = database.reference
    var nexthandler: CoRAddEditVendor? = null
    var nexthandlerdel: CoRDeleteVendor? = null

    @ExperimentalCoroutinesApi
    suspend fun getVendors(): java.util.ArrayList<Vendor> {
        val userId =
            UserSessionHelper.getUserSession("user_id") as String
        val eventId =
            UserSessionHelper.getUserSession("event_id") as String

        val postRef =
            myRef.child("User").child(userId).child("Event").child(eventId)
                .child("Vendor").orderByChild("name")
        val vendorList = java.util.ArrayList<Vendor>()

        try {
            for (snapChild in postRef.awaitsSingle()?.children!!) {
                val vendorItem = snapChild.getValue(Vendor::class.java)
                vendorItem!!.key = snapChild.key.toString()
                vendorList.add(vendorItem)
            }
        } catch (e: Exception) {
            println(e.message)
        }
        return vendorList
    }

    fun getAllVendorList(
        dataFetched: FirebaseSuccessVendorList
    ) {
        val userId =
            UserSessionHelper.getUserSession("user_id") as String
        val eventId =
            UserSessionHelper.getUserSession("event_id") as String

        val postRef =
            myRef.child("User").child(userId).child("Event").child(eventId)
                .child("Vendor").orderByChild("name")
        val vendorList = ArrayList<Vendor>()

        val vendorListenerActive = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(p0: DataSnapshot) {
                vendorList.clear()

                for (snapshot in p0.children) {
                    val vendoritem = snapshot.getValue(Vendor::class.java)!!
                    vendoritem.key = snapshot.key.toString()
                    vendorList.add(vendoritem)
                }
                dataFetched.onVendorList(vendorList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        postRef.addValueEventListener(vendorListenerActive)
    }

    private fun addVendor(
        vendor: Vendor,
        vendoraddedflag: FirebaseAddEditVendorSuccess
    ) {
        val userId =
            UserSessionHelper.getUserSession("user_id") as String
        val eventId =
            UserSessionHelper.getUserSession("event_id") as String

        val postRef =
            myRef.child("User").child(userId).child("Event").child(eventId)
                .child("Vendor").push()

        //---------------------------------------
        // Getting the time and date to record in the recently created guest
        val timestamp = Time(System.currentTimeMillis())
        val vendordatetime = Date(timestamp.time)
        val sdf = SimpleDateFormat("MM/dd/yyyy h:mm:ss a")
        //---------------------------------------

        val vendoradd = hashMapOf(
            "name" to vendor.name,
            "phone" to vendor.phone,
            "email" to vendor.email,
            "category" to vendor.category,
            "eventid" to vendor.eventid,
            "placeid" to vendor.placeid,
            "location" to vendor.location,
            "createdatetime" to sdf.format(vendordatetime),
            "googlevendorname" to vendor.googlevendorname,
            "ratingnumber" to vendor.ratingnumber,
            "reviews" to vendor.reviews,
            "rating" to vendor.rating
        )

        postRef.setValue(vendoradd as Map<String, Any>)
            .addOnSuccessListener {
                vendor.key = postRef.key.toString()
                vendoraddedflag.onVendorAddedEdited(true, vendor)
                Log.d(
                    TAG,
                    "Vendor ${vendor.name} successfully added on ${sdf.format(vendordatetime)}"
                )
            }
            .addOnFailureListener {
                vendoraddedflag.onVendorAddedEdited(false, vendor)
                Log.e(TAG, "Vendor ${vendor.name} failed to be added")
            }
    }

    private fun editVendor(
        vendor: Vendor,
        vendoreditedflag: FirebaseAddEditVendorSuccess
    ) {
        val userId =
            UserSessionHelper.getUserSession("user_id") as String
        val eventId =
            UserSessionHelper.getUserSession("event_id") as String

        val postRef =
            myRef.child("User").child(userId).child("Event").child(eventId)
                .child("Vendor").child(vendor.key)

        val vendoredit = hashMapOf(
            "name" to vendor.name,
            "phone" to vendor.phone,
            "email" to vendor.email,
            "category" to vendor.category,
            "eventid" to vendor.eventid,
            "placeid" to vendor.placeid,
            "location" to vendor.location,
            "googlevendorname" to vendor.googlevendorname,
            "ratingnumber" to vendor.ratingnumber,
            "reviews" to vendor.reviews,
            "rating" to vendor.rating
        )

        postRef.setValue(vendoredit as Map<String, Any>)
            .addOnSuccessListener {
                vendoreditedflag.onVendorAddedEdited(true, vendor)
                Log.d(TAG, "Vendor ${vendor.name} successfully edited")
            }
            .addOnFailureListener {
                vendoreditedflag.onVendorAddedEdited(false, vendor)
                Log.e(TAG, "Vendor ${vendor.name} failed to be edited")
            }
    }

    private fun deleteVendor(
        vendorId: String,
        vendordeletedflag: FirebaseDeleteVendorSuccess
    ) {
        val userId =
            UserSessionHelper.getUserSession("user_id") as String
        val eventId =
            UserSessionHelper.getUserSession("event_id") as String

        val postRef =
            myRef.child("User").child(userId).child("Event").child(eventId)
                .child("Vendor").child(vendorId)
                .removeValue()
                .addOnSuccessListener {
                    vendordeletedflag.onVendorDeleted(true, vendorId)
                    Log.d(TAG, "Vendor $vendorId successfully deleted")
                }
                .addOnFailureListener {
                    vendordeletedflag.onVendorDeleted(false, vendorId)
                    Log.e(TAG, "Vendor $vendorId failed to be deleted")
                }
    }

    @ExperimentalCoroutinesApi
    suspend fun Query.awaitsSingle(): DataSnapshot? =
        suspendCancellableCoroutine { continuation ->
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        continuation.resume(snapshot)
                    } catch (exception: Exception) {
                        continuation.resumeWithException(exception)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    val exception = when (error.toException()) {
                        is FirebaseException -> error.toException()
                        else -> Exception("The Firebase call for reference $this was cancelled")
                    }
                    continuation.resumeWithException(exception)
                }
            }
            continuation.invokeOnCancellation { this.removeEventListener(listener) }
            this.addListenerForSingleValueEvent(listener)
        }

    override fun onAddEditVendor(vendor: Vendor) {
        if (vendor.key.isEmpty()) {
            addVendor(
                vendor,
                object : FirebaseAddEditVendorSuccess {
                    override fun onVendorAddedEdited(flag: Boolean, vendor: Vendor) {
                        if (flag) {
                            nexthandler?.onAddEditVendor(vendor)
                        }
                    }
                })
        } else {
            editVendor(
                vendor, object : FirebaseAddEditVendorSuccess {
                    override fun onVendorAddedEdited(flag: Boolean, vendor: Vendor) {
                        if (flag) {
                            nexthandler?.onAddEditVendor(vendor)
                        }
                    }
                }
            )
        }
    }

    override fun onDeleteVendor(vendorId: String) {
        deleteVendor(
            vendorId,
            object : FirebaseDeleteVendorSuccess {
                override fun onVendorDeleted(flag: Boolean, vendorId: String) {
                    if (flag) {
                        nexthandlerdel?.onDeleteVendor(vendorId)
                    }
                }
            })
    }

    interface FirebaseAddEditVendorSuccess {
        fun onVendorAddedEdited(flag: Boolean, vendor: Vendor)
    }

    interface FirebaseDeleteVendorSuccess {
        fun onVendorDeleted(flag: Boolean, vendorId: String)
    }

    interface FirebaseSuccessVendorList {
        fun onVendorList(list: ArrayList<Vendor>)
    }

    companion object {
        const val TAG = "VendorModel"
        const val ACTIVEFLAG = "Y"
        const val INACTIVEFLAG = "Y"
    }
}