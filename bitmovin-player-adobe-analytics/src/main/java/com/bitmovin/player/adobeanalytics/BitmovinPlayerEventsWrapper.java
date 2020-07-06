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
    private Map<String, UpstreamCallback> bitmovinEventsMap = new HashMap<String, UpstreamCallback>();
    private final String TAG = "BitmovinEventsWrapper";

    public static final String SOURCE_LOADED_EVENT = "sourceloaded";
    public static final String READY_EVENT = "ready";
    public static final String PLAY_EVENT = "play";
    public static final String PLAYING_EVENT = "playing";
    public static final String PAUSED_EVENT = "paused";
    public static final String SEEK_EVENT = "seek";
    public static final String SEEKED_EVENT = "seeked";
    public static final String BUFFERING_STARTED_EVENT = "stallstarted";
    public static final String BUFFERING_ENDED_EVENT = "stallended";
    public static final String ERROR_EVENT = "error";
    public static final String PLAYBACK_FINISHED_EVENT = "playbackfinished";
    public static final String SOURCE_UNLOADED_EVENT = "sourceunloaded";
    public static final String PLAYER_DESTROYED_EVENT = "destroyed";
    public static final String AD_BREAK_STARTED = "adbreakstarted";
    public static final String AD_BREAK_FINISHED = "adbreakfinished";
    public static final String AD_STARTED = "adstarted";
    public static final String AD_FINISHED = "adfinished";
    public static final String AD_ERROR = "aderror";

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

    private interface UpstreamCallback {
        public void on (Object callback);
        public void off ();
    }

    private class onSourceLoadedListener implements OnSourceLoadedListener, UpstreamCallback {

        private SourceLoadedCB upstreamCB = null;

        @Override
        public void on (Object callback) {
            upstreamCB = (SourceLoadedCB)callback;
        }
        @Override
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

    private class onReadyListener implements OnReadyListener, UpstreamCallback {

        private ReadyCB upstreamCB = null;
        @Override
        public void on (Object callback) {
            upstreamCB = (ReadyCB)callback;
        }
        @Override
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

    private class onPlayListener implements OnPlayListener, UpstreamCallback {

        private PlayCB upstreamCB = null;
        @Override
        public void on (Object callback) {
            upstreamCB = (PlayCB)callback;
        }
        @Override
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

    private class onPlayingListener implements OnPlayingListener, UpstreamCallback {

        private PlayingCB upstreamCB = null;
        @Override
        public void on (Object callback) {
            upstreamCB = (PlayingCB)callback;
        }
        @Override
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

    private class onPausedListener implements OnPausedListener, UpstreamCallback {

        private PausedCB upstreamCB = null;
        @Override
        public void on (Object callback) {
            upstreamCB = (PausedCB)callback;
        }
        @Override
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

    private class onSeekListener implements OnSeekListener, UpstreamCallback {

        private SeekStartedCB upstreamCB = null;
        @Override
        public void on (Object callback) {
            upstreamCB = (SeekStartedCB)callback;
        }
        @Override
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

    private class onSeekedListener implements OnSeekedListener, UpstreamCallback {

        private SeekEndedCB upstreamCB = null;
        @Override
        public void on (Object callback) {
            upstreamCB = (SeekEndedCB)callback;
        }
        @Override
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

    private class onStallStartedListener implements OnStallStartedListener, UpstreamCallback {

        private BufferingStartedCB upstreamCB = null;
        @Override
        public void on (Object callback) {
            upstreamCB = (BufferingStartedCB)callback;
        }
        @Override
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

    private class onStallEndedListener implements OnStallEndedListener, UpstreamCallback {

        private BufferingEndedCB upstreamCB = null;
        @Override
        public void on (Object callback) {
            upstreamCB = (BufferingEndedCB)callback;
        }
        @Override
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

    private class onSourceUnloadedListener implements OnSourceUnloadedListener, UpstreamCallback {

        private SourceUnloadedCB upstreamCB = null;
        @Override
        public void on (Object callback) {
            upstreamCB = (SourceUnloadedCB)callback;
        }
        @Override
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

    private class onErrorListener implements OnErrorListener, UpstreamCallback {

        private ErrorCB upstreamCB = null;
        @Override
        public void on (Object callback) {
            upstreamCB = (ErrorCB)callback;
        }
        @Override
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

    private class onPlaybackFinishedListener implements OnPlaybackFinishedListener, UpstreamCallback {

        private PlaybackFinishedCB upstreamCB = null;
        @Override
        public void on (Object callback) {
            upstreamCB = (PlaybackFinishedCB)callback;
        }
        @Override
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

    private class onDestroyListener implements OnDestroyListener, UpstreamCallback {

        private PlayerDestroyedCB upstreamCB = null;
        @Override
        public void on (Object callback) {
            upstreamCB = (PlayerDestroyedCB)callback;
        }
        @Override
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

    private class onAdBreakStartedListener implements OnAdBreakStartedListener, UpstreamCallback {

        private AdBreakStartedCB upstreamCB = null;
        @Override
        public void on (Object callback) {
            upstreamCB = (AdBreakStartedCB)callback;
        }
        @Override
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

    private class onAdBreakFinishedListener implements OnAdBreakFinishedListener, UpstreamCallback {

        private AdBreakFinishedCB upstreamCB = null;
        @Override
        public void on (Object callback) {
            upstreamCB = (AdBreakFinishedCB)callback;
        }
        @Override
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

    private class onAdStartedListener implements OnAdStartedListener, UpstreamCallback {

        private AdStartedCB upstreamCB = null;
        @Override
        public void on (Object callback) {
            upstreamCB = (AdStartedCB)callback;
        }
        @Override
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

    private class onAdFinishedListener implements OnAdFinishedListener, UpstreamCallback {

        private AdFinishedCB upstreamCB = null;
        @Override
        public void on (Object callback) {
            upstreamCB = (AdFinishedCB)callback;
        }
        @Override
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

    private class onAdErrorListener implements OnAdErrorListener, UpstreamCallback {

        private AdErrorCB upstreamCB = null;
        @Override
        public void on (Object callback) {
            upstreamCB = (AdErrorCB)callback;
        }
        @Override
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
        this.bitmovinEventsMap.put(SOURCE_LOADED_EVENT, new onSourceLoadedListener());
        this.bitmovinPlayer.addEventListener((OnSourceLoadedListener)bitmovinEventsMap.get(SOURCE_LOADED_EVENT));

        this.bitmovinEventsMap.put(READY_EVENT, new onReadyListener());
        this.bitmovinPlayer.addEventListener((OnReadyListener)bitmovinEventsMap.get(READY_EVENT));

        this.bitmovinEventsMap.put(PLAY_EVENT, new onPlayListener());
        this.bitmovinPlayer.addEventListener((OnPlayListener)bitmovinEventsMap.get(PLAY_EVENT));

        this.bitmovinEventsMap.put(PLAYING_EVENT, new onPlayingListener());
        this.bitmovinPlayer.addEventListener((OnPlayingListener)bitmovinEventsMap.get(PLAYING_EVENT));

        this.bitmovinEventsMap.put(PAUSED_EVENT, new onPausedListener());
        this.bitmovinPlayer.addEventListener((OnPausedListener)bitmovinEventsMap.get(PAUSED_EVENT));

        this.bitmovinEventsMap.put(SEEK_EVENT, new onSeekListener());
        bitmovinPlayer.addEventListener((OnSeekListener)bitmovinEventsMap.get(SEEK_EVENT));

        bitmovinEventsMap.put(SEEKED_EVENT, new onSeekedListener());
        bitmovinPlayer.addEventListener((OnSeekedListener)bitmovinEventsMap.get(SEEKED_EVENT));

        bitmovinEventsMap.put(BUFFERING_STARTED_EVENT, new onStallStartedListener());
        bitmovinPlayer.addEventListener((OnStallStartedListener)bitmovinEventsMap.get(BUFFERING_STARTED_EVENT));

        bitmovinEventsMap.put(BUFFERING_ENDED_EVENT, new onStallEndedListener());
        bitmovinPlayer.addEventListener((OnStallEndedListener)bitmovinEventsMap.get(BUFFERING_ENDED_EVENT));

        bitmovinEventsMap.put(SOURCE_UNLOADED_EVENT, new onSourceUnloadedListener());
        bitmovinPlayer.addEventListener((OnSourceUnloadedListener)bitmovinEventsMap.get(SOURCE_UNLOADED_EVENT));

        bitmovinEventsMap.put(ERROR_EVENT, new onErrorListener());
        bitmovinPlayer.addEventListener((OnErrorListener)bitmovinEventsMap.get(ERROR_EVENT));

        bitmovinEventsMap.put(PLAYBACK_FINISHED_EVENT, new onPlaybackFinishedListener());
        bitmovinPlayer.addEventListener((OnPlaybackFinishedListener)bitmovinEventsMap.get(PLAYBACK_FINISHED_EVENT));

        bitmovinEventsMap.put(PLAYER_DESTROYED_EVENT, new onDestroyListener());
        bitmovinPlayer.addEventListener((OnDestroyListener)bitmovinEventsMap.get(PLAYER_DESTROYED_EVENT));

        bitmovinEventsMap.put(AD_BREAK_STARTED, new onAdBreakStartedListener());
        bitmovinPlayer.addEventListener((OnAdBreakStartedListener)bitmovinEventsMap.get(AD_BREAK_STARTED));

        bitmovinEventsMap.put(AD_BREAK_FINISHED, new onAdBreakFinishedListener());
        bitmovinPlayer.addEventListener((OnAdBreakFinishedListener)bitmovinEventsMap.get(AD_BREAK_FINISHED));

        bitmovinEventsMap.put(AD_STARTED, new onAdStartedListener());
        bitmovinPlayer.addEventListener((OnAdStartedListener)bitmovinEventsMap.get(AD_STARTED));

        bitmovinEventsMap.put(AD_FINISHED, new onAdFinishedListener());
        bitmovinPlayer.addEventListener((OnAdFinishedListener)bitmovinEventsMap.get(AD_FINISHED));

        bitmovinEventsMap.put(AD_ERROR, new onAdErrorListener());
        bitmovinPlayer.addEventListener((OnAdErrorListener)bitmovinEventsMap.get(AD_ERROR));
    }

    public void removeListeners() {
        for (Map.Entry mapElement : bitmovinEventsMap.entrySet()) {
            bitmovinPlayer.removeEventListener((EventListener)mapElement.getValue());
        }
    }

    public void addUpstreamCallback (String eventName, Object callback) {

        UpstreamCallback obj = bitmovinEventsMap.get(eventName);
        obj.on(callback);
    }

    public void removeUpstreamCallback (String eventName) {
        UpstreamCallback obj = bitmovinEventsMap.get(eventName);
        obj.off();
    }

}
