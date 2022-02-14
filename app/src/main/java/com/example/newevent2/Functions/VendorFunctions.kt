package com.example.newevent2.Functions

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import com.example.newevent2.*
import com.example.newevent2.Model.*

var vendormodel = VendorModel()
lateinit var vendordbhelper: VendorDBHelper
private lateinit var usermodel: UserModel
@SuppressLint("StaticFieldLeak")
private lateinit var contactsAll: ContactsAll
@SuppressLint("StaticFieldLeak")
private lateinit var vendorCreateEdit: VendorCreateEdit

internal fun addVendor(context: Context, vendoritem: Vendor, caller: String) {
    try {
        //------------------------------------------------
        // Adding a new record in Firebase
        val user = getUserSession(context)
        vendormodel.userid = user.key
        vendormodel.eventid = user.eventid
        //taskmodel.task = taskitem
        //------------------------------------------------
        // Adding a new record in Local DB
        vendordbhelper = VendorDBHelper(context)
        //taskdbhelper.task = taskitem
        //------------------------------------------------
        // Updating User information in Firebase
        usermodel = UserModel(user.key)
        //usermodel.tasksactive = user.tasksactive
        //------------------------------------------------
        if (caller == "contact"){
            contactsAll = ContactsAll()
            contactsAll.mContext = context
            val chainofcommand = orderChainAddContact(vendormodel, vendordbhelper, usermodel, contactsAll)
            chainofcommand.onAddEditVendor(vendoritem)
        } else if (caller == "vendor") {
            vendorCreateEdit = VendorCreateEdit()
            vendorCreateEdit.mContext = context
            val chainofcommand = orderChainAddVendor(vendormodel, vendordbhelper, usermodel, vendorCreateEdit)
            chainofcommand.onAddEditVendor(vendoritem)
        } else if (caller == "none") {
            val chainofcommand = orderChainAddNone(vendormodel, vendordbhelper, usermodel)
            chainofcommand.onAddEditVendor(vendoritem)
        }
        //------------------------------------------------

        //------------------------------------------------
        //chainofcommand.onAddEditGuest(guestitem)
        //------------------------------------------------
        // Updating User information in Session
//        user.vendors = user.vendors + 1
//        user.hasvendor = VendorModel.ACTIVEFLAG
//        user.saveUserSession(context)
        //------------------------------------------------
        // It's fair to believe that asynchronous calls were already executed at this point
        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("CATEGORY", vendoritem.category)
        bundle.putString("LOCATION", vendoritem.location)
        bundle.putString("COUNTRY", user.country)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("ADDVENDOR", bundle)
        //------------------------------------------------
        Toast.makeText(context, context.getString(R.string.successaddvendor), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        val errormsg = context.getString(R.string.erroraddvendor)
        errormsg.plus(e.message)
        Toast.makeText(
            context,
            errormsg,
            Toast.LENGTH_LONG
        ).show()
    }
}

internal fun deleteVendor(context: Context, vendoritem: Vendor) {
    try {
        val user = getUserSession(context)
        vendormodel.userid = user.key
        vendormodel.eventid = user.eventid
        //taskmodel.task = taskitem
        //------------------------------------------------
        // Adding a new record in Local DB
        vendordbhelper = VendorDBHelper(context)
        vendordbhelper.vendor = vendoritem
        //------------------------------------------------
        // Updating User information in Firebase
        usermodel = UserModel(user.key)
        usermodel.vendorsactive = user.vendors
        //------------------------------------------------
        // Updating User information in Session
//        user.vendors = user.vendors - 1
//        if (user.vendors == 0) user.hasvendor = VendorModel.INACTIVEFLAG
//        user.saveUserSession(context)

        val chainofcommand =
            orderChainDel(usermodel, vendordbhelper, vendormodel)
        chainofcommand.onDeleteVendor(vendoritem)
        //------------------------------------------------
        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("CATEGORY", vendoritem.category)
        bundle.putString("LOCATION", vendoritem.location)
        bundle.putString("COUNTRY", user.country)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("DELETEVENDOR", bundle)
        //------------------------------------------------
        Toast.makeText(context, context.getString(R.string.successdeletevendor), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        val errormsg = context.getString(R.string.errordeletevendor)
        errormsg.plus(e.message)
        Toast.makeText(
            context,
            errormsg,
            Toast.LENGTH_LONG
        ).show()
    }
}

internal fun editVendor(context: Context, vendoritem: Vendor) {
    try {
        //---------------------------------------------------
        val user = getUserSession(context)
        vendormodel.userid = user.key
        vendormodel.eventid = user.eventid
        //taskmodel.task = taskitem
        //------------------------------------------------
        // Adding a new record in Local DB
        vendordbhelper = VendorDBHelper(context)
        //taskdbhelper.task = taskitem
        //------------------------------------------------
        vendorCreateEdit = VendorCreateEdit()
        vendorCreateEdit.mContext = context

        val chainofcommand = orderChainEdit(vendormodel, vendordbhelper, vendorCreateEdit)
        chainofcommand.onAddEditVendor(vendoritem)
        //------------------------------------------------
        // ------- Analytics call ----------------
        val bundle = Bundle()
        bundle.putString("CATEGORY", vendoritem.category)
        bundle.putString("LOCATION", vendoritem.location)
        bundle.putString("COUNTRY", user.country)
        MyFirebaseApp.mFirebaseAnalytics.logEvent("EDITVENDOR", bundle)
        //------------------------------------------------
        Toast.makeText(context, context.getString(R.string.successeditvendor), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        val errormsg = context.getString(R.string.erroreditvendor)
        errormsg.plus(e.message)
        Toast.makeText(
            context,
            errormsg,
            Toast.LENGTH_LONG
        ).show()
    }
}

internal fun contacttoVendor(context: Context, contactid: String): Vendor {
    val contactvendor = Vendor()
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

    contactvendor.name =
        cursor?.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY))
            .toString()

    contactvendor.phone =
        try {
            phonecursor?.getString(phonecursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                .toString()
        } catch (e: IndexOutOfBoundsException) {
            ""
        }
    contactvendor.email =
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
    return contactvendor
}

private fun orderChainAddContact(
    vendorModel: VendorModel,
    vendorDBHelper: VendorDBHelper,
    userModel: UserModel,
    contactsAll: ContactsAll
): CoRAddEditVendor {
    vendorModel.nexthandler = vendorDBHelper
    vendorDBHelper.nexthandler = userModel
    userModel.nexthandlerv = contactsAll
    return vendorModel
}

private fun orderChainAddVendor(
    vendorModel: VendorModel,
    vendorDBHelper: VendorDBHelper,
    userModel: UserModel,
    vendorCreateEdit: VendorCreateEdit
): CoRAddEditVendor {
    vendorModel.nexthandler = vendorDBHelper
    vendorDBHelper.nexthandler = userModel
    userModel.nexthandlerv = vendorCreateEdit
    return vendorModel
}


private fun orderChainAddNone(
    vendorModel: VendorModel,
    vendorDBHelper: VendorDBHelper,
    userModel: UserModel
): CoRAddEditVendor {
    vendorModel.nexthandler = vendorDBHelper
    vendorDBHelper.nexthandler = userModel
    return vendorModel
}

private fun orderChainDel(
    userModel: UserModel,
    vendorDBHelper: VendorDBHelper,
    vendorModel: VendorModel
): CoRDeleteVendor {
    userModel.nexthandlerdelv = vendorDBHelper
    vendorDBHelper.nexthandlerdel = vendorModel
    return userModel
}

private fun orderChainEdit(
    vendorModel: VendorModel,
    vendorDBHelper: VendorDBHelper,
    vendorCreateEdit: VendorCreateEdit
): CoRAddEditVendor {
    vendorModel.nexthandler = vendorDBHelper
    vendorDBHelper.nexthandler = vendorCreateEdit
    return vendorModel
}