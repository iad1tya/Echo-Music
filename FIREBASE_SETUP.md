# Firebase Analytics Setup for Echo Music App

## Overview
Google Analytics Firebase has been successfully integrated into the Echo music app. This document explains how to complete the setup.

## What's Already Done ✅

1. **Dependencies Added**: Firebase Analytics dependencies are added to `build.gradle.kts`
2. **Google Services Plugin**: Configured in both project and app-level build files
3. **Analytics Helper**: Created a utility class for easy event tracking
4. **Basic Tracking**: Added app lifecycle and screen view tracking
5. **Template Configuration**: Created `google-services.json` template

## What You Need to Do 🔧

### 1. Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a project" or "Add project"
3. Enter project name (e.g., "Echo Music App")
4. Follow the setup wizard

### 2. Add Android App to Firebase Project
1. In your Firebase project, click "Add app" and select Android
2. Enter package name: `iad1tya.echo.music`
3. Enter app nickname: "Echo Music"
4. Download the `google-services.json` file

### 3. Replace Template Configuration
1. Replace the template `app/google-services.json` with your downloaded file
2. The downloaded file should contain your actual project configuration

### 4. Add Debug Package (Optional)
If you want analytics for debug builds, add another app in Firebase Console:
1. Package name: `iad1tya.echo.music.dev`
2. Download another `google-services.json` and merge the configurations

## Analytics Events Available 📊

The app tracks the following events:

### Music Events
- `song_played` - When a song starts playing
- `song_paused` - When a song is paused
- `song_skipped` - When a song is skipped
- `playlist_created` - When a playlist is created
- `playlist_deleted` - When a playlist is deleted
- `song_added_to_playlist` - When a song is added to playlist
- `song_removed_from_playlist` - When a song is removed from playlist

### User Engagement
- `app_opened` - When the app is launched
- `app_backgrounded` - When the app goes to background
- `screen_viewed` - When a screen is viewed
- `button_clicked` - When a button is clicked
- `search_performed` - When user searches

### Settings & Errors
- `setting_changed` - When a setting is modified
- `error_occurred` - When an error happens

## How to Use AnalyticsHelper

```kotlin
// Track a song being played
AnalyticsHelper.logSongPlayed("Song Title", "Artist Name", "youtube")

// Track screen views
AnalyticsHelper.logScreenViewed("HomeScreen")

// Track button clicks
AnalyticsHelper.logButtonClicked("play_button", "HomeScreen")

// Track custom events
AnalyticsHelper.logEvent("custom_event", mapOf(
    "custom_param" to "value"
))
```

## Testing Analytics

1. Build and install the app
2. Use the app normally
3. Check Firebase Console > Analytics > Events (may take a few minutes to appear)
4. Use Firebase DebugView for real-time testing

## Important Notes ⚠️

- Analytics data may take up to 24 hours to appear in Firebase Console
- Debug builds will show data in DebugView immediately
- Make sure to replace the template `google-services.json` with your actual configuration
- The app will work without Firebase, but analytics won't be tracked

## Troubleshooting

If you encounter issues:
1. Verify `google-services.json` is in the correct location (`app/` directory)
2. Check that package names match exactly
3. Ensure Google Services plugin is applied
4. Clean and rebuild the project
5. Check Firebase Console for any error messages

## Next Steps

Once Firebase is configured:
1. Set up custom dashboards in Firebase Console
2. Configure conversion events
3. Set up user properties
4. Add more specific tracking events as needed
