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

import com.google.gwt.maps.client.base.HasLatLng;
import com.google.gwt.maps.client.event.EventCallback;
import com.google.mobile.trippy.web.client.TrippyBundle;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.event.ShowTripItemDetailsEvent;
import com.google.mobile.trippy.web.client.event.ShowTripScheduleEvent;
import com.google.mobile.trippy.web.client.presenter.header.TripOptionsPresenter;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for trip map.
 * 
 */
public class TripMapPresenter extends BaseMapPresenter {

  /**
   * interface for TripMapPresenter.
   * 
   */
  public static interface Display extends BaseMapPresenter.Display {
    TripItemPresenter.Display getInfoDisplay();
  }

  private final TripItemPresenter tripItemPresenter;
  private List<TripItem> shownTripItems;

  public TripMapPresenter(final Display tripMapView, final SingletonComponents singletonComponents,
      final Provider<BaseHeaderPresenter> headerProvider, final Provider<MapPresenter> mapProvider,
      final Provider<TripItemPresenter> tripItemProvider,
      final Provider<TripEditPopupPresenter> tripEditProvider,
      final Provider<TripOptionsPresenter> headerOptionsProvider) {
    super(tripMapView, singletonComponents, headerProvider, mapProvider, tripEditProvider,
        headerOptionsProvider, true);
    this.tripItemPresenter = tripItemProvider.get();
  }

  /**
   * Perform event handling on screen components such as buttons and map.
   * 
   * Events listened : None; Events fired: None
   */
  @Override
  public void bind() {
    tripDay = display.getSelectedPage() - 1;
    super.bind();
    tripItemPresenter.bind();
  }

  public void setTrip(final Trip trip, int day) {
    resultPinUrl = TrippyBundle.INSTANCE.bluePushPin().getURL();
    super.setMap(trip, day, new Runnable() {
      @Override
      public void run() {
        singletonComponents.getEventBus().fireEvent(new ShowTripScheduleEvent(trip));
      }
    });
    
    ((TripOptionsPresenter) headerOptions).setTrip(trip, day);
    display.populatePageList(trip.getDuration());
    
    if (shownItems == null) {
      shownItems = new ArrayList<Item>();
      shownTripItems = new ArrayList<TripItem>();
    } else {
      shownItems.clear();
      shownTripItems.clear();
    }
    
    if (day == Constants.UNSCHEDULED_DAY || day > trip.getDuration()) {
      day = Constants.UNSCHEDULED_DAY;
    }
    display.setSelectedPage(day + 1);
    showPageItems(day + 1);
  }

  public void setSelectedTripItem(String tripItemKey) {
    for (int i = 0; i < shownItems.size(); i++) {
      if (shownItems.get(i).getId().equals(tripItemKey)) {
        highlightResultIndex(i);
      }
    }
  }
  
  @Override
  public void release() {
    super.release();
    tripItemPresenter.release();
  }

  protected void populateView() {
    List<HasLatLng> points = populate();
    if (points != null && !points.isEmpty()) {
      mapPresenter.setBounds(points);
      highlightResultIndex(currentItem);
    }
  }

  @Override
  protected void highlightResultIndex(int index) {
    final TripItem tripItem = shownTripItems.get(index);
    tripItemPresenter.setTripItem(tripItem, true);
    highlightResultIndex(index, shownItems.get(index), new EventCallback() {
      @Override
      public void callback() {
        singletonComponents.getEventBus().fireEvent(new ShowTripItemDetailsEvent(tripItem));
      }
    });
  }

  @Override
  protected void showPageItems(int selectedPage) {
    tripDay = selectedPage - 1;
    shownItems.clear();
    shownTripItems.clear();
    final List<TripItem> allItems =
        singletonComponents.getTripItemService().getTripItems(trip.getKey());

    switch (tripDay) {
      case Constants.NO_SELECTED_DAY: // all items
        for (final TripItem item : allItems) {
          shownItems.add(new Item(item));
          shownTripItems.add(item);
        }
        break;
      case Constants.UNSCHEDULED_DAY: // unscheduled
        for (final TripItem item : allItems) {
          if (item.getStartDay() == Constants.UNSCHEDULED_DAY
              || item.getStartDay() > trip.getDuration()) {
            shownItems.add(new Item(item));
            shownTripItems.add(item);
          }
        }
        break;
      default:
        for (final TripItem item : allItems) {
          if (item.getStartDay() == tripDay) {
            shownItems.add(new Item(item));
            shownTripItems.add(item);
          }
        }
        break;
    }
    currentItem = 0;
    populateView();
  }
}
