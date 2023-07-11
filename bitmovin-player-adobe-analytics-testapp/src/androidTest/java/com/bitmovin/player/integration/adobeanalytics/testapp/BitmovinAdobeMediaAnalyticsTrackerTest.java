package com.bitmovin.player.integration.adobeanalytics.testapp;

import android.content.Intent;

import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.advertising.AdBreak;
import com.bitmovin.player.api.event.PlayerEvent;
import com.bitmovin.player.api.media.video.quality.VideoQuality;
import com.bitmovin.player.api.source.Source;
import com.bitmovin.player.integration.adobeanalytics.AdobeMediaAnalyticsDataOverride;
import com.bitmovin.player.integration.adobeanalytics.AdobeMediaAnalyticsEventsWrapper;
import com.bitmovin.player.integration.adobeanalytics.AdobeMediaAnalyticsTracker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    final double PREROLL_ADBREAK_STARTTIME = 0.0d;
    final double MIDROLL_ADBREAK_STARTTIME = 15.0d;
    final double PREROLL_AD_DURATION = 10.0d;
    final double BUMPER_AD_DURATION = 5.0d;
    final double MIDROLL_AD_DURATION = 10.0d;
    final double POSTROLL_AD_DURATION = 10.0d;
    final long ADBREAK_START_POSITION = 1L;
    final long AD_START_POSITION = 1L;

    final private TestAdobeMediaDataOverride customDataOverride = new TestAdobeMediaDataOverride();
    final AdobeMediaAnalyticsTracker bitmovinAmaTracker = new AdobeMediaAnalyticsTracker();
    double vodAssetDuration = 0.0d;

    public class TestAdobeMediaDataOverride extends AdobeMediaAnalyticsDataOverride {

        private long activeAdBreakPosition = 0L;
        private long activeAdPosition = 0L;

        @Override
        public HashMap<String, String> getMediaContextData (Player player) {
            HashMap<String, String> contextData = new HashMap<String, String>();
            contextData.put("os", "Android");
            contextData.put("version", "9.0");
            return contextData;
        }

        @Override
        public String getMediaName (Player player, Source activeSource) {
            // return appropriate value for media name
            return activeSource.getConfig().getUrl();
        }

        @Override
        public String getMediaUid (Player player, Source activeSource) {
            // return appropriate value for media id
            return activeSource.getConfig().getUrl();
        }

        @Override
        public String getAdBreakId (Player player, PlayerEvent.AdBreakStarted event) {
            // reset ad position in adBreak when receiving new adBreak event
            activeAdPosition = 0L;

            // return appropriate value for adBreak id
            AdBreak adBreak = event.getAdBreak();
            String adBreakId = adBreak.getId();
            return adBreakId;
        }

        @Override
        public long getAdBreakPosition (Player player, PlayerEvent.AdBreakStarted event) {
            // reset ad position in adBreak when receiving new adBreak event
            activeAdPosition = 0L;

            // return position of AdBreak in the content playback
            double scheduledTime = event.getAdBreak().getScheduleTime();
            if (scheduledTime == 0.0D) {
                // preroll adBreak
                activeAdBreakPosition = 1L;
            } else if (scheduledTime == player.getDuration()) {
                // postroll adBreak
                activeAdBreakPosition++;
            } else {
                // midroll adBreak
                activeAdBreakPosition++;
            }
            return activeAdBreakPosition;
        }

        @Override
        public String getAdName (Player player, PlayerEvent.AdStarted event) {
            // return appropriate value representing Ad name
            return event.getAd().getMediaFileUrl();
        }

        @Override
        public String getAdId (Player player, PlayerEvent.AdStarted event) {
            // return appropriate value representing Ad Id
            return event.getAd().getId();
        }

        @Override
        public long getAdPosition (Player player, PlayerEvent.AdStarted event) {
            // return position of Ad in Ad break
            return ++activeAdPosition;
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
                        bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, customDataOverride);
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
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, customDataOverride, mockAmaEventsWrapper);
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

    // TODO: This one is known to fail, investigate why and fix
