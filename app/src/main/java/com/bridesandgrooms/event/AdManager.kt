package com.bridesandgrooms.event

import android.app.Activity
import android.content.Context
import android.util.Log
import com.bridesandgrooms.event.UI.Fragments.SearchVendorFragment
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdManager(private val adDelayTimeMillis: Long) {
    private var lastAdShownTime: Long = 0
    var mRewardedAd: RewardedAd? = null
    var interstitialAd: InterstitialAd? = null
    private val TAG = "AdManager"

    // Function to load and show the Rewarded Ad with a delay
    fun loadRewardedAd(context: Context) {
        val currentTimeMillis = System.currentTimeMillis()

        // Check if the delay time has elapsed since the last ad was shown
        if (currentTimeMillis - lastAdShownTime >= adDelayTimeMillis) {
            // Delay time has elapsed, load and show the ad
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                context.getString(R.string.admob_reward_ad_unit_id),
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(TAG, adError.message)
                        mRewardedAd = null
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        Log.d(TAG, "Ad was loaded.")
                        mRewardedAd = rewardedAd
                        lastAdShownTime = currentTimeMillis

                        mRewardedAd?.fullScreenContentCallback =
                            object : FullScreenContentCallback() {
                                override fun onAdShowedFullScreenContent() {
                                    mRewardedAd = null
                                }

                                override fun onAdDismissedFullScreenContent() {
                                    Log.d(TAG, "Ad was dismissed.")
                                    mRewardedAd = null
                                }
                            }
                    }
                })
        }
    }
    fun showRewardedAd(context: Context) {
        if (mRewardedAd != null) {
            mRewardedAd?.show(context as Activity) { rewardItem ->
                // Handle the reward here
                val rewardType = rewardItem.type
                val rewardAmount = rewardItem.amount
                Log.d(SearchVendorFragment.TAG, "User earned reward: $rewardType ($rewardAmount)")
            }
        } else {
            Log.d(SearchVendorFragment.TAG, "The rewarded ad wasn't ready yet.")
            loadRewardedAd(context)
        }
    }

    fun loadInterstitialAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            context.getString(R.string.admob_interstitial_ad_unit_id), // Replace with your AdMob ad unit ID
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Log.d(TAG, "Ad was loaded.")

                    interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Ad dismissed.")
                            interstitialAd = null
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Log.d(TAG, "Ad failed to show: ${adError.message}")
                            interstitialAd = null
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.d(TAG, "Ad is being shown.")
                            interstitialAd = null
                        }
                    }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, "Ad failed to load: ${adError.message}")
                    interstitialAd = null
                }
            }
        )
    }
    fun showInterstitialAd(context: Context) {
        if (interstitialAd != null) {
            interstitialAd?.show(context as Activity)
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.")
            loadInterstitialAd(context)
        }
    }
}