package com.bitmovin.player.adobeanalytics.testapp;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.adobeanalytics.AdobeMediaAnalyticsEventsWrapper;
import com.bitmovin.player.adobeanalytics.AdobeMediaAnalyticsTracker;
import com.bitmovin.player.adobeanalytics.AdobeMediaAnalyticsTracker.AdobeMediaAnalyticsDataOverrides;

import static org.junit.Assert.*;
import android.content.Intent;

import java.util.HashMap;

@RunWith(MockitoJUnitRunner.class)
public class BitmovinAdobeMediaAnalyticsTrackerTest {

    final AdobeMediaAnalyticsTracker bitmovinAmaTracker = new AdobeMediaAnalyticsTracker();
    final long VERIFY_CREATEMEDIAOBJECT_TIMEOUT = (1 * 1000);
    final long VERIFY_TRACKSESSIONSTART_TIMEOUT = (1 * 1000);
    final long VERIFY_TRACKPLAY_TIMEOUT = (10 * 1000);
    final long VERIFY_UPDATEPLAYHEAD_TIMEOUT = (20 * 1000);
    final int VERIFY_UPDATEPLAYHEAD_COUNT = 40;
    final long VERIFY_TRACKPAUSE_TIMEOUT = (1 * 1000);
    final long VERIFY_TRACKRESUME_TIMEOUT = (1 * 1000);
    final long VERIFY_TRACKSEEKSTART_TIMEOUT = (1 * 1000);
    final long VERIFY_TRACKSEEKCOMPLETE_TIMEOUT = (10 * 1000);
    final long SEEKTO_END_INTERVAL = 2;
    final long VERIFY_TRACKCOMPLETE_TIMEOUT = ((2 + SEEKTO_END_INTERVAL) * 1000);
    final long VERIFY_TRACKSESSIONEND_TIMEOUT = (1 * 1000);

    final private TestAMADataOverrides dataOverrides = new TestAMADataOverrides();

    public class TestAMADataOverrides implements AdobeMediaAnalyticsDataOverrides {

        @Override
        public HashMap<String, String> contextDataOverride (BitmovinPlayer player) {
            HashMap<String, String> contextData = new HashMap<String, String>();
            contextData.put("os", "Android");
            contextData.put("version", "9.0");
            return contextData;
        }

        @Override
        public String mediaNameOverride (BitmovinPlayer player) {
            return "testname";
        }

