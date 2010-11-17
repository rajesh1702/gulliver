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
import com.google.mobile.trippy.web.client.event.ShowTripScheduleEvent;
import com.google.mobile.trippy.web.client.presenter.BaseHeaderPresenter;
import com.google.mobile.trippy.web.client.presenter.EventHandlerPresenter;
import com.google.mobile.trippy.web.client.presenter.TripEditPopupPresenter;
import com.google.mobile.trippy.web.client.presenter.TripSharePresenter;
import com.google.mobile.trippy.web.client.presenter.header.LogOutOptionsPresenter;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.shared.models.Trip;

/**
 * This class is responsible to create a screen presenter for sharing a trip.
 * 
 *
 */
public class TripShareScreenPresenter implements 
    EventHandlerPresenter<TripShareScreenPresenter.Display> {

  /**
   *  Interface to view the create trip screen.
   */
  public static interface Display extends View {
    void setHeader(final BaseHeaderPresenter.Display display);
    void setBody(final TripSharePresenter.Display display);
  }

  private final Display display;
  private final BaseHeaderPresenter tripListHeaderPresenter;
  private final TripSharePresenter tripSharePresenter;
  private final TripEditPopupPresenter tripEditPopupPresenter;
  private final LogOutOptionsPresenter headerOptions;
  
  public TripShareScreenPresenter(final Display display,
      final Provider<BaseHeaderPresenter> tripListHeaderProvider,
      final Provider<TripSharePresenter> tripShareProvider,
      final Provider<TripEditPopupPresenter> tripEditProvider,
      final Provider<LogOutOptionsPresenter> headerOptionsProvider) {
    this.display = display;
    this.tripListHeaderPresenter = tripListHeaderProvider.get();
    this.tripSharePresenter = tripShareProvider.get();
    this.tripEditPopupPresenter = tripEditProvider.get();
    this.headerOptions = headerOptionsProvider.get();
  }

  @Override
  public void bind() {
    tripListHeaderPresenter.bind();
    tripSharePresenter.bind();
    headerOptions.bind();
    tripEditPopupPresenter.bind();
  }

  /**
   * Set Trip.
   * 
   */
  public void setTrip(final Trip trip) {
    tripEditPopupPresenter.setTrip(trip);
    tripListHeaderPresenter.setTitleString(Constants.SHARE_STR + trip.getName(), new Runnable() {
      @Override
      public void run() {
        tripEditPopupPresenter.showPopup();
      }
    });
    tripListHeaderPresenter.setNavigation(Constants.ITINERARY_STR, new Runnable() {
      @Override
      public void run() {
        getEventBus().fireEvent(new ShowTripScheduleEvent(trip));
      }
    });
    tripListHeaderPresenter.setSubNavigationVisible(false);
    tripListHeaderPresenter.setOptions(headerOptions, true);
    tripSharePresenter.setTrip(trip);
    populateView();
  }
  
  @Override
  public HandlerManager getEventBus() {
    return tripSharePresenter.getEventBus();
  }

  @Override
  public void release() {
    tripListHeaderPresenter.release();
    tripSharePresenter.release();
    headerOptions.release();
    tripEditPopupPresenter.release();
  }

  @Override
  public Display getDisplay() {
    return display;
  }

  private void populateView() {
    display.setHeader(tripListHeaderPresenter.getDisplay());
    display.setBody(tripSharePresenter.getDisplay());
  }
}
