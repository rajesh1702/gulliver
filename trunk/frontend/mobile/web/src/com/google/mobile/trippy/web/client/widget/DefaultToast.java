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

package com.google.mobile.trippy.web.client.widget;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.mobile.trippy.web.client.view.ToastStyleCss;

/**
 * Default implementation for toast
 * 
 * 
 */
public class DefaultToast implements Toast {

  private Label toastLabel;
  private Label loadingLabel;

  private PopupPanel toastPanel;
  private PopupPanel loadingPanel;

  private Timer toastTimer;
  final ToastStyleCss toastStyle;

  private static final int DEFAULT_STATUS_TIME_MILLIS = 5000;

  public DefaultToast() {
    toastStyle = new ToastStyleCss();
    initializeToast();
    initializeLoading();
  }

  public void showToast(String message, int timeInMillis) {
    toastLabel.setText(message);
    toastPanel.setAutoHideEnabled(false);
    toastTimer.schedule(timeInMillis);
    toastPanel.center();
  }

  public void showToast(String message) {
    showToast(message, DEFAULT_STATUS_TIME_MILLIS);
  }

  public void showLoading(String message) {
    loadingLabel.setText(message);
    loadingPanel.setPopupPositionAndShow(new PositionCallback() {

      @Override
      public void setPosition(int offsetWidth, int offsetHeight) {
        int left = (Window.getClientWidth() - loadingPanel.getOffsetWidth()) >> 1;
        int top = 2;
        loadingPanel.setPopupPosition(
            Math.max(Window.getScrollLeft() + left, 0), Window.getScrollTop() + top);
      }
    });
  }

  public void hideLoading() {
    loadingPanel.hide();
  }

  private void initializeLoading() {
    loadingLabel = new Label();
    loadingLabel.setStyleName(toastStyle.style.base());
    loadingPanel = new PopupPanel();
    loadingPanel.add(loadingLabel);
    loadingPanel.setStyleName(toastStyle.style.popUpBase());
  }

  private void initializeToast() {
    toastLabel = new Label();
    toastLabel.setStyleName(toastStyle.style.base());
    toastPanel = new PopupPanel();
    toastPanel.add(toastLabel);

    toastTimer = new Timer() {
      @Override
      public void run() {
        if (toastPanel.isShowing()) {
          toastPanel.hide();
        }
      }
    };
    toastPanel.setStyleName(toastStyle.style.popUpBase());
  }

  @Override
  public void hideToast() {
    toastPanel.hide();
    toastTimer.cancel();
  }
}
