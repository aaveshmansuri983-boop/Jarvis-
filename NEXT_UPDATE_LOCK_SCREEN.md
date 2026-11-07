# Next update roadmap — Lock-screen assistant

These features are planned for a later update. They are NOT included in the current APK yet.

## Lock-screen voice answers
- Answer the current time and date while the phone is locked.
- Read the current battery percentage and charging state.
- Add an optional “Hey Jarvis” wake-word mode.
- Support simple approved commands such as setting an alarm, subject to Android permissions.

## Reliability and privacy requirements
- Implement a foreground microphone service with a visible Android notification and explicit user opt-in.
- Explain microphone, notification, and lock-screen permissions before requesting them.
- Respect Android lock-screen security; never bypass device authentication or expose private notifications.
- Provide a clear stop/pause control and battery-usage settings.
- Test on the Redmi 12 5G / Android version before release.

## Release rule
Do not destabilize the current working build to rush these features. Add them in a separate update after the existing UI, voice, and APK build have been tested.