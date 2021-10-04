package com.example.newevent2.Functions

import Application.CalendarEvent
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.example.newevent2.*
import com.example.newevent2.Model.*
import com.example.newevent2.Model.Event
import com.google.firebase.analytics.FirebaseAnalytics

private lateinit var usermodel: UserModel
private lateinit var eventmodele: EventModel

internal fun addUser(context: Context, useritem: User) {
    try {
        // Updating User information in Firebase
        usermodel = UserModel(useritem.key)
        //------------------------------------------------
        val chainofcommand = orderChainAdd(usermodel)
        chainofcommand.onAddEditUser(useritem)
        //------------------------------------------------
        // Updating User information in Session
        useritem.saveUserSession(context)
        //------------------------------------------------
        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("AUTHTYPE", useritem.authtype)
        bundle.putString("COUNTRY", useritem.country)
        bundle.putString("LANGUAGE", useritem.language)
        MyFirebaseApp.mFirebaseAnalytics!!.logEvent("ADDUSER", bundle)
        //------------------------------------------------
        Toast.makeText(context, "User was created successully", Toast.LENGTH_LONG).show()
        //addEvent(context, eventitem)
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "There was an error trying create the user ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

internal fun editUser(context: Context, useritem: User) {
    try {
        // Updating User information in Firebase
        usermodel = UserModel(useritem.key)
        //------------------------------------------------
        val chainofcommand = orderChainEdit(usermodel)
        chainofcommand.onAddEditUser(useritem)
        //------------------------------------------------
        // Updating User information in Session
        useritem.saveUserSession(context)
        //------------------------------------------------
        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("AUTHTYPE", useritem.authtype)
        bundle.putString("COUNTRY", useritem.country)
        bundle.putString("LANGUAGE", useritem.language)
        MyFirebaseApp.mFirebaseAnalytics!!.logEvent("EDITUSER", bundle)
        //------------------------------------------------
        Toast.makeText(context, "User was edit successully", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "There was an error trying to edit the user ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}


private fun orderChainAdd(
    userModel: UserModel
): CoRAddEditUser {
    userModel.nexthandleru
    return userModel
}

private fun orderChainEdit(
    userModel: UserModel
): CoRAddEditUser {
    userModel.nexthandleru
    return userModel
}