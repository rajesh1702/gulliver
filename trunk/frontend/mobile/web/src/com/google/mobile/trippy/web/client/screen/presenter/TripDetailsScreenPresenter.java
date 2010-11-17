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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.event.ShowTripListEvent;
import com.google.mobile.trippy.web.client.event.ShowTripOnMapEvent;
import com.google.mobile.trippy.web.client.event.TripUpdatedEvent;
import com.google.mobile.trippy.web.client.event.TripUpdatedEventHandler;
import com.google.mobile.trippy.web.client.presenter.BaseHeaderPresenter;
import com.google.mobile.trippy.web.client.presenter.DayListPresenter;
import com.google.mobile.trippy.web.client.presenter.EventHandlerPresenter;
import com.google.mobile.trippy.web.client.presenter.header.TripOptionsPresenter;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.shared.models.Trip;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible to show the DayList Screen.
 * 
 */
public class TripDetailsScreenPresenter implements
    EventHandlerPresenter<TripDetailsScreenPresenter.Display> {

  /**
   * Interface to view the trip details screen.
   */
  public static interface Display extends View {
    void setHeader(BaseHeaderPresenter.Display display);
    void setBody(DayListPresenter.Display dayList);
  }

  private final Display display;
  private final BaseHeaderPresenter headerPresenter;
  private final DayListPresenter dayListPresenter;
  private final TripOptionsPresenter headerOptions;
  private final List<HandlerRegistration> handlers;
  private Trip trip;
  
  public TripDetailsScreenPresenter(final Display display,
      final Provider<BaseHeaderPresenter> headerProvider,
      final Provider<DayListPresenter> dayListProvider,
      final Provider<TripOptionsPresenter> headerOptionsProvider) {
    this.display = display;
    this.headerPresenter = headerProvider.get();
    this.dayListPresenter = dayListProvider.get();
    this.headerOptions = headerOptionsProvider.get();
    this.handlers = new ArrayList<HandlerRegistration>();
  }

  /**
   * Handle user events on header.
   * 
   * Events Fired: None 
   * 
   * Events listened: TripUpdatedEvent, to make changes in header according to 
   * changes in trip name. 
   */
  @Override
  public void bind() {
    handlers.add(dayListPresenter.getEventBus().addHandler(TripUpdatedEvent.getType(),
        new TripUpdatedEventHandler() {
          @Override
          public void onTripUpdated(TripUpdatedEvent event) {
            if (event.getTrip().getKey().equals(trip.getKey())) {
              headerPresenter.setTitleString(trip.getName(), new Runnable() {
                @Override
                public void run() {
                  headerOptions.showEditPopUp(true);
                }
              });
            }
          }
        }));
    headerPresenter.bind();
    dayListPresenter.bind();
    headerOptions.bind();
  }

  /**
   * Set the trip to DayPresenter.
   */
  public void setTrip(final Trip trip) {
    this.trip = trip;
    
    // Populate header
    headerPresenter.setTitleString(trip.getName(), new Runnable() {
      @Override
      public void run() {
        headerOptions.showEditPopUp(true);
      }
    });
    
    headerPresenter.setNavigation(Constants.TRIPS_STR, new Runnable() {
      @Override
      public void run() {
        getEventBus().fireEvent(new ShowTripListEvent());
      }
    });

    headerPresenter.setSubNavigation(Constants.MAP_STR, new Runnable() {

      @Override
      public void run() {
        getEventBus().fireEvent(new ShowTripOnMapEvent(trip, Constants.NO_SELECTED_DAY, null));
      }
    });

    headerPresenter.setOptions(headerOptions, true);
    
    dayListPresenter.setTrip(trip);
    headerOptions.setMapScreen(false);
    headerOptions.setTrip(trip, Constants.NO_SELECTED_DAY);

    populateView();
  }

  @Override
  public HandlerManager getEventBus() {
    return dayListPresenter.getEventBus();
  }

  @Override
  public void release() {
    for (HandlerRegistration handler : handlers) {
      if (handler != null) {
        handler.removeHandler();
      }
    }
    handlers.clear();
    headerPresenter.release();
    dayListPresenter.release();
    headerOptions.release();
  }

  @Override
  public Display getDisplay() {
    return display;
  }

  public void showEditPopUp(final boolean visible) {
    headerOptions.showEditPopUp(visible);
  }

  private void populateView() {
    display.setHeader(headerPresenter.getDisplay());
    display.setBody(dayListPresenter.getDisplay());
  }


}
