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
import com.google.mobile.trippy.web.client.event.ShowFilteredTripListEvent;
import com.google.mobile.trippy.web.client.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates the search panel to search trip item.
 * 
 * 
 */
public class TripListFilterPresenter implements
    EventHandlerPresenter<TripListFilterPresenter.Display> {

  /**
   * Interface for the view of this presenter
   */
  public interface Display extends View {
    String getSearchText();

    HasClickHandlers getSearchButton();

    HasClickHandlers getCancelButton();
    
    void clearSearchBox();

    void setPopupVisible(boolean visible);
  }

  private final Display display;
  private final HandlerManager eventBus;
  private final List<HandlerRegistration> handlers;
  
  public TripListFilterPresenter(final Display display,
      final SingletonComponents singletonComponents) {
    this.display = display;
    this.eventBus = singletonComponents.getEventBus();
    this.handlers = new ArrayList<HandlerRegistration>();
  }

  /**
   * Event handling on view's components. Search trips from the trip list.
   * 
   * Events fired : None Events listened : None
   */
  @Override
  public void bind() {
    handlers.add(display.getSearchButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        String searchQuery = display.getSearchText();
        if (!searchQuery.trim().isEmpty()) {
          display.setPopupVisible(false);
          eventBus.fireEvent(new ShowFilteredTripListEvent(searchQuery));
        }
      }
    }));
    
    handlers.add(display.getCancelButton().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        display.setPopupVisible(false);
      }
    }));
  }

  @Override
  public Display getDisplay() {
    return display;
  }

  @Override
  public HandlerManager getEventBus() {
    return eventBus;
  }

  @Override
  public void release() {
    for (HandlerRegistration handler : handlers) {
      handler.removeHandler();
    }
    handlers.clear();
  }

  /**
   * Clears Search Box when once search is performed.
   * 
   */
  public void clearSearchBox() {
    display.clearSearchBox();
  }

  public void showPopup() {
    display.setPopupVisible(true);
  }
}
