package com.example.newevent2

import com.example.newevent2.Model.Vendor

interface CoRAddEditVendor {
    fun onAddEditVendor(vendor: Vendor)
}

interface CoRDeleteVendor {
    fun onDeleteVendor(vendor: Vendor)
}