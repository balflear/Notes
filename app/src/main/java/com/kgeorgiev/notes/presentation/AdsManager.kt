package com.kgeorgiev.notes.presentation

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.apptracker.android.listener.AppModuleListener
import com.apptracker.android.nativead.ATNativeAd
import com.apptracker.android.nativead.ATNativeAdCollection
import com.apptracker.android.nativead.ATNativeListener
import com.apptracker.android.track.AppTracker
import com.kgeorgiev.notes.BuildConfig
import javax.inject.Singleton

/**
 * Created by kostadin.georgiev on 10/29/2019.
 * Ads by Leadbolt.com
 */
@Singleton
class AdsManager constructor(private val appContext: Context) {
    private val TAG = AdsManager::class.java.name
    private val IRRESTIAL_AD_TYPE = "inapp"
    private val REWARD_AD_TYPE = "reward"

    private var isIrrestialAdLoaded = false
    private var isRewardAdLoaded = false


    fun showIrrestialAd() {
        if (isIrrestialAdLoaded) {
            Log.e(TAG, "Show AD")
            AppTracker.loadModule(appContext, IRRESTIAL_AD_TYPE)
        } else {
            AppTracker.loadModuleToCache(appContext, IRRESTIAL_AD_TYPE) // Fetch ad again
        }
    }


    // Listeners goes bellow ----
    private val appModuleListener = object : AppModuleListener {
        override fun onModuleClosed(p0: String?, p1: Boolean) {
            Log.e(TAG, "Module closed:$p0")
            AppTracker.loadModuleToCache(appContext, p0) // Fetch ad again
        }

        override fun onModuleLoaded(p0: String?) {
            Log.e(TAG, "Module loaded:$p0")
        }

        override fun onModuleCached(p0: String?) {
            Log.e(TAG, "Module cached:$p0")
            when (p0) {
                IRRESTIAL_AD_TYPE -> {
                    isIrrestialAdLoaded = true
                }

                REWARD_AD_TYPE -> {
                    isRewardAdLoaded = true
                }
            }
        }

        override fun onModuleClicked(p0: String?) {
            Log.e(TAG, "Module clicked:$p0")
            AppTracker.loadModuleToCache(appContext, p0) // Fetch ad again
        }

        override fun onModuleFailed(p0: String?, p1: String?, p2: Boolean) {
            Log.e(TAG, "Module failed:$p0")
        }

    }

    private val nativeAdsListener = object : ATNativeListener {
        override fun onAdClicked(p0: ATNativeAd?) {
            Log.e(TAG, "NativeAd clicked")
        }

        override fun onAdDisplayed(p0: ATNativeAd?) {
            Log.e(TAG, "NativeAd displayed")
        }

        override fun onAdsLoaded(p0: ATNativeAdCollection?) {
            Log.e(TAG, "NativeAds loaded")
        }

        override fun onAdsFailed(p0: String?) {
            Log.e(TAG, "NativeAd error loading:$p0")
        }
    }

    init {
        val adsId = BuildConfig.ADS_SDK_ID
        if (!TextUtils.isEmpty(adsId)) {
            Log.e(TAG, "INIT ADS MANAGER")

            AppTracker.setModuleListener(appModuleListener)
            AppTracker.setNativeListener(nativeAdsListener)
            AppTracker.startSession(appContext, adsId)

            AppTracker.loadModuleToCache(appContext, IRRESTIAL_AD_TYPE)
            AppTracker.loadModuleToCache(appContext, REWARD_AD_TYPE)

            // Native-ads
            AppTracker.loadNativeAdsWithCaching()
        }
    }
}