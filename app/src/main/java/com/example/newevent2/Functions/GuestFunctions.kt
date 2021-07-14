package com.example.newevent2.Functions

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import com.example.newevent2.*
import com.example.newevent2.Functions.getUserSession
import com.example.newevent2.Model.*
import kotlinx.android.synthetic.main.contacts_all.view.*

var guestmodel = GuestModel()
lateinit var guestdbhelper: GuestDBHelper
private lateinit var usermodel: UserModel
private lateinit var contactsAll: ContactsAll

internal fun addGuest(context: Context, guestitem: Guest) {
    try {
        //------------------------------------------------
        // Adding a new record in Firebase
        val user = com.example.newevent2.Functions.getUserSession(context!!)
        guestmodel.userid = user.key
        guestmodel.eventid = user.eventid
        //taskmodel.task = taskitem
        //------------------------------------------------
        // Adding a new record in Local DB
        guestdbhelper = GuestDBHelper(context)
        //taskdbhelper.task = taskitem
        //------------------------------------------------
        // Updating User information in Firebase
        usermodel = UserModel(user.key)
        //usermodel.tasksactive = user.tasksactive
        //------------------------------------------------
        contactsAll = ContactsAll()
        contactsAll.mContext = context
        //------------------------------------------------
        val chainofcommand = orderChainAdd(guestmodel, guestdbhelper, usermodel, contactsAll)
        chainofcommand.onAddEditGuest(guestitem)
        //------------------------------------------------
        // Updating User information in Session
        user.guests = user.guests + 1
        user.hasguest = GuestModel.ACTIVEFLAG
        user.saveUserSession(context)
        //------------------------------------------------
        // It's fair to believe that asynchronous calls were already executed at this point
        Toast.makeText(context, "Guest was created successully", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "There was an error trying create the guest ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

internal fun deleteGuest(context: Context, guestitem: Guest) {
    try {
        val user = getUserSession(context!!)
        guestmodel.userid = user.key
        guestmodel.eventid = user.eventid
        //taskmodel.task = taskitem
        //------------------------------------------------
        // Adding a new record in Local DB
        guestdbhelper = GuestDBHelper(context)
        guestdbhelper.guest = guestitem
        //------------------------------------------------
        // Updating User information in Firebase
        usermodel = UserModel(user.key)
        usermodel.guestsactive = user.guests
        //------------------------------------------------
        // Updating User information in Session
        user.guests = user.guests - 1
        if (user.guests == 0) user.hasguest = GuestModel.INACTIVEFLAG
        user.saveUserSession(context)

        val chainofcommand =
            orderChainDel(usermodel, guestdbhelper, guestmodel)
        chainofcommand.onDeleteGuest(guestitem)
        //------------------------------------------------
        Toast.makeText(context, "Guest was deleted successully", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "There was an error trying delete the guest ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

internal fun editGuest(context: Context, guestitem: Guest) {
    try {
        //---------------------------------------------------
        val user = getUserSession(context!!)
        guestmodel.userid = user.key
        guestmodel.eventid = user.eventid
        //taskmodel.task = taskitem
        //------------------------------------------------
        // Adding a new record in Local DB
        guestdbhelper = GuestDBHelper(context)
        //taskdbhelper.task = taskitem
        //------------------------------------------------

        val chainofcommand = orderChainEdit(guestmodel, guestdbhelper)
        chainofcommand.onAddEditGuest(guestitem)
        //------------------------------------------------
        Toast.makeText(context, "Guest was edited successully", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "There was an error trying edit the guest ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

internal fun contacttoGuest(context: Context, contactid: String): Guest {
    var contactguest = Guest()
    var cursor: Cursor?
    var phonecursor: Cursor?
    var emailcursor: Cursor?

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

//    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//    mPhoneNumber.setOnFocusChangeListener { p0, p1 ->
//        PhoneNumberUtils.formatNumber(
//            mPhoneNumber.text.toString(),
//            tm.simCountryIso
//        )
//    }
    return contactguest
}

private fun orderChainAdd(
    guestModel: GuestModel,
    guestDBHelper: GuestDBHelper,
    userModel: UserModel,
    contactsAll: ContactsAll
): CoRAddEditGuest {
    guestModel.nexthandler = guestDBHelper
    guestDBHelper.nexthandler = userModel
    userModel.nexthandlerg = contactsAll
    return guestModel
}

private fun orderChainDel(
    userModel: UserModel,
    guestDBHelper: GuestDBHelper,
    guestModel: GuestModel
): CoRDeleteGuest {
    userModel.nexthandlerdelg = guestDBHelper
    guestDBHelper.nexthandlerdel = guestModel
    return userModel
}

private fun orderChainEdit(
    guestModel: GuestModel,
    guestDBHelper: GuestDBHelper
): CoRAddEditGuest {
    guestModel.nexthandler = guestDBHelper
    return guestModel
}