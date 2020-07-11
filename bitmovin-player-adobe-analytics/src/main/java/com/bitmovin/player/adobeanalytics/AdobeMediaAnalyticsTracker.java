package com.bitmovin.player.adobeanalytics;

import android.util.Log;

import com.adobe.marketing.mobile.Media;
import com.adobe.marketing.mobile.MediaConstants;
import com.adobe.marketing.mobile.MediaTracker;
import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.config.PlayerConfiguration;
import com.bitmovin.player.config.PlaybackConfiguration;
import com.bitmovin.player.api.event.data.DestroyEvent;
import com.bitmovin.player.api.event.data.ErrorEvent;
import com.bitmovin.player.api.event.data.PausedEvent;
import com.bitmovin.player.api.event.data.PlayEvent;
import com.bitmovin.player.api.event.data.PlaybackFinishedEvent;
import com.bitmovin.player.api.event.data.PlayingEvent;
import com.bitmovin.player.api.event.data.ReadyEvent;
import com.bitmovin.player.api.event.data.SeekEvent;
import com.bitmovin.player.api.event.data.SeekedEvent;
import com.bitmovin.player.api.event.data.SourceLoadedEvent;
import com.bitmovin.player.api.event.data.SourceUnloadedEvent;
import com.bitmovin.player.api.event.data.StallEndedEvent;
import com.bitmovin.player.api.event.data.StallStartedEvent;
import com.bitmovin.player.api.event.data.TimeChangedEvent;
import com.bitmovin.player.api.event.data.VideoPlaybackQualityChangedEvent;
import com.bitmovin.player.api.event.data.AdBreakStartedEvent;
import com.bitmovin.player.api.event.data.AdBreakFinishedEvent;
import com.bitmovin.player.api.event.data.AdStartedEvent;
import com.bitmovin.player.api.event.data.AdFinishedEvent;
import com.bitmovin.player.api.event.data.AdErrorEvent;

import java.util.HashMap;

public class AdobeMediaAnalyticsTracker {

    private final String TAG = "AdobeMediaAnalyticsTracker";
    private BitmovinPlayer bitmovinPlayer;
    private AdobeMediaAnalyticsDataOverrides amaDataOverride;
    private MediaTracker mediaTracker;
    private BitmovinPlayerEventsWrapper bitmovinPlayerEventsObj;
    private AdobeMediaAnalyticsEventsWrapper bitmovinAdobeEventsObj;
    private BitmovinPlayerEventHandler bitmovinPlayerEventHandler;
    private HashMap<String, Object> mediaObject;
    private HashMap<String, String> contextData;
    private HashMap<String, Object> adBreakObject;
    private HashMap<String, Object> adObject;

    public interface AdobeMediaAnalyticsDataOverrides {
        HashMap<String, String> contextDataOverride (BitmovinPlayer player);
        String mediaNameOverride (BitmovinPlayer player);
        String mediaUidOverride (BitmovinPlayer player);
    }

