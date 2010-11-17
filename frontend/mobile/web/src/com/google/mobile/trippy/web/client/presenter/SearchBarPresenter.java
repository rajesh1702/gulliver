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
import com.google.gwt.maps.client.base.HasLatLngBounds;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.event.SearchQueryEvent;
import com.google.mobile.trippy.web.client.i18n.Message;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.shared.models.POIType;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.SearchItem.SearchType;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates the search panel to search trip item.
 * 
 * 
 */
public class SearchBarPresenter implements EventHandlerPresenter<SearchBarPresenter.Display> {

  public static final POIType[] LP_POI_TYPES =
      {POIType.SEE, POIType.SHOP, POIType.SLEEP, POIType.NIGHT, POIType.EAT, POIType.DO,
          POIType.GENERAL};

  /**
   * Interface for the view of this presenter
   */
  public interface Display extends View {
    
    HasClickHandlers getCancelButton();
    
    String getSearchText();

    HasClickHandlers getSearchButton();

    HasClickHandlers[] getLPLinks();

    void defaultSearchText();

    void setPopupVisible(boolean visible);

    void clearSearchText();

    public HasClickHandlers getTextArea();
  }

  private Trip trip;
  private int day;
  private HasLatLngBounds searchBounds;

  private final Display display;
  private final HandlerManager eventBus;
  private final Message message;
  private final List<HandlerRegistration> handlers;
  
  public SearchBarPresenter(final Display display, final SingletonComponents singletonComponents) {
    this.display = display;
    this.eventBus = singletonComponents.getEventBus();
    this.handlers = new ArrayList<HandlerRegistration>();
    this.message = singletonComponents.getMessage();
  }

  public void setTrip(final Trip trip, final int day, HasLatLngBounds searchBounds) {
    Preconditions.checkNotNull(trip);
    this.trip = trip;
    this.day = day;
    this.searchBounds = searchBounds;
  }

  /**
   * Event handling on view's components. Do Google search request when "Search"
   * button is clicked. Do Lonely planet search request if lonely planet
   * recommendation link is clicked.
   * 
   * Events fired : None Events listened : None
   */
  @Override
  public void bind() {
    handlers.add(display.getSearchButton().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        String searchQuery = display.getSearchText();
        if (!searchQuery.trim().isEmpty()
            && !(searchQuery.equalsIgnoreCase(message.initialSearchTextAreaContent()))) {
          display.setPopupVisible(false);
          startGoogleSearch(modifySearchQueryIfNeeded(searchQuery));
        }
      }
    }));

    handlers.add(display.getTextArea().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        if (display.getSearchText().equals(
          message.initialSearchTextAreaContent())) {
           display.clearSearchText();
        }
      }
    }));
 
    handlers.add(display.getCancelButton().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        display.setPopupVisible(false);
      }
    }));

    
    HasClickHandlers[] lpLinks = display.getLPLinks();
    for (int i = 0; i < lpLinks.length; i++) {
      final int index = i;
      handlers.add(lpLinks[index].addClickHandler(new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
          display.setPopupVisible(false);
          startLPSearch(LP_POI_TYPES[index]);
        }
      }));
    }
  }

  private String modifySearchQueryIfNeeded(String input) {
    if ("activities".equalsIgnoreCase(input)) {
      return Constants.ACTIVITIES_SEARCH_REPLACEMENT;
    } else if ("general".equalsIgnoreCase(input)) {
      return Constants.GENERAL_SEARCH_REPLACEMENT;
    }
    return input;
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
      if (handler != null) {
        handler.removeHandler();
      }
    }
    handlers.clear();
  }

  public void clearDisplay() {
    display.defaultSearchText();
  }

  public void showPopup() {
    display.setPopupVisible(true);
  }
  
  
  private void startGoogleSearch(String searchText) {
    eventBus
        .fireEvent(new SearchQueryEvent(trip, day, searchText, searchBounds, SearchType.GOOGLE));
  }

  private void startLPSearch(POIType poiType) {
    eventBus.fireEvent(new SearchQueryEvent(trip, day, poiType.toString(), searchBounds,
        SearchType.LP));
  }
}
