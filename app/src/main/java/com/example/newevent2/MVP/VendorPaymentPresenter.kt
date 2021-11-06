package com.example.newevent2.MVP

import android.content.Context
import com.example.newevent2.Model.Vendor
import com.example.newevent2.PaymentCreateEdit

class VendorPaymentPresenter(
    val context: Context,
    val fragment: PaymentCreateEdit
) :
    VendorPresenter.VendorList {

        private var presentervendor: VendorPresenter = VendorPresenter(context, this)

        init {
            presentervendor.getVendorList()
        }

        override fun onVendorList(list: ArrayList<Vendor>) {
            val vendorlist = ArrayList<String>()
            for (vendor in list){
                vendorlist.add(vendor.name)
            }
            fragment.onVAVendors(vendorlist)
        }

        override fun onVendorListError(errcode: String) {
            fragment.onVAVendorsError(VendorPresenter.ERRCODEVENDORS)
        }

        interface VAVendors {
            fun onVAVendors(
                list: ArrayList<String>
            )

            fun onVAVendorsError(errcode: String)
        }
    }
