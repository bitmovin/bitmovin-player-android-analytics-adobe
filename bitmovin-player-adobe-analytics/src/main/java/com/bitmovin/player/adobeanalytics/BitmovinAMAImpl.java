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

import java.util.HashMap;

public class BitmovinAMAImpl {

    private final String TAG = "BitmovinAMAImpl";
    private BitmovinPlayer bitmovinPlayer;
    private MediaTracker mediaTracker;
    private BitmovinPlayerEventsWrapper bitmovinPlayerEventsObj;
    private BitmovinAMAEventsWrapper bitmovinAdobeEventsObj;
    private PlayerPlayerEventHandler bitmovinPlayerEventHandler;
    private BitmovinAMADataMapper amaDataMapperObject;

    private class PlayerPlayerEventHandler implements BitmovinPlayerEventsWrapper.SourceLoadedCB, BitmovinPlayerEventsWrapper.ReadyCB,
            BitmovinPlayerEventsWrapper.PlayCB,  BitmovinPlayerEventsWrapper.PlayingCB, BitmovinPlayerEventsWrapper.PausedCB,
            BitmovinPlayerEventsWrapper.SeekStartedCB, BitmovinPlayerEventsWrapper.SeekEndedCB, BitmovinPlayerEventsWrapper.PlaybackFinishedCB,
            BitmovinPlayerEventsWrapper.ErrorCB, BitmovinPlayerEventsWrapper.SourceUnloadedCB, BitmovinPlayerEventsWrapper.PlayerDestroyedCB {

        private BitmovinPlayer bitmovinPlayer;

        PlayerPlayerEventHandler (BitmovinPlayer player) {
            this.bitmovinPlayer = player;
        }

        @Override
        public void onSourceLoaded(SourceLoadedEvent event) {
            Log.d(TAG, "onSourceLoadedCB");

            HashMap<String, Object> mediaObject = amaDataMapperObject.createMediaObject("test", "testid",
                    this.bitmovinPlayer.getDuration(),
                    MediaConstants.StreamType.VOD);

            bitmovinAdobeEventsObj.trackSessionStart(mediaObject, null);
        }

        @Override
        public void onReady(ReadyEvent event) {
            Log.d(TAG, "onReadyCB");
        }

        @Override
        public void onPlay(PlayEvent event) {
            Log.d(TAG, "onPlayCB");

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
        public void onSeekStarted(SeekEvent event) {
            Log.d(TAG, "onSeekStarted");
        }

        @Override
        public void onSeekEnded(SeekedEvent event) {
            Log.d(TAG, "onSeekEnded");
        }

        @Override
        public void onError(ErrorEvent event) {
            Log.d(TAG, "onError");

            bitmovinAdobeEventsObj.trackError(event.getMessage());
        }

        @Override
        public void onSourceUnloaded(SourceUnloadedEvent event) {
            Log.d(TAG, "onSourceUnloaded");
        }

        @Override
        public void onPlayerDestroyed(DestroyEvent event) {
            Log.d(TAG, "onPlayerDestroyed");
            bitmovinAdobeEventsObj.trackSessionEnd();
        }
    }

    public BitmovinAMAImpl(BitmovinPlayer bitmovinPlayer) {

        this.bitmovinPlayer = bitmovinPlayer;

        // instantiate Adobe Media analytics tracker object
        this.mediaTracker = Media.createTracker();

        // instantiate Bitmovin player events handler object
        this.bitmovinPlayerEventsObj = new BitmovinPlayerEventsWrapper(this.bitmovinPlayer);

        amaDataMapperObject = new BitmovinAMADataMapper ();

        // instantiate Adobe Media analytics events wrapper object
        this.bitmovinAdobeEventsObj = new BitmovinAMAEventsWrapper(this.mediaTracker);

        this.bitmovinPlayerEventHandler = new PlayerPlayerEventHandler(this.bitmovinPlayer);

        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.SOURCE_LOADED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.READY_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.PLAY_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.PLAYING_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.PAUSED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.SEEK_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.SEEKED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.PLAYBACK_FINISHED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.ERROR_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.SOURCE_UNLOADED_EVENT, this.bitmovinPlayerEventHandler);
        this.bitmovinPlayerEventsObj.addUpstreamCallback(BitmovinPlayerEventsWrapper.PLAYER_DESTROYED_EVENT, this.bitmovinPlayerEventHandler);
    }

    public void destroyTracker() {
        bitmovinPlayerEventsObj.removeListeners();
    }

}
