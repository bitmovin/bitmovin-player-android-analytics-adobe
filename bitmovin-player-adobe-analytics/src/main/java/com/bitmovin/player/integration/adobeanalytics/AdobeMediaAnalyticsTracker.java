package com.bitmovin.player.integration.adobeanalytics;

import android.util.Log;

import com.adobe.marketing.mobile.Media;
import com.adobe.marketing.mobile.MediaConstants;
import com.adobe.marketing.mobile.MediaTracker;
import com.bitmovin.player.api.PlaybackConfig;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerConfig;
import com.bitmovin.player.api.event.PlayerEvent;
import com.bitmovin.player.api.event.SourceEvent;
import com.bitmovin.player.api.media.video.quality.VideoQuality;
import com.bitmovin.player.api.source.Source;

import java.util.HashMap;

public class AdobeMediaAnalyticsTracker {

    private final String TAG = "AdobeAnalyticsTracker";
    private Player bitmovinPlayer;
    private AdobeMediaAnalyticsDataOverride adobeEventsDataOverride;
    private MediaTracker mediaTracker;
    private BitmovinPlayerEventsWrapper bitmovinPlayerEventsObj;
    private AdobeMediaAnalyticsEventsWrapper bitmovinAdobeEventsObj;
    private BitmovinPlayerEventHandler bitmovinPlayerEventHandler;
    private HashMap<String, Object> mediaObject;
    private HashMap<String, String> contextData;
    private HashMap<String, Object> adBreakObject;
    private HashMap<String, Object> adObject;
    private Source activeSource;
    private long activeAdPosition = 0L;
    private Boolean isSessionActive = false;

