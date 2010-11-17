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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.base.Utils;
import com.google.mobile.trippy.web.client.presenter.header.BaseHeaderOptionsPresenter;
import com.google.mobile.trippy.web.client.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates base header.
 * 
 * 
 */
public class BaseHeaderPresenter implements EventHandlerPresenter<BaseHeaderPresenter.Display> {
  /** Interface for view of header panel. */
  public static interface Display extends View {
    HasClickHandlers getTitleButton();
    HasClickHandlers getNavigationButton();
    HasClickHandlers getSubNavigationButton();
    void setScreenTitleButtonTitle(final String title);
    void setNavigationButtonTitle(final String title);
    void setSubNavigateButtonTitle(final String title);
    void setSubNavigateVisible(final boolean visible);
    void setOptions(final BaseHeaderOptionsPresenter options, final boolean isVisible);
  }

  protected final Display display;
  protected final HandlerManager eventBus;
  protected final List<HandlerRegistration> handlers;
  protected final Utils utils;

  public BaseHeaderPresenter(final Display display, final SingletonComponents singletonComponents) {
    this.display = display;
    this.eventBus = singletonComponents.getEventBus();
    this.handlers = new ArrayList<HandlerRegistration>();
    this.utils = singletonComponents.getUtils();
  }

  /**
   * Bind the presenter and the view.
   */
  @Override
  public void bind() {
    //No-ops
  }

  @Override
  public HandlerManager getEventBus() {
    return eventBus;
  }

  @Override
  public void release() {
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

  public void setTitleString(String title, final Runnable action) {
    utils.scrollToTop();
    display.setScreenTitleButtonTitle(title);
    if (action != null) {
      handlers.add(display.getTitleButton().addClickHandler(new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          action.run();
        }
      }));
    }
  }

  public void setNavigation(final String targetLabel, final Runnable action) {
    display.setNavigationButtonTitle(targetLabel);
    handlers.add(display.getNavigationButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        action.run();
      }
    }));
  }

  public void setSubNavigation(final String targetLabel, final Runnable action) {
    display.setSubNavigateButtonTitle(targetLabel);
    handlers.add(display.getSubNavigationButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        action.run();
      }
    }));
  }

  public void setSubNavigationVisible(final boolean visible) {
    display.setSubNavigateVisible(visible);
  }

  public void setOptions(BaseHeaderOptionsPresenter options, boolean isVisible) {
    display.setOptions(options, isVisible);
  }
}
