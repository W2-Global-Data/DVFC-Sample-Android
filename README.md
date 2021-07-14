# DVFC Sample App for Android

## Description

This is a sample app to outline the functionality of the W2 DVFC solution.

## Credentials

Please follow this guide for consuming GitHub Packages feeds:

https://help.github.com/en/packages/using-github-packages-with-your-projects-ecosystem

Open the `build.gradle` file in the root of the project and add your GitHub username and PAT token

## License Key

Open the `app/src/main/java/com/example/w2_clean_example/MainActivity.kt` file and replace "YOUR LICENSE KEY HERE" with the license key provided by W2.

## Running the sample

Open this sample in Android Studio (tested with version 4.2.2). Run the project as a standard Android Studio project.
More information can be found here: https://developer.android.com/training/basics/firstapp/running-app.


## Localization

An example of localization available can be found in `app/src/main/res` and these are the following available localizations:

```
    <!-- Facial Comparison -->
    <string name="hg_move_in_frame">Move in frame</string>
    <string name="hg_hold_steady">Hold steady</string>
    <string name="hg_blink">Blink!</string>
    <string name="hg_too_close">Too close! Move away</string>
    <string name="hg_too_far_away">Move Closer</string>
    <string name="hg_align_face_and_blink">Align face to begin</string>


    <!-- Document Verification -->
    <string name="acuant_camera_align">Align</string>
    <string name="acuant_camera_move_closer">Move Closer</string>
    <string name="acuant_camera_not_in_frame">Too close!</string>
    <string name="acuant_camera_hold_steady">Hold Steady</string>
    <string name="acuant_camera_capturing">Capturing</string>
```