package com.bridesandgrooms.event.Functions

import Application.GuestCreationException
import Application.GuestDeletionException
import android.content.Context
import android.util.Log
import com.bridesandgrooms.event.Model.*

internal fun addGuest(context: Context, userItem: User, guestitem: Guest) {
    try {
        val userdbhelper = UserDBHelper(context)
        val guestmodel = GuestModel()
        val guestdbhelper = GuestDBHelper(context)
        val usermodel = UserModel(userItem)

        val chainofcommand = orderChainAddGuest(guestmodel, guestdbhelper, userdbhelper, usermodel)
        chainofcommand.onAddEditGuest(context, userItem, guestitem)
    } catch (e: Exception) {
        Log.e("GuestFunctions.kt", e.message.toString())
        throw GuestCreationException("Error during guest creation: $e")
    }
}

internal fun deleteGuest(context: Context, userItem: User, guestitem: Guest) {
    try {
        val userdbhelper = UserDBHelper(context)
        val guestmodel = GuestModel()
        val guestdbhelper = GuestDBHelper(context)
        val usermodel = UserModel(userItem)

        val chainofcommand =
            orderChainDel(usermodel, userdbhelper, guestdbhelper, guestmodel)
        chainofcommand.onDeleteGuest(context, userItem, guestitem)
    } catch (e: Exception) {
        Log.e("GuestFunctions.kt", e.message.toString())
        throw GuestDeletionException("Error during guest deletion: $e")
    }
}

internal fun editGuest(context: Context, userItem: User, guestitem: Guest) {
    try {
        val guestmodel = GuestModel()
        val guestdbhelper = GuestDBHelper(context)

        val chainofcommand = orderChainEdit(guestmodel, guestdbhelper)
        chainofcommand.onAddEditGuest(context, userItem, guestitem)
    } catch (e: Exception) {
        Log.e("GuestFunctions.kt", e.message.toString())
        throw GuestCreationException("Error during guest edition: $e")
    }
}

private fun orderChainAddGuest(
    guestModel: GuestModel,
    guestDBHelper: GuestDBHelper,
    userdbhelper: UserDBHelper,
    userModel: UserModel
): CoRAddEditGuest {
    guestModel.nexthandler = guestDBHelper
    guestDBHelper.nexthandler = userdbhelper
    userdbhelper.nexthandlerg = userModel
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
    guestDBHelper: GuestDBHelper
): CoRAddEditGuest {
    guestModel.nexthandler = guestDBHelper
    return guestModel
}