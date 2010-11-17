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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lonelyplanet.trippy.android.TrippyActivity;
import com.lonelyplanet.trippy.android.framework.SimpleWebViewInitializer.SimpleWebChromeClient;

/**
 * Sets up a webview for Trippy AppEngine application
 */
public class TrippyWebViewInitializer {

  /**
   * WebViewClient for Trippy:
   * 1) Shows message for offline.
   * 2) Handles telephone URIs and external http
   *
   */
  public static class TrippyWebViewClient extends WebViewClient {
    TrippyWebViewClient(Activity activity, String logTag) {
      this.activity = activity;
      this.logTag = logTag;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
      Log.d(logTag, "start loading page...." + url);
      super.onPageStarted(view, url, favicon);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      Log.d(logTag, "override url..." + url);
      if (url.startsWith("tel:")) {
        Intent call = new Intent(Intent.ACTION_DIAL);
        call.setData(Uri.parse(url));
        activity.startActivity(call);
        return true;
      }
      if (url.startsWith(AppEngineAccountManager.LOGOUT_URL)) {
        activity.finish();
        return false;
      }
      if ((url.startsWith("http://") || url.startsWith("https://")) &&
          !url.startsWith(TrippyActivity.URL) &&
          !url.startsWith(AppEngineAccountManager.LOGIN_URL)) {
        Intent browse = new Intent(Intent.ACTION_VIEW);
        browse.setData(Uri.parse(url));
        activity.startActivity(browse);
        return true;
      }
      return false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      Log.d(logTag, "finish loading page...." + url);
      super.onPageFinished(view, url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description,
          String failingUrl) {
      Log.d(logTag, "onReceivedError" + errorCode);
     super.onReceivedError(view, errorCode, description, failingUrl);
     view.clearHistory();
     view.loadUrl(TrippyActivity.OFFLINE_URL);
    }

    private final Activity activity;
    private final String logTag;
  }

  /**
   * WebChromeClient for Trippy:
   * Dismisses progress dialog.
   *
   */
  public static class TrippyWebChromeClient extends SimpleWebChromeClient {
    public TrippyWebChromeClient(Activity activity, String logTag) {
      super(activity);
      this.logTag = logTag;
    }

    @Override
    public boolean onCreateWindow (WebView view, boolean dialog, boolean userGesture,
         Message resultMsg) {
      Log.d(logTag, "Create window" + dialog + " " + resultMsg);
      return false;
    }

    private String logTag;
  }

  private TrippyWebViewInitializer() {}

  /**
   * Sets up a webview for Trippy:
   * 1) Enable Javascript and HTML5
   * 2) Sets up Scrollbar
   * 3) Sets up key bindings
   * 4) Initializes user agent
   * 5) Shows message for offline.
   * 6) Shows progress dialog on initial load.
   * 7) Handles telephone URIs.
   * 8) Loads Splash Screen.
   * @param activity activity for getting data files and launching child activities.
   * @param logTag tag to use for logging.
   * @param userAgent if non-null, prefix to default user agent string
   * @param webview WebView to set up.
   */
  public static void initialize(final Activity activity, final String logTag,
      final String userAgent, final WebView webview) {
    SimpleWebViewInitializer.initialize(activity, webview,
        new TrippyWebChromeClient(activity, logTag));

    webview.setScrollbarFadingEnabled(true);
    webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
    webview.setOnKeyListener(new OnKeyListener() {
      public boolean onKey(View unusedView, int keyCode, KeyEvent event) {
        if ((event.getEventTime() > lastKeyEventTime + 500) &&
            (KeyEvent.KEYCODE_BACK == keyCode || KeyEvent.KEYCODE_SEARCH == keyCode)) {
          lastKeyEventTime = event.getEventTime();
          if (KeyEvent.KEYCODE_BACK == keyCode && webview.canGoBack()) {
            webview.goBack();
            return true;
          }
          if (KeyEvent.KEYCODE_SEARCH == keyCode) {
            Log.d(logTag, "Search!");
            webview.loadUrl("javascript:doSearch()");
            return true;
          }
        }
        return false;
      }
      private long lastKeyEventTime = 0;
    });

    final WebSettings s = webview.getSettings();
    s.setUserAgentString(userAgent + " " + s.getUserAgentString());
    ConnectivityManager mgr = (ConnectivityManager) activity.getSystemService(
        Context.CONNECTIVITY_SERVICE);
    if (!mgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected() &&
        !mgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
      Log.d(logTag, "Loading Trippy from cache");
      s.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }
    final TrippyWebViewClient client = new TrippyWebViewClient(activity, logTag);
    webview.setWebViewClient(client);
    webview.loadUrl(TrippyActivity.SPLASHSCREEN_URL);
  }

  public static GestureDetector.OnGestureListener getGestureListener(
      final WebView webview, final String logTag) {
    return new SimpleOnGestureListener() {
      private static final int SWIPE_MIN_DISTANCE = 100;
      public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float distance = e2.getX() - e1.getX();
        if (distance > SWIPE_MIN_DISTANCE) {
          webview.loadUrl("javascript:swipeRight()");
          Log.d(logTag, "Swipe Right");
          return true;
        } else if (distance < -SWIPE_MIN_DISTANCE) {
          webview.loadUrl("javascript:swipeLeft()");
          Log.d(logTag, "Swipe Left");
          return true;
        } else {
          return false;
        }
      }
    };
  }
}
