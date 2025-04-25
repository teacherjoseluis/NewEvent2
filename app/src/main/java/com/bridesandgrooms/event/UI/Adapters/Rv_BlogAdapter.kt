package com.bridesandgrooms.event.UI.Adapters

import Application.AnalyticsManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bridesandgrooms.event.Functions.Firebase.BlogPost
import androidx.constraintlayout.widget.ConstraintLayout
import com.bridesandgrooms.event.UI.Fragments.BlogFragmentActionListener
import com.bridesandgrooms.event.Functions.RemoteConfigSingleton
import com.bridesandgrooms.event.R
import com.bridesandgrooms.event.UI.Adapters.PaymentAdapter.Companion.SCREEN_NAME
import com.google.firebase.storage.FirebaseStorage
import com.firebase.ui.storage.images.FirebaseImageLoader

import com.google.firebase.storage.StorageReference

import com.bumptech.glide.module.AppGlideModule
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.AdChoicesView
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import java.io.InputStream


@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(
            StorageReference::class.java, InputStream::class.java,
            FirebaseImageLoader.Factory()
        )
    }
}

class Rv_BlogAdapter(
    private val fragmentActionListener: BlogFragmentActionListener,
    private val blogList: ArrayList<BlogPost>,
    val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val DEFAULT_VIEW_TYPE = 1
    private val NATIVE_AD_VIEW_TYPE = 2

    private val ADS_INTERVAL = 3
    private val showads = RemoteConfigSingleton.get_showads()

    override fun getItemViewType(position: Int): Int {
        // Adjust position to exclude ad slots
        val adjustedPosition = position - (position / ADS_INTERVAL)

        return if (showads && position > 0 && (position + 1) % ADS_INTERVAL == 0) {
            // Show ad at regular intervals
            NATIVE_AD_VIEW_TYPE
        } else {
            // Default item view
            DEFAULT_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        lateinit var genericViewHolder: RecyclerView.ViewHolder

        // Instantiates a layout XML file into its corresponding View objects
        when (p1) {
            DEFAULT_VIEW_TYPE -> {
                val v =
                    LayoutInflater.from(p0.context).inflate(R.layout.blog_item_layout, p0, false)
                genericViewHolder = Rv_BlogAdapterViewHolder(v)
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
        return if (showads) {
            blogList.size + (blogList.size / (ADS_INTERVAL - 1))
        } else {
            blogList.size
        }
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        //val adjustedPosition = p1 - (p1 / ADS_INTERVAL)
        when (p0) {
            is Rv_BlogAdapterViewHolder -> {
                val adjustedPosition = p1 - (p1 / ADS_INTERVAL)
                val item = blogList[adjustedPosition]
                p0.bind(item)
            }
            is NativeAdViewHolder -> {
                val adLoader = AdLoader.Builder(context, context.getString(R.string.admob_native_ad_unit_id))
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
        // Set Ad Attribution ("Ad")
        val attributionView = adView.findViewById<TextView>(R.id.ad_attribution)
        attributionView.visibility = View.VISIBLE

        // Set AdChoices Overlay
        val adChoicesContainer = adView.findViewById<FrameLayout>(R.id.ad_choices_container)
        val adChoicesView = AdChoicesView(adView.context)
        adChoicesContainer.removeAllViews()
        adChoicesContainer.addView(adChoicesView)

        // Set Media Content
        val mediaView = adView.findViewById<MediaView>(R.id.media_view)
        adView.mediaView = mediaView
        mediaView.setMediaContent(nativeAd.mediaContent)

        // Set Headline
        val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
        adView.headlineView = headlineView
        headlineView.text = nativeAd.headline

        // Set Call to Action
        val callToActionView = adView.findViewById<Button>(R.id.ad_call_to_action)
        adView.callToActionView = callToActionView
        callToActionView.text = nativeAd.callToAction
        callToActionView.visibility = if (nativeAd.callToAction != null) View.VISIBLE else View.INVISIBLE

        // Register the native ad with the NativeAdView
        adView.setNativeAd(nativeAd)
    }

    inner class Rv_BlogAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val blogCard: ConstraintLayout = itemView.findViewById(R.id.cardLayout)
        private val transitionDuration = 300L

        init {
            blogCard.setOnClickListener {
                AnalyticsManager.getInstance().trackUserInteraction(SCREEN_NAME, "blogCard", "click")
                handleClick()
            }
        }

        fun bind(blogPost: BlogPost) {
            blogCard.removeAllViews()

            val layoutId = R.layout.blog_item_layout
            val view =
                LayoutInflater.from(itemView.context).inflate(layoutId, blogCard, false)

            view.findViewById<TextView>(R.id.blogtitle).text = blogPost.title
            view.findViewById<TextView>(R.id.author).text = blogPost.author
            view.findViewById<TextView>(R.id.time).text = blogPost.readingTime

            val storage = FirebaseStorage.getInstance()
            val storageRef =
                storage.getReferenceFromUrl(blogPost.image)

            GlideApp.with(itemView.context)
                .load(storageRef)
                .into(view.findViewById(R.id.blogimage))

            Log.i("Image Loaded", "Image Loaded")

            blogCard.addView(view)
            animateView(view)
        }

        private fun animateView(view: View) {
            view.alpha = 0f // Set initial alpha to 0
            view.animate()
                .alpha(1f) // Animate alpha to 1
                .setDuration(transitionDuration)
                .start()
        }

        private fun getAdjustedPosition(position: Int): Int {
            return position - (position / ADS_INTERVAL)
        }

        private fun handleClick() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION && getItemViewType(position) == DEFAULT_VIEW_TYPE) {
                val adjustedPosition = getAdjustedPosition(position)
                if (adjustedPosition in blogList.indices) {
                    val blog = blogList[adjustedPosition]
                    fragmentActionListener.onBlogFragmentWithData(blog)
                } else {
                    Log.e("RecyclerView", "Adjusted position out of bounds: $adjustedPosition")
                }
            } else {
                Log.d("RecyclerView", "Clicked on an ad or invalid position")
            }
        }
    }

    inner class NativeAdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nativeAdView: NativeAdView = itemView.findViewById(R.id.nativeAd)
    }
}

