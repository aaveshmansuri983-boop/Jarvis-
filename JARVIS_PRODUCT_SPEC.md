# JARVIS NOVA — Product & Engineering Brief

This file is the source of truth for the next JARVIS NOVA update. Implement incrementally and preserve working features. Do not make cosmetic-only changes while claiming native capabilities are complete.

## Product vision
Build a polished, Iron-Man-inspired Android personal assistant with a minimal holographic home screen and voice-first interaction. The app must remain stable, understandable, accessible, and honest about Android platform limitations.

## Experience and design
- Make the JARVIS home screen the primary experience: dark premium interface, centered animated cyan/blue holographic core, restrained glow, subtle motion, readable typography, and clean spacing.
- Show useful glanceable information (time/date and available device status) without clutter.
- Keep visible controls minimal: microphone/listening state, permission/setup, and a clear way to exit or return to the normal launcher.
- Provide clear visual states: idle, listening, processing, speaking, permission required, offline/error.
- Ensure responsive layouts on common Android phone sizes; no clipped controls, overlapping text, broken avatar/assets, or duplicate chat messages.
- Support Hindi and Hinglish speech/text where the device speech services support them. Speak replies aloud and provide readable text fallback.

## Voice interaction
- Support tap-to-talk reliably first. Add wake phrase “Hey Jarvis” only through a properly implemented native Android component where supported.
- Do not claim continuous background or lock-screen listening unless it is implemented, permissioned, tested, and compliant with Android foreground-service and microphone rules.
- Explain microphone permission and show an unmistakable listening indicator with a stop control.
- Avoid unintended activation; handle recognition errors, silence, network/offline conditions, and lifecycle interruptions gracefully.

## Owner-only behavior and safety
- Owner voice recognition is a best-effort additional signal, not guaranteed identity proof. Do not promise that voice matching cannot be spoofed or that it will always reject every other speaker.
- For sensitive actions, require device authentication or explicit confirmation; never place calls, send messages, make purchases, or change security settings solely from an uncertain voice match.
- Never silently record or transmit audio. Minimize retained audio and disclose any processing.
- Do not hardcode owner email, secrets, API keys, subscription entitlements, or credentials in public client code. Owner/premium entitlement must be verified securely on a trusted backend after authenticated sign-in. If backend verification is not available, leave the feature clearly marked as not configured rather than granting access based on a client-side email check.

## Android launcher and lock screen
- A true JARVIS Home experience requires a native Android launcher activity with the HOME category and the user selecting it as their default launcher in Android settings.
- Always preserve a safe route back to the normal Android launcher. Never hide, disable, or interfere with the system launcher, emergency functions, device lock screen, or system navigation.
- Lock-screen responses must respect Android keyguard/privacy restrictions. Do not expose private notifications, personal data, or sensitive actions while the device is locked.
- Background wake-word support must use appropriate Android foreground-service declarations, runtime permissions, visible notification, lifecycle handling, and battery-conscious behavior. If unsupported on a device/OS version, provide tap-to-talk fallback.

## Engineering and stability rules
- Inspect the existing project and current behavior before changing files. Keep changes small, focused, and reversible.
- Preserve the existing application ID, signing/build configuration, permissions, and working screens unless a change is required and explained.
- Do not add fake buttons, placeholder integrations presented as working, or hardcoded success responses.
- Handle missing assets and network/API failures gracefully. Avoid duplicate event listeners and duplicate chat submissions.
- Keep secrets out of Git. Validate all external input and use secure network transport.
- Update documentation when behavior or permissions change.

## Delivery and acceptance checklist
1. App builds successfully with the repository's Android Gradle workflow.
2. Install and launch the APK on a real Android device; verify the main screen and navigation.
3. Verify responsive UI, asset fallback, chat scrolling, and no duplicate messages.
4. Verify tap-to-talk, Hindi/Hinglish recognition where available, spoken reply, permission denial, and microphone stop behavior.
5. Test background/lock-screen behavior separately on supported Android versions; document limitations and fallback.
6. Verify launcher selection and a safe return to the user's normal launcher.
7. Confirm no secrets or owner credentials are committed.
8. Report exactly what was implemented, what was tested, what failed, and what remains. Never describe an untested feature as complete.

## Priority order
P0: build stability, polished JARVIS home UI, reliable tap-to-talk, speech replies, permission/error handling.
P1: native launcher experience with safe exit, Hindi/Hinglish refinements, device-status cards.
P2: wake phrase/background listening and lock-screen interaction, only after native Android implementation and device testing.
P3: secure account/owner entitlements and subscriptions, only with a trusted backend and verified authentication.
