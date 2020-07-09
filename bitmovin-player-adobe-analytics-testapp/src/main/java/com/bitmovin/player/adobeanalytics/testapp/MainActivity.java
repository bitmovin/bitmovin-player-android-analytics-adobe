package com.bitmovin.player.adobeanalytics.testapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.config.PlayerConfiguration;
import com.bitmovin.player.config.advertising.AdItem;
import com.bitmovin.player.config.advertising.AdSource;
import com.bitmovin.player.config.advertising.AdSourceType;
import com.bitmovin.player.config.advertising.AdvertisingConfiguration;
import com.bitmovin.player.config.media.SourceConfiguration;

import com.bitmovin.player.adobeanalytics.BitmovinAMATracker;

import com.adobe.marketing.mobile.*;

public class MainActivity extends AppCompatActivity
{
    // These are IMA Sample Tags from https://developers.google.com/interactive-media-ads/docs/sdks/android/tags
    private static final String VMAP_AD_SOURCE = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostlongpod&cmsid=496&vid=short_tencue&correlator=";

    public BitmovinPlayerView bitmovinPlayerView;
    public SourceConfiguration bitmovinSourceConfiguration;
    public PlayerConfiguration bitmovinPlayerConfiguration;
    public AdvertisingConfiguration bitmovinAdConfiguration;
    public BitmovinAMATracker bitmovinAmaTracker;
    public BitmovinPlayer bitmovinPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Create a new source configuration
        this.bitmovinSourceConfiguration = new SourceConfiguration();
        // Add a new source item
        this.bitmovinSourceConfiguration.addSourceItem("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd");

        // Create AdSources
        AdSource vmapAdSource = new AdSource(AdSourceType.IMA, VMAP_AD_SOURCE);

        // Setup a pre-roll ad
        AdItem vmapAdRoll = new AdItem("", vmapAdSource);

        // Add the AdItems to the AdvertisingConfiguration
        this.bitmovinAdConfiguration = new AdvertisingConfiguration(vmapAdRoll);

        // Creating a new PlayerConfiguration
        this.bitmovinPlayerConfiguration = new PlayerConfiguration();
        // Assign created SourceConfiguration to the PlayerConfiguration
        this.bitmovinPlayerConfiguration.setSourceConfiguration(bitmovinSourceConfiguration);
        // Assing the AdvertisingConfiguration to the PlayerConfiguration
        // All ads in the AdvertisingConfiguration will be scheduled automatically
        // bitmovinPlayerConfiguration.setAdvertisingConfiguration(advertisingConfiguration);

        // Create new BitmovinPlayerView with our PlayerConfiguration
        this.bitmovinPlayerView = new BitmovinPlayerView(this, bitmovinPlayerConfiguration);
        this.bitmovinPlayerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        LinearLayout rootView = (LinearLayout) this.findViewById(R.id.activity_main);

        // Add BitmovinPlayerView to the layout
        rootView.addView(this.bitmovinPlayerView, 0);

        this.bitmovinPlayer = bitmovinPlayerView.getPlayer();

        //this.bitmovinAmaTracker = new BitmovinAMATracker();
        //this.bitmovinAmaTracker.createTracker(this.bitmovinPlayer, null);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        this.bitmovinPlayerView.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.bitmovinPlayerView.onResume();
    }

    @Override
    protected void onPause()
    {
        this.bitmovinPlayerView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        this.bitmovinPlayerView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        this.bitmovinPlayerView.onDestroy();
        super.onDestroy();
    }
}
