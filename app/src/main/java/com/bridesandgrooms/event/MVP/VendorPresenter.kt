package com.bridesandgrooms.event.MVP

import Application.Cache
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.bridesandgrooms.event.Functions.userdbhelper
import com.bridesandgrooms.event.Model.*

class VendorPresenter : Cache.VendorArrayListCacheData {

    private var activefragment = ""
    private var mContext: Context

    private lateinit var fragmentVA: VendorsAllPresenter
    private lateinit var fragmentVP: VendorPaymentPresenter

    private lateinit var cachevendor: Cache<Vendor>

    constructor(context: Context, fragment: VendorsAllPresenter) {
        fragmentVA = fragment
        mContext = context
        activefragment = "VA"
    }

    constructor(context: Context, fragment: VendorPaymentPresenter) {
        fragmentVP = fragment
        mContext = context
        activefragment = "VP"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getVendorList() {
        cachevendor = Cache(mContext, this)
        cachevendor.loadarraylist(Vendor::class)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onArrayListV(arrayList: ArrayList<Vendor>) {
        if (arrayList.size != 0) {
            when (activefragment) {
                "VA" -> fragmentVA.onVendorList(arrayList)
                "VP" -> fragmentVP.onVendorList(arrayList)
            }
        }
    }

    override fun onEmptyListV() {
        val user = userdbhelper.getUser(userdbhelper.getUserKey())
        val vendor = VendorModel()
        vendor.getAllVendorList(
            user.userid!!,
            user.eventid,
            object : VendorModel.FirebaseSuccessVendorList {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onVendorList(list: ArrayList<Vendor>) {
                    if (list.isNotEmpty()) {
                        // This may be heavy, getting all of the tasks from Firebase but storing them into the cache
                        cachevendor.save(list)

                        when (activefragment) {
                            "VA" -> fragmentVA.onVendorList(list)
                            "VP" -> fragmentVP.onVendorList(list)
                        }
                    }else {
                        // This is when there is no data coming from Firebase
                        when (activefragment) {
                            "VA" -> fragmentVA.onVendorListError(ERRCODEVENDORS)
                            "VP" -> fragmentVP.onVendorListError(ERRCODEVENDORS)}
                    }
                }
            }
        )
    }


    interface VendorList {
        fun onVendorList(list: ArrayList<Vendor>)
        fun onVendorListError(errcode: String)
    }

    companion object {
        const val ERRCODEVENDORS = "NOVENDORS"
        const val PERMISSION_CODE = 1001
    }
}
