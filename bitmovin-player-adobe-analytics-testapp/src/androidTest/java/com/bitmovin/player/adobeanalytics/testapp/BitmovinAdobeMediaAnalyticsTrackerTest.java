package com.bitmovin.player.adobeanalytics.testapp;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.config.quality.VideoQuality;

import com.bitmovin.player.adobeanalytics.AdobeMediaAnalyticsEventsWrapper;
import com.bitmovin.player.adobeanalytics.AdobeMediaAnalyticsTracker;
import com.bitmovin.player.adobeanalytics.AdobeMediaAnalyticsTracker.AdobeMediaAnalyticsDataOverrides;

import static org.junit.Assert.*;
import android.content.Intent;

import java.util.HashMap;

@RunWith(MockitoJUnitRunner.class)
public class BitmovinAdobeMediaAnalyticsTrackerTest {

    final long VERIFY_CREATEMEDIAOBJECT_TIMEOUT = (1 * 1000);
    final long VERIFY_TRACKSESSIONSTART_TIMEOUT = (1 * 1000);
    final long VERIFY_TRACKPLAY_TIMEOUT = (10 * 1000);
    final long VERIFY_UPDATEPLAYHEAD_TIMEOUT = (10 * 1000);
    final int VERIFY_UPDATEPLAYHEAD_COUNT = 20;
    final long VERIFY_TRACKPAUSE_TIMEOUT = (1 * 1000);
    final long VERIFY_TRACKRESUME_TIMEOUT = (1 * 1000);
    final long VERIFY_TRACKSEEKSTART_TIMEOUT = (1 * 1000);
    final long VERIFY_TRACKSEEKCOMPLETE_TIMEOUT = (10 * 1000);
    final long VERIFY_QUALITYSWITCH_TIMEOUT = (10 * 1000);
    final long SEEKTO_END_INTERVAL = 2;
    final long VERIFY_TRACKMUTE_TIMEOUT = (2 * 1000);
    final long VERIFY_TRACKCOMPLETE_TIMEOUT = (10 * 1000);
    final long VERIFY_TRACKSESSIONEND_TIMEOUT = (1 * 1000);
    final long VERIFY_CREATEADBREAKOBJECT_TIMEOUT = (10 * 1000);
    final long VERIFY_CREATEADOBJECT_TIMEOUT = (5 * 1000);
    final long VERIFY_ADBREAKSTARTED_TIMEOUT = (5 * 1000);
    final long VERIFY_ADSTARTED_TIMEOUT = (5 * 1000);
    final long VERIFY_ADFINISHED_TIMEOUT = (15 * 1000);
    final long VERIFY_ADSKIPPED_TIMEOUT = (15 * 1000);
    final long VERIFY_ADBREAKFINISHED_TIMEOUT = (1 * 1000);
    final Double PREROLL_ADBREAK_STARTTIME = 0.0d;
    final Double MIDROLL_ADBREAK_STARTTIME = 15.0d;
    final Double PREROLL_AD_DURATION = 10.0d;
    final Double BUMPER_AD_DURATION = 5.0d;
    final Double MIDROLL_AD_DURATION = 10.0d;
    final Double POSTROLL_AD_DURATION = 10.0d;
    final Long ADBREAK_START_POSITION = 1L;
    final Long AD_START_POSITION = 1L;

    final private TestAMADataOverrides dataOverrides = new TestAMADataOverrides();
    final AdobeMediaAnalyticsTracker bitmovinAmaTracker = new AdobeMediaAnalyticsTracker();
    Double vodAssetDuration = 0.0d;

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

