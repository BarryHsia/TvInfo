# Tv Info

A system information dashboard for Android TV and Google TV devices.

Built with **Kotlin + Jetpack Compose**, featuring a dark sci-fi themed UI with Netflix-style collapsible sidebar navigation.

## Features

- **6 Information Tabs**: Overview, Hardware, Display, Network, Software, Storage
- **Real Device Data**: All information read from Android SDK APIs, no fake data
- **TV Remote Optimized**: Full D-pad navigation with focus management
- **Netflix-style Sidebar**: Collapsible drawer that pushes content with smooth animation
- **Material Icons**: Using Material Icons Extended for consistent iconography
- **Firebase Integration**: Analytics event tracking + Crashlytics crash reporting

## Screenshots

| Overview | Hardware | Network |
|----------|----------|---------|
| ![Overview](docs/screenshots/01_overview.png) | ![Hardware](docs/screenshots/02_hardware.png) | ![Network](docs/screenshots/03_network.png) |

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Min SDK | 21 |
| Icons | Material Icons Extended |
| Analytics | Firebase Analytics |
| Crash Reporting | Firebase Crashlytics |
| Build | Gradle 8.9, AGP 8.7.3 |

## Build

```bash
# Debug APK
ANDROID_HOME=~/Android/Sdk ./gradlew assembleDebug

# Release AAB (for Google Play)
ANDROID_HOME=~/Android/Sdk ./gradlew bundleRelease

# Install to TV emulator
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Firebase Setup

1. Create a Firebase project at https://console.firebase.google.com
2. Add Android app with package name `com.tvinfo.app`
3. Download `google-services.json` to `app/` directory
4. Build and run

See [Firebase Guide](docs/firebase-guide.md) for details.

## Documentation

| Document | Description |
|----------|-------------|
| [Interaction Spec](docs/interaction-spec.md) | UI/UX interaction design specification |
| [Firebase Guide](docs/firebase-guide.md) | Firebase integration and event tracking |
| [Play Store Guide](docs/play-store-guide.md) | Google Play publishing checklist |
| [Store Listing](docs/store-listing.md) | App store description and metadata |
| [Privacy Policy](docs/privacy-policy.html) | User privacy policy |

## License

MIT
