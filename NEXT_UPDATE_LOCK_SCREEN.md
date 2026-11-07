# Next update roadmap — Lock-screen assistant and owner voice security

These features are planned for a later update. They are NOT included in the current APK yet.

## Lock-screen voice answers
- Answer the current time and date while the phone is locked.
- Read the current battery percentage and charging state.
- Add an optional “Hey Jarvis” wake-word mode.
- Support simple approved commands such as setting an alarm, subject to Android permissions.

## Owner voice authentication
- Add an owner voice enrollment flow that records a consented voice sample and creates a local voice profile where feasible.
- Verify the speaker before accepting owner-only personal commands; reject unverified speakers by default.
- Provide a secure owner re-enrollment/reset flow.
- Require Android device unlock or an additional PIN/confirmation for sensitive actions such as calls, messages, account changes, and private data access.
- Treat voice matching as a convenience signal, not foolproof authentication: recordings, replay, and AI voice clones can spoof it. Use anti-spoofing where practical and retain a secure fallback.
- Explain microphone use, storage, and deletion controls clearly; do not silently collect voice samples.

## Reliability and privacy requirements
- Implement a foreground microphone service with a visible Android notification and explicit user opt-in.
- Explain microphone, notification, and lock-screen permissions before requesting them.
- Respect Android lock-screen security; never bypass device authentication or expose private notifications.
- Provide a clear stop/pause control and battery-usage settings.
- Test on the Redmi 12 5G / Android version before release.

## Release rule
Do not destabilize the current working build to rush these features. Add them in a separate update after the existing UI, voice, and APK build have been tested.