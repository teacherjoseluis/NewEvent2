package com.bridesandgrooms.event.Functions

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import com.bridesandgrooms.event.*
import com.bridesandgrooms.event.Model.*

var guestmodel = GuestModel()
lateinit var guestdbhelper: GuestDBHelper
private lateinit var usermodel: UserModel
@SuppressLint("StaticFieldLeak")
private lateinit var contactsAll: ContactsAll
@SuppressLint("StaticFieldLeak")
private lateinit var guestCreateEdit: GuestCreateEdit

internal fun addGuest(context: Context, guestitem: Guest, caller: String) {
    try {
        //------------------------------------------------
        // Updating User information in Local DB
        userdbhelper = UserDBHelper(context)
        //------------------------------------------------
        // Adding a new record in Firebase
        val user = userdbhelper.getUser(userdbhelper.getUserKey())
        guestmodel.userid = user.userid!!
        guestmodel.eventid = user.eventid
        //taskmodel.task = taskitem
        //------------------------------------------------
        // Adding a new record in Local DB
        guestdbhelper = GuestDBHelper(context)
        //taskdbhelper.task = taskitem
        //------------------------------------------------
        // Updating User information in Firebase
        usermodel = UserModel(user.userid)
        //usermodel.tasksactive = user.tasksactive
        //------------------------------------------------
        if (caller == "contact"){
        contactsAll = ContactsAll()
        contactsAll.mContext = context
                val chainofcommand = orderChainAddContact(
                    guestmodel,
                    guestdbhelper,
                    userdbhelper,
                    usermodel
                )
                chainofcommand.onAddEditGuest(guestitem)
        } else if (caller == "guest") {
            guestCreateEdit = GuestCreateEdit()
            guestCreateEdit.mContext = context
            val chainofcommand = orderChainAddGuest(guestmodel, guestdbhelper, userdbhelper, usermodel, guestCreateEdit)
            chainofcommand.onAddEditGuest(guestitem)
        } else if (caller == "none") {
            val chainofcommand = orderChainAddNone(guestmodel, guestdbhelper, userdbhelper, usermodel)
            chainofcommand.onAddEditGuest(guestitem)
        }
        //------------------------------------------------

        //------------------------------------------------
        //chainofcommand.onAddEditGuest(guestitem)
        //------------------------------------------------
        // Updating User information in Session
//        user.guests = user.guests + 1
//        user.hasguest = GuestModel.ACTIVEFLAG
//        user.saveUserSession(context)
        //------------------------------------------------
        // It's fair to believe that asynchronous calls were already executed at this point

        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("RSVP", guestitem.rsvp)
        bundle.putString("COMPANION", guestitem.companion)
        bundle.putString("COUNTRY", user.country)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("ADDGUEST", bundle)
        //------------------------------------------------

        Toast.makeText(context, context.getString(R.string.successaddguest), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        val errormsg = context.getString(R.string.erroraddguest)
        errormsg.plus(e.message)
        Toast.makeText(
            context,
            errormsg,
            Toast.LENGTH_LONG
        ).show()
    }
}

internal fun deleteGuest(context: Context, guestitem: Guest) {
    try {
        // Updating User information in Local DB
        userdbhelper = UserDBHelper(context)
        //------------------------------------------------
        val user = userdbhelper.getUser(userdbhelper.getUserKey())
        guestmodel.userid = user.userid!!
        guestmodel.eventid = user.eventid
        //taskmodel.task = taskitem
        //------------------------------------------------
        // Adding a new record in Local DB
        guestdbhelper = GuestDBHelper(context)
        guestdbhelper.guest = guestitem
        //------------------------------------------------
        // Updating User information in Firebase
        usermodel = UserModel(user.userid)
        usermodel.guestsactive = user.guests
        //------------------------------------------------
        // Updating User information in Session
//        user.guests = user.guests - 1
//        if (user.guests == 0) user.hasguest = GuestModel.INACTIVEFLAG
//        user.saveUserSession(context)

        val chainofcommand =
            orderChainDel(usermodel, userdbhelper, guestdbhelper, guestmodel)
        chainofcommand.onDeleteGuest(guestitem)
        //------------------------------------------------
        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("RSVP", guestitem.rsvp)
        bundle.putString("COMPANION", guestitem.companion)
        bundle.putString("COUNTRY", user.country)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("DELETEGUEST", bundle)
        //------------------------------------------------
        Toast.makeText(context, context.getString(R.string.successdeleteguest), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        val errormsg = context.getString(R.string.errordeleteguest)
        errormsg.plus(e.message)
        Toast.makeText(
            context,
            errormsg,
            Toast.LENGTH_LONG
        ).show()
    }
}

internal fun editGuest(context: Context, guestitem: Guest) {
    try {
        // Updating User information in Local DB
        userdbhelper = UserDBHelper(context)
        //---------------------------------------------------
        val user = userdbhelper.getUser(userdbhelper.getUserKey())
        guestmodel.userid = user.userid!!
        guestmodel.eventid = user.eventid
        //taskmodel.task = taskitem
        //------------------------------------------------
        // Adding a new record in Local DB
        guestdbhelper = GuestDBHelper(context)
        //taskdbhelper.task = taskitem
        //------------------------------------------------
        guestCreateEdit = GuestCreateEdit()
        guestCreateEdit.mContext = context

        val chainofcommand = orderChainEdit(guestmodel, guestdbhelper, guestCreateEdit)
        chainofcommand.onAddEditGuest(guestitem)
        //------------------------------------------------
        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("RSVP", guestitem.rsvp)
        bundle.putString("COMPANION", guestitem.companion)
        bundle.putString("COUNTRY", user.country)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("EDITGUEST", bundle)
        //------------------------------------------------
        Toast.makeText(context, context.getString(R.string.successeditguest), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        val errormsg = context.getString(R.string.erroreditguest)
        errormsg.plus(e.message)
        Toast.makeText(
            context,
            errormsg,
            Toast.LENGTH_LONG
        ).show()
    }
}

@SuppressLint("Range")
internal fun contacttoGuest(context: Context, contactid: String): Guest {
    val contactguest = Guest()
    val cursor: Cursor?
    val phonecursor: Cursor?
    val emailcursor: Cursor?

    val whereclause = StringBuffer()
    whereclause.append(ContactsContract.Contacts._ID)
    whereclause.append(" = ")
    whereclause.append(contactid)

    val whereclausephone = StringBuffer()
    whereclausephone.append(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
    whereclausephone.append(" = ")
    whereclausephone.append(contactid)

    val whereclauseemail = StringBuffer()
    whereclauseemail.append(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
    whereclauseemail.append(" = ")
    whereclauseemail.append(contactid)

    cursor =
        context.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            whereclause.toString(),
            null, null
        )

    phonecursor =
        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            whereclausephone.toString(),
            null, null
        )

    emailcursor =
        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            null,
            whereclauseemail.toString(),
            null, null
        )

    cursor?.moveToNext()
    phonecursor?.moveToNext()
    emailcursor?.moveToNext()

    contactguest.name =
        cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
            .toString()

    contactguest.phone =
        try {
            phonecursor?.getString(phonecursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                .toString()
        } catch (e: IndexOutOfBoundsException) {
            ""
        }
    contactguest.email =
        try {
            emailcursor?.getString(emailcursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                .toString()
        } catch (e: IndexOutOfBoundsException) {
            ""
        }

    cursor?.let { cursor.close() }
    phonecursor?.let { phonecursor.close() }
    emailcursor?.let { emailcursor.close() }

    return contactguest
}

private fun orderChainAddContact(
    guestModel: GuestModel,
    guestDBHelper: GuestDBHelper,
    userdbhelper: UserDBHelper,
    userModel: UserModel
): CoRAddEditGuest {
    guestModel.nexthandler = guestDBHelper
    guestDBHelper.nexthandler = userdbhelper
    userdbhelper.nexthandleru = userModel
    return guestModel
}

private fun orderChainAddGuest(
    guestModel: GuestModel,
    guestDBHelper: GuestDBHelper,
    userdbhelper: UserDBHelper,
    userModel: UserModel,
    guestCreateEdit: GuestCreateEdit
): CoRAddEditGuest {
    guestModel.nexthandler = guestDBHelper
    guestDBHelper.nexthandler = userdbhelper
    userdbhelper.nexthandlerg = userModel
    userModel.nexthandlerg = guestCreateEdit
    return guestModel
}


private fun orderChainAddNone(
    guestModel: GuestModel,
    guestDBHelper: GuestDBHelper,
    userdbhelper: UserDBHelper,
    userModel: UserModel
): CoRAddEditGuest {
    guestModel.nexthandler = guestDBHelper
    guestDBHelper.nexthandler = userdbhelper
    userdbhelper.nexthandleru = userModel
    return guestModel
}

private fun orderChainDel(
    userModel: UserModel,
    userdbhelper: UserDBHelper,
    guestDBHelper: GuestDBHelper,
    guestModel: GuestModel
): CoRDeleteGuest {
    userModel.nexthandlerdelg = userdbhelper
    userdbhelper.nexthandlerdelg = guestDBHelper
    guestDBHelper.nexthandlerdel = guestModel
    return userModel
}

private fun orderChainEdit(
    guestModel: GuestModel,
    guestDBHelper: GuestDBHelper,
    guestCreateEdit: GuestCreateEdit
): CoRAddEditGuest {
    guestModel.nexthandler = guestDBHelper
    guestDBHelper.nexthandler = guestCreateEdit
    return guestModel
}