package com.bitmovin.player.adobeanalytics.testapp;

import android.app.Application;

import com.adobe.marketing.mobile.*;
import com.adobe.marketing.mobile.AdobeCallback;
import com.adobe.marketing.mobile.Analytics;
import com.adobe.marketing.mobile.Identity;
import com.adobe.marketing.mobile.InvalidInitException;
import com.adobe.marketing.mobile.Lifecycle;
import com.adobe.marketing.mobile.LoggingMode;
import com.adobe.marketing.mobile.Media;
import com.adobe.marketing.mobile.MobileCore;
//import com.adobe.marketing.mobile.MobileServices;
import com.adobe.marketing.mobile.Signal;
//import com.adobe.marketing.mobile.UserProfile;

public class MainApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        // register adobe extensions with
        MobileCore.setApplication(this);
        MobileCore.setLogLevel(LoggingMode.DEBUG);
        try {
            Media.registerExtension();
            Analytics.registerExtension();
            Identity.registerExtension();
            Lifecycle.registerExtension();
            Signal.registerExtension();
            MobileCore.start(new AdobeCallback () {
                @Override
                public void call(Object o) {
                    MobileCore.configureWithAppID("2e662f4d7a02/f7314e644f8f/launch-98a1c3e547bf-development");
                }
            });
        } catch (InvalidInitException e) {

        }
    }
}
