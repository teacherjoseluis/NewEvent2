package com.bridesandgrooms.event.Functions

import Application.VendorCreationException
import Application.VendorDeletionException
import android.content.Context
import android.util.Log
import com.bridesandgrooms.event.Model.*

internal fun addVendor(vendoritem: Vendor) {
    try {
        val vendormodel = VendorModel()
        val vendordbhelper = VendorDBHelper()
        val usermodel = UserModel()
        val userdbhelper = UserDBHelper()

        val chainofcommand = orderChainAddVendor(vendormodel, vendordbhelper, usermodel, userdbhelper)
        chainofcommand.onAddEditVendor(vendoritem)
    } catch (e: Exception) {
        Log.e("VendorFunctions.kt", e.message.toString())
        throw VendorCreationException("Error during vendor creation: $e")
    }
}

internal fun deleteVendor(vendorId: String) {
    try {
        val vendormodel = VendorModel()
        val vendordbhelper = VendorDBHelper()
        val usermodel = UserModel()
        val userdbhelper = UserDBHelper()

        val chainofcommand =
            orderChainDel(usermodel, vendordbhelper, vendormodel, userdbhelper)
        chainofcommand.onDeleteVendor(vendorId)
    } catch (e: Exception) {
        Log.e("VendorFunctions.kt", e.message.toString())
        throw VendorDeletionException("Error during vendor deletion: $e")
    }
}

internal fun editVendor(vendoritem: Vendor) {
    try {
        val vendormodel = VendorModel()
        val vendordbhelper = VendorDBHelper()

        val chainofcommand = orderChainEdit(vendormodel, vendordbhelper)
        chainofcommand.onAddEditVendor(vendoritem)
    } catch (e: Exception) {
        Log.e("VendorFunctions.kt", e.message.toString())
        throw VendorCreationException("Error during vendor edition: $e")
    }
}

private fun orderChainAddVendor(
    vendorModel: VendorModel,
    vendorDBHelper: VendorDBHelper,
    userModel: UserModel,
    userdbhelper: UserDBHelper
): CoRAddEditVendor {
    vendorModel.nexthandler = vendorDBHelper
    vendorDBHelper.nexthandler = userModel
    userModel.nexthandlerv = userdbhelper
    return vendorModel
}

private fun orderChainDel(
    userModel: UserModel,
    vendorDBHelper: VendorDBHelper,
    vendorModel: VendorModel,
    userdbhelper: UserDBHelper
): CoRDeleteVendor {
    userModel.nexthandlerdelv = vendorDBHelper
    vendorDBHelper.nexthandlerdel = vendorModel
    vendorModel.nexthandlerdel = userdbhelper
    return userModel
}

private fun orderChainEdit(
    vendorModel: VendorModel,
    vendorDBHelper: VendorDBHelper
): CoRAddEditVendor {
    vendorModel.nexthandler = vendorDBHelper
    return vendorModel
}