    private class BitmovinPlayerEventHandler implements BitmovinPlayerEventsWrapper.SourceLoadedEventHandler, BitmovinPlayerEventsWrapper.ReadyEventHandler,
            BitmovinPlayerEventsWrapper.PlayEventHandler,  BitmovinPlayerEventsWrapper.PlayingEventHandler, BitmovinPlayerEventsWrapper.PausedEventHandler,
            BitmovinPlayerEventsWrapper.SeekStartEventHandler, BitmovinPlayerEventsWrapper.SeekCompleteEventHandler, BitmovinPlayerEventsWrapper.PlaybackFinishedEventHandler,
            BitmovinPlayerEventsWrapper.BufferStartEventHandler, BitmovinPlayerEventsWrapper.BufferCompleteEventHandler, BitmovinPlayerEventsWrapper.TimeChangedEventHandler,
            BitmovinPlayerEventsWrapper.ErrorEventHandler, BitmovinPlayerEventsWrapper.SourceUnloadedEventHandler, BitmovinPlayerEventsWrapper.PlayerDestroyedEventHandler,
            BitmovinPlayerEventsWrapper.VideoPlaybackQualityChangedEventHandler, BitmovinPlayerEventsWrapper.AdBreakStartedEventHandler, BitmovinPlayerEventsWrapper.AdBreakFinishedEventHandler,
            BitmovinPlayerEventsWrapper.AdStartedEventHandler, BitmovinPlayerEventsWrapper.AdFinishedEventHandler, BitmovinPlayerEventsWrapper.AdErrorEventHandler {

        private BitmovinPlayer bitmovinPlayer;

        BitmovinPlayerEventHandler (BitmovinPlayer player) {
            this.bitmovinPlayer = player;
        }

        @Override
        public void onSourceLoaded(SourceLoadedEvent event) {
            Log.d(TAG, "onSourceLoadedEventHandler");
        }

        @Override
        public void onReady(ReadyEvent event) {
            Log.d(TAG, "onReadyEventHandler");
            PlayerConfiguration playerConfig = bitmovinPlayer.getConfig();
            PlaybackConfiguration playbackConfig = playerConfig.getPlaybackConfiguration();

            // calling onPlay as this callback will not be triggered for autoPlay case
            if (playbackConfig.isAutoplayEnabled()) {
                onPlay(null);
            }
        }

        @Override
        public void onPlay(PlayEvent event) {
            Log.d(TAG, "onPlayEventHandler");
            String mediaName = "";
            String mediaId = "";
            if (amaDataOverride != null) {
                mediaName = amaDataOverride.mediaNameOverride(bitmovinPlayer);
                mediaId = amaDataOverride.mediaUidOverride(bitmovinPlayer);
                contextData = amaDataOverride.contextDataOverride(bitmovinPlayer);
            }
            mediaObject = bitmovinAdobeEventsObj.createMediaObject(mediaName, mediaId,
                    bitmovinPlayer.getDuration(),
                    bitmovinPlayer.isLive()? MediaConstants.StreamType.LIVE:MediaConstants.StreamType.VOD);

            bitmovinAdobeEventsObj.trackSessionStart(mediaObject, contextData);
            bitmovinPlayerEventsObj.removeEventHandler(BitmovinPlayerEventsWrapper.PLAY_EVENT);
        }

        @Override
        public void onPlaying(PlayingEvent event) {
            Log.d(TAG, "onPlayingEventHandler");
            bitmovinAdobeEventsObj.trackPlay();
        }

        @Override
        public void onPlaybackFinished(PlaybackFinishedEvent event) {
            Log.d(TAG, "onPlaybackFinishedEventHandler");

            bitmovinAdobeEventsObj.trackComplete();
            bitmovinAdobeEventsObj.trackSessionEnd();
        }

        @Override
        public void onPaused(PausedEvent event) {
            Log.d(TAG, "onPaused");

            bitmovinAdobeEventsObj.trackPause();
        }

        @Override
        public void onSeekStart(SeekEvent event) {
            Log.d(TAG, "onSeekStart");

            bitmovinAdobeEventsObj.trackSeekStart();
        }

        @Override
        public void onSeekComplete(SeekedEvent event) {
            Log.d(TAG, "onSeekComplete");
            bitmovinAdobeEventsObj.trackSeekComplete();
        }

        @Override
        public void onError(ErrorEvent event) {
            Log.d(TAG, "onError");

            bitmovinAdobeEventsObj.trackError(event.getMessage());
        }

        @Override
        public void onSourceUnloaded(SourceUnloadedEvent event) {
            Log.d(TAG, "onSourceUnloaded");
            bitmovinAdobeEventsObj.trackSessionEnd();
        }

        @Override
        public void onPlayerDestroyed(DestroyEvent event) {
            Log.d(TAG, "onPlayerDestroyed");
            bitmovinAdobeEventsObj.trackSessionEnd();
        }

        @Override
        public void onTimeChanged(TimeChangedEvent event) {
            Log.d(TAG, "onTimeChanged");
            bitmovinAdobeEventsObj.updateCurrentPlayhead(event.getTime());
        }

        @Override
        public void onBufferStart(StallStartedEvent event) {
            Log.d(TAG, "onBufferStart");
            bitmovinAdobeEventsObj.trackBufferStart();
        }

        @Override
        public void onBufferComplete(StallEndedEvent event) {
            Log.d(TAG, "onBufferComplete");
            bitmovinAdobeEventsObj.trackBufferComplete();
        }

        @Override
        public void onVideoPlaybackQualityChanged(VideoPlaybackQualityChangedEvent event) {
            Log.d(TAG, "onVideoPlaybackQualityChanged");
            HashMap<String, Object> qoeObject = Media.createQoEObject(event.getNewVideoQuality().getBitrate(), 0, event.getNewVideoQuality().getFrameRate(), bitmovinPlayer.getDroppedVideoFrames());
            bitmovinAdobeEventsObj.trackBitrateChange(qoeObject);
        }

        @Override
        public void onAdBreakStarted (AdBreakStartedEvent event) {
            Log.d(TAG, "onAdBreakStarted");

        }

        @Override
        public void onAdBreakFinished (AdBreakFinishedEvent event) {
            Log.d(TAG, "onAdBreakFinished");
        }

        @Override
        public void onAdStarted (AdStartedEvent event) {
            Log.d(TAG, "onAdStarted");
        }

        @Override
        public void onAdFinished (AdFinishedEvent event) {
            Log.d(TAG, "onAdFinished");
        }

        @Override
        public void onAdError (AdErrorEvent event) {
            Log.d(TAG, "onAdError");
        }
    }

    public void createTracker(BitmovinPlayer bitmovinPlayer, AdobeMediaAnalyticsDataOverrides dataOverride) {
        this.createTracker(bitmovinPlayer, dataOverride, null);
    }

    public void createTracker(BitmovinPlayer bitmovinPlayer, AdobeMediaAnalyticsDataOverrides dataOverride, AdobeMediaAnalyticsEventsWrapper eventsWrapper) {
        if (bitmovinPlayer == null) {
            throw new IllegalArgumentException("BitmovinPlayer argument cannot be null");
        }

        // save the Bitmovin player instance
        this.bitmovinPlayer = bitmovinPlayer;

        // save the data override object
        this.amaDataOverride = dataOverride;

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

        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.SOURCE_LOADED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.READY_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.PLAY_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.PLAYING_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.PAUSED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.SEEK_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.SEEKED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.TIME_CHANGED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.VIDEO_PLAYBACK_QUALITY_CHANGED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.PLAYBACK_FINISHED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.ERROR_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.SOURCE_UNLOADED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.PLAYER_DESTROYED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.AD_BREAK_STARTED, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.AD_BREAK_FINISHED, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.AD_STARTED, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.AD_FINISHED, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.AD_ERROR, this.bitmovinPlayerEventHandler);
    }

    public void destroyTracker() {

        // remove event listeners
        if (bitmovinPlayerEventsObj != null) {
            bitmovinPlayerEventsObj.removeAllEventHandlers();
        }

        bitmovinPlayer = null;
        amaDataOverride = null;
        mediaTracker = null;
        bitmovinPlayerEventsObj = null;
        bitmovinAdobeEventsObj = null;
        bitmovinPlayerEventHandler = null;
        mediaObject = null;
        contextData = null;
    }

}
