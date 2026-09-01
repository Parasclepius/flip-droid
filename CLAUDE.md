# Flipper Android — custom fork

Fork of the official Flipper Zero Android app, imported at release **1.8.1.1878** (tag `upstream-1.8.1.1878`).
Goal is a rebuilt UI/UX, not a rewrite: the BLE/RPC layer is upstream's and stays close to upstream so
firmware-compatibility fixes can be pulled forward.

## Layout

- `components/` — ~270 Gradle modules, almost all split `api` / `impl` (+ occasional `noop` for disabled features).
  Depend on the `api` module and let Dagger/Anvil bind the `impl`.
- `instances/android/app` — the phone app; wires every module together.
- `instances/wearable` — Wear OS companion.
- `build-logic/` — convention plugins (`flipper.android-app`, `flipper.android-compose`, …) and `ApkConfig`.
- `config/detekt/detekt.yml` — lint rules, including a Decompose ruleset. CI enforces these.

Key areas for this fork:

| Area | Where |
| --- | --- |
| Bottom tabs (Device/Archive/Apps/Tools) | `components/bottombar/impl/.../BottomBarTabEnum.kt` |
| Device screen (front page) | `components/info/impl/.../screens/ComposableDeviceInfoScreen.kt` |
| Device metrics model | `components/bridge/connection/feature/rpcinfo/.../FlipperRpcInformation.kt` |
| Capability system | `components/bridge/connection/feature/provider/api/FFeatureProvider.kt` + ~18 sibling features |
| Firmware update sources | `components/updater/downloader/.../DownloaderApiImpl.kt` (single hardcoded endpoint) |
| Archive (key mirror, not the filesystem) | `components/archive/` |
| Filesystem browser | `components/filemngr/` |
| FAP catalog | `components/faphub/` |
| Tools tab (only 2 items) | `components/toolstab/impl/.../ComposableHub.kt` |
| Theme | `components/core/ui/theme/` — note **two** systems, legacy `LocalPallet` and newer `FlipperPalletV2` |

## Building

**Not buildable on the Raspberry Pi.** `aapt2` ships x86_64-only; builds happen on the x86_64 laptop.

Requirements: **JDK 17 or 21** (not 25 — AGP 8.11 rejects it), Android SDK platform 35, ~16 GB RAM
(`gradle.properties` asks for an 8 GB heap). Gradle 8.14.2 comes from the wrapper.

```
git clone --recurse-submodules <repo> && cd flipper-android
echo "sdk.dir=$HOME/Android/Sdk" > local.properties
./gradlew :instances:android:app:assembleDebug
```

Useful flags for a fork — these switch modules to their `noop` variants:

- `-Pis_google_feature=false` — drops Firebase/Wear GMS.
- `-Pis_metric_enabled=false` — drops Sentry + Countly analytics.

`instances/android/app/google-services.json` is still upstream's Firebase project. Replace or remove it.

## Conventions

- Kotlin 2.2, Compose, **Decompose** for navigation (components are `DecomposeComponent` with a `Render()`),
  Dagger + Anvil (`@ContributesBinding`, `@ContributesAssistedFactory`) for DI, Ktor for network, Room for storage.
- Dependencies go through `gradle/libs.versions.toml`. Never hardcode a version in a build script.
- New module? `settings.gradle.kts` must list it, and the app module must depend on it or Anvil won't see it.
- Protobuf definitions are a submodule (`components/bridge/pbutils/src/main/proto`, pinned to tag `0.16`).
  Do not edit them; they mirror firmware.

## Working agreements

- Tag `upstream-1.8.1.1878` is the untouched import. `main` is that plus build fixes; fork work goes on branches.
- Prefer migrating touched screens onto `FlipperPalletV2` rather than extending legacy `LocalPallet`.
