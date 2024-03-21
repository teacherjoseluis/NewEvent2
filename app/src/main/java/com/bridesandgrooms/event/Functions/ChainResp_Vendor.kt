package com.bridesandgrooms.event.Functions

import android.content.Context
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.Model.Vendor

interface CoRAddEditVendor {
    fun onAddEditVendor(context: Context, user: User, vendor: Vendor)
}

interface CoRDeleteVendor {
    fun onDeleteVendor(context: Context, user: User, vendor: Vendor)
}