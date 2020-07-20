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
import com.bitmovin.player.api.event.data.TimeChangedEvent;
import com.bitmovin.player.api.event.listener.OnMutedListener;
import com.bitmovin.player.api.event.data.MutedEvent;
import com.bitmovin.player.api.event.listener.OnUnmutedListener;
import com.bitmovin.player.api.event.data.UnmutedEvent;
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
import com.bitmovin.player.api.event.data.AdSkippedEvent;
import com.bitmovin.player.api.event.listener.OnAdErrorListener;
import com.bitmovin.player.api.event.data.AdErrorEvent;
import com.bitmovin.player.api.event.listener.OnVideoPlaybackQualityChangedListener;
import com.bitmovin.player.api.event.data.VideoPlaybackQualityChangedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BitmovinPlayerEventsWrapper {

    private BitmovinPlayer bitmovinPlayer;
    private Map<String, EventHandler> bitmovinEventsMap = new HashMap<String, EventHandler>();
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
    public static final String TIME_CHANGED_EVENT = "timechanged";
    public static final String VIDEO_PLAYBACK_QUALITY_CHANGED_EVENT = "videoplaybackqualitychange";
    public static final String AUDIO_MUTED_EVENT = "audiomuted";
    public static final String AUDIO_UNMUTED_EVENT = "audiounmuted";
    public static final String ERROR_EVENT = "error";
    public static final String PLAYBACK_FINISHED_EVENT = "playbackfinished";
    public static final String SOURCE_UNLOADED_EVENT = "sourceunloaded";
    public static final String PLAYER_DESTROYED_EVENT = "destroyed";
    public static final String AD_BREAK_STARTED = "adbreakstarted";
    public static final String AD_BREAK_FINISHED = "adbreakfinished";
    public static final String AD_STARTED = "adstarted";
    public static final String AD_FINISHED = "adfinished";
    public static final String AD_SKIPPED = "adskipped";
    public static final String AD_ERROR = "aderror";

    BitmovinPlayerEventsWrapper (BitmovinPlayer player) {
        this.bitmovinPlayer = player;
        // register all events in the default list
        registerListeners();
    }

    interface SourceLoadedEventHandler {
        void onSourceLoaded (SourceLoadedEvent event);
    }

    interface ReadyEventHandler {
        void onReady (ReadyEvent event);
    }

    interface PlayEventHandler {
        void onPlay (PlayEvent event);
    }

    interface PlayingEventHandler {
        void onPlaying (PlayingEvent event);
    }

    interface PausedEventHandler {
        void onPaused (PausedEvent event);
    }

    interface SeekStartEventHandler {
        void onSeekStart (SeekEvent event);
    }

    interface SeekCompleteEventHandler {
        void onSeekComplete (SeekedEvent event);
    }

    interface BufferStartEventHandler {
        void onBufferStart (StallStartedEvent event);
    }

    interface BufferCompleteEventHandler {
        void onBufferComplete (StallEndedEvent event);
    }

    interface TimeChangedEventHandler {
        void onTimeChanged (TimeChangedEvent event);
    }

    interface VideoPlaybackQualityChangedEventHandler {
        void onVideoPlaybackQualityChanged(VideoPlaybackQualityChangedEvent event);
    }

    interface AudioMutedEventHandler {
        void onAudioMuted ();
    }

    interface AudioUnmutedEventHandler {
        void onAudioUnmuted ();
    }

    interface ErrorEventHandler {
        void onError (ErrorEvent event);
    }

    interface PlaybackFinishedEventHandler {
        void onPlaybackFinished (PlaybackFinishedEvent event);
    }

    interface SourceUnloadedEventHandler {
        void onSourceUnloaded(SourceUnloadedEvent event);
    }

    interface PlayerDestroyedEventHandler {
        void onPlayerDestroyed (DestroyEvent event);
    }

    interface AdBreakStartedEventHandler {
        void onAdBreakStarted (AdBreakStartedEvent event);
    }

    interface AdBreakFinishedEventHandler {
        void onAdBreakFinished (AdBreakFinishedEvent event);
    }

    interface AdStartedEventHandler {
        void onAdStarted (AdStartedEvent event);
    }

    interface AdFinishedEventHandler {
        void onAdFinished (AdFinishedEvent event);
    }

    interface AdSkippedEventHandler {
        void onAdSkipped (AdSkippedEvent event);
    }

    interface AdErrorEventHandler {
        void onAdError (AdErrorEvent event);
    }

    private interface EventHandler {
        public void on (Object callback);
        public void off ();
    }

    private class onSourceLoadedListener implements OnSourceLoadedListener, EventHandler {

        private SourceLoadedEventHandler upstreamEventHandler = null;

        @Override
        public void on (Object callback) {
            upstreamEventHandler = (SourceLoadedEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onSourceLoaded(SourceLoadedEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onSourceLoaded(event);
            }
        }

    }

    private class onReadyListener implements OnReadyListener, EventHandler {

        private ReadyEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (ReadyEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onReady(ReadyEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onReady(event);
            }
        }
    }

    private class onPlayListener implements OnPlayListener, EventHandler {

        private PlayEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (PlayEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onPlay(PlayEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onPlay(event);
            }
        }
    }

    private class onPlayingListener implements OnPlayingListener, EventHandler {

        private PlayingEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (PlayingEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onPlaying(PlayingEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onPlaying(event);
            }
        }
    }

    private class onPausedListener implements OnPausedListener, EventHandler {

        private PausedEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (PausedEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onPaused(PausedEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onPaused(event);
            }
        }
    }

    private class onSeekListener implements OnSeekListener, EventHandler {

        private SeekStartEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (SeekStartEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onSeek(SeekEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onSeekStart(event);
            }
        }
    }

    private class onSeekedListener implements OnSeekedListener, EventHandler {

        private SeekCompleteEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (SeekCompleteEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onSeeked(SeekedEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onSeekComplete(event);
            }
        }
    }

    private class onStallStartedListener implements OnStallStartedListener, EventHandler {

        private BufferStartEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (BufferStartEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onStallStarted(StallStartedEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onBufferStart(event);
            }
        }
    }

    private class onStallEndedListener implements OnStallEndedListener, EventHandler {

        private BufferCompleteEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (BufferCompleteEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onStallEnded(StallEndedEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onBufferComplete(event);
            }
        }
    }

    private class onTimeChangedListener implements OnTimeChangedListener, EventHandler {

        private TimeChangedEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (TimeChangedEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onTimeChanged(TimeChangedEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onTimeChanged(event);
            }
        }
    }

    private class onVideoPlaybackQualityChangedListener implements OnVideoPlaybackQualityChangedListener, EventHandler {

        private VideoPlaybackQualityChangedEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (VideoPlaybackQualityChangedEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onVideoPlaybackQualityChanged(VideoPlaybackQualityChangedEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onVideoPlaybackQualityChanged(event);
            }
        }
    }

    private class onSourceUnloadedListener implements OnSourceUnloadedListener, EventHandler {

        private SourceUnloadedEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (SourceUnloadedEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onSourceUnloaded(SourceUnloadedEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onSourceUnloaded(event);
            }
        }
    }

    private class onMutedListener implements OnMutedListener, EventHandler {

        private AudioMutedEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (AudioMutedEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onMuted(MutedEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onAudioMuted();
            }
        }
    }

    private class onUnmutedListener implements OnUnmutedListener, EventHandler {

        private AudioUnmutedEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (AudioUnmutedEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onUnmuted(UnmutedEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onAudioUnmuted();
            }
        }
    }

    private class onErrorListener implements OnErrorListener, EventHandler {

        private ErrorEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (ErrorEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onError(ErrorEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onError(event);
            }
        }
    }

    private class onPlaybackFinishedListener implements OnPlaybackFinishedListener, EventHandler {

        private PlaybackFinishedEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (PlaybackFinishedEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onPlaybackFinished(PlaybackFinishedEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onPlaybackFinished(event);
            }
        }
    }

    private class onDestroyListener implements OnDestroyListener, EventHandler {

        private PlayerDestroyedEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (PlayerDestroyedEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onDestroy(DestroyEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onPlayerDestroyed(event);
            }
        }
    }

    private class onAdBreakStartedListener implements OnAdBreakStartedListener, EventHandler {

        private AdBreakStartedEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (AdBreakStartedEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onAdBreakStarted(AdBreakStartedEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onAdBreakStarted(event);
            }
        }
    }

    private class onAdBreakFinishedListener implements OnAdBreakFinishedListener, EventHandler {

        private AdBreakFinishedEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (AdBreakFinishedEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onAdBreakFinished(AdBreakFinishedEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onAdBreakFinished(event);
            }
        }
    }

    private class onAdStartedListener implements OnAdStartedListener, EventHandler {

        private AdStartedEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (AdStartedEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onAdStarted(AdStartedEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onAdStarted(event);
            }
        }
    }

    private class onAdFinishedListener implements OnAdFinishedListener, EventHandler {

        private AdFinishedEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (AdFinishedEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onAdFinished(AdFinishedEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onAdFinished(event);
            }
        }
    }

    private class onAdErrorListener implements OnAdErrorListener, EventHandler {

        private AdErrorEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (AdErrorEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onAdError(AdErrorEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onAdError(event);
            }
        }
    }

    private class onAdSkippedListener implements OnAdSkippedListener, EventHandler {

        private AdSkippedEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (AdSkippedEventHandler)callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onAdSkipped(AdSkippedEvent event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onAdSkipped(event);
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

        bitmovinEventsMap.put(TIME_CHANGED_EVENT, new onTimeChangedListener());
        bitmovinPlayer.addEventListener((OnTimeChangedListener)bitmovinEventsMap.get(TIME_CHANGED_EVENT));

        bitmovinEventsMap.put(VIDEO_PLAYBACK_QUALITY_CHANGED_EVENT, new onVideoPlaybackQualityChangedListener());
        bitmovinPlayer.addEventListener((OnVideoPlaybackQualityChangedListener)bitmovinEventsMap.get(VIDEO_PLAYBACK_QUALITY_CHANGED_EVENT));

        bitmovinEventsMap.put(SOURCE_UNLOADED_EVENT, new onSourceUnloadedListener());
        bitmovinPlayer.addEventListener((OnSourceUnloadedListener)bitmovinEventsMap.get(SOURCE_UNLOADED_EVENT));

        bitmovinEventsMap.put(AUDIO_MUTED_EVENT, new onMutedListener());
        bitmovinPlayer.addEventListener((OnMutedListener)bitmovinEventsMap.get(AUDIO_MUTED_EVENT));

        bitmovinEventsMap.put(AUDIO_UNMUTED_EVENT, new onUnmutedListener());
        bitmovinPlayer.addEventListener((OnUnmutedListener)bitmovinEventsMap.get(AUDIO_UNMUTED_EVENT));

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

        bitmovinEventsMap.put(AD_SKIPPED, new onAdSkippedListener());
        bitmovinPlayer.addEventListener((onAdSkippedListener)bitmovinEventsMap.get(AD_SKIPPED));

        bitmovinEventsMap.put(AD_ERROR, new onAdErrorListener());
        bitmovinPlayer.addEventListener((OnAdErrorListener)bitmovinEventsMap.get(AD_ERROR));
    }

    public void removeAllEventHandlers() {
        for (Map.Entry mapElement : bitmovinEventsMap.entrySet()) {
            EventListener listener = (EventListener) mapElement.getValue();
            ((EventHandler) listener).off();
            bitmovinPlayer.removeEventListener(listener);
        }
    }

    public void addEventHandler (String eventName, Object callback) {
        EventHandler obj = bitmovinEventsMap.get(eventName);
        obj.on(callback);
    }

    public void removeEventHandler (String eventName) {
        EventHandler obj = bitmovinEventsMap.get(eventName);
        obj.off();
    }

}
