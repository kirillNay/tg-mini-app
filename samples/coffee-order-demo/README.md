# Coffee Order Demo

Example Compose Multiplatform application that lives in `samples/` and demonstrates how to structure a project when:

- shared UI and business state live in `commonMain`
- `tg-mini-app` is used only from `jsMain`
- Android and iOS reuse the same screens through demo hosts without direct Telegram dependencies

## Structure

- `composeApp/src/commonMain` - shared UI, catalog, cart, checkout, settings, platform bridge contract
- `composeApp/src/jsMain` - real Telegram Mini App host, `telegramWebApp`, WebApp buttons, CloudStorage/localStorage bridge
- `composeApp/src/androidMain` - Android demo host
- `composeApp/src/iosMain` - iOS `MainViewController` for a native host app

## Run

All commands below are expected from the repository root:

### Web

Development server:

```bash
./gradlew -p samples/coffee-order-demo :composeApp:jsBrowserDevelopmentRun
```

Production bundle:

```bash
./gradlew -p samples/coffee-order-demo :composeApp:jsBrowserProductionWebpack
```

For a real Telegram Mini App, point your web host to the generated web bundle and open it from Telegram. The web sample uses `telegramWebApp` only in `jsMain`.

### Android

Before the first Android build, point Gradle to your SDK in one of two ways:

- set `ANDROID_HOME`
- or create `samples/coffee-order-demo/local.properties` with `sdk.dir=/absolute/path/to/Android/sdk`

Build debug APK:

```bash
./gradlew -p samples/coffee-order-demo :composeApp:assembleDebug
```

Install to a connected device or emulator:

```bash
./gradlew -p samples/coffee-order-demo :composeApp:installDebug
```

You can also open `samples/coffee-order-demo` as a Gradle project in Android Studio and run the `composeApp` Android configuration.

### iOS

Build the iOS simulator framework:

```bash
./gradlew -p samples/coffee-order-demo :composeApp:linkDebugFrameworkIosSimulatorArm64
```

The shared iOS entry point is:

- `composeApp/src/iosMain/kotlin/com/kirillnay/tgminiapp/samples/coffee/MainViewController.kt`

To run it from Xcode, create a minimal iOS host app and use Gradle's generated embed task:

```bash
./gradlew -p samples/coffee-order-demo :composeApp:embedAndSignAppleFrameworkForXcode
```

Minimal SwiftUI wrapper for the host app:

```swift
import SwiftUI
import CoffeeOrderDemo

struct ComposeRoot: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

@main
struct SampleHostApp: App {
    var body: some Scene {
        WindowGroup {
            ComposeRoot()
                .ignoresSafeArea(.all)
        }
    }
}
```

## What the sample demonstrates

- shared catalog, cart, checkout, and settings screens
- platform-specific bridge abstraction for host features
- Telegram back button and main button wiring on Web
- Telegram CloudStorage usage with localStorage fallback on Web
- native demo hosts on Android and iOS that reuse the same Compose UI
