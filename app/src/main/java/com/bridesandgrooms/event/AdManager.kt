package com.bridesandgrooms.event

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdManager(private val adDelayTimeMillis: Long) {
    private var lastAdShownTime: Long = 0
    var mRewardedAd: RewardedAd? = null

    // Function to load and show the Rewarded Ad with a delay
    fun loadAndShowRewardedAd(context: Context) {
        val currentTimeMillis = System.currentTimeMillis()

        // Check if the delay time has elapsed since the last ad was shown
        if (currentTimeMillis - lastAdShownTime >= adDelayTimeMillis) {
            // Delay time has elapsed, load and show the ad
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                "ca-app-pub-2710265419603943/7504459342",
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(TaskCreateEdit.TAG, adError.message)
                        mRewardedAd = null
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        Log.d(TaskCreateEdit.TAG, "Ad was loaded.")
                        mRewardedAd = rewardedAd
                        lastAdShownTime = currentTimeMillis
                    }
                })

            // Ad listener in case I want to add additional behavior
            mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TaskCreateEdit.TAG, "Ad was shown.")
                }

//                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
//                    // Called when ad fails to show.
//                    Log.d(TaskCreateEdit.TAG, "Ad failed to show.")
//                }

                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TaskCreateEdit.TAG, "Ad was dismissed.")
                    mRewardedAd = null
                }
            }
            // Update the time when the last ad was shown
        }
    }
}