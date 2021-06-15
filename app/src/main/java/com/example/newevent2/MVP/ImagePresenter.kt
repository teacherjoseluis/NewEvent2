package com.example.newevent2.MVP

import Application.Cache
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.example.newevent2.Functions.getImgfromStorage
import com.example.newevent2.MainActivity
import com.example.newevent2.EventSummary

class ImagePresenter : Cache.EventImageCacheData {

    private var activefragment = ""

    @SuppressLint("StaticFieldLeak")
    private lateinit var inflatedView: View

    @SuppressLint("StaticFieldLeak")
    private lateinit var mContext: Context

    private lateinit var cacheimage: Cache<Bitmap>
    private lateinit var fragmentMED: EventSummary
    private lateinit var fragmentMA: MainActivity

    var userid = ""
    var eventid = ""

    constructor(context: Context, fragment: EventSummary, view: View) {
        fragmentMED = fragment
        inflatedView = view
        mContext = context
        activefragment = "MED"
    }

    constructor(context: Context, fragment: MainActivity) {
        fragmentMA = fragment
        //inflatedView = view
        mContext = context
        activefragment = "MA"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEventImage() {
        cacheimage = Cache(mContext, this)
        cacheimage.loadimage("eventimage")
    }

    override fun onEventImage(image: Bitmap) {
        when (activefragment) {
            "MED" -> fragmentMED.onEventImage(
                mContext,
                inflatedView,
                image
            )
            "MA" -> fragmentMA.onEventImage(
                mContext,
                null,
                image
            )
        }
    }

    override fun onEmptyEventImage(errorcode: String) {
        val storageRef =
            getImgfromStorage("eventimage", userid, eventid)
        if (storageRef != null) {
            cacheimage.save("eventimage", storageRef)
            when (activefragment) {
                "MED" -> fragmentMED.onEventImage(
                    mContext,
                    inflatedView,
                    storageRef
                )
                "MA" -> fragmentMA.onEventImage(
                    mContext,
                    null,
                    storageRef
                )
            }
        }
    }

    interface EventImage {
        fun onEventImage(
            context: Context,
            inflatedView: View?,
            packet: Any
        )
    }

}