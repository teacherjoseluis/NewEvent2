package com.bridesandgrooms.event.Functions

import com.bridesandgrooms.event.Model.Vendor

interface CoRAddEditVendor {
    fun onAddEditVendor(vendor: Vendor)
}

interface CoRDeleteVendor {
    fun onDeleteVendor(vendor: Vendor)
}