package com.bitmovin.player.integration.adobeanalytics;

import android.util.Log;

import com.adobe.marketing.mobile.Media;
import com.adobe.marketing.mobile.MediaConstants;
import com.adobe.marketing.mobile.MediaTracker;
import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.config.media.SourceItem;
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
import com.bitmovin.player.api.event.data.AdSkippedEvent;
import com.bitmovin.player.api.event.data.AdErrorEvent;
import com.bitmovin.player.config.quality.VideoQuality;

import java.util.HashMap;

public class AdobeMediaAnalyticsTracker {

    private final String TAG = "AdobeMediaAnalyticsTracker";
    private BitmovinPlayer bitmovinPlayer;
    private AdobeMediaAnalyticsDataOverride adobeEventsDataOverride;
    private MediaTracker mediaTracker;
    private BitmovinPlayerEventsWrapper bitmovinPlayerEventsObj;
    private AdobeMediaAnalyticsEventsWrapper bitmovinAdobeEventsObj;
    private BitmovinPlayerEventHandler bitmovinPlayerEventHandler;
    private HashMap<String, Object> mediaObject;
    private HashMap<String, String> contextData;
    private HashMap<String, Object> adBreakObject;
    private HashMap<String, Object> adObject;
    private SourceItem activeSourceItem;
    private long activeAdPosition = 0L;

