package com.bitmovin.player.adobeanalytics.testapp;

import androidx.test.core.app.ActivityScenario;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.adobeanalytics.BitmovinAMATracker;
import com.bitmovin.player.adobeanalytics.BitmovinAMAEventsWrapper;
import com.bitmovin.player.adobeanalytics.BitmovinAMATracker.BitmovinAMADataOverrides;

import static org.junit.Assert.*;

import java.util.HashMap;

//@RunWith(AndroidJUnit4.class)
@RunWith(MockitoJUnitRunner.class)
public class BitmovinAMATrackerTest {

    final BitmovinAMATracker bitmovinAmaTracker = new BitmovinAMATracker();

    final private TestAMADataOverrides dataOverrides = new TestAMADataOverrides();

    public class TestAMADataOverrides implements BitmovinAMADataOverrides {

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
    public void createBitmovinAMATracker_NullPlayerAndNullDataOverride() {

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
    public void createBitmovinAMATracker_ValidPlayerAndNullDataOverride() {

        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);
        activityScenario.onActivity ( activity -> {
            try {
                bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, null);
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
    public void createBitmovinAMATracker_ValidPlayerAndValidDataOverride() {

        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);
        activityScenario.onActivity ( activity -> {
                    try {
                        bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, dataOverrides);
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
    public void test_sessionStartManualPlay() {

        BitmovinAMAEventsWrapper mockAmaEventsWrapper = mock(BitmovinAMAEventsWrapper.class);
        //BitmovinAMATracker tracker = new BitmovinAMATracker();
        ActivityScenario<MainActivity> activityScenario = ActivityScenario.launch(MainActivity.class);
        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.createTracker(activity.bitmovinPlayer, dataOverrides, mockAmaEventsWrapper);
            activity.bitmovinPlayer.play();
        });

        verify(mockAmaEventsWrapper, times(1)).createMediaObject(anyString(), anyString(), anyDouble(), anyString());
        verify(mockAmaEventsWrapper, times(1)).trackSessionStart(anyMap(), eq(null));
        verify(mockAmaEventsWrapper, timeout(10000).times(1)).trackPlay();
        verify(mockAmaEventsWrapper, timeout(10000).times(10)).updateCurrentPlayhead(anyDouble());

        activityScenario.onActivity ( activity -> {
            bitmovinAmaTracker.destroyTracker();
        });

        activityScenario.close();
    }

    @Test
    public void destroyTracker() {
    }
    
}