    private class BitmovinPlayerEventHandler implements
            BitmovinPlayerEventsWrapper.SourceLoadedEventHandler,
            BitmovinPlayerEventsWrapper.ReadyEventHandler,
            BitmovinPlayerEventsWrapper.PlayEventHandler,
            BitmovinPlayerEventsWrapper.PlayingEventHandler,
            BitmovinPlayerEventsWrapper.PausedEventHandler,
            BitmovinPlayerEventsWrapper.SeekStartEventHandler,
            BitmovinPlayerEventsWrapper.SeekCompleteEventHandler,
            BitmovinPlayerEventsWrapper.PlaybackFinishedEventHandler,
            BitmovinPlayerEventsWrapper.BufferStartEventHandler,
            BitmovinPlayerEventsWrapper.BufferCompleteEventHandler,
            BitmovinPlayerEventsWrapper.TimeChangedEventHandler,
            BitmovinPlayerEventsWrapper.PlayerErrorEventHandler,
            BitmovinPlayerEventsWrapper.SourceErrorEventHandler,
            BitmovinPlayerEventsWrapper.SourceUnloadedEventHandler,
            BitmovinPlayerEventsWrapper.VideoPlaybackQualityChangedEventHandler,
            BitmovinPlayerEventsWrapper.AdBreakStartedEventHandler,
            BitmovinPlayerEventsWrapper.AdBreakFinishedEventHandler,
            BitmovinPlayerEventsWrapper.AdStartedEventHandler,
            BitmovinPlayerEventsWrapper.AdFinishedEventHandler,
            BitmovinPlayerEventsWrapper.AdSkippedEventHandler,
            BitmovinPlayerEventsWrapper.AdErrorEventHandler,
            BitmovinPlayerEventsWrapper.AudioMutedEventHandler,
            BitmovinPlayerEventsWrapper.AudioUnmutedEventHandler {

        private Player bitmovinPlayer;

        BitmovinPlayerEventHandler (Player player) {
            this.bitmovinPlayer = player;
        }

        @Override
        public void onSourceLoaded(SourceEvent.Loaded event) {
            Log.d(TAG, "onSourceLoadedEventHandler");

            // Event order is not guaranteed, especially between autoplay and manual playback start.
            // Adding this in a few events which could come first.
            setUpTrackingIfNoActiveSession();
        }

        @Override
        public void onReady(PlayerEvent.Ready event) {
            Log.d(TAG, "onReadyEventHandler");
            PlayerConfig playerConfig = bitmovinPlayer.getConfig();
            PlaybackConfig playbackConfig = playerConfig.getPlaybackConfig();
        }

        @Override
        public void onPlay(PlayerEvent.Play event) {
            Log.d(TAG, "onPlayEventHandler");

            // Event order is not guaranteed, especially between autoplay and manual playback start.
            // Adding this in a few events which could come first.
            setUpTrackingIfNoActiveSession();
        }

        @Override
        public void onPlaying(PlayerEvent.Playing event) {
            Log.d(TAG, "onPlayingEventHandler");
            bitmovinAdobeEventsObj.trackPlay();
        }

        @Override
        public void onPaused(PlayerEvent.Paused event) {
            Log.d(TAG, "onPaused");

            bitmovinAdobeEventsObj.trackPause();
        }

        @Override
        public void onSeekStart(PlayerEvent.Seek event) {
            Log.d(TAG, "onSeekStart");

            bitmovinAdobeEventsObj.trackSeekStart();
        }

        @Override
        public void onSeekComplete(PlayerEvent.Seeked event) {
            Log.d(TAG, "onSeekComplete");
            bitmovinAdobeEventsObj.trackSeekComplete();
        }

        @Override
        public void onTimeChanged(PlayerEvent.TimeChanged event) {
            Log.d(TAG, "onTimeChanged");
            bitmovinAdobeEventsObj.updateCurrentPlayhead(event.getTime());
        }

        @Override
        public void onBufferStart(PlayerEvent.StallStarted event) {
            Log.d(TAG, "onBufferStart");
            bitmovinAdobeEventsObj.trackBufferStart();
        }

        @Override
        public void onBufferComplete(PlayerEvent.StallEnded event) {
            Log.d(TAG, "onBufferComplete");
            bitmovinAdobeEventsObj.trackBufferComplete();
        }

        @Override
        public void onVideoPlaybackQualityChanged(PlayerEvent.VideoPlaybackQualityChanged event) {
            // Event order is not guaranteed, especially between autoplay and manual playback start.
            // Adding this in a few events which could come first.
            setUpTrackingIfNoActiveSession();

            Log.d(TAG, "onVideoPlaybackQualityChanged");
            VideoQuality newVideoQuality = event.getNewVideoQuality();
            long bitrate = (newVideoQuality != null) ? newVideoQuality.getBitrate() : 0;
            double frameRate = (newVideoQuality != null) ? newVideoQuality.getFrameRate() : 0;
            long droppedVideoFrames = bitmovinPlayer.getDroppedVideoFrames();

            HashMap<String, Object> qoeObject = bitmovinAdobeEventsObj.createQoeObject(bitrate, 0.0, frameRate, droppedVideoFrames);
            bitmovinAdobeEventsObj.trackBitrateChange(qoeObject);
        }

        @Override
        public void onAudioMuted(PlayerEvent.Muted event) {
            Log.d(TAG, "onAudioMuted");

            bitmovinAdobeEventsObj.trackMute();
        }

        @Override
        public void onAudioUnmuted(PlayerEvent.Unmuted event) {
            Log.d(TAG, "onAudioUnmuted");

            bitmovinAdobeEventsObj.trackUnmute();
        }

        @Override
        public void onPlayerError(PlayerEvent.Error event) {
            Log.d(TAG, "onPlayerError");

            bitmovinAdobeEventsObj.trackError(event.getMessage());
        }

        @Override
        public void onSourceError(SourceEvent.Error event) {
            Log.d(TAG, "onSourceError");

            bitmovinAdobeEventsObj.trackError(event.getMessage());
        }

        @Override
        public void onPlaybackFinished(PlayerEvent.PlaybackFinished event) {
            Log.d(TAG, "onPlaybackFinishedEventHandler");

            bitmovinAdobeEventsObj.trackComplete();

            // remove PLAY_EVENT handler to avoid sending duplicate complete events
            bitmovinPlayerEventsObj.off(BitmovinPlayerEventsWrapper.PLAYBACK_FINISHED_EVENT);
        }

        @Override
        public void onSourceUnloaded(SourceEvent.Unloaded event) {
            // Destroying the player will also implicitly trigger the onSourceUnloaded event
            // Hence there is no need to track onPlayerDestroyed separately
            Log.d(TAG, "onSourceUnloaded");
            bitmovinAdobeEventsObj.trackSessionEnd();
            isSessionActive = false;
        }

        @Override
        public void onAdBreakStarted (PlayerEvent.AdBreakStarted event) {
            Log.d(TAG, "onAdBreakStarted");

            // do not send AdBreak started event if adBreak object is null
            if (event.getAdBreak() == null) {
                return;
            }

            String adBreakId = adobeEventsDataOverride.getAdBreakId(this.bitmovinPlayer, event);
            long adBreakPosition = adobeEventsDataOverride.getAdBreakPosition(this.bitmovinPlayer, event);
            double adBreakStartTime = event.getAdBreak().getScheduleTime();
            adBreakObject = bitmovinAdobeEventsObj.createAdBreakObject(adBreakId, adBreakPosition, adBreakStartTime);
            bitmovinAdobeEventsObj.trackAdBreakStarted(adBreakObject);
            activeAdPosition = 0L;

            // remove TIME_CHANGED handler to avoid sending play head updates during Ad playback
            bitmovinPlayerEventsObj.off(BitmovinPlayerEventsWrapper.TIME_CHANGED_EVENT);
        }

        @Override
        public void onAdBreakFinished (PlayerEvent.AdBreakFinished event) {
            Log.d(TAG, "onAdBreakFinished");

            // do not send AdBreak completed event if adBreak object is null
            if (event.getAdBreak() == null) {
                return;
            }

            bitmovinAdobeEventsObj.trackAdBreakComplete();
            activeAdPosition = 0L;

            // add TIME_CHANGED handler to resume sending play head updates during Main content playback
            bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.TIME_CHANGED_EVENT, bitmovinPlayerEventHandler);
        }

