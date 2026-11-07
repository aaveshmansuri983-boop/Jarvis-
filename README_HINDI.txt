JARVIS ZERO ANDROID PROJECT

यह शुरुआती Android APK wrapper है जिसमें JARVIS ZERO का मौजूदा HTML UI Android WebView के अंदर चलता है।

Build:
1. इस ZIP को extract करें।
2. GitHub repository में JARVIS_ZERO_Android नाम का folder और उसके अंदर की सभी फाइलें upload करें।
3. GitHub में .github/workflows/build-apk.yml नाम से workflow file बनाएं और साथ वाली JARVIS_ZERO_GITHUB_WORKFLOW.yml की सामग्री उसमें paste करें।
4. Actions tab > Build JARVIS ZERO APK > Run workflow.
5. सफल build के बाद Artifacts में JARVIS-ZERO-debug-apk डाउनलोड करें।

सीमाएँ:
- यह APK wrapper है; AI brain / LLM backend इसमें शामिल नहीं है।
- Voice recognition Android WebView/browser support पर निर्भर है और microphone permission माँगेगा।
- Hey Jarvis केवल ऐप खुला रहने पर best-effort है; स्क्रीन लॉक/बैकग्राउंड में हमेशा सुनना उपलब्ध नहीं है।
- Calls/WhatsApp/Instagram जैसे काम मौजूदा HTML की सीमित link/open सुविधाओं तक सीमित हैं; यह पूरे फोन का unrestricted control नहीं है।
