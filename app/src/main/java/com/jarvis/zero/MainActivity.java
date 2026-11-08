package com.jarvis.zero;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.content.ClipboardManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private WebView web;
    private PermissionRequest pendingRequest;
    private GoogleSignInClient googleClient;
    private static final int GOOGLE_SIGN_IN = 72;
    private static final int WHATSAPP_CONTACT_PICK = 73;
    private String pendingWhatsAppMessage = "";

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        web = new WebView(this);
        setContentView(web);
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        web.setWebViewClient(new WebViewClient());
        web.addJavascriptInterface(new CloudAiBridge(), "JarvisNative");
        if (!BuildConfig.GOOGLE_WEB_CLIENT_ID.isEmpty()) {
            GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID).build();
            googleClient = GoogleSignIn.getClient(this, options);
        }
        web.setWebChromeClient(new WebChromeClient() {
            @Override public void onPermissionRequest(PermissionRequest request) {
                runOnUiThread(() -> {
                    boolean audio = false;
                    for (String resource : request.getResources()) {
                        if (PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(resource)) audio = true;
                    }
                    if (audio && checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        request.grant(new String[]{PermissionRequest.RESOURCE_AUDIO_CAPTURE});
                    } else {
                        pendingRequest = request;
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 41);
                    }
                });
            }
        });
        web.loadUrl("file:///android_asset/index.html");
    }



    private void authResult(boolean ok, String message) {
        if (web != null) web.evaluateJavascript("window.onJarvisGoogleAuth(" + ok + "," +
            JSONObject.quote(message) + ")", null);
    }

    private void verifyGoogleToken(String token) {
        new Thread(() -> {
            HttpURLConnection c = null;
            try {
                URL url = new URL("https://oauth2.googleapis.com/tokeninfo?id_token=" +
                    URLEncoder.encode(token, "UTF-8"));
                c = (HttpURLConnection) url.openConnection();
                c.setConnectTimeout(12000);
                c.setReadTimeout(12000);
                int code = c.getResponseCode();
                if (code != 200) throw new Exception("Google token verification failed.");
                StringBuilder out = new StringBuilder();
                try (BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8))) {
                    String line; while ((line = r.readLine()) != null) out.append(line);
                }
                JSONObject info = new JSONObject(out.toString());
                String email = info.optString("email", "");
                boolean verified = "true".equalsIgnoreCase(info.optString("email_verified", "false"));
                String audience = info.optString("aud", "");
                if (!verified || !BuildConfig.GOOGLE_WEB_CLIENT_ID.equals(audience)) {
                    throw new Exception("Google account verification incomplete.");
                }
                boolean isOwner = !BuildConfig.OWNER_EMAIL.isEmpty() && BuildConfig.OWNER_EMAIL.equalsIgnoreCase(email);
                runOnUiThread(() -> authResult(true, email + "|" + (isOwner ? "owner" : "free")));
            } catch (Exception e) {
                String msg = e.getMessage() == null ? "Google sign-in failed." : e.getMessage();
                runOnUiThread(() -> authResult(false, msg));
            } finally {
                if (c != null) c.disconnect();
            }
        }).start();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WHATSAPP_CONTACT_PICK) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                String phone = "";
                try (Cursor cursor = getContentResolver().query(data.getData(),
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) phone = cursor.getString(0);
                } catch (Exception ignored) { }
                String digits = phone == null ? "" : phone.replaceAll("[^0-9]", "");
                if (digits.startsWith("0")) digits = digits.substring(1);
                if (digits.length() == 10) digits = "91" + digits;
                if (digits.length() < 10) {
                    whatsappResult(false, "Selected contact ka valid phone number nahi mila.");
                } else {
                    try {
                        String url = "https://wa.me/" + digits + "?text=" + URLEncoder.encode(pendingWhatsAppMessage, "UTF-8");
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        whatsappResult(true, "WhatsApp chat khul gayi. Message bhejne ke liye WhatsApp mein Send dabayein.");
                    } catch (Exception e) {
                        whatsappResult(false, "WhatsApp open nahi hua. Kya WhatsApp installed hai?");
                    }
                }
            } else {
                whatsappResult(false, "Contact selection cancel kar di.");
            }
            pendingWhatsAppMessage = "";
            return;
        }
        if (requestCode == GOOGLE_SIGN_IN) {
            if (resultCode != RESULT_OK || data == null) {
                authResult(false, "Google sign-in cancel hua ya screen se response nahi mila. Dobara try karein.");
                return;
            }
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account.getIdToken() == null) throw new Exception("Google ID token missing. OAuth setup check karein.");
                verifyGoogleToken(account.getIdToken());
            } catch (Exception e) {
                String detail = e instanceof ApiException ? "Google sign-in error (status " + ((ApiException)e).getStatusCode() + "). Check Android OAuth client package com.jarvis.zero and SHA-1 fingerprint in Google Cloud." : (e.getMessage() == null ? "Google sign-in cancelled." : e.getMessage());
                authResult(false, detail);
            }
        }
    }

    private void whatsappResult(boolean ok, String message) {
        if (web != null) web.evaluateJavascript("window.onJarvisWhatsAppResult(" + ok + "," +
            JSONObject.quote(message) + ")", null);
    }

    private class CloudAiBridge {


        @JavascriptInterface
        public void pickWhatsAppContact(String message) {
            runOnUiThread(() -> {
                pendingWhatsAppMessage = message == null ? "" : message;
                try {
                    Intent pick = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    startActivityForResult(pick, WHATSAPP_CONTACT_PICK);
                } catch (Exception e) {
                    whatsappResult(false, "Phone Contacts app open nahi ho paayi.");
                }
            });
        }

        @JavascriptInterface
        public String getClipboardText() {
            try {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                if (clipboard == null || !clipboard.hasPrimaryClip()) return "";
                ClipData clip = clipboard.getPrimaryClip();
                if (clip == null || clip.getItemCount() == 0) return "";
                CharSequence value = clip.getItemAt(0).coerceToText(MainActivity.this);
                return value == null ? "" : value.toString();
            } catch (Exception ignored) {
                return "";
            }
        }

        @JavascriptInterface
        public void checkGoogleSession() {
            runOnUiThread(() -> {
                if (googleClient == null) { authResult(false, "Google OAuth setup missing."); return; }
                googleClient.silentSignIn().addOnCompleteListener(MainActivity.this, task -> {
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account == null || account.getIdToken() == null) {
                            authResult(false, "Please sign in with owner Google account.");
                        } else {
                            verifyGoogleToken(account.getIdToken());
                        }
                    } catch (Exception e) {
                        authResult(false, "Please sign in with owner Google account.");
                    }
                });
            });
        }

        @JavascriptInterface
        public void startGoogleSignIn() {
            runOnUiThread(() -> {
                if (googleClient == null) { authResult(false, "Google OAuth setup missing."); return; }
                try {
                    startActivityForResult(googleClient.getSignInIntent(), GOOGLE_SIGN_IN);
                } catch (Exception e) {
                    authResult(false, "Google sign-in screen nahi khul saki: " +
                        (e.getMessage() == null ? "Unknown error" : e.getMessage()));
                }
            });
        }

        @JavascriptInterface
        public String openApp(String packageName) {
            try {
                String[] allowed = {"com.whatsapp", "com.whatsapp.w4b", "com.google.android.youtube", "com.instagram.android"};
                boolean permitted = false;
                for (String item : allowed) if (item.equals(packageName)) permitted = true;
                if (!permitted) return "BLOCKED";
                android.content.Intent launch = getPackageManager().getLaunchIntentForPackage(packageName);
                if (launch == null) return "NOT_INSTALLED";
                launch.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(launch);
                return "OK";
            } catch (Exception e) {
                return e.getMessage() == null ? "OPEN_FAILED" : e.getMessage();
            }
        }

        @JavascriptInterface
        public void askAI(String requestId, String endpoint, String model, String apiKey, String prompt) {
            new Thread(() -> {
                HttpURLConnection connection = null;
                String result = "";
                boolean success = false;
                try {
                    URL url = new URL(endpoint);
                    String host = url.getHost();
                    if (!"https".equalsIgnoreCase(url.getProtocol()) ||
                        !(host.equals("openrouter.ai") || host.equals("api.groq.com"))) {
                        throw new IllegalArgumentException("Only official HTTPS OpenRouter/Groq endpoints are allowed.");
                    }
                    if (apiKey == null || apiKey.trim().isEmpty() || model == null || model.trim().isEmpty()) {
                        throw new IllegalArgumentException("API key aur model zaroori hain.");
                    }
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(15000);
                    connection.setReadTimeout(45000);
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Authorization", "Bearer " + apiKey);
                    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                    JSONObject body = new JSONObject();
                    body.put("model", model);
                    body.put("temperature", 0.65);
                    body.put("max_tokens", 1800);
                    JSONArray messages = new JSONArray();
                    messages.put(new JSONObject().put("role", "system").put("content",
                        "You are JARVIS NOVA, the user's capable personal AI assistant. Speak naturally in the user's language, especially Hindi/Hinglish; understand casual spelling, speech-to-text errors, and short follow-ups. Be intelligent, practical, warm, direct, and concise by default, but explain deeply when asked. Use the conversation context to resolve references and remember the current task within this chat. For questions requiring current facts, clearly say when you cannot verify live information; never invent news, prices, sources, device state, or results. Give step-by-step Android guidance when useful. Distinguish advice from actions: never claim you opened apps, changed settings, sent messages, set alarms, accessed files, or controlled the phone unless a connected app/tool explicitly confirms it. If a capability is unavailable, say so and give the safest workable alternative. Ask only one short clarification when essential. Respect the user's privacy and owner-only access; never reveal secrets or ask them to paste API keys into chat. Be open-minded, but do not provide instructions that facilitate serious harm, abuse, or illegal activity."));
                    messages.put(new JSONObject().put("role", "user").put("content", prompt));
                    body.put("messages", messages);
                    byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
                    try (OutputStream os = connection.getOutputStream()) { os.write(bytes); }
                    int code = connection.getResponseCode();
                    java.io.InputStream stream = code >= 200 && code < 300 ? connection.getInputStream() : connection.getErrorStream();
                    StringBuilder response = new StringBuilder();
                    if (stream != null) {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                            String line; while ((line = reader.readLine()) != null) response.append(line);
                        }
                    }
                    result = response.toString();
                    success = code >= 200 && code < 300;
                    if (!success) result = "HTTP " + code + ": " + result;
                } catch (Exception e) {
                    result = e.getMessage() == null ? "Network request failed" : e.getMessage();
                } finally {
                    if (connection != null) connection.disconnect();
                }
                final boolean ok = success;
                final String payload = result;
                runOnUiThread(() -> {
                    if (web != null) web.evaluateJavascript("window.onJarvisAIResult(" +
                        JSONObject.quote(requestId) + "," + ok + "," + JSONObject.quote(payload) + ")", null);
                });
            }).start();
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == 41 && pendingRequest != null) {
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                pendingRequest.grant(new String[]{PermissionRequest.RESOURCE_AUDIO_CAPTURE});
            } else {
                pendingRequest.deny();
            }
            pendingRequest = null;
        }
    }

    @Override public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }
}
