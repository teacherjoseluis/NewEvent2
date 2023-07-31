package com.bridesandgrooms.event.MVP

import android.content.Context
import com.bridesandgrooms.event.Model.Vendor
import com.bridesandgrooms.event.PaymentCreateEdit

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
