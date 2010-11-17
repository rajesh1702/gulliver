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

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.mobile.trippy.web.client.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for menu item
 * 
 *
 */
public class MenuItemPresenter implements EventHandlerPresenter<MenuItemPresenter.Display> {

  /** Interface for view of this presenter*/
  public interface Display extends View {
    HasClickHandlers getMenuItem();
    void setLabel(String label);
    void setIcon (ImageResource icon);
    void setLabelVisible(boolean visible);
    void setIconVisible(boolean visible);
    void setDisabled(boolean visible);
  }
  
  private final Display display;
  private final List<HandlerRegistration> handlers;
  private ClickHandler clickHandler;
  
  /**
   * Constructor
   */
  public MenuItemPresenter(Display display) {
    this.display = display;
    this.handlers = new ArrayList<HandlerRegistration>();
  }
  
  public void setContent(String label, ImageResource icon, ClickHandler action) {
    clickHandler = action;
    
    if (label == null) {
      display.setLabelVisible(false);
    } else {
      display.setLabel(label);
    }
    
    if (icon == null) {
      display.setIconVisible(false);
    } else {
      display.setIcon(icon);
    }
  }
  
  @Override
  public void bind() {
    if (clickHandler != null) {
      handlers.add(display.getMenuItem().addClickHandler(clickHandler));
    }
  }

  @Override
  public HandlerManager getEventBus() {
    return null;
  }

  @Override
  public void release() {
    for (HandlerRegistration handler : handlers) {
      handler.removeHandler();
    }
    handlers.clear();
  }

  @Override
  public Display getDisplay() {
    return display;
  }
  
  public void setDisabled(boolean isDisabled) {
    display.setDisabled(isDisabled);
  }
}
