package com.bitmovin.player.adobeanalytics;

import android.util.Log;

import com.adobe.marketing.mobile.Media;
import com.adobe.marketing.mobile.MediaConstants;
import com.adobe.marketing.mobile.MediaTracker;
import com.bitmovin.player.BitmovinPlayer;
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

import java.util.HashMap;

public class BitmovinAMATracker {

    private final String TAG = "BitmovinAMATracker";
    private BitmovinPlayer bitmovinPlayer;
    private BitmovinAMADataOverrides amaDataOverride;
    private MediaTracker mediaTracker;
    private BitmovinPlayerEventsWrapper bitmovinPlayerEventsObj;
    private BitmovinAMAEventsWrapper bitmovinAdobeEventsObj;
    private BitmovinPlayerEventHandler bitmovinPlayerEventHandler;
    private HashMap<String, Object> mediaObject;
    private HashMap<String, String> contextData;

    public interface BitmovinAMADataOverrides {
        HashMap<String, String> contextDataOverride (BitmovinPlayer player);
        String mediaNameOverride (BitmovinPlayer player);
        String mediaUidOverride (BitmovinPlayer player);
    }

    private class BitmovinPlayerEventHandler implements BitmovinPlayerEventsWrapper.SourceLoadedCB, BitmovinPlayerEventsWrapper.ReadyCB,
            BitmovinPlayerEventsWrapper.PlayCB,  BitmovinPlayerEventsWrapper.PlayingCB, BitmovinPlayerEventsWrapper.PausedCB,
            BitmovinPlayerEventsWrapper.SeekStartCB, BitmovinPlayerEventsWrapper.SeekCompleteCB, BitmovinPlayerEventsWrapper.PlaybackFinishedCB,
            BitmovinPlayerEventsWrapper.BufferStartCB, BitmovinPlayerEventsWrapper.BufferCompleteCB, BitmovinPlayerEventsWrapper.TimeChangedCB,
            BitmovinPlayerEventsWrapper.ErrorCB, BitmovinPlayerEventsWrapper.SourceUnloadedCB, BitmovinPlayerEventsWrapper.PlayerDestroyedCB,
            BitmovinPlayerEventsWrapper.VideoPlaybackQualityChangedCB {

        private BitmovinPlayer bitmovinPlayer;

        BitmovinPlayerEventHandler (BitmovinPlayer player) {
            this.bitmovinPlayer = player;
        }

        @Override
        public void onSourceLoaded(SourceLoadedEvent event) {
            Log.d(TAG, "onSourceLoadedCB");

            String mediaName = "";
            String mediaId = "";
            if (amaDataOverride != null) {
                mediaName = amaDataOverride.mediaNameOverride(bitmovinPlayer);
                mediaId = amaDataOverride.mediaUidOverride(bitmovinPlayer);
                contextData = amaDataOverride.contextDataOverride(bitmovinPlayer);
            }

        }

        @Override
        public void onReady(ReadyEvent event) {
            Log.d(TAG, "onReadyCB");
        }

        @Override
        public void onPlay(PlayEvent event) {
            Log.d(TAG, "onPlayCB");
            mediaObject = bitmovinAdobeEventsObj.createMediaObject("testname", "testid",
                    bitmovinPlayer.getDuration(),
                    MediaConstants.StreamType.VOD);

            bitmovinAdobeEventsObj.trackSessionStart(mediaObject, null);
            bitmovinPlayerEventsObj.removeUpstreamCallback(BitmovinPlayerEventsWrapper.PLAY_EVENT);
        }

        @Override
        public void onPlaying(PlayingEvent event) {
            Log.d(TAG, "onPlayingCB");
            bitmovinAdobeEventsObj.trackPlay();
        }

        @Override
        public void onPlaybackFinished(PlaybackFinishedEvent event) {
            Log.d(TAG, "onPlaybackFinishedCB");

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
    }

    public void createTracker(BitmovinPlayer bitmovinPlayer, BitmovinAMADataOverrides dataOverride) {
        this.createTracker(bitmovinPlayer, dataOverride, null);
    }

    public void createTracker(BitmovinPlayer bitmovinPlayer, BitmovinAMADataOverrides dataOverride, BitmovinAMAEventsWrapper eventsWrapper) {
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
            this.bitmovinAdobeEventsObj = new BitmovinAMAEventsWrapper(this.mediaTracker);
        } else {
            this.bitmovinAdobeEventsObj = eventsWrapper;
        }

        // instantiate Bitmovin player event handler and register it with events wrapper
        this.bitmovinPlayerEventHandler = new BitmovinPlayerEventHandler(this.bitmovinPlayer);

        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.SOURCE_LOADED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.READY_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.PLAY_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.PLAYING_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.PAUSED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.SEEK_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.SEEKED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.TIME_CHANGED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.VIDEO_PLAYBACK_QUALITY_CHANGED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.PLAYBACK_FINISHED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.ERROR_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.SOURCE_UNLOADED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.PLAYER_DESTROYED_EVENT, this.bitmovinPlayerEventHandler);
    }

    public void destroyTracker() {

        // remove event listeners
        if (bitmovinPlayerEventsObj != null) {
            bitmovinPlayerEventsObj.removeListeners();
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
