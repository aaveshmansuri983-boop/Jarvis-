JARVIS ZERO ANDROID PROJECT (APK BUILD PROJECT)

यह Android Studio/AndroidIDE project है, APK नहीं।

Build:
1. AndroidIDE या Android Studio में इस पूरे JarvisAndroid folder को खोलें।
2. Gradle sync पूरा होने दें (पहली बार इंटरनेट और Android SDK डाउनलोड की जरूरत होगी)।
3. Build > Build APK(s) चुनें।
4. APK app/build/outputs/apk/debug/app-debug.apk में मिलेगा।

वर्तमान स्थिति:
- v3 वेब UI Android WebView में पैक है।
- ऐप के अंदर browser speech recognition device/WebView support पर निर्भर है।
- यह project अभी native always-on Hey Jarvis background wake-word service नहीं देता।
- Calls dialer खोलती हैं; WhatsApp message user review/send करता है; Reels automatic scrolling implement नहीं है।
- Accessibility automation, native wake-word foreground service और robust offline AI अगले development चरण हैं।
- कोई paid API key शामिल नहीं है।
