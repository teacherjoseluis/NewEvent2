package com.example.newevent2

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newevent2.Functions.sumStrings
import com.example.newevent2.Model.VendorPayment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.redmadrobot.acronymavatar.AvatarView


class Rv_VendorAdapter(private val contactlist: ArrayList<VendorPayment>, val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), ItemTouchAdapterAction {

    private val DEFAULT_VIEW_TYPE = 1
    private val NATIVE_AD_VIEW_TYPE = 2

    //--------------------------------------------------
    // For Native Ad Implementation
    override fun getItemViewType(position: Int): Int {
        if (position != 0 && position % 4 == 0) {
            return NATIVE_AD_VIEW_TYPE
        }
        return DEFAULT_VIEW_TYPE
    }
    //--------------------------------------------------

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        lateinit var genericViewHolder: RecyclerView.ViewHolder
        when (p1) {
            DEFAULT_VIEW_TYPE -> {
                val v = LayoutInflater.from(p0.context)
                    .inflate(R.layout.vendor_item_layout, p0, false)
                //context = p0.context
                genericViewHolder = VendorViewHolder(v)
            }
            NATIVE_AD_VIEW_TYPE -> {
                val v = LayoutInflater.from(p0.context)
                    .inflate(R.layout.native_ad_layout, p0, false)
                //context = p0.context
                genericViewHolder = NativeAdViewHolder(v)
            }
        }
        return genericViewHolder
    }

    override fun getItemCount(): Int {
        return contactlist.size
    }

    // public abstract void onBindViewHolder (VH holder, int position)

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        when (p0) {
            is VendorViewHolder -> {
                p0.contactname.text = contactlist[p1].vendor.name
                p0.paymentscount.text = contactlist[p1].amountlist.size.toString()
                p0.paymentsamount.text = sumStrings(contactlist[p1].amountlist)

                try {
//            p0.contactavatar.setImageDrawable(
//                LetterAvatar(
//                    context,
//                    context.getColor(R.color.azulmasClaro),
//                    p0.contactname.text.toString().substring(0, 2),
//                    10
//                )
//            )
                    p0.contactavatar.setText(p0.contactname.text.toString())

                } catch (e: Exception) {
                    p0.contactavatar.setImageResource(R.drawable.avatar2)
                }

                p0.itemView.setOnClickListener {
                    val vendordetail = Intent(context, VendorCreateEdit::class.java)
                    vendordetail.putExtra("vendor", contactlist[p1].vendor)
                    context.startActivity(vendordetail)
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
        adView.mediaView = adView.findViewById(R.id.ad_media)

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        //(adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)
        adView.mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP)

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
    }

    class VendorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactname = itemView.findViewById<TextView>(R.id.contactname)!!
        val contactavatar = itemView.findViewById<AvatarView>(R.id.contactavatar)!!
        val paymentscount: TextView = itemView.findViewById(R.id.paymentscount)
        val paymentsamount: TextView = itemView.findViewById(R.id.paymentsamount)
    }

    class NativeAdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nativeAdView: NativeAdView = itemView.findViewById(R.id.nativeAd) as NativeAdView
    }

    override fun onItemSwiftLeft(position: Int, recyclerView: RecyclerView, action: String) {
        TODO("Not yet implemented")
    }

    override fun onItemSwiftRight(position: Int, recyclerView: RecyclerView, action: String) {
//        val vendorswift = contactlist[position].vendor
//        val vendorbackup = Vendor().apply {
//            key = contactlist[position].vendor.key
//            name = contactlist[position].vendor.name
//            phone = contactlist[position].vendor.phone
//            email = contactlist[position].vendor.email
//            category = contactlist[position].vendor.category
//            eventid = contactlist[position].vendor.eventid
//            placeid = contactlist[position].vendor.placeid
//            location = contactlist[position].vendor.location
//        }
//
//        if (action == DELETEACTION) {
//            contactlist.removeAt(position)
//            notifyItemRemoved(position)
//            deleteVendor(context, vendorswift)
//            Snackbar.make(recyclerView, "Vendor deleted", Snackbar.LENGTH_LONG)
//                .setAction("UNDO") {
//                    contactlist.add(vendorbackup)
//                    notifyItemInserted(contactlist.lastIndex)
//                    addVendor(context, vendorbackup, CALLER)
//                }.show()
//        }
    }

    companion object {
        const val TAG = "Rv_VendorAdapter"
    }
}
