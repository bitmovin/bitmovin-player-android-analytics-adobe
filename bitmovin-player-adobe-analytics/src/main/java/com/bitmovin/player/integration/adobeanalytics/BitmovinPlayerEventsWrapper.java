package com.bitmovin.player.integration.adobeanalytics;

import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.event.EventListener;
import com.bitmovin.player.api.event.PlayerEvent;
import com.bitmovin.player.api.event.SourceEvent;

import java.util.HashMap;
import java.util.Map;

public class BitmovinPlayerEventsWrapper {

    private Player bitmovinPlayer;
    private final Map<String, EventListener> bitmovinEventsMap = new HashMap<>();
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
    public static final String PLAYER_ERROR_EVENT = "playererror";
    public static final String SOURCE_ERROR_EVENT = "sourceerror";
    public static final String PLAYBACK_FINISHED_EVENT = "playbackfinished";
    public static final String SOURCE_UNLOADED_EVENT = "sourceunloaded";
    public static final String AD_BREAK_STARTED = "adbreakstarted";
    public static final String AD_BREAK_FINISHED = "adbreakfinished";
    public static final String AD_STARTED = "adstarted";
    public static final String AD_FINISHED = "adfinished";
    public static final String AD_SKIPPED = "adskipped";
    public static final String AD_ERROR = "aderror";

    BitmovinPlayerEventsWrapper (Player player) {
        this.bitmovinPlayer = player;
        // register all events in the default list
        registerListeners();
    }

    interface SourceLoadedEventHandler {
        void onSourceLoaded (SourceEvent.Loaded event);
    }

    interface ReadyEventHandler {
        void onReady (PlayerEvent.Ready event);
    }

    interface PlayEventHandler {
        void onPlay (PlayerEvent.Play event);
    }

    interface PlayingEventHandler {
        void onPlaying (PlayerEvent.Playing event);
    }

    interface PausedEventHandler {
        void onPaused (PlayerEvent.Paused event);
    }

    interface SeekStartEventHandler {
        void onSeekStart (PlayerEvent.Seek event);
    }

    interface SeekCompleteEventHandler {
        void onSeekComplete (PlayerEvent.Seeked event);
    }

    interface BufferStartEventHandler {
        void onBufferStart (PlayerEvent.StallStarted event);
    }

    interface BufferCompleteEventHandler {
        void onBufferComplete (PlayerEvent.StallEnded event);
    }

    interface TimeChangedEventHandler {
        void onTimeChanged (PlayerEvent.TimeChanged event);
    }

    interface VideoPlaybackQualityChangedEventHandler {
        void onVideoPlaybackQualityChanged(PlayerEvent.VideoPlaybackQualityChanged event);
    }

    interface AudioMutedEventHandler {
        void onAudioMuted (PlayerEvent.Muted event);
    }

    interface AudioUnmutedEventHandler {
        void onAudioUnmuted (PlayerEvent.Unmuted event);
    }

    interface PlayerErrorEventHandler {
        void onPlayerError (PlayerEvent.Error event);
    }

    interface SourceErrorEventHandler {
        void onSourceError (SourceEvent.Error event);
    }

    interface PlaybackFinishedEventHandler {
        void onPlaybackFinished (PlayerEvent.PlaybackFinished event);
    }

    interface SourceUnloadedEventHandler {
        void onSourceUnloaded(SourceEvent.Unloaded event);
    }

    interface AdBreakStartedEventHandler {
        void onAdBreakStarted (PlayerEvent.AdBreakStarted event);
    }

    interface AdBreakFinishedEventHandler {
        void onAdBreakFinished (PlayerEvent.AdBreakFinished event);
    }

    interface AdStartedEventHandler {
        void onAdStarted (PlayerEvent.AdStarted event);
    }

    interface AdFinishedEventHandler {
        void onAdFinished (PlayerEvent.AdFinished event);
    }

    interface AdSkippedEventHandler {
        void onAdSkipped (PlayerEvent.AdSkipped event);
    }

