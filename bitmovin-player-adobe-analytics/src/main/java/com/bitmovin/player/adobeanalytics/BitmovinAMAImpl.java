package com.bitmovin.player.adobeanalytics;

import com.adobe.marketing.mobile.Media;
import com.adobe.marketing.mobile.MediaTracker;
import com.bitmovin.player.BitmovinPlayer;

public class BitmovinAMAImpl {

    private final String TAG = "BitmovinAMAImpl";
    private BitmovinPlayer bitmovinPlayer;
    private MediaTracker mediaTracker;
    private BitmovinPlayerEventsWrapper bitmovinEventsObj;
    private BitmovinAMAEventsWrapper bitmovinAdobeEventsObj;

    public boolean createTracker(BitmovinPlayer bitmovinPlayer) {

        this.bitmovinPlayer = bitmovinPlayer;

        // instantiate Adobe Media analytics tracker object
        this.mediaTracker = Media.createTracker();

        // instantiate Bitmovin player events handler object
        this.bitmovinEventsObj = new BitmovinPlayerEventsWrapper(this.bitmovinPlayer);

        // instantiate Adobe Media analytics events wrapper object
        this.bitmovinAdobeEventsObj = new BitmovinAMAEventsWrapper(this.mediaTracker);


        return true;
    }

    public void destroyTracker() {
        bitmovinEventsObj.removeAllListeners();
    }

}
