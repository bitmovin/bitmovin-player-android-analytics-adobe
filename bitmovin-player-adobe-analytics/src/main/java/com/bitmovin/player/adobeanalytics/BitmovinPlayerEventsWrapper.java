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
import java.util.Iterator;
import java.util.List;

public class BitmovinPlayerEventsWrapper {

    private class onSourceLoadedListener implements OnSourceLoadedListener {

        @Override
        public void onSourceLoaded(SourceLoadedEvent event) {
            Log.d(TAG, "onSourceLoaded");
        }
    }

    private class onReadyListener implements OnReadyListener {

        @Override
        public void onReady(ReadyEvent event) {
            Log.d(TAG, "onReady");
        }
    }

    private class onPlayListener implements OnPlayListener {

        @Override
        public void onPlay(PlayEvent event) {
            Log.d(TAG, "onPlay");
        }
    }

    private class onPlayingListener implements OnPlayingListener {

        @Override
        public void onPlaying(PlayingEvent event) {
            Log.d(TAG, "onPlaying");
        }
    }

    private class onPausedListener implements OnPausedListener {

        @Override
        public void onPaused(PausedEvent event) {
            Log.d(TAG, "onPaused");
        }
    }

    private class onSeekListener implements OnSeekListener {

        @Override
        public void onSeek(SeekEvent event) {
            Log.d(TAG, "onSeek");
        }
    }

    private class onSeekedListener implements OnSeekedListener {

        @Override
        public void onSeeked(SeekedEvent event) {
            Log.d(TAG, "onSeeked");
        }
    }

    private class onSourceUnloadedListener implements OnSourceUnloadedListener {

        @Override
        public void onSourceUnloaded(SourceUnloadedEvent event) {
            Log.d(TAG, "onSourceLoaded");
        }
    }

    private class onErrorListener implements OnErrorListener {

        @Override
        public void onError(ErrorEvent event) {
            Log.d(TAG, "onError");
        }
    }

    private class onPlaybackFinishedListener implements OnPlaybackFinishedListener {

        @Override
        public void onPlaybackFinished(PlaybackFinishedEvent event) {
            Log.d(TAG, "onPlaybackFinished");
        }
    }

    private class onDestroyListener implements OnDestroyListener {

        @Override
        public void onDestroy(DestroyEvent event) {
            Log.d(TAG, "onDestroy");
        }
    }

    private class onAdBreakStartedListener implements OnAdBreakStartedListener {

        @Override
        public void onAdBreakStarted(AdBreakStartedEvent event) {
            Log.d(TAG, "onAdBreakStarted");
        }
    }

    private class onAdBreakFinishedListener implements OnAdBreakFinishedListener {

        @Override
        public void onAdBreakFinished(AdBreakFinishedEvent event) {
            Log.d(TAG, "onAdBreakFinished");
        }
    }

    private class onAdStartedListener implements OnAdStartedListener {

        @Override
        public void onAdStarted(AdStartedEvent event) {
            Log.d(TAG, "onAdStarted");
        }
    }

    private class onAdStartedFinished implements OnAdFinishedListener {

        @Override
        public void onAdFinished(AdFinishedEvent event) {
            Log.d(TAG, "onAdFinished");
        }
    }

    private class onAdError implements OnAdErrorListener {

        @Override
        public void onAdError(AdErrorEvent event) {
            Log.d(TAG, "onAdError");
        }
    }

    private BitmovinPlayer bitmovinPlayer;
    private final String TAG = "BitmovinEventsWrapper";

    BitmovinPlayerEventsWrapper (BitmovinPlayer player) {
        this.bitmovinPlayer = player;
        // register all events in the default list
        registerAllEvents();
    }

    private void registerAllEvents() {

    }

    public void removeAllListeners() {

    }

}
