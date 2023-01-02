package com.jacob.game;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


public class UtilsAdmob {
    protected Boolean enable_banner = true;
    protected Boolean enable_inter  = true;
    protected Boolean banner_at_bottom = true;
    protected Boolean banner_not_overlap = false;
    protected AdView mAdView = null;
    protected Activity activity;
    protected InterstitialAd mInterstitialAd = null;

    public void setContext(Activity act){
        activity = act;
    }

    public void init(){
        enable_banner = activity.getResources().getBoolean(R.bool.enable_banner);
        banner_at_bottom = activity.getResources().getBoolean(R.bool.banner_at_bottom);
        banner_not_overlap = activity.getResources().getBoolean(R.bool.banner_not_overlap);
        enable_inter  = activity.getResources().getBoolean(R.bool.enable_inter);


        if(!isConnectionAvailable()){
            enable_banner = false;
            enable_inter  = false;
        }

        if(!enable_banner && !enable_inter){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("Jacob_mlk", "hide space of banner");
                    AdView banner = activity.findViewById(R.id.adView);
                    banner.setVisibility(View.GONE);
                }
            });
            return;
        }

        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        prepare_banner();
        prepare_inter();
    }

    protected void prepare_banner(){
        if(!enable_banner) return;

        mAdView = activity.findViewById(R.id.adView);
        if(!banner_at_bottom){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("Jacob_mlk", "move banner to top");
                    LinearLayout main = activity.findViewById(R.id.main);
                    AdView banner = activity.findViewById(R.id.adView);
                    main.removeViewAt(1);
                    main.addView(banner, 0);
                }
            });
        }

        if(!banner_not_overlap){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("Jacob_mlk", "set banner overlap");
                    AdView banner = activity.findViewById(R.id.adView);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) banner.getLayoutParams();
                    params.setMargins(0, -140,0,0);
                }
            });
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                Log.d("Jacob", "Error load banner : "+ adError.getMessage());
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }

    protected void prepare_inter(){
        if(!enable_inter) return;

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity,activity.getResources().getString(R.string.id_inter), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i("Jacob", "onAdLoaded");
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("Jacob", "The ad was dismissed.");
                        prepare_inter();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("Jacob", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.d("Jacob", "The ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i("Jacob", loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });
    }

    public void show_inter(){
        if(!enable_inter) return;

        if (mInterstitialAd == null) {
            Log.d("Jacob", "The interstitial wasn't loaded yet.");
            return;
        }

        Log.d("Jacob", "inter is loaded ...");
        mInterstitialAd.show(activity);
    }

    public void on_pause(){
        if (mAdView != null) {
            if(enable_banner){
                mAdView.pause();
            }
        }
    }

    public void on_resume(){
        if (mAdView != null) {
            if(enable_banner){
                mAdView.resume();
            }
        }
    }

    public void on_destroy(){
        if (mAdView != null) {
            if(enable_banner) {
                mAdView.destroy();
            }
        }
    }

    @SuppressWarnings( "deprecation" )
    public boolean isConnectionAvailable(){
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return ( cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting() );
    }
}
