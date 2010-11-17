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

import com.google.common.base.Preconditions;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.mobile.trippy.web.client.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible to create a trip edit popup.
 * 
 * 
 */
public class DayPopupPresenter implements 
    EventHandlerPresenter<DayPopupPresenter.Display> {

  /**
   * Interface for the view of this presenter
   */
  public interface Display extends View {

    void setPopupVisible(boolean visible);
    
    List<HasClickHandlers> getDayHandlers();
    
    HasClickHandlers getCancel();

    void createPopUp(int daysCount);
  }

  private final Display display;
  private final List<HandlerRegistration> handlers;
  
  public DayPopupPresenter(final Display display) {
    this.display = display;
    this.handlers = new ArrayList<HandlerRegistration>();
  }

  public void showPopup() {
    display.setPopupVisible(true);
  }
  
  public void hidePopup() {
    display.setPopupVisible(false);
  }
  /**
   * Set the trip detail header.
   * 
   */
  public void setDays(final int days) {
    Preconditions.checkArgument(days > 0);
    display.createPopUp(days);
  }

  @Override
  public Display getDisplay() {
    return display;
  }
  
  public List<HasClickHandlers> getDayClickHandlers() {
    return display.getDayHandlers();
  }

  @Override
  public void bind() {
    handlers.add(display.getCancel().addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        display.setPopupVisible(false);
      }
    }));
  }

  @Override
  public HandlerManager getEventBus() {
    return null;
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
}
