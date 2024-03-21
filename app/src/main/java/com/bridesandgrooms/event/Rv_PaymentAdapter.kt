package com.bridesandgrooms.event

import Application.AnalyticsManager
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.PermissionUtils
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton
import com.bridesandgrooms.event.Functions.deletePayment
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.Model.Payment
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.Model.UserModel
import com.bridesandgrooms.event.UI.ItemTouchAdapterAction
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface ItemSwipeListenerPayment {
    fun onItemSwiped(paymentList: MutableList<Payment>)
}

class Rv_PaymentAdapter(
    private val coroutineScope: CoroutineScope,
    private val paymentList: MutableList<Payment>,
    private val swipeListener: ItemSwipeListenerPayment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchAdapterAction {

    lateinit var context: Context
    lateinit var usermodel: UserModel

    private val DEFAULT_VIEW_TYPE = 1
    private val NATIVE_AD_VIEW_TYPE = 2

    private val ADS_INTERVAL = 4
    private val showads = RemoteConfigSingleton.get_showads()
    //--------------------------------------------------
    // For Native Ad Implementation

    override fun getItemViewType(position: Int): Int {
        return if ((position > 0 && (position + 1) % ADS_INTERVAL == 0) && showads) {
            // It's time to show an ad
            NATIVE_AD_VIEW_TYPE
        } else {
            // Show a payment item
            DEFAULT_VIEW_TYPE
        }
    }
    //--------------------------------------------------

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        lateinit var genericViewHolder: RecyclerView.ViewHolder
        when (p1) {
            DEFAULT_VIEW_TYPE -> {
                val v = LayoutInflater.from(p0.context)
                    .inflate(R.layout.payment_item_layout, p0, false)

                context = p0.context
                genericViewHolder = PaymentViewHolder(v)
            }
            NATIVE_AD_VIEW_TYPE -> {
                val v = LayoutInflater.from(p0.context)
                    .inflate(R.layout.native_ad_layout, p0, false)
                context = p0.context
                genericViewHolder = NativeAdViewHolder(v)
            }
        }
        return genericViewHolder
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val adjustedPosition = p1 - (p1 / ADS_INTERVAL)
        when (p0) {
            is PaymentViewHolder -> {
                if (adjustedPosition >= 0 && adjustedPosition < paymentList.size) {
                    p0.paymentname?.text = paymentList[p1].name
                    p0.paymentdate?.text = paymentList[p1].date
                    p0.paymentamount?.text = paymentList[p1].amount
                    val resourceId = context.resources.getIdentifier(
                        Category.getCategory(paymentList[p1].category).drawable, "drawable",
                        context.packageName
                    )
                    p0.categoryavatar.setImageResource(resourceId)

                    p0.itemView.setOnClickListener {
                        val paymentdetail = Intent(context, PaymentCreateEdit::class.java)
                        paymentdetail.putExtra("payment", paymentList[p1])
                        context.startActivity(paymentdetail)
                    }
                }
            }
            is NativeAdViewHolder -> {
                val adLoader = AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
                    .forNativeAd {
//                        NativeAd.OnNativeAdLoadedListener {
                        Log.d("AD1015", it.responseInfo.toString())
                        Log.d("AD1015", it.mediaContent.toString())
                        populateNativeAdView(it, p0.nativeAdView)
//                            p0.nativeAdView!!.visibility=View.VISIBLE
//                            p0.nativeAdView!!.setNativeAd(it)
//                        }
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            Log.d("AD1015", p0.message)
                        }
                    })
                    .build()
                adLoader.loadAd(AdRequest.Builder().build())
            }
        }
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
        //adView.mediaView = adView.findViewById(R.id.ad_media)

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        //(adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView?.setMediaContent(nativeAd.mediaContent)
        adView.mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
    }

    class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val paymentname: TextView? = itemView.findViewById(R.id.paymentname)
        val paymentdate: TextView? = itemView.findViewById(R.id.paymentdate)
        val paymentamount: TextView? = itemView.findViewById(R.id.paymentamounts)
        val categoryavatar = itemView.findViewById<ImageView>(R.id.categoryavatar)!!
    }

    class NativeAdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nativeAdView: NativeAdView = itemView.findViewById(R.id.nativeAd) as NativeAdView
    }

    override fun onItemSwiftLeft(
        context: Context,
        position: Int,
        recyclerView: RecyclerView,
        action: String
    ) {
        TODO("Not yet implemented")
    }

    override fun onItemSwiftRight(
        context: Context,
        position: Int,
        recyclerView: RecyclerView,
        action: String
    ) {
        AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME,"Delete Payment")
        val user = User().getUser(context)
            try {
                val paymentswift = paymentList[position]
                if (action == DELETEACTION) {
                    paymentList.removeAt(position)
                    notifyItemRemoved(position)
                    //coroutineScope.launch {
                        deletePayment(context, user, paymentswift)
                    //}

                    val snackbar =
                        Snackbar.make(recyclerView, "Payment deleted", Snackbar.LENGTH_LONG)
                    snackbar.show()

                    swipeListener.onItemSwiped(paymentList)
                }
            } catch (e: Exception) {
                println(e.message)
            }
       // }
    }

    companion object {
        const val TAG = "Rv_PaymentAdapter"
        const val DELETEACTION = "delete"
        const val SCREEN_NAME = "Rv PaymentAdapter"
    }
}