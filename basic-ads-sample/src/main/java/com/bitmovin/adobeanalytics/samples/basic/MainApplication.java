package com.bitmovin.adobeanalytics.samples.basic;

import android.app.Application;

import com.adobe.marketing.mobile.*;

public class MainApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        // register adobe extensions with
        MobileCore.setApplication(this);
        try {
            Media.registerExtension();
            Analytics.registerExtension();
            Identity.registerExtension();
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
