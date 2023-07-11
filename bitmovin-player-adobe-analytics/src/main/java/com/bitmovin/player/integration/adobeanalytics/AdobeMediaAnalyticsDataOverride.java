package com.bitmovin.player.integration.adobeanalytics;

import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.advertising.Ad;
import com.bitmovin.player.api.advertising.AdBreak;
import com.bitmovin.player.api.event.PlayerEvent;
import com.bitmovin.player.api.source.Source;

import java.util.HashMap;

public class AdobeMediaAnalyticsDataOverride {

    private long activeAdBreakPosition = 0L;

    public HashMap<String, String> getMediaContextData (Player player) {
        // no context data by default
        return null;
    }

    public String getMediaName (Player player, Source activeSourceItem) {
        // empty media name by default
        return "";
    }

    public String getMediaUid (Player player, Source activeSourceItem) {
        // empty media Id by default
        return "";
    }

    public String getAdBreakId (Player player, PlayerEvent.AdBreakStarted event) {
        AdBreak adBreak = event.getAdBreak();
        return ((adBreak != null) ? adBreak.getId() : "");
    }

    public long getAdBreakPosition (Player player, PlayerEvent.AdBreakStarted event) {
        return ++activeAdBreakPosition;
    }

    public String getAdName (Player player, PlayerEvent.AdStarted event) {
        Ad ad = event.getAd();
        return ((ad != null) ? ad.getMediaFileUrl() : "");
    }

    public String getAdId (Player player, PlayerEvent.AdStarted event) {
        Ad ad = event.getAd();
        return ((ad != null) ? ad.getId() : "");
    }

    public long getAdPosition (Player player, PlayerEvent.AdStarted event) {
        return event.getIndexInQueue();
    }

}
