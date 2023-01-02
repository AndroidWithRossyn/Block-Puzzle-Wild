package com.jacob.game;

import android.app.Activity;
import android.util.Log;
import com.google.ads.consent.*;
import java.net.MalformedURLException;
import java.net.URL;

public class Gdpr {
    private String TAG = "Jacob";
    private ConsentForm form;

    public void make(Activity activity){
        if(!activity.getResources().getBoolean(R.bool.enable_gdpr)){
            return;
        }
        // Here is GDPR  :
        //================
        ConsentInformation consentInformation = ConsentInformation.getInstance(activity);

        String[] publisherIds = {activity.getResources().getString(R.string.id_publisher)};

        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated.
                Log.d(TAG,"onConsentInfoUpdated");
                switch (consentStatus){
                    case PERSONALIZED:
                        Log.d(TAG,"PERSONALIZED");
                        ConsentInformation.getInstance(activity)
                                .setConsentStatus(ConsentStatus.PERSONALIZED);
                        break;

                    case NON_PERSONALIZED:
                        Log.d(TAG,"NON_PERSONALIZED");
                        ConsentInformation.getInstance(activity)
                                .setConsentStatus(ConsentStatus.PERSONALIZED);
                        break;

                    case UNKNOWN:
                        Log.d(TAG,"UNKNOWN");
                        if(ConsentInformation.getInstance(activity).isRequestLocationInEeaOrUnknown()){


                            URL privacyUrl = null;
                            try {
                                privacyUrl = new URL(activity.getResources().getString(R.string.privacy_url));
                            } catch (MalformedURLException e) {
                                //e.printStackTrace();
                                // Handle error.
                            }
                            form = new ConsentForm.Builder(activity, privacyUrl)
                                    .withListener(new ConsentFormListener() {
                                        @Override
                                        public void onConsentFormLoaded() {
                                            // Consent form loaded successfully.
                                            Log.d(TAG,"onConsentFormLoaded");
                                            showform();
                                        }

                                        @Override
                                        public void onConsentFormOpened() {
                                            // Consent form was displayed.
                                            Log.d(TAG,"onConsentFormOpened");
                                        }

                                        @Override
                                        public void onConsentFormClosed(
                                                ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                                            // Consent form was closed.
                                            Log.d(TAG,"onConsentFormClosed");
                                        }

                                        @Override
                                        public void onConsentFormError(String errorDescription) {
                                            // Consent form error.
                                            Log.d(TAG,"onConsentFormError");
                                            Log.d(TAG,errorDescription);
                                        }
                                    })
                                    .withPersonalizedAdsOption()
                                    .withNonPersonalizedAdsOption()
                                    .build();

                            form.load();

                        }else{
                            Log.d(TAG,"PERSONALIZED else");
                            ConsentInformation.getInstance(activity)
                                    .setConsentStatus(ConsentStatus.PERSONALIZED);
                        }


                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
                Log.d(TAG,"onFailedToUpdateConsentInfo");
                Log.d(TAG,errorDescription);
            }
        });
    }

    private void showform(){
        if (form!=null){
            Log.d(TAG,"show ok");
            form.show();
        }

    }
}
