# Bitmovin Player Adobe Analytics Integration
This is an open-source project to enable the use of a third-party component (Adobe Video Heartbeat) with the Bitmovin Player Android SDK.

## Maintenance and Update
This project is not part of a regular maintenance or update schedule. For any update requests, please take a look at the guidance further below.

## Contributions to this project
As an open-source project, we are pleased to accept any and all changes, updates and fixes from the community wishing to use this project. Please see [CONTRIBUTING.md](CONTRIBUTING.md) for more details on how to contribute.

## Reporting player bugs
If you come across a bug related to the player, please raise this through your support ticketing system.

## Need more help?
Should you want some help updating this project (update, modify, fix or otherwise) and cant contribute for any reason, please raise your request to your bitmovin account team, who can discuss your request.

## Support and SLA Disclaimer
As an open-source project and not a core product offering, any request, issue or query related to this project is excluded from any SLA and Support terms that a customer might have with either Bitmovin or another third-party service provider or Company contributing to this project. Any and all updates are purely at the contributor's discretion.

Thank you for your contributions!

## Requirements
-----------------
1. Adobe Experience Cloud account
2. Bitmovin account
3. Adobe Experience Platform for mobile AEP SDKs is configured and "launch-app-id" setup as provided in link [here](https://aep-sdks.gitbook.io/docs/using-mobile-extensions/mobile-core/configuration)

## Getting started
------------------

### Configuration

#### Application Configuration
1. Add the Bitmovin 'adobeanalytics' module dependency in your Application project using app's Gradle file. 

```
implementation 'com.bitmovin.player.integration:adobeanalytics:<version>@aar'
```

You SHOULD replace `<version>` with the version number of Bitmovin AdobeAnaytics module. Or you could also replace `<version>` with `+` to get the latest version.

2. Add Adobe Media Analytics to your application. Please refer to [Adobe documentation](https://aep-sdks.gitbook.io/docs/using-mobile-extensions/adobe-media-analytics#add-media-analytics-to-your-app)

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

3. Register Media with mobile core. Please refer to [Adobe documentation](https://aep-sdks.gitbook.io/docs/using-mobile-extensions/adobe-media-analytics#register-media-with-mobile-core)

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

`your-launch-app-id` : ApplicationId setup following steps [here](https://aep-sdks.gitbook.io/docs/using-mobile-extensions/mobile-core/configuration)

## Usage
----------------
The Bitmovin AdobeAnalytics module provides information for each video uniquely. The module exposes following APIs to application for this purpose.

1. `AdobeMediaAnalyticsTracker`: This class is the entry point to be used by the application to start tracking Adobe Media Analytics events using the following APIs:

- `createTracker`: Events for each video playback are tracked uniquely, so the application needs to create the tracker object using this API. This API takes
  1. `BitmovinPlayer` object(mandatory and non-null) and 
  2. `AdobeMediaAnalyticsDataOverride`(optional so can be null) as input.

- `destroyTracker`: The tracker for a given playback session SHOULD be destroyed after the playback session is completed, stopped or destroyed.

2. `AdobeMediaAnalyticsDataOverride`: This class is default implementation of the data override methods and can be extended by the application to provide custom override methods for the values of fields of Adobe Media Analytics event data.

The specification of Adobe Media Analytics event data can be found [here](https://aep-sdks.gitbook.io/docs/using-mobile-extensions/adobe-media-analytics/media-api-reference)
A brief description of overridable methods can be found below.

##### Media

| Method (With Signature)                                                  | Default Value | Description|
| :-----------------------------------------------------------------------:|:-------------:|-----------:|
| String getMediaName (BitmovinPlayer player, SourceItem activeSourceItem) | 	""         | Should return the name of media being played|
| String getMediaUid (BitmovinPlayer player, SourceItem activeSourceItem)  | 	""         | Should return the name of media being played|
| HashMap<String, String> getMediaContextData (BitmovinPlayer player)      | 	null       | Should return the custom metadata for the playback session|

##### Ads

| Method (With Signature)                                                     | Default Value          | Description |
| --------------------------------------------------------------------------- |:----------------------:|:-----------:|
| String getAdBreakId (BitmovinPlayer player, AdBreakStartedEvent event)      | generated AdBreakID    | Should return the ID of the current Ad Break|
| long getAdBreakPosition (BitmovinPlayer player, AdBreakStartedEvent event)  | index of AdBreak       | Should return index of current adBreak among all Ad Breaks|
| String getAdName (BitmovinPlayer player, AdStartedEvent event)              | generated AdID         | Should return name of the current Ad|
| String getAdId (BitmovinPlayer player, AdStartedEvent event)                | generated AdID         | Should return ID of the current Ad|
| long getAdPosition (BitmovinPlayer player, AdStartedEvent event)            | index of Ad in AdBreak | Should return index of current Ad in Ad Break|


### Cleanup
`AdobeMediaAnalyticsTracker.destroyTracker` API should be used to tell the system that the tracking is completed and it should begin garbage collection. When a playback session is finished, this method needs to be used to stop tracking.

**NOTE:** It is up to the application to call `AdobeMediaAnalyticsTracker.destroyTracker` whenever the player is destroyed. Not doing so may cause errors.