    interface AdErrorEventHandler {
        void onAdError (PlayerEvent.AdError event);
    }

    private interface EventHandler {
        public void on (Object callback);
        public void off ();
    }

    private class onSourceLoadedListener implements EventListener<SourceEvent.Loaded>, EventHandler {
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
        public void onEvent(SourceEvent.Loaded event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onSourceLoaded(event);
            }
        }
    }

    private class onReadyListener implements EventListener<PlayerEvent.Ready>, EventHandler {
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
        public void onEvent(PlayerEvent.Ready event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onReady(event);
            }
        }
    }

    private class onPlayListener implements EventListener<PlayerEvent.Play>, EventHandler {
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
        public void onEvent(PlayerEvent.Play event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onPlay(event);
            }
        }
    }

    private class onPlayingListener implements EventListener<PlayerEvent.Playing>, EventHandler {
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
        public void onEvent(PlayerEvent.Playing event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onPlaying(event);
            }
        }
    }

    private class onPausedListener implements EventListener<PlayerEvent.Paused>, EventHandler {
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
        public void onEvent(PlayerEvent.Paused event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onPaused(event);
            }
        }
    }

    private class onSeekListener implements EventListener<PlayerEvent.Seek>, EventHandler {
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
        public void onEvent(PlayerEvent.Seek event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onSeekStart(event);
            }
        }
    }

    private class onSeekedListener implements EventListener<PlayerEvent.Seeked>, EventHandler {
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
        public void onEvent(PlayerEvent.Seeked event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onSeekComplete(event);
            }
        }
    }

    private class onStallStartedListener implements EventListener<PlayerEvent.StallStarted>, EventHandler {
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
        public void onEvent(PlayerEvent.StallStarted event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onBufferStart(event);
            }
        }
    }

    private class onStallEndedListener implements EventListener<PlayerEvent.StallEnded>, EventHandler {
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
        public void onEvent(PlayerEvent.StallEnded event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onBufferComplete(event);
            }
        }
    }

    private class onTimeChangedListener implements EventListener<PlayerEvent.TimeChanged>, EventHandler {
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
        public void onEvent(PlayerEvent.TimeChanged event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onTimeChanged(event);
            }
        }
    }

    private class onVideoPlaybackQualityChangedListener implements EventListener<PlayerEvent.VideoPlaybackQualityChanged>, EventHandler {
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
        public void onEvent(PlayerEvent.VideoPlaybackQualityChanged event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onVideoPlaybackQualityChanged(event);
            }
        }
    }

    private class onSourceUnloadedListener implements EventListener<SourceEvent.Unloaded>, EventHandler {
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
        public void onEvent(SourceEvent.Unloaded event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onSourceUnloaded(event);
            }
        }
    }

    private class onMutedListener implements EventListener<PlayerEvent.Muted>, EventHandler {
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
        public void onEvent(PlayerEvent.Muted event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onAudioMuted(event);
            }
        }
    }

    private class onUnmutedListener implements EventListener<PlayerEvent.Unmuted>, EventHandler {
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
        public void onEvent(PlayerEvent.Unmuted event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onAudioUnmuted(event);
            }
        }
    }

    private class onPlayerErrorListener implements EventListener<PlayerEvent.Error>, EventHandler {
        private PlayerErrorEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (PlayerErrorEventHandler) callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onEvent(PlayerEvent.Error event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onPlayerError(event);
            }
        }
    }

    private class onSourceErrorListener implements EventListener<SourceEvent.Error>, EventHandler {
        private SourceErrorEventHandler upstreamEventHandler = null;
        @Override
        public void on (Object callback) {
            upstreamEventHandler = (SourceErrorEventHandler) callback;
        }
        @Override
        public void off () {
            upstreamEventHandler = null;
        }

        @Override
        public void onEvent(SourceEvent.Error event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onSourceError(event);
            }
        }
    }

    private class onPlaybackFinishedListener implements EventListener<PlayerEvent.PlaybackFinished>, EventHandler {
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
        public void onEvent(PlayerEvent.PlaybackFinished event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onPlaybackFinished(event);
            }
        }
    }

    private class onAdBreakStartedListener implements EventListener<PlayerEvent.AdBreakStarted>, EventHandler {
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
        public void onEvent(PlayerEvent.AdBreakStarted event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onAdBreakStarted(event);
            }
        }
    }

    private class onAdBreakFinishedListener implements EventListener<PlayerEvent.AdBreakFinished>, EventHandler {
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
        public void onEvent(PlayerEvent.AdBreakFinished event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onAdBreakFinished(event);
            }
        }
    }

    private class onAdStartedListener implements EventListener<PlayerEvent.AdStarted>, EventHandler {
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
        public void onEvent(PlayerEvent.AdStarted event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onAdStarted(event);
            }
        }
    }

    private class onAdFinishedListener implements EventListener<PlayerEvent.AdFinished>, EventHandler {
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
        public void onEvent(PlayerEvent.AdFinished event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onAdFinished(event);
            }
        }
    }

    private class onAdErrorListener implements EventListener<PlayerEvent.AdError>, EventHandler {
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
        public void onEvent(PlayerEvent.AdError event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onAdError(event);
            }
        }
    }

    private class onAdSkippedListener implements EventListener<PlayerEvent.AdSkipped>, EventHandler {
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
        public void onEvent(PlayerEvent.AdSkipped event) {
            if (upstreamEventHandler != null) {
                upstreamEventHandler.onAdSkipped(event);
            }
        }
    }

    private void registerListeners() {
        this.bitmovinEventsMap.put(SOURCE_LOADED_EVENT, new onSourceLoadedListener());
        this.bitmovinPlayer.on(SourceEvent.Loaded.class, bitmovinEventsMap.get(SOURCE_LOADED_EVENT));

        this.bitmovinEventsMap.put(READY_EVENT, new onReadyListener());
        this.bitmovinPlayer.on(PlayerEvent.Ready.class, bitmovinEventsMap.get(READY_EVENT));

        this.bitmovinEventsMap.put(PLAY_EVENT, new onPlayListener());
        this.bitmovinPlayer.on(PlayerEvent.Play.class, bitmovinEventsMap.get(PLAY_EVENT));

        this.bitmovinEventsMap.put(PLAYING_EVENT, new onPlayingListener());
        this.bitmovinPlayer.on(PlayerEvent.Playing.class, bitmovinEventsMap.get(PLAYING_EVENT));

        this.bitmovinEventsMap.put(PAUSED_EVENT, new onPausedListener());
        this.bitmovinPlayer.on(PlayerEvent.Paused.class, bitmovinEventsMap.get(PAUSED_EVENT));

        this.bitmovinEventsMap.put(SEEK_EVENT, new onSeekListener());
        this.bitmovinPlayer.on(PlayerEvent.Seek.class, bitmovinEventsMap.get(SEEK_EVENT));

        bitmovinEventsMap.put(SEEKED_EVENT, new onSeekedListener());
        this.bitmovinPlayer.on(PlayerEvent.Seeked.class, bitmovinEventsMap.get(SEEKED_EVENT));

        bitmovinEventsMap.put(BUFFERING_STARTED_EVENT, new onStallStartedListener());
        this.bitmovinPlayer.on(PlayerEvent.StallStarted.class, bitmovinEventsMap.get(BUFFERING_STARTED_EVENT));

        bitmovinEventsMap.put(BUFFERING_ENDED_EVENT, new onStallEndedListener());
        this.bitmovinPlayer.on(PlayerEvent.StallEnded.class, bitmovinEventsMap.get(BUFFERING_ENDED_EVENT));

        bitmovinEventsMap.put(TIME_CHANGED_EVENT, new onTimeChangedListener());
        this.bitmovinPlayer.on(PlayerEvent.TimeChanged.class, bitmovinEventsMap.get(TIME_CHANGED_EVENT));

        bitmovinEventsMap.put(VIDEO_PLAYBACK_QUALITY_CHANGED_EVENT, new onVideoPlaybackQualityChangedListener());
        this.bitmovinPlayer.on(PlayerEvent.VideoPlaybackQualityChanged.class, bitmovinEventsMap.get(VIDEO_PLAYBACK_QUALITY_CHANGED_EVENT));

        bitmovinEventsMap.put(SOURCE_UNLOADED_EVENT, new onSourceUnloadedListener());
        this.bitmovinPlayer.on(SourceEvent.Unloaded.class, bitmovinEventsMap.get(SOURCE_UNLOADED_EVENT));

        bitmovinEventsMap.put(AUDIO_MUTED_EVENT, new onMutedListener());
        this.bitmovinPlayer.on(PlayerEvent.Muted.class, bitmovinEventsMap.get(AUDIO_MUTED_EVENT));

        bitmovinEventsMap.put(AUDIO_UNMUTED_EVENT, new onUnmutedListener());
        this.bitmovinPlayer.on(PlayerEvent.Unmuted.class, bitmovinEventsMap.get(AUDIO_UNMUTED_EVENT));

        bitmovinEventsMap.put(PLAYER_ERROR_EVENT, new onPlayerErrorListener());
        this.bitmovinPlayer.on(PlayerEvent.Error.class, bitmovinEventsMap.get(PLAYER_ERROR_EVENT));

        bitmovinEventsMap.put(SOURCE_ERROR_EVENT, new onSourceErrorListener());
        this.bitmovinPlayer.on(SourceEvent.Error.class, bitmovinEventsMap.get(SOURCE_ERROR_EVENT));

        bitmovinEventsMap.put(PLAYBACK_FINISHED_EVENT, new onPlaybackFinishedListener());
        this.bitmovinPlayer.on(PlayerEvent.PlaybackFinished.class, bitmovinEventsMap.get(PLAYBACK_FINISHED_EVENT));

        bitmovinEventsMap.put(AD_BREAK_STARTED, new onAdBreakStartedListener());
        this.bitmovinPlayer.on(PlayerEvent.AdBreakStarted.class, bitmovinEventsMap.get(AD_BREAK_STARTED));

        bitmovinEventsMap.put(AD_BREAK_FINISHED, new onAdBreakFinishedListener());
        this.bitmovinPlayer.on(PlayerEvent.AdBreakFinished.class, bitmovinEventsMap.get(AD_BREAK_FINISHED));

        bitmovinEventsMap.put(AD_STARTED, new onAdStartedListener());
        this.bitmovinPlayer.on(PlayerEvent.AdStarted.class, bitmovinEventsMap.get(AD_STARTED));

        bitmovinEventsMap.put(AD_FINISHED, new onAdFinishedListener());
        this.bitmovinPlayer.on(PlayerEvent.AdFinished.class, bitmovinEventsMap.get(AD_FINISHED));

        bitmovinEventsMap.put(AD_SKIPPED, new onAdSkippedListener());
        this.bitmovinPlayer.on(PlayerEvent.AdSkipped.class, bitmovinEventsMap.get(AD_SKIPPED));

        bitmovinEventsMap.put(AD_ERROR, new onAdErrorListener());
        this.bitmovinPlayer.on(PlayerEvent.AdError.class, bitmovinEventsMap.get(AD_ERROR));

        // TODO handle TimeShift
    }

    public void removeAllEventHandlers() {
        for (Map.Entry mapElement : bitmovinEventsMap.entrySet()) {
            EventListener listener = (EventListener) mapElement.getValue();
            ((EventHandler) listener).off();
            bitmovinPlayer.off(listener);
        }
    }

    public void on(String eventName, Object callback) {
        EventHandler obj = (EventHandler) bitmovinEventsMap.get(eventName);
        obj.on(callback);
    }

    public void off(String eventName) {
        EventHandler obj = (EventHandler) bitmovinEventsMap.get(eventName);
        obj.off();
    }

}
