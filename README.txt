JARVIS ZERO v3 — FREE ANDROID-FRIENDLY WEB PROTOTYPE

Included:
- Hindi speech input and speech output (browser/device support required)
- In-app manual voice mode
- Hey Jarvis wake phrase while the page is open and recognition remains active
- Google search, YouTube, Instagram Reels page
- Phone dialer launch using tel: (user confirms/places the call)
- WhatsApp chat link; user chooses/authorizes sending in WhatsApp
- Basic time/date and help commands
- No paid API key, subscription, or backend

IMPORTANT:
This is a web app source package, NOT an APK. It does not provide unrestricted phone control.
The web app cannot reliably listen when screen is locked, browser is backgrounded/closed, or Android stops it. Reliable background wake-word needs a native Android foreground service and microphone permission.
It cannot autonomously scroll Instagram Reels or send WhatsApp messages without user interaction. Android accessibility automation would require a separate native implementation and explicit user enablement. Calls open the dialer; they are not silently placed.

Run:
1. Extract ZIP.
2. Serve the app/ folder from HTTPS, or use a local development web server.
3. Open index.html in a supported Android browser (Chrome recommended).
4. Allow microphone permission.
5. Tap बोलना शुरू करें or enable Hey Jarvis while app remains open.

Example commands:
- Hey Jarvis, YouTube kholo
- Google par cricket search karo
- Instagram reels kholo
- Call 9876543210 (opens dialer; confirm there)
- WhatsApp kholo
