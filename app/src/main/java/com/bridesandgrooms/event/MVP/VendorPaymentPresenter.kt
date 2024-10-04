package com.bridesandgrooms.event.MVP

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.bridesandgrooms.event.Model.Vendor
import com.bridesandgrooms.event.UI.Fragments.PaymentCreateEdit

class VendorPaymentPresenter(
    val context: Context,
    val fragment: PaymentCreateEdit
) :
    VendorPresenter.VendorList {

    private var presentervendor: VendorPresenter = VendorPresenter(context, this)
    private val mHandler = Handler(Looper.getMainLooper())

    fun getVendorList() {
        Thread {
            presentervendor.getVendorList()
        }.start()
    }

    override fun onVendorList(list: ArrayList<Vendor>) {
        mHandler.post {
            val vendorlist = ArrayList<String>()
            for (vendor in list) {
                vendorlist.add(vendor.name)
            }
            fragment.onVAVendors(vendorlist)
        }
    }

    override fun onVendorListError(errcode: String) {
        mHandler.post {
            fragment.onVAVendorsError(VendorPresenter.ERRCODEVENDORS)
        }
    }

    interface VAVendors {
        fun onVAVendors(
            list: ArrayList<String>
        )

        fun onVAVendorsError(errcode: String)
    }
}
