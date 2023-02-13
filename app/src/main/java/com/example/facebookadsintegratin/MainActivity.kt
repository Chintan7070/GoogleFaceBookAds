package com.example.facebookadsintegratin


import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.facebook.ads.*
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var rewardedVideoAd: RewardedVideoAd
    private lateinit var interstitialAd: InterstitialAd
    private lateinit var adView: LinearLayout
    private lateinit var nativeAdLayout: NativeAdLayout
    private lateinit var adView1: AdView
    val TAG = "MainActivity"

    private var nativeAd: NativeAd? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mId: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        Log.e(TAG, "onCreate: deviceIdes--" + mId)
        AudienceNetworkAds.initialize(this);

        /*   val android_id =
               Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
           val deviceId: String = md5(android_id).toUpperCase()*/

        AdSettings.addTestDevice("13caa635-b11c-41b5-a7e9-bbbf61116de5");
//        Log.v(TAG, "is Admob Test Device ? $deviceId") //to confirm it worked

        banner();
        loadNativeAd()

        btnIntertial.setOnClickListener {
            loadInterstialAds();
        }

        btnReward.setOnClickListener {
            loadRewardAds();
        }


    }

    fun md5(s: String): String {
        try {
            // Create MD5 Hash
            val digest: MessageDigest = MessageDigest
                .getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest: ByteArray = digest.digest()

            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices) {
                var h = Integer.toHexString(0xFF and messageDigest[i].toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "md5: Logger +" + e.message)
        }
        return ""
    }

    private fun loadRewardAds() {

        rewardedVideoAd = RewardedVideoAd(this, "686218633203030_686240609867499")

        val rewardedVideoAdListener = object : RewardedVideoAdListener {
            override fun onError(p0: Ad?, p1: AdError?) {
                Log.e(TAG, "Interstitial ad failed to load: " + p1!!.errorMessage)

            }

            override fun onAdLoaded(p0: Ad?) {
                Log.d(TAG, "Interstitial ad  onAdLoaded!")
                rewardedVideoAd.show();

            }

            override fun onAdClicked(p0: Ad?) {
                Log.d(TAG, "Interstitial ad  onAdClicked!")

            }

            override fun onLoggingImpression(p0: Ad?) {
                Log.d(TAG, "Interstitial ad  onLoggingImpression!")

            }

            override fun onRewardedVideoCompleted() {
                Log.d(TAG, "Interstitial ad  onRewardedVideoCompleted!")

            }

            override fun onRewardedVideoClosed() {

                Log.d(TAG, "Interstitial ad  onRewardedVideoClosed!")

            }

        }
        rewardedVideoAd.loadAd(
            rewardedVideoAd.buildLoadAdConfig()
                .withAdListener(rewardedVideoAdListener)
                .build()
        );

//        rewardedVideoAd.show()
    }

    private fun loadInterstialAds() {
        interstitialAd = InterstitialAd(this, "686218633203030_686233593201534")
        val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.")
            }

            override fun onInterstitialDismissed(ad: Ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.")
            }

            override fun onError(ad: Ad, adError: AdError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.errorMessage)
            }

            override fun onAdLoaded(ad: Ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!")
                // Show the ad
                interstitialAd.show()
            }

            override fun onAdClicked(ad: Ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!")
            }

            override fun onLoggingImpression(ad: Ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!")
            }
        }
        interstitialAd.loadAd(
            interstitialAd.buildLoadAdConfig()
                .withAdListener(interstitialAdListener)
                .build()
        )
    }

    private fun banner() {

        adView1 = AdView(this, "686218633203030_686218939869666", AdSize.BANNER_HEIGHT_50)
        val adContainer = findViewById<View>(R.id.banner_container) as LinearLayout
        adContainer.addView(adView1)
        adView1.loadAd()
    }

    private fun loadNativeAd() {
        nativeAd = NativeAd(this, "686218633203030_686228529868707")
        val nativeAdListener: NativeAdListener = object : NativeAdListener {
            override fun onMediaDownloaded(ad: Ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.")
            }

            override fun onError(ad: Ad, adError: AdError) {
                // Native ad failed to load
                Log.e(TAG, "Native ad failed to load: " + adError.errorMessage)
            }

            override fun onAdLoaded(ad: Ad) {
                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "Native ad is loaded and ready to be displayed!")
                if (ad == null || nativeAd != ad) {
                    return;
                }
                inflateAd(ad)
            }

            override fun onAdClicked(ad: Ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!")
            }

            override fun onLoggingImpression(ad: Ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!")
            }
        }

        // Request an ad
        nativeAd!!.loadAd(
            nativeAd!!.buildLoadAdConfig()
                .withAdListener(nativeAdListener)
                .build()
        )

    }

    private fun inflateAd(ad: Ad) {

        nativeAd!!.unregisterView()

        // Add the Ad view into the ad container.

        // Add the Ad view into the ad container.
        nativeAdLayout = findViewById<NativeAdLayout>(R.id.native_ad_container)
        val inflater = LayoutInflater.from(this@MainActivity)
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        adView =
            inflater.inflate(R.layout.native_ad_layout_1, nativeAdLayout, false) as LinearLayout
        nativeAdLayout.addView(adView)

        // Add the AdOptionsView

        // Add the AdOptionsView
        val adChoicesContainer = findViewById<LinearLayout>(R.id.ad_choices_container)
        val adOptionsView = AdOptionsView(this@MainActivity, nativeAd, nativeAdLayout)
        adChoicesContainer.removeAllViews()
        adChoicesContainer.addView(adOptionsView, 0)

        // Create native UI using the ad metadata.

        // Create native UI using the ad metadata.
        val nativeAdIcon = adView.findViewById<MediaView>(R.id.native_ad_icon)
        val nativeAdTitle = adView.findViewById<TextView>(R.id.native_ad_title)
        val nativeAdMedia = adView.findViewById<MediaView>(R.id.native_ad_media)
        val nativeAdSocialContext = adView.findViewById<TextView>(R.id.native_ad_social_context)
        val nativeAdBody = adView.findViewById<TextView>(R.id.native_ad_body)
        val sponsoredLabel = adView.findViewById<TextView>(R.id.native_ad_sponsored_label)
        val nativeAdCallToAction: Button = adView.findViewById(R.id.native_ad_call_to_action)

        // Set the Text.

        // Set the Text.
        nativeAdTitle.text = nativeAd!!.advertiserName
        nativeAdBody.text = nativeAd!!.adBodyText
        nativeAdSocialContext.text = nativeAd!!.adSocialContext
        nativeAdCallToAction.setVisibility(if (nativeAd!!.hasCallToAction()) View.VISIBLE else View.INVISIBLE)
        nativeAdCallToAction.setText(nativeAd!!.adCallToAction)
        sponsoredLabel.text = nativeAd!!.sponsoredTranslation

        // Create a list of clickable views

        // Create a list of clickable views
        val clickableViews: MutableList<View> = ArrayList()
        clickableViews.add(nativeAdTitle)
        clickableViews.add(nativeAdCallToAction)

        // Register the Title and CTA button to listen for clicks.

        // Register the Title and CTA button to listen for clicks.
        nativeAd!!.registerViewForInteraction(
            adView, nativeAdMedia, nativeAdIcon, clickableViews
        )

    }


}