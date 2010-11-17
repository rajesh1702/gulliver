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

import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.event.SearchClickEvent;
import com.google.mobile.trippy.web.client.event.SearchClickEventHandler;
import com.google.mobile.trippy.web.client.event.ShowFilteredTripListEvent;
import com.google.mobile.trippy.web.client.event.ShowTripListEvent;
import com.google.mobile.trippy.web.client.presenter.MenuPresenter;
import com.google.mobile.trippy.web.client.presenter.TripListFilterPresenter;

/**
 * Presenter for options header for screens showing trip data.
 * 
 */
public class TripListOptionsPresenter extends MenuOptionsPresenter {

  private final TripListFilterPresenter filterPresenter;
  
  public TripListOptionsPresenter(final MenuOptionsPresenter.Display display,
      final SingletonComponents singletonComponents,
      final Provider<TripListFilterPresenter> filterProvider,
      final Provider<MenuPresenter> menuProvider) {
    super(display);
    super.singletonComponents = singletonComponents;
    super.menuPresenter = menuProvider.get();
    this.filterPresenter = filterProvider.get();
  }

  /**
   * Handle user events on header.
   * 
   * Events Fired: None 
   * 
   * Events listened: SearchClickEvent, it is fired by clicking on native 
   * search button.
   */
  @Override
  public void bind() {
    super.bind();
    
    filterPresenter.bind();
    super.setSearchAction(new Runnable() {
      @Override
      public void run() {
        filterPresenter.clearSearchBox();
        filterPresenter.showPopup();
      }
    });
    
    // For hardware search action.
    HANDLERS.add(singletonComponents.getEventBus().addHandler(
        SearchClickEvent.getType(), new SearchClickEventHandler() {
      @Override
      public void onSearch(SearchClickEvent event) {
        filterPresenter.clearSearchBox();
        filterPresenter.showPopup();
      }
    }));
  }

  @Override
  public void release() {
    super.release();
    filterPresenter.release();
  }

  @Override
  public void addMenu() {
    menuPresenter.clear();

    menuPresenter.addMenuItem(Constants.ALL_TRIPS_STR, TrippyBundle.INSTANCE.iconShowOnMap(),
        new Runnable() {
          @Override
          public void run() {
            singletonComponents.getEventBus().fireEvent(new ShowTripListEvent());
          }
        });
    
    menuPresenter.addMenuItem(Constants.CURRENT_TRIPS_STR, TrippyBundle.INSTANCE.iconShowOnMap(),
        new Runnable() {
          @Override
          public void run() {
            singletonComponents.getEventBus().fireEvent(new ShowFilteredTripListEvent(
                Constants.CURRENT_TRIPS_STR));
          }
        });
    
      menuPresenter.addMenuItem(Constants.UPCOMING_TRIPS_STR, TrippyBundle.INSTANCE.listIcon(),
          new Runnable() {
            @Override
            public void run() {
              singletonComponents.getEventBus().fireEvent(new ShowFilteredTripListEvent(
                  Constants.UPCOMING_TRIPS_STR));
            }
          });

      menuPresenter.addMenuItem(Constants.PAST_TRIPS_STR, TrippyBundle.INSTANCE.iconShowOnMap(),
          new Runnable() {
            @Override
            public void run() {
              singletonComponents.getEventBus().fireEvent(new ShowFilteredTripListEvent(
                  Constants.PAST_TRIPS_STR));
            }
          });
    
    menuPresenter.setPopupTitle(Constants.TRIP_LIST_MENU_STR);

    super.addMenu();
  }
}
