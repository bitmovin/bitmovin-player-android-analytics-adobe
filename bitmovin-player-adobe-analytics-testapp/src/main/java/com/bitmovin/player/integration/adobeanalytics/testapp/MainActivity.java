package com.bitmovin.player.integration.adobeanalytics.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.PlaybackConfig;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerConfig;
import com.bitmovin.player.api.advertising.AdItem;
import com.bitmovin.player.api.advertising.AdSource;
import com.bitmovin.player.api.advertising.AdSourceType;
import com.bitmovin.player.api.advertising.AdvertisingConfig;
import com.bitmovin.player.api.source.Source;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.integration.adobeanalytics.AdobeMediaAnalyticsTracker;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    public static final String AUTOPLAY_KEY = "autoplay";
    public static final String VMAP_KEY = "vmapTag";
    public static final String SOURCE_KEY = "source";

    public PlayerView bitmovinPlayerView;
    public Source bitmovinSource;
    public PlaybackConfig bitmovinPlaybackConfig;
    public AdvertisingConfig bitmovinAdConfiguration;
    public PlayerConfig bitmovinPlayerConfig;
    public AdobeMediaAnalyticsTracker bitmovinAmaTracker;
    public Player bitmovinPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        Boolean autoPlay = intent.getBooleanExtra(MainActivity.AUTOPLAY_KEY, false);
        String vmapTagUrl = intent.getStringExtra(MainActivity.VMAP_KEY);
        String sourceUrl = intent.getStringExtra(MainActivity.SOURCE_KEY);

        // Create a new source configuration
        String defaultSourceUrl = "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd";
        // Add a new source item
        if (sourceUrl == null) {
            this.bitmovinSource = Source.create(SourceConfig.fromUrl(defaultSourceUrl));
        } else {
            this.bitmovinSource = Source.create(SourceConfig.fromUrl(sourceUrl));
        }

        this.bitmovinPlaybackConfig = new PlaybackConfig();
        this.bitmovinPlaybackConfig.setAutoplayEnabled(autoPlay);

        this.bitmovinPlayerConfig = new PlayerConfig();
        this.bitmovinPlayerConfig.setPlaybackConfig(bitmovinPlaybackConfig);


        // Create AdSources
        if (vmapTagUrl != null) {
            AdSource vmapAdSource = new AdSource(AdSourceType.Ima, vmapTagUrl);

            // Setup ad
            AdItem vmapAdRoll = new AdItem("", vmapAdSource);

            // Add the AdItems to the AdvertisingConfiguration
            this.bitmovinAdConfiguration = new AdvertisingConfig(vmapAdRoll);
            // Assing the AdvertisingConfiguration to the PlayerConfiguration
            // All ads in the AdvertisingConfiguration will be scheduled automatically
            this.bitmovinPlayerConfig.setAdvertisingConfig(this.bitmovinAdConfiguration);
        }

        this.bitmovinPlayer = Player.create(this, this.bitmovinPlayerConfig);

        this.bitmovinPlayerView = new PlayerView(this, this.bitmovinPlayer);
        this.bitmovinPlayerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        LinearLayout rootView = (LinearLayout) this.findViewById(R.id.activity_main);

        // Add BitmovinPlayerView to the layout
        rootView.addView(this.bitmovinPlayerView, 0);

        this.bitmovinPlayer.load(this.bitmovinSource);
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
