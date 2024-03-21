package com.bridesandgrooms.event.Functions

import Application.VendorCreationException
import Application.VendorDeletionException
import android.content.Context
import android.util.Log
import com.bridesandgrooms.event.Model.*

internal fun addVendor(context: Context, userItem: User, vendoritem: Vendor) {
    try {
        val vendormodel = VendorModel()
        val vendordbhelper = VendorDBHelper(context)
        val usermodel = UserModel(userItem)

        val chainofcommand = orderChainAddVendor(vendormodel, vendordbhelper, usermodel)
        chainofcommand.onAddEditVendor(context, userItem, vendoritem)
    } catch (e: Exception) {
        Log.e("VendorFunctions.kt", e.message.toString())
        throw VendorCreationException("Error during vendor creation: $e")
    }
}

internal fun deleteVendor(context: Context, userItem: User, vendoritem: Vendor) {
    try {
        val vendormodel = VendorModel()
        val vendordbhelper = VendorDBHelper(context)
        val usermodel = UserModel(userItem)

        val chainofcommand =
            orderChainDel(usermodel, vendordbhelper, vendormodel)
        chainofcommand.onDeleteVendor(context, userItem, vendoritem)
    } catch (e: Exception) {
        Log.e("VendorFunctions.kt", e.message.toString())
        throw VendorDeletionException("Error during vendor deletion: $e")
    }
}

internal fun editVendor(context: Context, userItem: User, vendoritem: Vendor) {
    try {
        val vendormodel = VendorModel()
        val vendordbhelper = VendorDBHelper(context)

        val chainofcommand = orderChainEdit(vendormodel, vendordbhelper)
        chainofcommand.onAddEditVendor(context, userItem, vendoritem)
    } catch (e: Exception) {
        Log.e("VendorFunctions.kt", e.message.toString())
        throw VendorCreationException("Error during vendor edition: $e")
    }
}

private fun orderChainAddVendor(
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
    vendorDBHelper: VendorDBHelper
): CoRAddEditVendor {
    vendorModel.nexthandler = vendorDBHelper
    return vendorModel
}