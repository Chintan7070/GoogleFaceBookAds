package com.example.facebookadsintegratin

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewarded.ServerSideVerificationOptions
import kotlinx.android.synthetic.main.activity_google_ads.*
import kotlinx.android.synthetic.main.native_small_adview.view.*


class GoogleAdsActivity : AppCompatActivity() {
    private var rewardedAd: RewardedAd? = null
    private lateinit var mAdView: AdView
    private var mInterstitialAd: InterstitialAd? = null
    val TAG = "GoogleAdsActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_ads)
        MobileAds.initialize(this)

        mAdView = findViewById<AdView>(R.id.adView)

        btnInterstial.setOnClickListener {
            interstialAdsLoad();
        }

        banner();
        nativeAds();

        btnReward.setOnClickListener {
            rewardAdsLoad();
        }


    }

    private fun nativeAds() {

        /*MobileAds.initialize(this)
        val adBuilder = AdLoader.Builder(
            this@GoogleAdsActivity,
            "ca-app-pub-3940256099942544/2247696110"
        ) // the unitId is a sample
            .forNativeAd() { nativeAd ->
                Log.e(TAG, "inflaterinflater" + nativeAd)
                desplayNativeAd(myAd, nativeAd)
            }
        adBuilder.build()*/


        val adRequest = AdRequest.Builder().build()
        var myAd = findViewById<FrameLayout>(R.id.myAd)

        val builder = AdLoader.Builder(applicationContext, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { nativeAd ->
                Log.e(TAG, "nativeAds: NativeAds ---- $nativeAd")
                desplayNativeAd(myAd,nativeAd)
            }.withAdListener(object : AdListener() {

            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        builder.loadAd(adRequest)


    }

    private fun desplayNativeAd(myAd: ViewGroup, nativeAd: NativeAd) {

        val inflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                as LayoutInflater

        val adView: NativeAdView =
            inflater.inflate(R.layout.native_small_adview, null) as NativeAdView


        val headlineView = adView.findViewById<TextView>(R.id.nativeAdSmallTitle)
        headlineView.text = nativeAd.headline
        adView.headlineView = headlineView

        val nativeAdSmallDesc = adView.findViewById<TextView>(R.id.nativeAdSmallDesc);
        nativeAdSmallDesc.text = nativeAd.adChoicesInfo.toString()
        adView.bodyView = nativeAdSmallDesc

        val advertiser = adView.findViewById<TextView>(R.id.nativeAdSmallName)
        advertiser.text = nativeAd.advertiser
        adView.advertiserView = advertiser

        val imageAdView = adView.findViewById<ImageView>(R.id.nativeAdSmallImage)
        Glide.with(this)
            .load(nativeAd.icon)
            .into(imageAdView)

        val mediaView = adView.findViewById<MediaView>(R.id.nativeAdSmallMedia)
        adView.mediaView = mediaView

        adView.setNativeAd(nativeAd)

        myAd!!.removeAllViews()
        myAd.addView(adView)

    }

    private fun rewardAdsLoad() {

        val adRequest: AdRequest = AdRequest.Builder().build()
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
            adRequest, object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error.
                    Log.d(TAG, loadAdError.toString())
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    Log.d(TAG, "Ad was loaded.")
                    if (rewardedAd != null) {

                        val options = ServerSideVerificationOptions.Builder()
                            .setCustomData("SAMPLE_CUSTOM_DATA_STRING")
                            .build()
                        rewardedAd!!.setServerSideVerificationOptions(options)


                        if (rewardedAd != null) {
                            val activityContext: Activity = this@GoogleAdsActivity
                            rewardedAd!!.show(
                                activityContext
                            ) { rewardItem -> // Handle the reward.
                                Log.d(TAG, "The user earned the reward.")
                                val rewardAmount = rewardItem.amount
                                val rewardType = rewardItem.type
                                Toast.makeText(
                                    this@GoogleAdsActivity,
                                    "rewadsAmount:=" + rewardAmount + " :: rewardType :=" + rewardType,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Log.d(TAG, "The rewarded ad wasn't ready yet.")
                        }

                        rewardListner();


                    }
                }
            })
    }

    private fun rewardListner() {

        rewardedAd!!.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.")
                }

                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad dismissed fullscreen content.")
                    rewardedAd = null
                }

                override fun onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.")
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.")
                }
            }

    }

    private fun interstialAdsLoad() {

        val adRequest: AdRequest = AdRequest.Builder().build()

        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(@NonNull interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd
                    mInterstitialAd!!.show(this@GoogleAdsActivity)
                    if (mInterstitialAd != null) {

                        mInterstitialAd!!.fullScreenContentCallback =
                            object : FullScreenContentCallback() {
                                override fun onAdClicked() {
                                    // Called when a click is recorded for an ad.
                                    Log.d(TAG, "Ad was clicked.")
                                }

                                override fun onAdDismissedFullScreenContent() {
                                    // Set the ad reference to null so you don't show the ad a second time.
                                    Log.d(TAG, "Ad dismissed fullscreen content.--  DISSMISS")
                                    mInterstitialAd = null
                                }


                                override fun onAdImpression() {
                                    // Called when an impression is recorded for an ad.
                                    Log.d(TAG, "Ad recorded an impression.")
                                }

                                override fun onAdShowedFullScreenContent() {
                                    // Called when ad is shown.
                                    Log.d(TAG, "Ad showed fullscreen content.")
                                }
                            }

                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
                    Log.i(TAG, "onAdLoaded")
                }

                override fun onAdFailedToLoad(@NonNull loadAdError: LoadAdError) {
                    // Handle the error
                    Log.d(TAG, loadAdError.toString())
                    mInterstitialAd = null
                }
            })


    }

    private fun banner() {
        val adView = AdView(this)
        adView.setAdSize(AdSize.BANNER)
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111")
        val adRequest: AdRequest = AdRequest.Builder().build()

        mAdView.loadAd(adRequest)


        mAdView.setAdListener(object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
//                Toast.makeText(this@GoogleAdsActivity, "", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "onAdLoaded: -- Ad loaded successfully!")
            }


            override fun onAdOpened() {
                // covers the screen.
//                Toast.makeText(this@GoogleAdsActivity, "Ad opens an overlay that covers the screen", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "onAdLoaded: -- cAd opens an overlay that covers the screen")

            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
//                Toast.makeText(this@GoogleAdsActivity, "Ad viewed by user!", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "onAdLoaded: --Ad viewed by user!")

            }
        })

    }

   /* companion object {
        class AdHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
            var tvLabel: TextView
            var viewGroup: ViewGroup

            init {
                tvLabel = view.findViewById<View>(R.id.nativeAdSmallTitle) as TextView
                viewGroup = view.findViewById<View>(R.id.nativeAdSmallMedia) as ViewGroup

                //   nativeAdLayout = (NativeAdLayout) view.findViewById(R.id.native_ad_container);
            }
        }
    }*/
}