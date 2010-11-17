/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lonelyplanet.trippy.android.framework;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Sets up a simple webview enabling Javascript and HTML5
 */
public class SimpleWebViewInitializer {
  /**
   * Max size for HTML5 application cache.
   */
  private static final long APP_CACHE_MAX_SIZE = Long.MAX_VALUE;

  /**
   * Simple Web Chrome Client that alerts JavaScript Errors
   *
   */
  public static class SimpleWebChromeClient extends WebChromeClient {
    public SimpleWebChromeClient(Activity activity) {
      this.activity = activity;
    }

    @Override
    public void onProgressChanged(WebView view, int progress) {
      // Activities and WebViews measure progress with different scales.
      // The progress meter will automatically disappear when we reach 100%
      activity.setProgress(progress * 100);
    }
    
    @Override
    public boolean onJsAlert(WebView view, String url, String message,
          final android.webkit.JsResult result)   {
      new AlertDialog.Builder(activity)
      .setTitle("Alert")
      .setMessage(message)
      .setPositiveButton(android.R.string.ok,
          new AlertDialog.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          result.confirm();
        }
      })
      .setCancelable(false)
      .create()
      .show();
      return true;
    }

    protected Activity activity;
  }

  private SimpleWebViewInitializer() {}

  /**
   * Sets up a simple webview enabling Javascript and HTML5
   * @param activity activity for getting data files and sending alert dialogs.
   * @param webview WebView to set up.
   */
  public static void initialize(final Activity activity,
                                WebView webview) {
    initialize(activity, webview, new SimpleWebChromeClient(activity));
  }

  /**
   * Sets up a simple webview enabling Javascript and HTML5
   * @param activity activity for getting data files.
   * @param webview WebView to set up.
   * @param webChromeClient WebChromeClient to use.
   */
  public static void initialize(final Activity activity,
                                WebView webview,
                                WebChromeClient webChromeClient) {
    final WebSettings s = webview.getSettings();
    // HTML5 API flags
    s.setAppCacheMaxSize(APP_CACHE_MAX_SIZE);
    s.setAppCachePath(activity.getDir("appcache", 0).getPath());
    s.setAppCacheEnabled(true);
    s.setDatabasePath(activity.getDir("databases", 0).getPath());
    s.setDatabaseEnabled(true);
    s.setDomStorageEnabled(true);
    s.setDatabasePath(activity.getDir("geolocation", 0).getPath());
    s.setGeolocationEnabled(true);
    // JavaScript and form flags
    s.setJavaScriptEnabled(true);
    s.setSaveFormData(true);
    s.setSavePassword(true);

    webview.setWebChromeClient(webChromeClient);
  }
}
