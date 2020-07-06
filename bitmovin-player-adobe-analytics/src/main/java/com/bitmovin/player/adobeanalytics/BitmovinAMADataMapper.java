package com.bitmovin.player.adobeanalytics;

import com.adobe.marketing.mobile.Media;
import com.adobe.marketing.mobile.Media.MediaType;

public class BitmovinAMADataMapper {

    class AdobeMediaObject {
        private String mediaName;
        private String mediaId;
        private Double mediaLength;
        private String mediaStreamType;
        private MediaType mediaType;

        public AdobeMediaObject (String name, String id, Double length, String streamType) {
            mediaName = name;
            mediaId = id;
            mediaLength = length;
            mediaStreamType = streamType;
            mediaType = MediaType.Video;
        }

    }

    class AdobeAdBreakObject {
        private String adBreakName;
        private Long adBreakPosition;
        private Double adBreakStartTime;

        public AdobeAdBreakObject (String name, Long position, Double startTime) {
            adBreakName = name;
            adBreakPosition = position;
            adBreakStartTime = startTime;
        }
    }

    class AdobeAdObject {
        private String adName;
        private String adId;
        private Long adPosition;
        private Double adLength;

        public AdobeAdObject (String name, String id, Long position, Double duration) {
            adName = name;
            adId = id;
            adPosition = position;
            adLength = duration;
        }
    }

    class AdobeChapterObject {
        private String chapterName;
        private Long chapterPosition;
        private Double chapterLength;
        private  Double chapterStartTime;

        public AdobeChapterObject (String name, Long position, Double duration, Double startTime) {
            chapterName = name;
            chapterPosition = position;
            chapterLength = duration;
            chapterStartTime = startTime;
        }
    }

    class AdobeQoeObject {
        private Long qoeBitrate;
        private Double qoeStartupTime;
        private Double qoeFps;
        private Long qoeDroppedFrames;

        public AdobeQoeObject (Long bitrate, Double startupTime, Double fps, Long droppedFrames) {
            qoeBitrate = bitrate;
            qoeStartupTime = startupTime;
            qoeFps = fps;
            qoeDroppedFrames = droppedFrames;
        }
    }

    class AdobeStateObject {
        private String stateName;
        public AdobeStateObject (String name) {
            stateName = name;
        }
    }

    public AdobeMediaObject createMediaObject (String name, String id, Double length, String streamType) {
        AdobeMediaObject obj = new AdobeMediaObject (name, id, length, streamType);
        return obj;
    }

    public AdobeAdBreakObject createAdBreakObject (String name, Long position, Double startTime) {
        AdobeAdBreakObject obj = new AdobeAdBreakObject (name, position, startTime);
        return obj;
    }

    public AdobeAdObject createAdObject (String name, String id, Long position, Double duration) {
        AdobeAdObject obj = new AdobeAdObject (name, id, position, duration);
        return obj;
    }

    public AdobeChapterObject createChapterObject (String name, Long position, Double duration, Double startTimee) {
        AdobeChapterObject obj = new AdobeChapterObject (name, position, duration, startTimee);
        return obj;
    }

    public AdobeQoeObject createQoeObject (Long bitrate, Double startupTime, Double fps, Long droppedFrames) {
        AdobeQoeObject obj = new AdobeQoeObject (bitrate, startupTime, fps, droppedFrames);
        return obj;
    }

    public AdobeStateObject createStateObject (String name) {
        AdobeStateObject obj = new AdobeStateObject (name);
        return obj;
    }

}
