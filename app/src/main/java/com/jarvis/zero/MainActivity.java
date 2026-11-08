package com.jarvis.zero;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
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


    private class CloudAiBridge {
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
                    body.put("temperature", 0.7);
                    body.put("max_tokens", 1200);
                    JSONArray messages = new JSONArray();
                    messages.put(new JSONObject().put("role", "system").put("content",
                        "You are Jarvis, a helpful Hindi and Hinglish personal AI assistant. Answer accurately, explain uncertainty, and never claim to perform device actions unless the app confirms them. Be open-minded but do not assist serious harm."));
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
