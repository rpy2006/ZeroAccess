# ZeroAccess

A privacy and performance monitoring utility for Android. ZeroAccess gives you complete visibility into which apps are accessing your sensitive permissions and consuming your device RAM — with one-tap controls to block and kill them.

---

## Features

### Privacy Monitor
- Scans all installed apps for sensitive permission usage — Camera, Microphone, Location, Storage, and Contacts
- Calculates a **Privacy Score** (0–100) based on how many apps have excessive permissions
- Real-time permission event alerts with risk levels (High / Medium / Low)
- Per-app permission breakdown with detailed manifest inspection

### RAM Monitor
- Live system RAM usage — shows used, free, and total memory
- Lists every background process sorted by RAM consumption (PSS)
- Shows process state — Foreground, Service, Visible, Background, Cached
- Per-app RAM bar with colour coding: blue (safe) → amber (moderate) → red (high)
- Kill individual background apps with one tap
- **Force Stop All Background** — kills every non-foreground process in one action

### Permission Control
- Force Block All — opens Android system settings per app to manually revoke permissions
- Quick-block tiles for Location, Camera, Microphone, Contacts, and Storage
- Master block button with active/unblock state
- Night Mode block schedule (23:00 – 06:00)
- Work Mode schedule (09:00 – 17:00)

### App Monitor
- Full list of installed apps with permission summary
- Search and filter by app name
- Tap any app to see its full permission profile
- Alert feed showing recent permission access events

### Insights
- Bar chart breakdown of how many apps use each permission type
- Privacy tips and recommendations

---

## Screenshots

> Home · Apps · RAM Monitor · Insights · Settings

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 34 (Android 14) |
| UI | XML layouts, Material Components 1.12 |
| Navigation | BottomNavigationView + Fragment transactions |
| Data | SharedPreferences via StorageHelper |
| Memory | ActivityManager + Debug.MemoryInfo |
| Permissions | PackageManager + AppOpsManager |
| Build | Gradle 8.4, AGP 8.4.0 |
| IDE | AndroidIDE / Android Studio |

---

## Project Structure

```
ZeroAccess/
└── app/src/main/
    ├── java/com/zeroaccess/
    │   ├── SplashActivity.java
    │   ├── OnboardingActivity.java
    │   ├── MainActivity.java
    │   ├── HomeFragment.java
    │   ├── AppsFragment.java
    │   ├── InsightsFragment.java
    │   ├── SettingsFragment.java
    │   ├── RamMonitorFragment.java
    │   ├── AppDetailActivity.java
    │   ├── ForceBlockActivity.java
    │   ├── AppAdapter.java
    │   ├── AlertAdapter.java
    │   ├── RamAppAdapter.java
    │   ├── AppInfo.java
    │   ├── AlertItem.java
    │   ├── RamAppItem.java
    │   ├── PermissionHelper.java
    │   ├── StorageHelper.java
    │   ├── PermissionMonitorService.java
    │   └── ZeroAccessibilityService.java
    ├── res/
    │   ├── drawable/       — 31 vector icons (ic_*.xml)
    │   ├── layout/         — 13 XML layouts
    │   ├── menu/           — bottom_nav_menu.xml
    │   ├── values/         — colors, themes, strings, dimens
    │   └── xml/            — accessibility_config.xml
    └── AndroidManifest.xml
```

---

## Permissions Used

| Permission | Purpose |
|---|---|
| `PACKAGE_USAGE_STATS` | Read app usage statistics for permission scanning |
| `QUERY_ALL_PACKAGES` | List all installed apps |
| `KILL_BACKGROUND_PROCESSES` | Force-stop background app processes |
| `GET_TASKS` | Read running process list for RAM monitor |
| `FOREGROUND_SERVICE` | Keep permission monitor service alive |
| `POST_NOTIFICATIONS` | Show monitoring notifications |

> All data is processed and stored **locally on device** using SharedPreferences. Nothing is sent to any server.

---

## Setup

### Requirements
- Android device running API 26+ (Android 8.0 Oreo or higher)
- AndroidIDE or Android Studio

### Build

```bash
# Clone or extract the project
cd ZeroAccess

# Build debug APK
./gradlew assembleDebug

# APK output
app/build/outputs/apk/debug/app-debug.apk
```

### First Launch
1. Open the app — complete the 4-slide onboarding
2. Go to **Settings → Usage Access** and grant permission
3. Optionally enable **Accessibility Service** for automation features
4. Return to Home — your Privacy Score will calculate automatically

---

## Navigation

| Tab | Screen |
|---|---|
| Home | Privacy score, permission tiles, quick insights |
| Apps | Installed app list + permission alerts feed |
| RAM | Live RAM usage + background process killer |
| Insights | Permission usage charts + privacy tips |
| Settings | Block controls, schedules, system access, developer info |

---

## Design System

| Token | Value |
|---|---|
| Background | `#0A0A0F` |
| Surface | `#0F0F1A` |
| Divider | `#141420` |
| Accent Blue | `#5B8DEF` |
| Accent Red | `#E05A5A` |
| Text Primary | `#F0F0F8` |
| Text Disabled | `#3D4060` |
| Safe Green | `#22C55E` |
| Warning Amber | `#E8A440` |
| Location | `#E8A440` |
| Microphone | `#8B5CF6` |
| Camera | `#5B8DEF` |
| Storage | `#22C4D0` |
| Contacts | `#22C55E` |

Dark-only UI. All icons are self-contained vector XML drawables — no font dependency.

---

## Architecture Notes

- **Single Activity** — `MainActivity` hosts all fragments via `FragmentContainerView`
- **No ViewModel / LiveData** — background threads post directly to `runOnUiThread` with `isAdded()` guards to prevent crashes on fragment detachment
- **StorageHelper** — centralised SharedPreferences wrapper for alerts, modes, block state, and privacy score
- **PermissionHelper** — stateless utility class for permission scanning, privacy score calculation, and accessibility checks
- **RamMonitorFragment** — batch-fetches all process memory in a single `getProcessMemoryInfo()` call for performance

---

## Known Limitations

- Android does not allow silent permission revocation without root. The Force Block feature opens system settings so you can revoke permissions manually.
- `killBackgroundProcesses()` kills background processes but apps can restart automatically. It is not equivalent to a root-level force stop.
- Usage Stats permission must be granted manually from Settings — Android does not allow runtime prompting for this permission.

---

## Developer

**Rohit Prasad Yadav**
- Instagram: [@yadav_enterprises](https://www.instagram.com/rohit.md)
- Email: rohitprasadyadav06@gmail.com

---

## License

This project is proprietary software developed by Rohit Prasad Yadav. All rights reserved.
