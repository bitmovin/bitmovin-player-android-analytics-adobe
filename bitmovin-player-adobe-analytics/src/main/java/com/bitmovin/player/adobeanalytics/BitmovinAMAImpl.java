package com.bitmovin.player.adobeanalytics;

import android.util.Log;

import com.adobe.marketing.mobile.Media;
import com.adobe.marketing.mobile.MediaConstants;
import com.adobe.marketing.mobile.MediaTracker;
import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.api.event.data.PlayEvent;
import com.bitmovin.player.api.event.data.PlaybackFinishedEvent;
import com.bitmovin.player.api.event.data.PlayingEvent;
import com.bitmovin.player.api.event.data.ReadyEvent;
import com.bitmovin.player.api.event.data.SourceLoadedEvent;

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
            BitmovinPlayerEventsWrapper.PlayCB,  BitmovinPlayerEventsWrapper.PlayingCB, BitmovinPlayerEventsWrapper.PlaybackFinishedCB {

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
        }

        @Override
        public void onPlaybackFinished(PlaybackFinishedEvent event) {
            Log.d(TAG, "onPlaybackFinishedCB");

            bitmovinAdobeEventsObj.trackComplete();
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

        this.bitmovinPlayerEventsObj.addUpstreamCallback((BitmovinPlayerEventsWrapper.SourceLoadedCB)this.bitmovinPlayerEventHandler);
        //this.bitmovinPlayerEventsObj.addUpstreamCallback((BitmovinPlayerEventsWrapper.ReadyCB)this.bitmovinPlayerEventHandler);
        //this.bitmovinPlayerEventsObj.addUpstreamCallback((BitmovinPlayerEventsWrapper.PlayCB)this.bitmovinPlayerEventHandler);
        //this.bitmovinPlayerEventsObj.addUpstreamCallback((BitmovinPlayerEventsWrapper.PlayingCB)this.bitmovinPlayerEventHandler);
        //this.bitmovinPlayerEventsObj.addUpstreamCallback((BitmovinPlayerEventsWrapper.PlaybackFinishedCB)this.bitmovinPlayerEventHandler);

    }

    public void destroyTracker() {
        bitmovinAdobeEventsObj.trackSessionEnd();
        bitmovinPlayerEventsObj.removeListeners();
    }

}
