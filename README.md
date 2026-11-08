# Jarvis Zero

Android APK build is configured with GitHub Actions.

## Current interface
- Futuristic JARVIS NOVA dashboard with responsive Android-first layout
- Photorealistic holographic assistant avatar (no cartoon/vector character)
- Home, chat, tools, and settings navigation
- Quick actions for time, date, weather search, YouTube, and Google
- Hindi text chat and browser speech-recognition controls

## Build
The workflow builds a debug APK on every push to `main` and uploads it as the `Jarvis-Zero-debug-apk` artifact. Download the artifact from the latest successful run under the repository's Actions tab.

Note: this is a zero-cost starter assistant; some controls open browser/app links and do not provide full device-level automation.

## Planned next update
Lock-screen time/date and battery answers, optional Hey Jarvis wake-word mode, and privacy-safe background voice support are documented in [NEXT_UPDATE_LOCK_SCREEN.md](NEXT_UPDATE_LOCK_SCREEN.md). These are planned only and are not part of the current APK yet.

<!-- Rebuild trigger: Google OAuth client ID configuration -->