//    @Test
//    public void test_trackSessionStartAutoPlay() {
//
//        AdobeMediaAnalyticsEventsWrapper mockAmaEventsWrapper = mock(AdobeMediaAnalyticsEventsWrapper.class);
//
//        Intent launchIntent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
//        launchIntent.putExtra(MainActivity.AUTOPLAY_KEY, true);
//        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(launchIntent);
//        activityScenario.onActivity ( activity -> {
//            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, customDataOverride, mockAmaEventsWrapper);
//        });
//
//        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEMEDIAOBJECT_TIMEOUT).times(1)).createMediaObject(anyString(), anyString(), anyDouble(), anyString());
//        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONSTART_TIMEOUT).times(1)).trackSessionStart(anyMap(), anyMap());
//        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKPLAY_TIMEOUT).times(1)).trackPlay();
//        verify(mockAmaEventsWrapper, timeout(VERIFY_UPDATEPLAYHEAD_TIMEOUT).times(VERIFY_UPDATEPLAYHEAD_COUNT)).updateCurrentPlayhead(anyDouble());
//
//        activityScenario.onActivity ( activity -> {
//            activity.bitmovinPlayer.destroy();
//        });
//
//        activityScenario.close();
//    }

    @Test
    public void test_trackPausePlay() {

        AdobeMediaAnalyticsEventsWrapper mockAmaEventsWrapper = mock(AdobeMediaAnalyticsEventsWrapper.class);
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);
        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, customDataOverride, mockAmaEventsWrapper);
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
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, customDataOverride, mockAmaEventsWrapper);
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
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, customDataOverride, mockAmaEventsWrapper);
            activity.bitmovinPlayer.play();
        });

        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEMEDIAOBJECT_TIMEOUT).times(1)).createMediaObject(anyString(), anyString(), anyDouble(), anyString());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKSESSIONSTART_TIMEOUT).times(1)).trackSessionStart(anyMap(), anyMap());
        verify(mockAmaEventsWrapper, timeout(VERIFY_TRACKPLAY_TIMEOUT).times(1)).trackPlay();
        verify(mockAmaEventsWrapper, timeout(VERIFY_UPDATEPLAYHEAD_TIMEOUT).times(VERIFY_UPDATEPLAYHEAD_COUNT)).updateCurrentPlayhead(anyDouble());

        activityScenario.onActivity ( activity -> {
            List<VideoQuality> videoQualities = activity.bitmovinSource.getAvailableVideoQualities();
            VideoQuality currVideoQuality = activity.bitmovinPlayer.getVideoQuality();
            VideoQuality switchToVideoQuality = videoQualities.get(0);
            for (VideoQuality quality : videoQualities) {
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
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, customDataOverride, mockAmaEventsWrapper);
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
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, customDataOverride, mockAmaEventsWrapper);
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
    public void test_trackSessionEndOnDestroyAfterPlaybackFinished () {

        AdobeMediaAnalyticsEventsWrapper mockAmaEventsWrapper = mock(AdobeMediaAnalyticsEventsWrapper.class);
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);

        // start playback and check tracking events
        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, customDataOverride, mockAmaEventsWrapper);
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
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, customDataOverride, mockAmaEventsWrapper);
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
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, customDataOverride, mockAmaEventsWrapper);
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
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, customDataOverride, mockAmaEventsWrapper);
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
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, customDataOverride, mockAmaEventsWrapper);
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
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, customDataOverride, mockAmaEventsWrapper);
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
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, customDataOverride, mockAmaEventsWrapper);
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

        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEADBREAKOBJECT_TIMEOUT).times(1)).createAdBreakObject(anyString(), eq(ADBREAK_START_POSITION + 1L), eq(MIDROLL_ADBREAK_STARTTIME));
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
        clearInvocations(mockAmaEventsWrapper);

        verify(mockAmaEventsWrapper, timeout(VERIFY_CREATEADBREAKOBJECT_TIMEOUT).times(1)).createAdBreakObject(anyString(), eq(ADBREAK_START_POSITION + 2L), eq(vodAssetDuration));
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