package com.bridesandgrooms.event.UI.Adapters

import Application.AnalyticsManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton
import com.bridesandgrooms.event.Functions.deletePayment
import com.bridesandgrooms.event.Model.Category
import com.bridesandgrooms.event.Model.Payment
import com.bridesandgrooms.event.Model.User
import com.bridesandgrooms.event.Model.UserModel
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.TPP_PaymentFragmentActionListener
import com.bridesandgrooms.event.UI.ItemTouchAdapterAction
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.snackbar.Snackbar

interface ItemSwipeListenerPayment {
    fun onItemSwiped(paymentList: MutableList<Payment>)
}

class PaymentAdapter(
    private val fragmentActionListener: TPP_PaymentFragmentActionListener,
    private val paymentList: MutableList<Payment>,
    private val swipeListener: ItemSwipeListenerPayment,
    val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchAdapterAction {

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
                    .inflate(R.layout.payment_cardview_expanded, p0, false)

                genericViewHolder = PaymentViewHolder(v)
            }

            NATIVE_AD_VIEW_TYPE -> {
                val v = LayoutInflater.from(p0.context)
                    .inflate(R.layout.native_ad_layout, p0, false)

                genericViewHolder = NativeAdViewHolder(v)
            }
        }
        return genericViewHolder
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val adjustedPosition = p1 - (p1 / ADS_INTERVAL)
        when (p0) {
            is PaymentViewHolder -> {
                if (adjustedPosition >= 0 && adjustedPosition < paymentList.size) {
                    val payment = paymentList[p1]
                    p0.bind(payment)
                }
            }

            is NativeAdViewHolder -> {
                val adLoader = AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
                    .forNativeAd {
                        Log.d("AD1015", it.responseInfo.toString())
                        Log.d("AD1015", it.mediaContent.toString())
                        populateNativeAdView(it, p0.nativeAdView)
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
        adView.mediaView?.setMediaContent(nativeAd.mediaContent)
        adView.mediaView?.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
        adView.setNativeAd(nativeAd)
    }

    inner class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val paymentCardView: ConstraintLayout = itemView.findViewById(R.id.paymentcardview)
        private val paymentname: TextView? = itemView.findViewById(R.id.paymentName)
        private val paymentcategory: TextView? = itemView.findViewById(R.id.paymentCategory)
        private val paymentdate: TextView? = itemView.findViewById(R.id.paymentDate)
        private val paymentamount: TextView? = itemView.findViewById(R.id.paymentAmount)
        private val categoryavatar = itemView.findViewById<ImageView>(R.id.paymentImageView)!!

        init {
            paymentCardView.setOnClickListener {
                handleClick()
            }
        }

        fun bind(payment: Payment) {
            val resourceId = context.resources.getIdentifier(
                Category.getCategory(payment.category).drawable, "drawable",
                context.packageName
            )

            paymentname?.text = payment.name
            paymentdate?.text = payment.date
            paymentcategory?.text = payment.category
            paymentamount?.text = payment.amount
            categoryavatar.setImageResource(resourceId)
        }

        private fun handleClick() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val payment = paymentList[position]
                fragmentActionListener.onPaymentFragmentWithData(payment)
            }
        }
    }

    inner class NativeAdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "Delete Payment")
        //val user = User().getUser()
        try {
            val paymentswift = paymentList[position]
            if (action == DELETEACTION) {
                paymentList.removeAt(position)
                notifyItemRemoved(position)
                //coroutineScope.launch {
                deletePayment(paymentswift.key)
                //}

                val snackbar =
                    Snackbar.make(recyclerView, "Payment deleted", Snackbar.LENGTH_LONG)
                snackbar.show()

                swipeListener.onItemSwiped(paymentList)
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    companion object {
        const val TAG = "PaymentAdapter"
        const val DELETEACTION = "delete"
        const val SCREEN_NAME = "PaymentAdapter"
    }
}