        @Override
        public String mediaUidOverride (BitmovinPlayer player) {
            return "testid";
        }
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_createTrackerWithNullPlayerAndNullDataOverride() {

        // create Bitmovin Adobe Media Analytics tracker object
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);
        activityScenario.onActivity ( activity -> {
                    try {
                        bitmovinAmaTracker.createTracker(null, null);
                    } catch (IllegalArgumentException e) {
                        // Expectation is to receive IllegalArgumentException exception with first parameter as null
                        assertEquals("BitmovinPlayer argument cannot be null", e.getMessage());
                    } catch (Exception e) {
                        // Expectation is to not receive any other exception
                        assertTrue("Received unexpected exception: " + e, false);
                    }
                }
        );

        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.destroyTracker();
        });

        activityScenario.close();
    }

    @Test
    public void test_createTrackerWithValidPlayerAndNullDataOverride() {

        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);
        activityScenario.onActivity ( activity -> {
            try {
                bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, null);
            } catch (Exception e) {
                // Expectation is to not receive any exception
                assertTrue("Received unexpected exception: " + e, false);
            }
        }
        );

        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.destroyTracker();
        });

        activityScenario.close();
    }

    @Test
    public void test_createTrackerWithValidPlayerAndValidDataOverride() {

        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);
        activityScenario.onActivity ( activity -> {
                    try {
                        bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, dataOverrides);
                    } catch (Exception e) {
                        // Expectation is to not receive any exception
                        assertTrue("Received unexpected exception: " + e, false);
                    }
                }
        );

        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.destroyTracker();
        });

        activityScenario.close();
    }

    @Test
    public void test_trackSessionStartManualPlay() {

        AdobeMediaAnalyticsEventsWrapper mockAmaEventsWrapper = mock(AdobeMediaAnalyticsEventsWrapper.class);
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);
        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, dataOverrides, mockAmaEventsWrapper);
            activity.bitmovinPlayer.play();
        });

        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEMEDIAOBJECT_TIMEOUT).times(1)).createMediaObject(anyString(), anyString(), anyDouble(), anyString());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONSTART_TIMEOUT).times(1)).trackSessionStart(anyMap(), anyMap());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKPLAY_TIMEOUT).times(1)).trackPlay();
        verify(mockAmaEventsWrapper, timeout(VERIFY_UPDATEPLAYHEAD_TIMEOUT).times(VERIFY_UPDATEPLAYHEAD_COUNT)).updateCurrentPlayhead(anyDouble());

        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.destroyTracker();
        });

        activityScenario.close();
    }

    @Test
    public void test_trackSessionStartAutoPlay() {

        AdobeMediaAnalyticsEventsWrapper mockAmaEventsWrapper = mock(AdobeMediaAnalyticsEventsWrapper.class);

        Intent launchIntent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        launchIntent.putExtra(MainActivity.AUTOPLAY_KEY, true);
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(launchIntent);
        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, dataOverrides, mockAmaEventsWrapper);
        });

        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEMEDIAOBJECT_TIMEOUT).times(1)).createMediaObject(anyString(), anyString(), anyDouble(), anyString());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONSTART_TIMEOUT).times(1)).trackSessionStart(anyMap(), anyMap());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKPLAY_TIMEOUT).times(1)).trackPlay();
        verify(mockAmaEventsWrapper, timeout(VERIFY_UPDATEPLAYHEAD_TIMEOUT).times(VERIFY_UPDATEPLAYHEAD_COUNT)).updateCurrentPlayhead(anyDouble());

        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.destroyTracker();
        });

        activityScenario.close();
    }

    @Test
    public void test_trackPausePlay() {

        AdobeMediaAnalyticsEventsWrapper mockAmaEventsWrapper = mock(AdobeMediaAnalyticsEventsWrapper.class);
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);
        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, dataOverrides, mockAmaEventsWrapper);
            activity.bitmovinPlayer.play();
        });

        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEMEDIAOBJECT_TIMEOUT).times(1)).createMediaObject(anyString(), anyString(), anyDouble(), anyString());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONSTART_TIMEOUT).times(1)).trackSessionStart(anyMap(), anyMap());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKPLAY_TIMEOUT).times(1)).trackPlay();
        verify(mockAmaEventsWrapper, timeout(VERIFY_UPDATEPLAYHEAD_TIMEOUT).times(VERIFY_UPDATEPLAYHEAD_COUNT)).updateCurrentPlayhead(anyDouble());

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.pause();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKPAUSE_TIMEOUT).times(1)).trackPause();

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.play();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKRESUME_TIMEOUT).times(2)).trackPlay();

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.pause();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKPAUSE_TIMEOUT).times(2)).trackPause();

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.play();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKRESUME_TIMEOUT).times(3)).trackPlay();

        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.destroyTracker();
        });

        activityScenario.close();
    }

    @Test
    public void test_trackSeek() {

        AdobeMediaAnalyticsEventsWrapper mockAmaEventsWrapper = mock(AdobeMediaAnalyticsEventsWrapper.class);
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);
        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, dataOverrides, mockAmaEventsWrapper);
            activity.bitmovinPlayer.play();
        });

        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEMEDIAOBJECT_TIMEOUT).times(1)).createMediaObject(anyString(), anyString(), anyDouble(), anyString());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONSTART_TIMEOUT).times(1)).trackSessionStart(anyMap(), anyMap());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKPLAY_TIMEOUT).times(1)).trackPlay();
        verify(mockAmaEventsWrapper, timeout(VERIFY_UPDATEPLAYHEAD_TIMEOUT).times(VERIFY_UPDATEPLAYHEAD_COUNT)).updateCurrentPlayhead(anyDouble());

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.seek(30);
        });

        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSEEKSTART_TIMEOUT).times(1)).trackSeekStart();
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSEEKCOMPLETE_TIMEOUT).times(1)).trackSeekComplete();

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.seek(10);
        });

        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSEEKSTART_TIMEOUT).times(2)).trackSeekStart();
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSEEKCOMPLETE_TIMEOUT).times(2)).trackSeekComplete();

        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.destroyTracker();
        });

        activityScenario.close();
    }

    @Test
    public void test_trackSessionEndOnPlaybackFinished () {

        AdobeMediaAnalyticsEventsWrapper mockAmaEventsWrapper = mock(AdobeMediaAnalyticsEventsWrapper.class);
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);

        // start playback and check tracking events
        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, dataOverrides, mockAmaEventsWrapper);
            activity.bitmovinPlayer.play();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEMEDIAOBJECT_TIMEOUT).times(1)).createMediaObject(anyString(), anyString(), anyDouble(), anyString());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONSTART_TIMEOUT).times(1)).trackSessionStart(anyMap(), anyMap());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKPLAY_TIMEOUT).times(1)).trackPlay();
        verify(mockAmaEventsWrapper, timeout(VERIFY_UPDATEPLAYHEAD_TIMEOUT).times(VERIFY_UPDATEPLAYHEAD_COUNT)).updateCurrentPlayhead(anyDouble());

        // seek near to end of playback and verify that trackComplete and trackSessionEnd events are fired
        activityScenario.onActivity ( activity -> {
            Double duration = activity.bitmovinPlayer.getDuration();
            activity.bitmovinPlayer.seek(duration - SEEKTO_END_INTERVAL);
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSEEKSTART_TIMEOUT).times(1)).trackSeekStart();
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSEEKCOMPLETE_TIMEOUT).times(1)).trackSeekComplete();
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKCOMPLETE_TIMEOUT).times(1)).trackComplete();
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONEND_TIMEOUT).times(1)).trackSessionEnd();

        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.destroyTracker();
        });

        activityScenario.close();
    }

    @Test
    public void test_trackSessionEndOnUnload () {

        AdobeMediaAnalyticsEventsWrapper mockAmaEventsWrapper = mock(AdobeMediaAnalyticsEventsWrapper.class);
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);

        // start playback and check tracking events
        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, dataOverrides, mockAmaEventsWrapper);
            activity.bitmovinPlayer.play();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEMEDIAOBJECT_TIMEOUT).times(1)).createMediaObject(anyString(), anyString(), anyDouble(), anyString());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONSTART_TIMEOUT).times(1)).trackSessionStart(anyMap(), anyMap());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKPLAY_TIMEOUT).times(1)).trackPlay();
        verify(mockAmaEventsWrapper, timeout(VERIFY_UPDATEPLAYHEAD_TIMEOUT).times(VERIFY_UPDATEPLAYHEAD_COUNT)).updateCurrentPlayhead(anyDouble());

        // seek near to end of playback and verify that trackComplete and trackSessionEnd events are fired
        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.unload();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKCOMPLETE_TIMEOUT).times(0)).trackComplete();
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONEND_TIMEOUT).times(1)).trackSessionEnd();


        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.destroyTracker();
        });

        activityScenario.close();
    }

    @Test
    public void test_trackSessionEndOnDestroy () {

        AdobeMediaAnalyticsEventsWrapper mockAmaEventsWrapper = mock(AdobeMediaAnalyticsEventsWrapper.class);
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);

        // start playback and check tracking events
        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, dataOverrides, mockAmaEventsWrapper);
            activity.bitmovinPlayer.play();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEMEDIAOBJECT_TIMEOUT).times(1)).createMediaObject(anyString(), anyString(), anyDouble(), anyString());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONSTART_TIMEOUT).times(1)).trackSessionStart(anyMap(), anyMap());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKPLAY_TIMEOUT).times(1)).trackPlay();
        verify(mockAmaEventsWrapper, timeout(VERIFY_UPDATEPLAYHEAD_TIMEOUT).times(VERIFY_UPDATEPLAYHEAD_COUNT)).updateCurrentPlayhead(anyDouble());

        // seek near to end of playback and verify that trackComplete and trackSessionEnd events are fired
        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.destroy();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKCOMPLETE_TIMEOUT).times(0)).trackComplete();
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONEND_TIMEOUT).times(1)).trackSessionEnd();


        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.destroyTracker();
        });

        activityScenario.close();
    }

    @Test
    public void test_trackAdBreakStartedVmap () {

        AdobeMediaAnalyticsEventsWrapper mockAmaEventsWrapper = mock(AdobeMediaAnalyticsEventsWrapper.class);

        // launch activity with VMAP TAG with single preroll, 3 midroll ads and 1 post roll ad
        Intent launchIntent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        String vmapTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostpod&cmsid=496&vid=short_onecue&correlator=";
        launchIntent.putExtra(MainActivity.VMAP_KEY, vmapTagUrl);
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(launchIntent);

        // start playback and check tracking events
        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, dataOverrides, mockAmaEventsWrapper);
            activity.bitmovinPlayer.play();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEMEDIAOBJECT_TIMEOUT).times(1)).createMediaObject(anyString(), anyString(), anyDouble(), anyString());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONSTART_TIMEOUT).times(1)).trackSessionStart(anyMap(), anyMap());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKPLAY_TIMEOUT).times(1)).trackPlay();
        verify(mockAmaEventsWrapper, timeout(VERIFY_UPDATEPLAYHEAD_TIMEOUT).times(VERIFY_UPDATEPLAYHEAD_COUNT)).updateCurrentPlayhead(anyDouble());

        // seek near to end of playback and verify that trackComplete and trackSessionEnd events are fired
        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.destroy();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKCOMPLETE_TIMEOUT).times(0)).trackComplete();
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONEND_TIMEOUT).times(1)).trackSessionEnd();


        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.destroyTracker();
        });

        activityScenario.close();
    }


    @Test
    public void destroyTracker() {
    }

}