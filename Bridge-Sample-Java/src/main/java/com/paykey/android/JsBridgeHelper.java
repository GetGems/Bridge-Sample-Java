package com.paykey.android;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.util.HashMap;

/**
 * Created by talkol on 12/20/14.
 */
public class JsBridgeHelper
{
    private WebView webView;
    private HashMap<String,JsCallback> activeCalls;

    // singleton
    private static volatile JsBridgeHelper Instance = null;
    public static JsBridgeHelper getInstance() {
        JsBridgeHelper localInstance = Instance;
        if (localInstance == null) {
            synchronized (JsBridgeHelper.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new JsBridgeHelper();
                }
            }
        }
        return localInstance;
    }

    private JsBridgeHelper()
    {
        this.activeCalls = new HashMap<>();
        this.webView = new WebView(ApplicationLoader.applicationContext);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.setWebChromeClient(new JsChromeClient());
        addApi16WebViewSettings();
        this.webView.addJavascriptInterface(new AndroidJavascriptInterface(), "androidInterface");
        this.webView.loadUrl("file:///android_asset/bridge/index.html");
    }

    @TargetApi(16)
    private void addApi16WebViewSettings()
    {
        if (Build.VERSION.SDK_INT >= 16)
        {
            // we might need loadDataWithBaseURL in older APIs
            this.webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }
    }

    public void executeJsFunction(String funcName, String arg, JsCallback callback)
    {
        String callId = String.valueOf(Utilities.random.nextLong());
        this.activeCalls.put(callId, callback);
        String jsUrl = "javascript:androidBridge.nativeToJs('" + callId + "','" + funcName + "','" + arg + "')";
        this.webView.loadUrl(jsUrl);
    }

    private void jsFunctionReturned(String callId, final String res)
    {
        final JsCallback callback = this.activeCalls.remove(callId);
        if (callback != null)
        {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
                @Override
                public void run()
                {
                    callback.onResult(res);
                }
            });
        }
    }

    // classes and interfaces

    private class AndroidJavascriptInterface
    {
        @JavascriptInterface
        public void callback(String callId, String res)
        {
            jsFunctionReturned(callId, res);
        }
    }

    private class JsChromeClient extends WebChromeClient
    {
        @Override
        public boolean onConsoleMessage(ConsoleMessage cm)
        {
            Log.d("JsBridge", String.format("%s @ %d: %s", cm.message(), cm.lineNumber(), cm.sourceId()));
            return true;
        }
    }

    public static interface JsCallback
    {
        public void onResult(String res);
    }
}
