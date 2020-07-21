# bitmovin-player-android-adobe-analytics
-----------------------------------------
This module allows for the integration of your Abode Experience Media Analytics backend with your Bitmovin Player Android SDK.

## Requirements
-----------------
1. Adobe Experience Cloud account
2. Bitmovin account
3. Adobe Experience Platform for mobile AEP SDKs is configured and "launch-app-id" setup as provided in link below
   https://aep-sdks.gitbook.io/docs/using-mobile-extensions/mobile-core/configuration

## Getting started
------------------

### Configuration

#### Application Configuration
1. Add the Bitmovin 'adobeanalytics' module dependency in your Application project using app's Gradle file. 

```
implementation 'com.bitmovin.player.integration:adobeanalytics:0.1.0@aar'
```

2. Add Adobe Media Analytics to your application. Please refer to Adobe documentation (https://aep-sdks.gitbook.io/docs/using-mobile-extensions/adobe-media-analytics#add-media-analytics-to-your-app)

- Add the Media extension and its dependencies to your project using the app's Gradle file.

```
implementation 'com.adobe.marketing.mobile:sdk-core:1.+'
implementation 'com.adobe.marketing.mobile:analytics:1.+'
implementation 'com.adobe.marketing.mobile:media:2.+'
```

- Import the Media extension in your application's main activity.

```
import com.adobe.marketing.mobile.*;
```

3. Register Media with mobile core. Please refer to Adobe documentation (https://aep-sdks.gitbook.io/docs/using-mobile-extensions/adobe-media-analytics#register-media-with-mobile-core)

```
import com.adobe.marketing.mobile.*;

public class MobileApp extends Application {

  @Override
  public void onCreate() {
      super.onCreate();
      MobileCore.setApplication(this);

      try {
          Media.registerExtension();
          Analytics.registerExtension();
          Identity.registerExtension();
          MobileCore.start(new AdobeCallback () {
              @Override
              public void call(Object o) {
                  MobileCore.configureWithAppID("your-launch-app-id");
              }
          });
      } catch (InvalidInitException e) {

      }
  }
}
```

`your-launch-app-id` : ApplicationId setup following steps in https://aep-sdks.gitbook.io/docs/using-mobile-extensions/mobile-core/configuration

## Usage
----------------
bitmovin-player-analytics-adobe provides information for each video uniquely. To handle this each instance of the Bitmovin player needs to be passed to the module using `AdobeMediaAnalyticsTracker.createTracker()` API.
The `bitmovin-player-adobe-analytics` module exposes following Interfaces to application.
1. `AdobeMediaAnalyticsTracker` : This class is the entry point to be used by application to start tracking of Adobe media analytics events using following 2 APIs.

- `createTracker` : Events for each video playback are tracked uniquely, so Application needs to create the tracker object using this API. This API takes
                     `BitmovinPlayer` object(mandatory and non-null) and `AdobeMediaAnalyticsDataOverride`(optional so can be null) as input.

- `destroyTracker` : The tracker for a given playback session SHOULD be destroyed after playback session is completed, stopped or destroyed.

2. `AdobeMediaAnalyticsDataOverride` : This class is default implementation of the data override interface and can be extended by Application to provide custom
                                        override methods for the values of fields of Adobe Media analytics event data. The specification of Adobe media Analytics
                                        event data can be found at https://aep-sdks.gitbook.io/docs/using-mobile-extensions/adobe-media-analytics/media-api-reference
                                        A brief description of overridable methods can be found below.
##### Media

| Method (With Signature)                                                  | Default Value | Description|
| :-----------------------------------------------------------------------:|:-------------:|-----------:|
| String getMediaName (BitmovinPlayer player, SourceItem activeSourceItem) | 	""         | Should return the name of media being played|
| String getMediaUid (BitmovinPlayer player, SourceItem activeSourceItem)  | 	""         | Should return the name of media being played|
| HashMap<String, String> getMediaContextData (BitmovinPlayer player)      | 	null       | Should return the custom metadata for the Playback Session|

##### Ads

| Method (With Signature)                                                     | Default Value          | Description |
| --------------------------------------------------------------------------- |:----------------------:|:-----------:|
| String getAdBreakId (BitmovinPlayer player, AdBreakStartedEvent event)      | generated AdBreakID    | Should return the ID of the current Ad Break|
| Long getAdBreakPosition (BitmovinPlayer player, AdBreakStartedEvent event)  | index of AdBreak       | Should return index of current adBreak among all Ad Breaks|
| String getAdName (BitmovinPlayer player, AdStartedEvent event)              | generated AdID         | Should return name of the current Ad|
| String getAdId (BitmovinPlayer player, AdStartedEvent event)                | generated AdID         | Should return ID of the current Ad|
| Long getAdPosition (BitmovinPlayer player, AdStartedEvent event)            | index of Ad in AdBreak | Should return index of current Ad in Ad Break|


### Cleanup
`AdobeMediaAnalyticsTracker.destroyTracker` API should be used tells the system that you are done tracking and it should begin garbage collection. When you have finished with a playback session, use this method to stop tracking.

Note it is up to Application to call `AdobeMediaAnalyticsTracker.destroyTracker` whenever player is destroyed. Not doing so may cause errors.
