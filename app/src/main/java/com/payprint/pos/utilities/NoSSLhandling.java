package com.payprint.pos.utilities;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NoSSLhandling extends WebViewClient {
    @Override
    public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error){
        handler.proceed();
    }
}
