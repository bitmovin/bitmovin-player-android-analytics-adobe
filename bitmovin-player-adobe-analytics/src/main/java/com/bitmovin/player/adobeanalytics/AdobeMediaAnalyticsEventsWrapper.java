package com.bitmovin.player.adobeanalytics;

import com.adobe.marketing.mobile.Media;
import com.adobe.marketing.mobile.MediaConstants;
import com.adobe.marketing.mobile.MediaTracker;

import java.util.HashMap;
import java.util.Map;

public class AdobeMediaAnalyticsEventsWrapper {

    private final String TAG = "AdobeMediaAnalyticsEventsWrapper";
    private MediaTracker mediaTracker;

    AdobeMediaAnalyticsEventsWrapper(MediaTracker tracker) {
        mediaTracker = tracker;
    }

    public void trackSessionStart(Map<String, Object> mediaInfo, Map<String, String> contextData) {
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

    public void trackBufferStart() {
        if (mediaTracker != null) {
            mediaTracker.trackEvent(Media.Event.BufferStart, null, null);
        }
    }

    public void trackBufferComplete() {
        if (mediaTracker != null) {
            mediaTracker.trackEvent(Media.Event.BufferComplete, null, null);
        }
    }

    public void trackSeekStart() {
        if (mediaTracker != null) {
            mediaTracker.trackEvent(Media.Event.SeekStart, null, null);
        }
    }

    public void trackSeekComplete() {
        if (mediaTracker != null) {
            mediaTracker.trackEvent(Media.Event.SeekComplete, null, null);
        }
    }

    public void trackBitrateChange(Map<String, Object> qoeObject) {
        if (mediaTracker != null) {
            this.updateQoEObject(qoeObject);
            mediaTracker.trackEvent(Media.Event.BitrateChange, null, null);
        }
    }


    public void trackFullScreenEnter() {
        if (mediaTracker != null) {
            HashMap<String, Object> stateObject = Media.createStateObject(MediaConstants.PlayerState.FULLSCREEN);
            mediaTracker.trackEvent(Media.Event.StateStart, stateObject, null);
        }
    }

    public void trackFullScreenExit() {
        if (mediaTracker != null) {
            HashMap<String, Object> stateObject = Media.createStateObject(MediaConstants.PlayerState.FULLSCREEN);
            mediaTracker.trackEvent(Media.Event.StateEnd, stateObject, null);
        }
    }

    public void trackMute() {
        if (mediaTracker != null) {
            HashMap<String, Object> stateObject = Media.createStateObject(MediaConstants.PlayerState.MUTE);
            mediaTracker.trackEvent(Media.Event.StateStart, stateObject, null);
        }
    }

    public void trackUnmute() {
        if (mediaTracker != null) {
            HashMap<String, Object> stateObject = Media.createStateObject(MediaConstants.PlayerState.MUTE);
            mediaTracker.trackEvent(Media.Event.StateEnd, stateObject, null);
        }
    }

    public void trackAdBreakStarted(Map<String, Object> adBreakObject) {
        if (mediaTracker != null) {
            mediaTracker.trackEvent(Media.Event.AdBreakStart, adBreakObject, null);
        }
    }

    public void trackAdBreakComplete() {
        if (mediaTracker != null) {
            mediaTracker.trackEvent(Media.Event.AdBreakComplete, null, null);
        }
    }

    public void trackAdStarted(Map<String, Object> adObject, Map<String, String> adMetadata) {
        if (mediaTracker != null) {
            mediaTracker.trackEvent(Media.Event.AdStart, adObject, adMetadata);
        }
    }

    public void trackAdSkip() {
        if (mediaTracker != null) {
            mediaTracker.trackEvent(Media.Event.AdSkip, null, null);
        }
    }

    public void trackAdComplete() {
        if (mediaTracker != null) {
            mediaTracker.trackEvent(Media.Event.AdComplete, null, null);
        }
    }

    public void updateCurrentPlayhead(Double time) {
        if (mediaTracker != null) {
            mediaTracker.updateCurrentPlayhead(time);
        }
    }

    public void updateQoEObject(Map<String, Object> qoeObject) {
        if (mediaTracker != null) {
            mediaTracker.updateQoEObject(qoeObject);
        }
    }


    public HashMap<String, Object> createMediaObject (String name, String mediaId, Double length, String streamType) {
        return Media.createMediaObject(name,
                mediaId,
                length,
                streamType,
                Media.MediaType.Video);
    }

    public HashMap<String, Object> createAdBreakObject (String name, Long position, Double startTime) {
        return Media.createAdBreakObject(name, position, startTime);
    }

    public HashMap<String, Object> createAdObject (String name, String adId, Long position, Double duration) {
        return Media.createAdObject(name, adId, position, duration);
    }

    public HashMap<String, Object> createChapterObject (String name, Long position, Double duration, Double startTimee) {
        return Media.createChapterObject(name, position, duration, startTimee);
    }

    public HashMap<String, Object> createQoeObject (Long bitrate, Double startupTime, Double fps, Long droppedFrames) {
        return Media.createQoEObject(bitrate, startupTime, fps, droppedFrames);
    }

    public static HashMap<String, Object> createStateObject (String name) {
        return Media.createStateObject(name);
    }
}