    @Before
    public void tearDown() {
        bitmovinAmaTracker.destroyTracker();
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
            activity.bitmovinPlayer.destroy();
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
            activity.bitmovinPlayer.destroy();
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
        clearInvocations(mockAmaEventsWrapper);

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.play();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKRESUME_TIMEOUT).times(1)).trackPlay();
        clearInvocations(mockAmaEventsWrapper);

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.pause();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKPAUSE_TIMEOUT).times(1)).trackPause();
        clearInvocations(mockAmaEventsWrapper);

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.play();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKRESUME_TIMEOUT).times(1)).trackPlay();
        clearInvocations(mockAmaEventsWrapper);

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.destroy();
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
        clearInvocations(mockAmaEventsWrapper);

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.seek(10);
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSEEKSTART_TIMEOUT).times(1)).trackSeekStart();
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSEEKCOMPLETE_TIMEOUT).times(1)).trackSeekComplete();
        clearInvocations(mockAmaEventsWrapper);

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.destroy();
        });

        activityScenario.close();
    }

    @Test
    public void test_trackBitrateChange() {

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
            VideoQuality[] videoQualityArr = activity.bitmovinPlayer.getAvailableVideoQualities();
            VideoQuality currVideoQuality = activity.bitmovinPlayer.getVideoQuality();
            VideoQuality switchToVideoQuality = videoQualityArr[0];
            for (VideoQuality quality : videoQualityArr) {
                if ((quality.getBitrate() < switchToVideoQuality.getBitrate()) && (quality.getId() != currVideoQuality.getId())) {
                    switchToVideoQuality = quality;
                }
            }
            activity.bitmovinPlayer.setVideoQuality(switchToVideoQuality.getId());
        });

        verify(mockAmaEventsWrapper, timeout(VERIFY_QUALITYSWITCH_TIMEOUT).times(1)).createQoeObject(anyLong(), anyDouble(), anyDouble(), anyLong());
        verify(mockAmaEventsWrapper, timeout(VERIFY_QUALITYSWITCH_TIMEOUT).times(1)).trackBitrateChange(anyMap());

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.destroy();
        });

        activityScenario.close();
    }

    @Test
    public void test_trackMuteUnmute() {

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
            activity.bitmovinPlayer.mute();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKMUTE_TIMEOUT).times(1)).trackMute();

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.unmute();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKMUTE_TIMEOUT).times(1)).trackUnmute();

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.destroy();
        });
        activityScenario.close();
    }

    @Test
    public void test_trackError() {

        AdobeMediaAnalyticsEventsWrapper mockAmaEventsWrapper = mock(AdobeMediaAnalyticsEventsWrapper.class);

        Intent launchIntent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);

        // configuring DRM source asset, the playback should generate error as no DRM configuration is provided
        String errorSourceUrl = "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/dummy.mpd";
        launchIntent.putExtra(MainActivity.SOURCE_KEY, errorSourceUrl);
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(launchIntent);
        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, dataOverrides, mockAmaEventsWrapper);
            activity.bitmovinPlayer.play();
        });

        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEMEDIAOBJECT_TIMEOUT).times(1)).createMediaObject(anyString(), anyString(), anyDouble(), anyString());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONSTART_TIMEOUT).times(1)).trackSessionStart(anyMap(), anyMap());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKPLAY_TIMEOUT).times(1)).trackError(anyString());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKCOMPLETE_TIMEOUT).times(0)).trackComplete();
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONEND_TIMEOUT).times(1)).trackSessionEnd();

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.destroy();
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
            vodAssetDuration = activity.bitmovinPlayer.getDuration();
            activity.bitmovinPlayer.seek(vodAssetDuration - SEEKTO_END_INTERVAL);
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSEEKSTART_TIMEOUT).times(1)).trackSeekStart();
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSEEKCOMPLETE_TIMEOUT).times(1)).trackSeekComplete();
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKCOMPLETE_TIMEOUT).times(1)).trackComplete();

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.destroy();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONEND_TIMEOUT).times(1)).trackSessionEnd();

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
            activity.bitmovinPlayer.destroy();
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

        // destroy player instance before playback finish and verify that only trackSessionEnd event is fired
        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.destroy();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKCOMPLETE_TIMEOUT).times(0)).trackComplete();
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONEND_TIMEOUT).times(1)).trackSessionEnd();

        activityScenario.close();
    }

    @Test
    public void test_trackAdEventsVmapPrerollSingleAd () {

        AdobeMediaAnalyticsEventsWrapper mockAmaEventsWrapper = mock(AdobeMediaAnalyticsEventsWrapper.class);

        // launch activity with VMAP TAG with single preroll ad
        Intent launchIntent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        String vmapPrerollTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpreonly&cmsid=496&vid=short_onecue&correlator=";
        launchIntent.putExtra(MainActivity.VMAP_KEY, vmapPrerollTagUrl);
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(launchIntent);

        // start playback and check tracking events
        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, dataOverrides, mockAmaEventsWrapper);
            activity.bitmovinPlayer.play();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEMEDIAOBJECT_TIMEOUT).times(1)).createMediaObject(anyString(), anyString(), anyDouble(), anyString());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONSTART_TIMEOUT).times(1)).trackSessionStart(anyMap(), anyMap());

        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEADBREAKOBJECT_TIMEOUT).times(1)).createAdBreakObject(anyString(), eq(ADBREAK_START_POSITION), eq(PREROLL_ADBREAK_STARTTIME));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADBREAKSTARTED_TIMEOUT).times(1)).trackAdBreakStarted(anyMap());
        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEADOBJECT_TIMEOUT).times(1)).createAdObject(anyString(), anyString(), eq(AD_START_POSITION), eq(PREROLL_AD_DURATION));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADSTARTED_TIMEOUT).times(1)).trackAdStarted(anyMap(), eq(null));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADFINISHED_TIMEOUT).times(1)).trackAdComplete();
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADBREAKFINISHED_TIMEOUT).times(1)).trackAdBreakComplete();

        // verify that play head is not updated during ad playback
        verify(mockAmaEventsWrapper, times(0)).updateCurrentPlayhead(anyDouble());

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.destroy();
        });

        activityScenario.close();
    }

    @Test
    public void test_trackAdEventsVmapPrerollWithBumper () {

        AdobeMediaAnalyticsEventsWrapper mockAmaEventsWrapper = mock(AdobeMediaAnalyticsEventsWrapper.class);

        // launch activity with VMAP TAG with preroll + bumber
        Intent launchIntent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        String vmapPrerollTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpreonlybumper&cmsid=496&vid=short_onecue&correlator=";
        launchIntent.putExtra(MainActivity.VMAP_KEY, vmapPrerollTagUrl);
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(launchIntent);

        // start playback and check tracking events
        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, dataOverrides, mockAmaEventsWrapper);
            activity.bitmovinPlayer.play();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEMEDIAOBJECT_TIMEOUT).times(1)).createMediaObject(anyString(), anyString(), anyDouble(), anyString());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONSTART_TIMEOUT).times(1)).trackSessionStart(anyMap(), anyMap());

        // verify preroll ad break start events
        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEADBREAKOBJECT_TIMEOUT).times(1)).createAdBreakObject(anyString(), eq(ADBREAK_START_POSITION), eq(PREROLL_ADBREAK_STARTTIME));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADBREAKSTARTED_TIMEOUT).times(1)).trackAdBreakStarted(anyMap());

        // verify first preroll ad events
        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEADOBJECT_TIMEOUT).times(1)).createAdObject(anyString(), anyString(), eq(AD_START_POSITION), eq(PREROLL_AD_DURATION));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADSTARTED_TIMEOUT).times(1)).trackAdStarted(anyMap(), eq(null));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADFINISHED_TIMEOUT).times(1)).trackAdComplete();
        clearInvocations(mockAmaEventsWrapper);

        // verify bumber preroll ad events
        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEADOBJECT_TIMEOUT).times(1)).createAdObject(anyString(), anyString(), eq(AD_START_POSITION + 1L), eq(BUMPER_AD_DURATION));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADSTARTED_TIMEOUT).times(1)).trackAdStarted(anyMap(), eq(null));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADFINISHED_TIMEOUT).times(1)).trackAdComplete();

        // verify preroll ad break complete events
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADBREAKFINISHED_TIMEOUT).times(1)).trackAdBreakComplete();

        // verify that play head is not updated during ad playback
        verify(mockAmaEventsWrapper, times(0)).updateCurrentPlayhead(anyDouble());

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.destroy();
        });

        activityScenario.close();
    }

    @Test
    public void test_trackAdEventsVmapPostrollSingleAd () {

        AdobeMediaAnalyticsEventsWrapper mockAmaEventsWrapper = mock(AdobeMediaAnalyticsEventsWrapper.class);

        // launch activity with VMAP TAG with single post roll ad
        Intent launchIntent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        String vmapPrerollTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpostonly&cmsid=496&vid=short_onecue&correlator=";
        launchIntent.putExtra(MainActivity.VMAP_KEY, vmapPrerollTagUrl);
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
            vodAssetDuration = activity.bitmovinPlayer.getDuration();
            activity.bitmovinPlayer.seek(vodAssetDuration - SEEKTO_END_INTERVAL);
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSEEKSTART_TIMEOUT).times(1)).trackSeekStart();
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSEEKCOMPLETE_TIMEOUT).times(1)).trackSeekComplete();
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKCOMPLETE_TIMEOUT).times(1)).trackComplete();
        clearInvocations(mockAmaEventsWrapper);

        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEADBREAKOBJECT_TIMEOUT).times(1)).createAdBreakObject(anyString(), eq(ADBREAK_START_POSITION), eq(vodAssetDuration));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADBREAKSTARTED_TIMEOUT).times(1)).trackAdBreakStarted(anyMap());

        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEADOBJECT_TIMEOUT).times(1)).createAdObject(anyString(), anyString(), eq(AD_START_POSITION), eq(POSTROLL_AD_DURATION));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADSTARTED_TIMEOUT).times(1)).trackAdStarted(anyMap(), eq(null));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADFINISHED_TIMEOUT).times(1)).trackAdComplete();

        // verify that play head is not updated during ad playback
        verify(mockAmaEventsWrapper, times(0)).updateCurrentPlayhead(anyDouble());

        // verify adBreak complete event
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADBREAKFINISHED_TIMEOUT).times(1)).trackAdBreakComplete();

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.destroy();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONEND_TIMEOUT).times(1)).trackSessionEnd();

        activityScenario.close();
    }

    @Test
    public void test_trackAdEventsVmapPreMidPostrollSingleAds () {

        AdobeMediaAnalyticsEventsWrapper mockAmaEventsWrapper = mock(AdobeMediaAnalyticsEventsWrapper.class);

        // launch activity with VMAP TAG with single preroll, 3 midroll ads and single post roll ad
        Intent launchIntent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        String vmapPrerollTagUrl = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/ad_rule_samples&ciu_szs=300x250&ad_rule=1&impl=s&gdfp_req=1&env=vp&output=vmap&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ar%3Dpremidpostoptimizedpod&cmsid=496&vid=short_onecue&correlator=";
        launchIntent.putExtra(MainActivity.VMAP_KEY, vmapPrerollTagUrl);
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(launchIntent);

        // start playback and check tracking events
        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, dataOverrides, mockAmaEventsWrapper);
            activity.bitmovinPlayer.play();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEMEDIAOBJECT_TIMEOUT).times(1)).createMediaObject(anyString(), anyString(), anyDouble(), anyString());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONSTART_TIMEOUT).times(1)).trackSessionStart(anyMap(), anyMap());

        // verify preroll Ad tracking events
        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEADBREAKOBJECT_TIMEOUT).times(1)).createAdBreakObject(anyString(), eq(ADBREAK_START_POSITION), eq(PREROLL_ADBREAK_STARTTIME));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADBREAKSTARTED_TIMEOUT).times(1)).trackAdBreakStarted(anyMap());
        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEADOBJECT_TIMEOUT).times(1)).createAdObject(anyString(), anyString(), eq(AD_START_POSITION), eq(PREROLL_AD_DURATION));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADSTARTED_TIMEOUT).times(1)).trackAdStarted(anyMap(), eq(null));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADFINISHED_TIMEOUT).times(1)).trackAdComplete();
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADBREAKFINISHED_TIMEOUT).times(1)).trackAdBreakComplete();
        // verify that play head is not updated during ad playback
        verify(mockAmaEventsWrapper, times(0)).updateCurrentPlayhead(anyDouble());
        clearInvocations(mockAmaEventsWrapper);

        // seek beyond mid point to to verify midroll Ad tracking events
        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.seek(MIDROLL_ADBREAK_STARTTIME + 1.0d);
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSEEKSTART_TIMEOUT).times(1)).trackSeekStart();
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSEEKCOMPLETE_TIMEOUT).times(1)).trackSeekComplete();

        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEADBREAKOBJECT_TIMEOUT).times(1)).createAdBreakObject(anyString(), eq(ADBREAK_START_POSITION), eq(MIDROLL_ADBREAK_STARTTIME));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADBREAKSTARTED_TIMEOUT).times(1)).trackAdBreakStarted(anyMap());
        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEADOBJECT_TIMEOUT).times(1)).createAdObject(anyString(), anyString(), eq(AD_START_POSITION), eq(MIDROLL_AD_DURATION));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADSTARTED_TIMEOUT).times(1)).trackAdStarted(anyMap(), eq(null));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADFINISHED_TIMEOUT).times(1)).trackAdComplete();
        clearInvocations(mockAmaEventsWrapper);
        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEADOBJECT_TIMEOUT).times(1)).createAdObject(anyString(), anyString(), eq(AD_START_POSITION + 1L), eq(MIDROLL_AD_DURATION));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADSTARTED_TIMEOUT).times(1)).trackAdStarted(anyMap(), eq(null));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADFINISHED_TIMEOUT).times(1)).trackAdComplete();
        clearInvocations(mockAmaEventsWrapper);
        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEADOBJECT_TIMEOUT).times(1)).createAdObject(anyString(), anyString(), eq(AD_START_POSITION + 2L), eq(MIDROLL_AD_DURATION));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADSTARTED_TIMEOUT).times(1)).trackAdStarted(anyMap(), eq(null));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADFINISHED_TIMEOUT).times(1)).trackAdComplete();
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADBREAKFINISHED_TIMEOUT).times(1)).trackAdBreakComplete();
        // verify that play head is not updated during ad playback
        verify(mockAmaEventsWrapper, times(0)).updateCurrentPlayhead(anyDouble());
        clearInvocations(mockAmaEventsWrapper);

        // seek to end to verify postroll Ad tracking events
        activityScenario.onActivity ( activity -> {
            vodAssetDuration = activity.bitmovinPlayer.getDuration();
            activity.bitmovinPlayer.seek(vodAssetDuration);
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSEEKSTART_TIMEOUT).times(1)).trackSeekStart();
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSEEKCOMPLETE_TIMEOUT).times(1)).trackSeekComplete();

        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKCOMPLETE_TIMEOUT).times(1)).trackComplete();

        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEADBREAKOBJECT_TIMEOUT).times(1)).createAdBreakObject(anyString(), eq(ADBREAK_START_POSITION), eq(vodAssetDuration));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADBREAKSTARTED_TIMEOUT).times(1)).trackAdBreakStarted(anyMap());
        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEADOBJECT_TIMEOUT).times(1)).createAdObject(anyString(), anyString(), eq(AD_START_POSITION), eq(POSTROLL_AD_DURATION));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADSTARTED_TIMEOUT).times(1)).trackAdStarted(anyMap(), eq(null));
        verify(mockAmaEventsWrapper, timeout(VERIFY_ADFINISHED_TIMEOUT).times(1)).trackAdComplete();

        // verify that play head is not updated during ad playback
        verify(mockAmaEventsWrapper, times(0)).updateCurrentPlayhead(anyDouble());

        verify(mockAmaEventsWrapper, timeout(VERIFY_ADBREAKFINISHED_TIMEOUT).times(1)).trackAdBreakComplete();
        clearInvocations(mockAmaEventsWrapper);

        activityScenario.onActivity ( activity -> {
            activity.bitmovinPlayer.destroy();
        });
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONEND_TIMEOUT).times(1)).trackSessionEnd();

        activityScenario.close();
    }

    @Test
    public void destroyTracker() {
    }

}