package com.bridesandgrooms.event.MVP

import Application.Cache
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.RequiresApi
import com.bridesandgrooms.event.DashboardEvent
import com.bridesandgrooms.event.Functions.getImgfromStorage
import com.bridesandgrooms.event.MainActivity
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.Model.UserDBHelper

class ImagePresenter : Cache.EventImageCacheData, Cache.PlaceImageCacheData {

    private var activefragment = ""

    @SuppressLint("StaticFieldLeak")
    private lateinit var inflatedView: View

    @SuppressLint("StaticFieldLeak")
    private var mContext: Context

    private lateinit var cacheimage: Cache<Bitmap>
    private lateinit var fragmentMA: MainActivity
    private lateinit var fragmentDE: DashboardEvent

    private val mHandler = Handler(Looper.getMainLooper())

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

    fun getEventImage() {
        Thread {
            cacheimage = Cache(mContext, this)
            cacheimage.loadimage(EVENTIMAGE)
        }.start()
    }

    fun getPlaceImage() {
        Thread {
            cacheimage = Cache(mContext, this)
            cacheimage.loadimage(PLACEIMAGE)
        }.start()
    }

    override fun onEventImage(image: Bitmap) {
        mHandler.post {
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
    }

    override fun onEmptyEventImage(errorcode: String) {
        mHandler.post {
//        val userdbhelper = UserDBHelper(mContext)
            val user = User().getUser(mContext)
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
    }

    override fun onPlaceImage(image: Bitmap) {
        mHandler.post {
            when (activefragment) {
                "DE" -> fragmentDE.onPlaceImage(
                    mContext,
                    image
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onEmptyPlaceImage(errorcode: String) {
        mHandler.post {
            when (activefragment) {
                "DE" -> fragmentDE.onEmptyPlaceImageSD()
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

    interface PlaceImage {
        fun onPlaceImage(
            context: Context,
            image: Bitmap
        )

        fun onEmptyPlaceImageSD()
    }

    companion object {
        const val EVENTIMAGE = "eventimage"
        const val PLACEIMAGE = "placeimage"
    }
}