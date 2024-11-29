package com.bridesandgrooms.event.Functions

import Application.GuestCreationException
import Application.GuestDeletionException
import android.content.Context
import android.util.Log
import com.bridesandgrooms.event.Model.*

internal fun addGuest(guestitem: Guest) {
    try {
        val userdbhelper = UserDBHelper()
        val guestmodel = GuestModel()
        val guestdbhelper = GuestDBHelper()
        val usermodel = UserModel()

        val chainofcommand = orderChainAddGuest(guestmodel, guestdbhelper, userdbhelper, usermodel)
        chainofcommand.onAddEditGuest(guestitem)
    } catch (e: Exception) {
        Log.e("GuestFunctions.kt", e.message.toString())
        throw GuestCreationException("Error during guest creation: $e")
    }
}

internal fun deleteGuest(guestId: String) {
    try {
        val userdbhelper = UserDBHelper()
        val guestmodel = GuestModel()
        val guestdbhelper = GuestDBHelper()
        val usermodel = UserModel()

        val chainofcommand =
            orderChainDel(usermodel, userdbhelper, guestdbhelper, guestmodel)
        chainofcommand.onDeleteGuest(guestId)
    } catch (e: Exception) {
        Log.e("GuestFunctions.kt", e.message.toString())
        throw GuestDeletionException("Error during guest deletion: $e")
    }
}

internal fun editGuest(guestitem: Guest) {
    try {
        val guestmodel = GuestModel()
        val guestdbhelper = GuestDBHelper()

        val chainofcommand = orderChainEdit(guestmodel, guestdbhelper)
        chainofcommand.onAddEditGuest(guestitem)
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