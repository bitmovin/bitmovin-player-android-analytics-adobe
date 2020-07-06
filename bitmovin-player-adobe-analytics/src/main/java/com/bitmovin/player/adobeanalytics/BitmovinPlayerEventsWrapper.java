package com.bitmovin.player.adobeanalytics;

import android.util.Log;

import com.bitmovin.player.BitmovinPlayer;

import com.bitmovin.player.api.event.listener.EventListener;
import com.bitmovin.player.api.event.data.BitmovinPlayerEvent;
import com.bitmovin.player.api.event.listener.OnSourceLoadedListener;
import com.bitmovin.player.api.event.data.SourceLoadedEvent;
import com.bitmovin.player.api.event.listener.OnReadyListener;
import com.bitmovin.player.api.event.data.ReadyEvent;
import com.bitmovin.player.api.event.listener.OnPlayListener;
import com.bitmovin.player.api.event.data.PlayEvent;
import com.bitmovin.player.api.event.listener.OnPlayingListener;
import com.bitmovin.player.api.event.data.PlayingEvent;
import com.bitmovin.player.api.event.listener.OnPausedListener;
import com.bitmovin.player.api.event.data.PausedEvent;
import com.bitmovin.player.api.event.listener.OnRenderFirstFrameListener;
import com.bitmovin.player.api.event.listener.OnSeekListener;
import com.bitmovin.player.api.event.data.SeekEvent;
import com.bitmovin.player.api.event.listener.OnSeekedListener;
import com.bitmovin.player.api.event.data.SeekedEvent;
import com.bitmovin.player.api.event.listener.OnStallStartedListener;
import com.bitmovin.player.api.event.data.StallStartedEvent;
import com.bitmovin.player.api.event.listener.OnStallEndedListener;
import com.bitmovin.player.api.event.data.StallEndedEvent;
import com.bitmovin.player.api.event.listener.OnTimeChangedListener;
import com.bitmovin.player.api.event.listener.OnErrorListener;
import com.bitmovin.player.api.event.data.ErrorEvent;
import com.bitmovin.player.api.event.listener.OnPlaybackFinishedListener;
import com.bitmovin.player.api.event.data.PlaybackFinishedEvent;
import com.bitmovin.player.api.event.listener.OnSourceUnloadedListener;
import com.bitmovin.player.api.event.data.SourceUnloadedEvent;
import com.bitmovin.player.api.event.listener.OnDestroyListener;
import com.bitmovin.player.api.event.data.DestroyEvent;
import com.bitmovin.player.api.event.listener.OnAdBreakStartedListener;
import com.bitmovin.player.api.event.data.AdBreakStartedEvent;
import com.bitmovin.player.api.event.listener.OnAdBreakFinishedListener;
import com.bitmovin.player.api.event.data.AdBreakFinishedEvent;
import com.bitmovin.player.api.event.listener.OnAdStartedListener;
import com.bitmovin.player.api.event.data.AdStartedEvent;
import com.bitmovin.player.api.event.listener.OnAdFinishedListener;
import com.bitmovin.player.api.event.data.AdFinishedEvent;
import com.bitmovin.player.api.event.listener.OnAdSkippedListener;
import com.bitmovin.player.api.event.listener.OnAdErrorListener;
import com.bitmovin.player.api.event.data.AdErrorEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BitmovinPlayerEventsWrapper {

    private BitmovinPlayer bitmovinPlayer;
    private Map<Class, Object> bitmovinEventsMap = new HashMap<Class, Object>();
    private final String TAG = "BitmovinEventsWrapper";

    BitmovinPlayerEventsWrapper (BitmovinPlayer player) {
        this.bitmovinPlayer = player;
        // register all events in the default list
        registerListeners();
    }

    interface SourceLoadedCB {
        void onSourceLoaded (SourceLoadedEvent event);
    }

    interface ReadyCB {
        void onReady (ReadyEvent event);
    }

    interface PlayCB {
        void onPlay (PlayEvent event);
    }

    interface PlayingCB {
        void onPlaying (PlayingEvent event);
    }

    interface PausedCB {
        void onPaused (PausedEvent event);
    }

    interface SeekStartedCB {
        void onSeekStarted (SeekEvent event);
    }

    interface SeekEndedCB {
        void onSeekEnded (SeekedEvent event);
    }

    interface BufferingStartedCB {
        void onBufferingStarted (StallStartedEvent event);
    }

    interface BufferingEndedCB {
        void onBufferingEnded (StallEndedEvent event);
    }

    interface ErrorCB {
        void onError (ErrorEvent event);
    }

    interface PlaybackFinishedCB {
        void onPlaybackFinished (PlaybackFinishedEvent event);
    }

    interface SourceUnloadedCB {
        void onSourceUnloaded(SourceUnloadedEvent event);
    }

    interface PlayerDestroyedCB {
        void onPlayerDestroyed (DestroyEvent event);
    }

    interface AdBreakStartedCB {
        void onAdBreakStarted (AdBreakStartedEvent event);
    }

    interface AdBreakFinishedCB {
        void onAdBreakFinished (AdBreakFinishedEvent event);
    }

    interface AdStartedCB {
        void onAdStarted (AdStartedEvent event);
    }

    interface AdFinishedCB {
        void onAdFinished (AdFinishedEvent event);
    }

    interface AdErrorCB {
        void onAdError (AdErrorEvent event);
    }

    private class onSourceLoadedListener implements OnSourceLoadedListener {

        private SourceLoadedCB upstreamCB = null;
        public void on (SourceLoadedCB callback) {
            upstreamCB = callback;
        }
        public void off () {
            upstreamCB = null;
        }

        @Override
        public void onSourceLoaded(SourceLoadedEvent event) {
            Log.d(TAG, "onSourceLoaded");
            if (upstreamCB != null) {
                upstreamCB.onSourceLoaded(event);
            }
        }

    }

    private class onReadyListener implements OnReadyListener {

        private ReadyCB upstreamCB = null;
        public void on (ReadyCB callback) {
            upstreamCB = callback;
        }
        public void off () {
            upstreamCB = null;
        }

        @Override
        public void onReady(ReadyEvent event) {
            Log.d(TAG, "onReady");
            if (upstreamCB != null) {
                upstreamCB.onReady(event);
            }
        }
    }

    private class onPlayListener implements OnPlayListener {

        private PlayCB upstreamCB = null;
        public void on (PlayCB callback) {
            upstreamCB = callback;
        }
        public void off () {
            upstreamCB = null;
        }

        @Override
        public void onPlay(PlayEvent event) {
            Log.d(TAG, "onPlay");
            if (upstreamCB != null) {
                upstreamCB.onPlay(event);
            }
        }
    }

    private class onPlayingListener implements OnPlayingListener {

        private PlayingCB upstreamCB = null;
        public void on (PlayingCB callback) {
            upstreamCB = callback;
        }
        public void off () {
            upstreamCB = null;
        }

        @Override
        public void onPlaying(PlayingEvent event) {
            Log.d(TAG, "onPlaying");
            if (upstreamCB != null) {
                upstreamCB.onPlaying(event);
            }
        }
    }

    private class onPausedListener implements OnPausedListener {

        private PausedCB upstreamCB = null;
        public void on (PausedCB callback) {
            upstreamCB = callback;
        }
        public void off () {
            upstreamCB = null;
        }

        @Override
        public void onPaused(PausedEvent event) {
            Log.d(TAG, "onPaused");
            if (upstreamCB != null) {
                upstreamCB.onPaused(event);
            }
        }
    }

    private class onSeekListener implements OnSeekListener {

        private SeekStartedCB upstreamCB = null;
        public void on (SeekStartedCB callback) {
            upstreamCB = callback;
        }
        public void off () {
            upstreamCB = null;
        }

        @Override
        public void onSeek(SeekEvent event) {
            Log.d(TAG, "onSeek");
            if (upstreamCB != null) {
                upstreamCB.onSeekStarted(event);
            }
        }
    }

    private class onSeekedListener implements OnSeekedListener {

        private SeekEndedCB upstreamCB = null;
        public void on (SeekEndedCB callback) {
            upstreamCB = callback;
        }
        public void off () {
            upstreamCB = null;
        }

        @Override
        public void onSeeked(SeekedEvent event) {
            Log.d(TAG, "onSeeked");
            if (upstreamCB != null) {
                upstreamCB.onSeekEnded(event);
            }
        }
    }

    private class onStallStartedListener implements OnStallStartedListener {

        private BufferingStartedCB upstreamCB = null;
        public void on (BufferingStartedCB callback) {
            upstreamCB = callback;
        }
        public void off () {
            upstreamCB = null;
        }

        @Override
        public void onStallStarted(StallStartedEvent event) {
            Log.d(TAG, "onStallStarted");
            if (upstreamCB != null) {
                upstreamCB.onBufferingStarted(event);
            }
        }
    }

    private class onStallEndedListener implements OnStallEndedListener {

        private BufferingEndedCB upstreamCB = null;
        public void on (BufferingEndedCB callback) {
            upstreamCB = callback;
        }
        public void off () {
            upstreamCB = null;
        }

        @Override
        public void onStallEnded(StallEndedEvent event) {
            Log.d(TAG, "onStallEnded");
            if (upstreamCB != null) {
                upstreamCB.onBufferingEnded(event);
            }
        }
    }

    private class onSourceUnloadedListener implements OnSourceUnloadedListener {

        private SourceUnloadedCB upstreamCB = null;
        public void on (SourceUnloadedCB callback) {
            upstreamCB = callback;
        }
        public void off () {
            upstreamCB = null;
        }

        @Override
        public void onSourceUnloaded(SourceUnloadedEvent event) {
            Log.d(TAG, "onSourceLoaded");
            if (upstreamCB != null) {
                upstreamCB.onSourceUnloaded(event);
            }
        }
    }

    private class onErrorListener implements OnErrorListener {

        private ErrorCB upstreamCB = null;
        public void on (ErrorCB callback) {
            upstreamCB = callback;
        }
        public void off () {
            upstreamCB = null;
        }

        @Override
        public void onError(ErrorEvent event) {
            Log.d(TAG, "onError");
            if (upstreamCB != null) {
                upstreamCB.onError(event);
            }
        }
    }

    private class onPlaybackFinishedListener implements OnPlaybackFinishedListener {

        private PlaybackFinishedCB upstreamCB = null;
        public void on (PlaybackFinishedCB callback) {
            upstreamCB = callback;
        }
        public void off () {
            upstreamCB = null;
        }

        @Override
        public void onPlaybackFinished(PlaybackFinishedEvent event) {
            Log.d(TAG, "onPlaybackFinished");
            if (upstreamCB != null) {
                upstreamCB.onPlaybackFinished(event);
            }
        }
    }

    private class onDestroyListener implements OnDestroyListener {

        private PlayerDestroyedCB upstreamCB = null;
        public void on (PlayerDestroyedCB callback) {
            upstreamCB = callback;
        }
        public void off () {
            upstreamCB = null;
        }

        @Override
        public void onDestroy(DestroyEvent event) {
            Log.d(TAG, "onDestroy");
            if (upstreamCB != null) {
                upstreamCB.onPlayerDestroyed(event);
            }
        }
    }

    private class onAdBreakStartedListener implements OnAdBreakStartedListener {

        private AdBreakStartedCB upstreamCB = null;
        public void on (AdBreakStartedCB callback) {
            upstreamCB = callback;
        }
        public void off () {
            upstreamCB = null;
        }

        @Override
        public void onAdBreakStarted(AdBreakStartedEvent event) {
            Log.d(TAG, "onAdBreakStarted");
            if (upstreamCB != null) {
                upstreamCB.onAdBreakStarted(event);
            }
        }
    }

    private class onAdBreakFinishedListener implements OnAdBreakFinishedListener {

        private AdBreakFinishedCB upstreamCB = null;
        public void on (AdBreakFinishedCB callback) {
            upstreamCB = callback;
        }
        public void off () {
            upstreamCB = null;
        }

        @Override
        public void onAdBreakFinished(AdBreakFinishedEvent event) {
            Log.d(TAG, "onAdBreakFinished");
            if (upstreamCB != null) {
                upstreamCB.onAdBreakFinished(event);
            }
        }
    }

    private class onAdStartedListener implements OnAdStartedListener {

        private AdStartedCB upstreamCB = null;
        public void on (AdStartedCB callback) {
            upstreamCB = callback;
        }
        public void off () {
            upstreamCB = null;
        }

        @Override
        public void onAdStarted(AdStartedEvent event) {
            Log.d(TAG, "onAdStarted");
            if (upstreamCB != null) {
                upstreamCB.onAdStarted(event);
            }
        }
    }

    private class onAdFinishedListener implements OnAdFinishedListener {

        private AdFinishedCB upstreamCB = null;
        public void on (AdFinishedCB callback) {
            upstreamCB = callback;
        }
        public void off () {
            upstreamCB = null;
        }

        @Override
        public void onAdFinished(AdFinishedEvent event) {
            Log.d(TAG, "onAdFinished");
            if (upstreamCB != null) {
                upstreamCB.onAdFinished(event);
            }
        }
    }

    private class onAdErrorListener implements OnAdErrorListener {

        private AdErrorCB upstreamCB = null;
        public void on (AdErrorCB callback) {
            upstreamCB = callback;
        }
        public void off () {
            upstreamCB = null;
        }

        @Override
        public void onAdError(AdErrorEvent event) {
            Log.d(TAG, "onAdError");
            if (upstreamCB != null) {
                upstreamCB.onAdError(event);
            }
        }
    }

    private void registerListeners() {
        this.bitmovinEventsMap.put(SourceLoadedCB.class, new onSourceLoadedListener());
        this.bitmovinPlayer.addEventListener((OnSourceLoadedListener)bitmovinEventsMap.get(SourceLoadedCB.class));

        this.bitmovinEventsMap.put(ReadyCB.class, new onReadyListener());
        this.bitmovinPlayer.addEventListener((OnReadyListener)bitmovinEventsMap.get(ReadyCB.class));

        this.bitmovinEventsMap.put(PlayCB.class, new onPlayListener());
        this.bitmovinPlayer.addEventListener((OnPlayListener)bitmovinEventsMap.get(PlayCB.class));

        this.bitmovinEventsMap.put(PlayingCB.class, new onPlayingListener());
        this.bitmovinPlayer.addEventListener((OnPlayingListener)bitmovinEventsMap.get(PlayingCB.class));

        this.bitmovinEventsMap.put(PausedCB.class, new onPausedListener());
        this.bitmovinPlayer.addEventListener((OnPausedListener)bitmovinEventsMap.get(PausedCB.class));

        this.bitmovinEventsMap.put(SeekStartedCB.class, new onSeekListener());
        bitmovinPlayer.addEventListener((OnSeekListener)bitmovinEventsMap.get(SeekStartedCB.class));

        bitmovinEventsMap.put(SeekEndedCB.class, new onSeekedListener());
        bitmovinPlayer.addEventListener((OnSeekedListener)bitmovinEventsMap.get(SeekEndedCB.class));

        bitmovinEventsMap.put(BufferingStartedCB.class, new onStallStartedListener());
        bitmovinPlayer.addEventListener((OnStallStartedListener)bitmovinEventsMap.get(BufferingStartedCB.class));

        bitmovinEventsMap.put(BufferingEndedCB.class, new onStallEndedListener());
        bitmovinPlayer.addEventListener((OnStallEndedListener)bitmovinEventsMap.get(BufferingEndedCB.class));

        bitmovinEventsMap.put(SourceUnloadedCB.class, new onSourceUnloadedListener());
        bitmovinPlayer.addEventListener((OnSourceUnloadedListener)bitmovinEventsMap.get(SourceUnloadedCB.class));

        bitmovinEventsMap.put(ErrorCB.class, new onErrorListener());
        bitmovinPlayer.addEventListener((OnErrorListener)bitmovinEventsMap.get(ErrorCB.class));

        bitmovinEventsMap.put(PlaybackFinishedCB.class, new onPlaybackFinishedListener());
        bitmovinPlayer.addEventListener((OnPlaybackFinishedListener)bitmovinEventsMap.get(PlaybackFinishedCB.class));

        bitmovinEventsMap.put(PlayerDestroyedCB.class, new onDestroyListener());
        bitmovinPlayer.addEventListener((OnDestroyListener)bitmovinEventsMap.get(PlayerDestroyedCB.class));

        bitmovinEventsMap.put(AdBreakStartedCB.class, new onAdBreakStartedListener());
        bitmovinPlayer.addEventListener((OnAdBreakStartedListener)bitmovinEventsMap.get(AdBreakStartedCB.class));

        bitmovinEventsMap.put(AdBreakFinishedCB.class, new onAdBreakFinishedListener());
        bitmovinPlayer.addEventListener((OnAdBreakFinishedListener)bitmovinEventsMap.get(AdBreakFinishedCB.class));

        bitmovinEventsMap.put(AdStartedCB.class, new onAdStartedListener());
        bitmovinPlayer.addEventListener((OnAdStartedListener)bitmovinEventsMap.get(AdStartedCB.class));

        bitmovinEventsMap.put(AdFinishedCB.class, new onAdFinishedListener());
        bitmovinPlayer.addEventListener((OnAdFinishedListener)bitmovinEventsMap.get(AdFinishedCB.class));

        bitmovinEventsMap.put(AdErrorCB.class, new onAdErrorListener());
        bitmovinPlayer.addEventListener((OnAdErrorListener)bitmovinEventsMap.get(AdErrorCB.class));
    }

    public void removeListeners() {
        for (Map.Entry mapElement : bitmovinEventsMap.entrySet()) {
            bitmovinPlayer.removeEventListener((EventListener)mapElement.getValue());
        }
    }

    public void addUpstreamCallback (Object callback) {
        if (callback instanceof SourceLoadedCB) {
            onSourceLoadedListener obj = (onSourceLoadedListener)bitmovinEventsMap.get(SourceLoadedCB.class);
            obj.on((SourceLoadedCB)callback);
        } if (callback instanceof ReadyCB) {
            onSourceLoadedListener obj = (onSourceLoadedListener)bitmovinEventsMap.get(ReadyCB.class);
            obj.on((SourceLoadedCB)callback);
        }
    }

    public void removeUpstreamCallback (Object callback) {
        if (callback instanceof SourceLoadedCB) {
            onSourceLoadedListener obj = (onSourceLoadedListener)bitmovinEventsMap.get(SourceLoadedCB.class);
            obj.off();
        } if (callback instanceof ReadyCB) {
            onSourceLoadedListener obj = (onSourceLoadedListener)bitmovinEventsMap.get(ReadyCB.class);
            obj.off();
        }
    }

}
