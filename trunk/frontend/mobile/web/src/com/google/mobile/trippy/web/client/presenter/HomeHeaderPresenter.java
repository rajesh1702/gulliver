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

package com.google.mobile.trippy.web.client.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.presenter.header.LogOutOptionsPresenter;
import com.google.mobile.trippy.web.client.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates header for home page.
 * 
 * 
 */
public class HomeHeaderPresenter implements EventHandlerPresenter<HomeHeaderPresenter.Display> {
  /** Interface for view of header panel. */
  public static interface Display extends View {
    void setOptions(final LogOutOptionsPresenter options, final boolean isVisible);
  }

  protected final Display display;
  protected final SingletonComponents singletonComponents;
  protected final List<HandlerRegistration> handlers;
  private final LogOutOptionsPresenter headerOptions;

  public HomeHeaderPresenter(final Display display, final SingletonComponents singletonComponents,
      final Provider<LogOutOptionsPresenter> headerOptionsProvider) {
    this.display = display;
    this.singletonComponents = singletonComponents;
    this.handlers = new ArrayList<HandlerRegistration>();
    this.headerOptions = headerOptionsProvider.get();
  }

  /**
   * Bind the presenter and the view.
   * 
   * Listen for user events on display and take action. Also listen for
   * appropriate application events and update display accordingly
   */
  @Override
  public void bind() {
    headerOptions.bind();
  }

  @Override
  public HandlerManager getEventBus() {
    return singletonComponents.getEventBus();
  }

  @Override
  public void release() {
    headerOptions.release();
    for (HandlerRegistration handler : handlers) {
      if (handler != null) {
        handler.removeHandler();
      }
    }
    handlers.clear();
  }

  @Override
  public Display getDisplay() {
    return display;
  }
  
  public void populateView() {
    display.setOptions(headerOptions, true);
  }
}
