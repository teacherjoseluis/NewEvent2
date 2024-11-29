package com.bridesandgrooms.event.MVP

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.bridesandgrooms.event.Model.PaymentDBHelper
import com.bridesandgrooms.event.Model.Vendor
import com.bridesandgrooms.event.Model.VendorPayment
import com.bridesandgrooms.event.UI.Fragments.VendorsAll

class VendorsAllPresenter(
    val context: Context,
    val fragment: VendorsAll
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
            val vendorpaymentlist = ArrayList<VendorPayment>()
            val paymentDB = PaymentDBHelper()
            list.forEach { vendor ->
                val amountlist = paymentDB.getVendorPayments(vendor.key)!!
                vendorpaymentlist.add(VendorPayment(vendor, amountlist))
            }
            fragment.onVAVendors(vendorpaymentlist)
        }
    }

    override fun onVendorListError(errcode: String) {
        mHandler.post {
            fragment.onVAVendorsError(VendorPresenter.ERRCODEVENDORS)
        }
    }

    interface VAVendors {
        fun onVAVendors(
            vendorpaymentlist: ArrayList<VendorPayment>
        )

        fun onVAVendorsError(errcode: String)
    }
}