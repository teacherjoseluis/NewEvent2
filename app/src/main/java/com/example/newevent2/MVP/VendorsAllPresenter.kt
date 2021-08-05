package com.example.newevent2.MVP

import android.content.Context
import android.view.View
import com.example.newevent2.Functions.converttoDate
import com.example.newevent2.GuestsAll
import com.example.newevent2.Model.*
import com.example.newevent2.VendorsAll
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class VendorsAllPresenter(
    val context: Context,
    val fragment: VendorsAll,
    val view: View
) :
    VendorPresenter.VendorList {

    private var presentervendor: VendorPresenter = VendorPresenter(context!!, this)

    init {
        presentervendor.getVendorList()
    }

    override fun onVendorList(list: ArrayList<Vendor>) {
       fragment.onVAVendors(view, list)
    }

    override fun onVendorListError(errcode: String) {
        fragment.onVAVendorsError(view, VendorPresenter.ERRCODEVENDORS)
    }

    interface VAVendors {
        fun onVAVendors(
            inflatedView: View,
            list: ArrayList<Vendor>
        )

        fun onVAVendorsError(inflatedView: View, errcode: String)
    }
}