package com.example.newevent2.MVP

import android.content.Context
import android.view.View
import com.example.newevent2.Model.PaymentDBHelper
import com.example.newevent2.Model.Vendor
import com.example.newevent2.Model.VendorPayment
import com.example.newevent2.VendorsAll

class VendorsAllPresenter(
    val context: Context,
    val fragment: VendorsAll
) :
    VendorPresenter.VendorList {

    private var presentervendor: VendorPresenter = VendorPresenter(context, this)

    init {
        presentervendor.getVendorList()
    }

    override fun onVendorList(list: ArrayList<Vendor>) {
        val vendorpaymentlist = ArrayList<VendorPayment>()
        val paymentDB = PaymentDBHelper(context)
        list.forEach { vendor ->
            val amountlist = paymentDB.getVendorPayments(vendor.key)
            vendorpaymentlist.add(VendorPayment(vendor, amountlist))
        }
        fragment.onVAVendors(vendorpaymentlist)
    }

    override fun onVendorListError(errcode: String) {
        fragment.onVAVendorsError(VendorPresenter.ERRCODEVENDORS)
    }

    interface VAVendors {
        fun onVAVendors(
            vendorpaymentlist: ArrayList<VendorPayment>
        )

        fun onVAVendorsError(errcode: String)
    }
}