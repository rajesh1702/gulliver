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

package com.google.mobile.trippy.web.client.presenter.header;

import com.google.common.base.Preconditions;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.event.SearchClickEvent;
import com.google.mobile.trippy.web.client.event.SearchClickEventHandler;
import com.google.mobile.trippy.web.client.event.ShareTripEvent;
import com.google.mobile.trippy.web.client.event.ShowSearchResultsInListEvent;
import com.google.mobile.trippy.web.client.event.ShowSearchResultsOnMapEvent;
import com.google.mobile.trippy.web.client.presenter.MenuPresenter;
import com.google.mobile.trippy.web.client.presenter.SearchBarPresenter;
import com.google.mobile.trippy.web.shared.models.Trip;

/**
 * Presenter for header options for screens showing Search results.
 * 
 */
public class SearchResultsOptionsPresenter extends MenuOptionsPresenter {

  private final SearchBarPresenter searchPresenter;
  private Trip trip;
  private int tripDay;
  private String resultsKey;

  public SearchResultsOptionsPresenter(final MenuOptionsPresenter.Display display,
          final SingletonComponents singletonComponents,
          final Provider<SearchBarPresenter> searchProvider,
          final Provider<MenuPresenter> menuProvider) {
    super(display);
    super.singletonComponents = singletonComponents;
    this.searchPresenter = searchProvider.get();
    this.menuPresenter = menuProvider.get();
  }

  public void setSearchResults(final Trip trip, final int day, final String key) {
    Preconditions.checkNotNull(trip);
    Preconditions.checkNotNull(key);

    this.trip = trip;
    this.tripDay = day;
    this.resultsKey = key;

    searchPresenter.setTrip(trip, day, null);
    
    setSearchAction(new Runnable() {
      @Override
      public void run() {
        doSearch();
      }
    });
    
    addMenu();
  }
  
  /**
   * Handle user events on header options.
   * 
   * Events Fired: None 
   * 
   * Events listened: SearchClickEvent, it is fired by clicking on native 
   * search button.
   */
  @Override
  public void bind() {
    super.bind();
    searchPresenter.bind();
    
    // For hardware search action.
    HANDLERS.add(singletonComponents.getEventBus().addHandler(
        SearchClickEvent.getType(), new SearchClickEventHandler() {
      @Override
      public void onSearch(SearchClickEvent event) {
        doSearch();
      }
    }));
  }

  @Override
  public void release() {
    super.release();
    searchPresenter.release();
  }

  @Override
  public void addMenu() {
    menuPresenter.clear();

    if (!isMapScreen()) {
      // SHOW ON MAP
      menuPresenter.addMenuItem(Constants.SHOW_ON_MAP_STR, TrippyBundle.INSTANCE.iconShowOnMap(),
              new Runnable() {
                @Override
                public void run() {
                  singletonComponents.getEventBus().fireEvent(
                          new ShowSearchResultsOnMapEvent(trip, tripDay, resultsKey));
                }
              });
    } else {
      // SHOW LIST
      menuPresenter.addMenuItem(Constants.SHOW_IN_LIST_STR, 
          TrippyBundle.INSTANCE.listIcon(), new Runnable() {
        @Override
        public void run() {
          singletonComponents.getEventBus().fireEvent(
                  new ShowSearchResultsInListEvent(trip, tripDay, resultsKey));
        }
      });
    }
    
    final boolean isContributor =
      singletonComponents.getUserUtils().isContributor(trip,
              singletonComponents.getUtils().getUserEmail());
    // "Share" trip item
    menuPresenter.addMenuItem(Constants.INVITE, TrippyBundle.INSTANCE.shareIcon(), !isContributor,
            new Runnable() {
              @Override
              public void run() {
                if (isContributor) {
                  singletonComponents.getEventBus().fireEvent(new ShareTripEvent(trip));
                } else {
                  singletonComponents.getToast().showToast(
                          singletonComponents.getMessage().unauthorizedShare());
                }
              }
            });

    super.addMenu();
  }

  private void doSearch() {
    if (searchClickListener != null) {
      searchClickListener.onSearchClick(searchPresenter);
    }
    searchPresenter.clearDisplay();
    searchPresenter.showPopup();
  }
}
