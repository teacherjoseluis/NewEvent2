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
import com.example.newevent2.Functions.getImgfromPlaces
import com.example.newevent2.Functions.getImgfromSD

class ImagePresenter : Cache.EventImageCacheData, Cache.PlaceImageCacheData {

    private var activefragment = ""

    @SuppressLint("StaticFieldLeak")
    private lateinit var inflatedView: View

    @SuppressLint("StaticFieldLeak")
    private lateinit var mContext: Context

    private lateinit var cacheimage: Cache<Bitmap>
    private lateinit var fragmentES: EventSummary
    private lateinit var fragmentMA: MainActivity

    // Place Image related variables
    var placeId = ""
    var ApiKey = ""

    constructor(context: Context, fragment: EventSummary, view: View) {
        fragmentES = fragment
        inflatedView = view
        mContext = context
        activefragment = "ES"
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
        cacheimage.loadimage(EVENTIMAGE)
    }

    fun getPlaceImage() {
        cacheimage = Cache(mContext, this)
        cacheimage.loadimage(PLACEIMAGE)
    }

    override fun onEventImage(image: Bitmap) {
        when (activefragment) {
            "ES" -> fragmentES.onEventImage(
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
        val user = com.example.newevent2.Functions.getUserSession(mContext!!)
        val storageRef =
            getImgfromStorage(EVENTIMAGE, user.key, user.eventid)
        if (storageRef != null) {
            cacheimage.save(EVENTIMAGE, storageRef)
            when (activefragment) {
                "ES" -> fragmentES.onEventImage(
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

    override fun onPlaceImage(image: Bitmap) {
        when (activefragment) {
            "ES" -> fragmentES.onPlaceImage(
                mContext,
                inflatedView,
                image
            )
        }
    }

    override fun onEmptyPlaceImage(errorcode: String) {
            when (activefragment) {
                "ES" -> fragmentES.onEmptyPlaceImageSD(inflatedView)
            }
    }

    interface EventImage {
        fun onEventImage(
            context: Context,
            inflatedView: View?,
            packet: Any
        )
    }

    interface PlaceImage {
        fun onPlaceImage(
            context: Context,
            inflatedView: View?,
            image: Bitmap
        )

        fun onEmptyPlaceImageSD(inflatedView: View?)
    }

    companion object {
        const val EVENTIMAGE = "eventimage"
        const val PLACEIMAGE = "placeimage"
    }
}