    private class BitmovinPlayerEventHandler implements BitmovinPlayerEventsWrapper.SourceLoadedEventHandler, BitmovinPlayerEventsWrapper.ReadyEventHandler,
            BitmovinPlayerEventsWrapper.PlayEventHandler,  BitmovinPlayerEventsWrapper.PlayingEventHandler, BitmovinPlayerEventsWrapper.PausedEventHandler,
            BitmovinPlayerEventsWrapper.SeekStartEventHandler, BitmovinPlayerEventsWrapper.SeekCompleteEventHandler, BitmovinPlayerEventsWrapper.PlaybackFinishedEventHandler,
            BitmovinPlayerEventsWrapper.BufferStartEventHandler, BitmovinPlayerEventsWrapper.BufferCompleteEventHandler, BitmovinPlayerEventsWrapper.TimeChangedEventHandler,
            BitmovinPlayerEventsWrapper.ErrorEventHandler, BitmovinPlayerEventsWrapper.SourceUnloadedEventHandler, BitmovinPlayerEventsWrapper.PlayerDestroyedEventHandler,
            BitmovinPlayerEventsWrapper.VideoPlaybackQualityChangedEventHandler, BitmovinPlayerEventsWrapper.AdBreakStartedEventHandler, BitmovinPlayerEventsWrapper.AdBreakFinishedEventHandler,
            BitmovinPlayerEventsWrapper.AdStartedEventHandler, BitmovinPlayerEventsWrapper.AdFinishedEventHandler,  BitmovinPlayerEventsWrapper.AdSkippedEventHandler,
            BitmovinPlayerEventsWrapper.AdErrorEventHandler, BitmovinPlayerEventsWrapper.AudioMutedEventHandler, BitmovinPlayerEventsWrapper.AudioUnmutedEventHandler {

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

            // get currently playing souce item
            PlayerConfiguration playerConfig = bitmovinPlayer.getConfig();
            activeSourceItem = playerConfig.getSourceItem();

            String mediaName = adobeEventsDataOverride.getMediaName(bitmovinPlayer, activeSourceItem);
            String mediaId = adobeEventsDataOverride.getMediaUid(bitmovinPlayer, activeSourceItem);
            contextData = adobeEventsDataOverride.getMediaContextData(bitmovinPlayer);
            mediaObject = bitmovinAdobeEventsObj.createMediaObject(mediaName, mediaId,
                    bitmovinPlayer.getDuration(),
                    bitmovinPlayer.isLive()? MediaConstants.StreamType.LIVE:MediaConstants.StreamType.VOD);

            bitmovinAdobeEventsObj.trackSessionStart(mediaObject, contextData);

            // remove PLAY_EVENT handler to avoid sending duplicate sessionStart events
            bitmovinPlayerEventsObj.removeEventHandler(BitmovinPlayerEventsWrapper.PLAY_EVENT);

            // add PLAYBACK_FINISHED_EVENT to when a session is started successfully
            // this is required as same will be removed after receiving PLAYBACK_FINISHED_EVENT
            // but will be required again for playback reload case after finished ones
            bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.PLAYBACK_FINISHED_EVENT, bitmovinPlayerEventHandler);
        }

        @Override
        public void onPlaying(PlayingEvent event) {
            Log.d(TAG, "onPlayingEventHandler");
            bitmovinAdobeEventsObj.trackPlay();
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
            VideoQuality newVideoQuality = event.getNewVideoQuality();
            long bitrate = (newVideoQuality != null) ? newVideoQuality.getBitrate() : 0;
            double frameRate = (newVideoQuality != null) ? newVideoQuality.getFrameRate() : 0;
            long droppedVideoFrames = bitmovinPlayer.getDroppedVideoFrames();

            HashMap<String, Object> qoeObject = bitmovinAdobeEventsObj.createQoeObject(bitrate, 0.0, frameRate, droppedVideoFrames);
            bitmovinAdobeEventsObj.trackBitrateChange(qoeObject);
        }

        @Override
        public void onAudioMuted() {
            Log.d(TAG, "onAudioMuted");

            bitmovinAdobeEventsObj.trackMute();
        }

        @Override
        public void onAudioUnmuted() {
            Log.d(TAG, "onAudioUnmuted");

            bitmovinAdobeEventsObj.trackUnmute();
        }

        @Override
        public void onError(ErrorEvent event) {
            Log.d(TAG, "onError");

            bitmovinAdobeEventsObj.trackError(event.getMessage());
        }

        @Override
        public void onPlaybackFinished(PlaybackFinishedEvent event) {
            Log.d(TAG, "onPlaybackFinishedEventHandler");

            bitmovinAdobeEventsObj.trackComplete();

            // remove PLAY_EVENT handler to avoid sending duplicate complete events
            bitmovinPlayerEventsObj.removeEventHandler(BitmovinPlayerEventsWrapper.PLAYBACK_FINISHED_EVENT);
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
        public void onAdBreakStarted (AdBreakStartedEvent event) {
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
            bitmovinPlayerEventsObj.removeEventHandler(BitmovinPlayerEventsWrapper.TIME_CHANGED_EVENT);
        }

        @Override
        public void onAdBreakFinished (AdBreakFinishedEvent event) {
            Log.d(TAG, "onAdBreakFinished");

            // do not send AdBreak completed event if adBreak object is null
            if (event.getAdBreak() == null) {
                return;
            }

            bitmovinAdobeEventsObj.trackAdBreakComplete();
            activeAdPosition = 0L;

            // add TIME_CHANGED handler to resume sending play head updates during Main content playback
            bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.TIME_CHANGED_EVENT, bitmovinPlayerEventHandler);
        }

        @Override
        public void onAdStarted (AdStartedEvent event) {
            Log.d(TAG, "onAdStarted");

            String adName = adobeEventsDataOverride.getAdName(this.bitmovinPlayer, event);
            String adId = adobeEventsDataOverride.getAdId(this.bitmovinPlayer, event);
            long adPosition = ++activeAdPosition;
            double adLength = event.getDuration();
            adObject = bitmovinAdobeEventsObj.createAdObject(adName, adId, adPosition, adLength);
            bitmovinAdobeEventsObj.trackAdStarted(adObject, null);
        }

        @Override
        public void onAdFinished (AdFinishedEvent event) {
            Log.d(TAG, "onAdFinished");
            bitmovinAdobeEventsObj.trackAdComplete();
        }

        @Override
        public void onAdSkipped (AdSkippedEvent event) {
            Log.d(TAG, "onAdSkipped");
            bitmovinAdobeEventsObj.trackAdSkip();
        }

        @Override
        public void onAdError (AdErrorEvent event) {
            Log.d(TAG, "onAdError");
        }
    }

    public void createTracker(BitmovinPlayer bitmovinPlayer, AdobeMediaAnalyticsDataOverride customDataOverride) {
        this.createTracker(bitmovinPlayer, customDataOverride, null);
    }

    public void createTracker(BitmovinPlayer bitmovinPlayer, AdobeMediaAnalyticsDataOverride customDataOverride, AdobeMediaAnalyticsEventsWrapper eventsWrapper) {
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

        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.SOURCE_LOADED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.READY_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.PLAY_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.PLAYING_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.PAUSED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.SEEK_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.SEEKED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.TIME_CHANGED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.VIDEO_PLAYBACK_QUALITY_CHANGED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.AUDIO_MUTED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.AUDIO_UNMUTED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.PLAYBACK_FINISHED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.ERROR_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.SOURCE_UNLOADED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.PLAYER_DESTROYED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.AD_BREAK_STARTED, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.AD_BREAK_FINISHED, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.AD_STARTED, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.AD_FINISHED, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.AD_SKIPPED, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addEventHandler(BitmovinPlayerEventsWrapper.AD_ERROR, this.bitmovinPlayerEventHandler);
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
        activeSourceItem = null;
    }

}
