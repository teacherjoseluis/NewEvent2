package com.bridesandgrooms.event.MVP

import Application.Cache
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.bridesandgrooms.event.DashboardEvent
import com.bridesandgrooms.event.Functions.getImgfromStorage
import com.bridesandgrooms.event.Functions.userdbhelper
import com.bridesandgrooms.event.MainActivity

class ImagePresenter : Cache.EventImageCacheData, Cache.PlaceImageCacheData {

    private var activefragment = ""

    @SuppressLint("StaticFieldLeak")
    private lateinit var inflatedView: View

    @SuppressLint("StaticFieldLeak")
    private var mContext: Context

    private lateinit var cacheimage: Cache<Bitmap>
    private lateinit var fragmentMA: MainActivity
    private lateinit var fragmentDE: DashboardEvent

    // Place Image related variables
    var placeId = ""

    constructor(context: Context, fragment: MainActivity) {
        fragmentMA = fragment
        //inflatedView = view
        mContext = context
        activefragment = "MA"
    }

    constructor(context: Context, fragment: DashboardEvent, view: View) {
        fragmentDE = fragment
        inflatedView = view
        mContext = context
        activefragment = "DE"
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
            "MA" -> fragmentMA.onEventImage(
                mContext,
                null,
                image
            )
            "DE" -> fragmentDE.onEventImage(
                mContext,
                inflatedView,
                image
            )
        }
    }

    override fun onEmptyEventImage(errorcode: String) {
        val user = userdbhelper.getUser(userdbhelper.getUserKey())
        val storageRef =
            getImgfromStorage(EVENTIMAGE, user.userid!!, user.eventid)
        cacheimage.save(EVENTIMAGE, storageRef)
        when (activefragment) {
            "MA" -> fragmentMA.onEventImage(
                mContext,
                null,
                storageRef
            )
            "DE" -> fragmentDE.onEventImage(
                mContext,
                inflatedView,
                storageRef
            )
        }
    }

    override fun onPlaceImage(image: Bitmap) {
        when (activefragment) {
            "DE" -> fragmentDE.onPlaceImage(
                mContext,
                inflatedView,
                image
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onEmptyPlaceImage(errorcode: String) {
        when (activefragment) {
            "DE" -> fragmentDE.onEmptyPlaceImageSD(inflatedView)
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