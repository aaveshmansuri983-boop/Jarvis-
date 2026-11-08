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


Google Owner Login setup (GitHub Actions):
1. Google Cloud Console me project banakar Google Auth Platform configure karein aur Google Sign-In enable karein.
2. OAuth Client ID (Web application) banayein. Iska Client ID hi GOOGLE_WEB_CLIENT_ID secret hoga.
3. Android OAuth client bhi banayein: package/application ID com.jarvis.zero aur app ki signing certificate SHA-1 fingerprint ke saath. SHA-1 mismatch ya missing Android OAuth client par Google login fail hoga.
4. GitHub repository > Settings > Secrets and variables > Actions me ye Repository secrets add karein:
   - GOOGLE_WEB_CLIENT_ID = Web application OAuth Client ID (secret nahi, client ID)
   - JARVIS_OWNER_EMAIL = owner ka exact verified Gmail address
5. Secrets save karne ke baad Actions tab me Build Jarvis Zero APK workflow ko Run workflow karein. Successful build ke artifact Jarvis-Zero-debug-apk se naya APK install karein.
6. OAuth consent screen ko testing se production me publish/verify karna zaroori ho sakta hai. Google Cloud Console ke current prompts follow karein.

Cloud AI API key:
- APK install aur owner login ke baad header me ⚙ API button kholein.
- Groq ya OpenRouter select karein, model ID aur API key paste karke Save AI settings dabayein.
- Key GitHub me nahi jaati; is phone ke app WebView local storage me save hoti hai. Public/shared device par key save na karein. Key ko kabhi GitHub commit ya chat me paste na karein.
- API key optional hai for local clock/date functions; cloud AI answers ke liye provider key aur internet chahiye.
