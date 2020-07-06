package com.bitmovin.player.adobeanalytics;

import com.adobe.marketing.mobile.Media;
import com.adobe.marketing.mobile.MediaTracker;

import java.util.Map;

public class BitmovinAMAEventsWrapper {

    private final String TAG = "BitmovinAMAEventsWrapper";
    private MediaTracker mediaTracker;

    BitmovinAMAEventsWrapper(MediaTracker tracker) {
        mediaTracker = tracker;
    }

    void trackSessionStart(Map<String, Object> mediaInfo, Map<String, String> contextData) {
        if (mediaTracker != null) {
            mediaTracker.trackSessionStart(mediaInfo, contextData);
        }
    }

    public void trackPlay() {
        if (mediaTracker != null) {
            mediaTracker.trackPlay();
        }
    }

    public void trackPause() {
        if (mediaTracker != null) {
            mediaTracker.trackPause();
        }
    }

    public void trackComplete() {
        if (mediaTracker != null) {
            mediaTracker.trackComplete();
        }
    }

    public void trackSessionEnd() {
        if (mediaTracker != null) {
            mediaTracker.trackSessionEnd();
        }
    }

    public void trackError(String errorId) {
        if (mediaTracker != null) {
            mediaTracker.trackError(errorId);
        }
    }

    public void trackEvent(Media.Event event,
                           Map<String, Object> info,
                           Map<String, String> data) {
        if (mediaTracker != null) {
            mediaTracker.trackEvent(event, info, data);
        }
    }
}
