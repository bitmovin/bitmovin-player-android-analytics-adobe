package com.bitmovin.player.adobeanalytics;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.api.event.data.AdBreakStartedEvent;
import com.bitmovin.player.api.event.data.AdStartedEvent;
import com.bitmovin.player.config.media.SourceItem;

import java.util.HashMap;

public class AdobeMediaAnalyticsDataOverride {

    private Long activeAdBreakPosition = 0L;

    public HashMap<String, String> getMediaContextData (BitmovinPlayer player) {
        // no context data by default
        return null;
    }

    public String getMediaName (BitmovinPlayer player, SourceItem activeSourceItem) {
        // empty media name by default
        return "";
    }

    public String getMediaUid (BitmovinPlayer player, SourceItem activeSourceItem) {
        // empty media Id by default
        return "";
    }

    public String getAdBreakId (BitmovinPlayer player, AdBreakStartedEvent event) {
        return event.getAdBreak().getId();
    }

    public Long getAdBreakPosition (BitmovinPlayer player, AdBreakStartedEvent event) {
        return ++activeAdBreakPosition;
    }

    public String getAdName (BitmovinPlayer player, AdStartedEvent event) {
        return event.getAd().getMediaFileUrl();
    }

    public String getAdId (BitmovinPlayer player, AdStartedEvent event) {
        return event.getAd().getId();
    }

    public Long getAdPosition (BitmovinPlayer player, AdStartedEvent event) {
        Long position = Long.valueOf(event.getIndexInQueue());
        return position;
    }

}
