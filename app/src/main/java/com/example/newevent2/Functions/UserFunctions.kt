package com.example.newevent2.Functions

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.example.newevent2.CoRAddEditUser
import com.example.newevent2.Model.MyFirebaseApp
import com.example.newevent2.Model.User
import com.example.newevent2.Model.UserModel
import com.example.newevent2.R

private lateinit var usermodel: UserModel

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
        MyFirebaseApp.mFirebaseAnalytics.logEvent("ADDUSER", bundle)
        //------------------------------------------------
        Toast.makeText(context, context.getString(R.string.successadduser), Toast.LENGTH_LONG).show()
        //addEvent(context, eventitem)
    } catch (e: Exception) {
        val errormsg = context.getString(R.string.erroradduser)
        errormsg.plus(e.message)
        Toast.makeText(
            context,
            errormsg,
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
        MyFirebaseApp.mFirebaseAnalytics.logEvent("EDITUSER", bundle)
        //------------------------------------------------
        Toast.makeText(context, context.getString(R.string.successedituser), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        val errormsg = context.getString(R.string.erroredituser)
        errormsg.plus(e.message)
        Toast.makeText(
            context,
            errormsg,
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