        @Override
        public void onAdStarted (PlayerEvent.AdStarted event) {
            Log.d(TAG, "onAdStarted");

            String adName = adobeEventsDataOverride.getAdName(this.bitmovinPlayer, event);
            String adId = adobeEventsDataOverride.getAdId(this.bitmovinPlayer, event);
            long adPosition = ++activeAdPosition;
            double adLength = event.getDuration();
            adObject = bitmovinAdobeEventsObj.createAdObject(adName, adId, adPosition, adLength);
            bitmovinAdobeEventsObj.trackAdStarted(adObject, null);
        }

        @Override
        public void onAdFinished (PlayerEvent.AdFinished event) {
            Log.d(TAG, "onAdFinished");
            bitmovinAdobeEventsObj.trackAdComplete();
        }

        @Override
        public void onAdSkipped (PlayerEvent.AdSkipped event) {
            Log.d(TAG, "onAdSkipped");
            bitmovinAdobeEventsObj.trackAdSkip();
        }

        @Override
        public void onAdError (PlayerEvent.AdError event) {
            Log.d(TAG, "onAdError");
        }

        public void setUpTrackingIfNoActiveSession() {
            if (isSessionActive) {
                return;
            }

            Log.d(TAG, "Set up tracking session");

            activeSource = bitmovinPlayer.getSource();

            String mediaName = adobeEventsDataOverride.getMediaName(bitmovinPlayer, activeSource);
            String mediaId = adobeEventsDataOverride.getMediaUid(bitmovinPlayer, activeSource);
            contextData = adobeEventsDataOverride.getMediaContextData(bitmovinPlayer);
            mediaObject = bitmovinAdobeEventsObj.createMediaObject(mediaName, mediaId,
                    bitmovinPlayer.getDuration(),
                    bitmovinPlayer.isLive()? MediaConstants.StreamType.LIVE:MediaConstants.StreamType.VOD);

            bitmovinAdobeEventsObj.trackSessionStart(mediaObject, contextData);
            isSessionActive = true;

            // add PLAYBACK_FINISHED_EVENT to when a session is started successfully
            // this is required as same will be removed after receiving PLAYBACK_FINISHED_EVENT
            // but will be required again for playback reload case after finished ones
            bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.PLAYBACK_FINISHED_EVENT, bitmovinPlayerEventHandler);
        }
    }

    public void createTracker(Player bitmovinPlayer, AdobeMediaAnalyticsDataOverride customDataOverride) {
        this.createTracker(bitmovinPlayer, customDataOverride, null);
    }

    public void createTracker(Player bitmovinPlayer, AdobeMediaAnalyticsDataOverride customDataOverride, AdobeMediaAnalyticsEventsWrapper eventsWrapper) {
        if (bitmovinPlayer == null) {
            throw new IllegalArgumentException("BitmovinPlayer argument cannot be null");
        }

        // save the Bitmovin player instance
        this.bitmovinPlayer = bitmovinPlayer;

        // save the data override object
        if (customDataOverride != null) {
            this.adobeEventsDataOverride = customDataOverride;
        } else {
            this.adobeEventsDataOverride = new AdobeMediaAnalyticsDataOverride();
        }

        // instantiate Adobe Media analytics tracker object
        this.mediaTracker = Media.createTracker();

        // instantiate Bitmovin player events handler object
        this.bitmovinPlayerEventsObj = new BitmovinPlayerEventsWrapper(this.bitmovinPlayer);

        if (eventsWrapper == null) {
            // instantiate Adobe Media analytics events wrapper object
            this.bitmovinAdobeEventsObj = new AdobeMediaAnalyticsEventsWrapper(this.mediaTracker);
        } else {
            this.bitmovinAdobeEventsObj = eventsWrapper;
        }

        this.contextData = null;

        // instantiate Bitmovin player event handler and register it with events wrapper
        this.bitmovinPlayerEventHandler = new BitmovinPlayerEventHandler(this.bitmovinPlayer);

        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.SOURCE_LOADED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.READY_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.PLAY_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.PLAYING_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.PAUSED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.SEEK_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.SEEKED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.TIME_CHANGED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.VIDEO_PLAYBACK_QUALITY_CHANGED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.AUDIO_MUTED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.AUDIO_UNMUTED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.PLAYBACK_FINISHED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.PLAYER_ERROR_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.SOURCE_ERROR_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.SOURCE_UNLOADED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.AD_BREAK_STARTED, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.AD_BREAK_FINISHED, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.AD_STARTED, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.AD_FINISHED, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.AD_SKIPPED, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.on(BitmovinPlayerEventsWrapper.AD_ERROR, this.bitmovinPlayerEventHandler);
    }

    public void destroyTracker() {

        // remove event listeners
        if (bitmovinPlayerEventsObj != null) {
            bitmovinPlayerEventsObj.removeAllEventHandlers();
        }

        bitmovinPlayer = null;
        adobeEventsDataOverride = null;
        mediaTracker = null;
        bitmovinPlayerEventsObj = null;
        bitmovinAdobeEventsObj = null;
        bitmovinPlayerEventHandler = null;
        mediaObject = null;
        contextData = null;
        adBreakObject = null;
        adObject = null;
        activeAdPosition = 0L;
        activeSource = null;
        isSessionActive = false;
    }

}
