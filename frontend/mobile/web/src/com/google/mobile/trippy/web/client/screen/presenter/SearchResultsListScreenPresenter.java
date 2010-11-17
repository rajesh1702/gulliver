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

package com.google.mobile.trippy.web.client.screen.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.event.BackEvent;
import com.google.mobile.trippy.web.client.event.ShowTripScheduleEvent;
import com.google.mobile.trippy.web.client.presenter.BaseHeaderPresenter;
import com.google.mobile.trippy.web.client.presenter.EventHandlerPresenter;
import com.google.mobile.trippy.web.client.presenter.SearchResultsListPresenter;
import com.google.mobile.trippy.web.client.presenter.TripEditPopupPresenter;
import com.google.mobile.trippy.web.client.presenter.header.SearchResultsOptionsPresenter;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.shared.models.SearchItem;
import com.google.mobile.trippy.web.shared.models.Trip;

import java.util.List;

/**
 * This class is responsible to show the search results in a list view.
 * 
 * 
 */
public class SearchResultsListScreenPresenter implements
    EventHandlerPresenter<SearchResultsListScreenPresenter.Display> {

  /**
   * Interface to view the create trip screen.
   */
  public static interface Display extends View {
    void setHeader(BaseHeaderPresenter.Display display);

    void setBody(SearchResultsListPresenter.Display display);
  }

  private final Display display;
  private final BaseHeaderPresenter headerPresenter;
  private final SearchResultsListPresenter searchResultsListPresenter;
  private final SearchResultsOptionsPresenter headerOptions;
  private final TripEditPopupPresenter tripEditPopupPresenter;

  public SearchResultsListScreenPresenter(final Display display,
      final Provider<BaseHeaderPresenter> headerProvider,
      final Provider<SearchResultsListPresenter> searchResultsListProvider,
      final Provider<TripEditPopupPresenter> tripEditProvider,
      final Provider<SearchResultsOptionsPresenter> headerOptionsProvider) {
    this.display = display;
    this.headerPresenter = headerProvider.get();
    this.searchResultsListPresenter = searchResultsListProvider.get();
    this.tripEditPopupPresenter = tripEditProvider.get();
    this.headerOptions = headerOptionsProvider.get();
  }

  public void setResults(final Trip trip, final int day, final List<SearchItem> results,
      final String key) {
    tripEditPopupPresenter.setTrip(trip);
    headerPresenter.setTitleString(trip.getName(), new Runnable() {
      @Override
      public void run() {
        tripEditPopupPresenter.showPopup();
      }
    });
    headerPresenter.setNavigation(Constants.ITINERARY_STR, new Runnable() {
      @Override
      public void run() {
        getEventBus().fireEvent(new ShowTripScheduleEvent(trip));
      }
    });
    headerPresenter.setSubNavigation(Constants.MAP_STR, new Runnable() {

      @Override
      public void run() {
        getEventBus().fireEvent(new BackEvent());
      }
    });
    headerPresenter.setOptions(headerOptions, true);
    searchResultsListPresenter.setResults(trip, day, results);
    headerOptions.setMapScreen(false);
    headerOptions.setSearchResults(trip, day, key);

    populateView();
  }

  @Override
  public void bind() {
    headerPresenter.bind();
    headerOptions.bind();
    tripEditPopupPresenter.bind();
  }

  @Override
  public HandlerManager getEventBus() {
    return headerPresenter.getEventBus();
  }

  @Override
  public void release() {
    headerPresenter.release();
    headerOptions.release();
    tripEditPopupPresenter.release();
  }

  @Override
  public Display getDisplay() {
    return display;
  }

  private void populateView() {
    display.setHeader(headerPresenter.getDisplay());
    display.setBody(searchResultsListPresenter.getDisplay());
  }
}
