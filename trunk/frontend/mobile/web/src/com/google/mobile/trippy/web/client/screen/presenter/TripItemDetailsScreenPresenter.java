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
import com.google.mobile.trippy.web.client.presenter.TripItemPresenter;
import com.google.mobile.trippy.web.client.presenter.header.TripItemOptionsPresenter;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

/**
 * This class is responsible to show trip item details page.
 * 
 * 
 */
public class TripItemDetailsScreenPresenter implements
    EventHandlerPresenter<TripItemDetailsScreenPresenter.Display> {

  /**
   * Interface to view the create trip screen.
   */
  public static interface Display extends View {
    void setHeader(final BaseHeaderPresenter.Display display);
    void setBody(final TripItemPresenter.Display display);
  }

  private final BaseHeaderPresenter headerPresenter;
  private final TripItemPresenter tripItemPresenter;
  private final TripEditPopupPresenter tripEditPopupPresenter;
  private final TripItemOptionsPresenter headerOptions;
  private final Display display;

  public TripItemDetailsScreenPresenter(final Display display,
      final Provider<BaseHeaderPresenter> headerProvider,
      final Provider<TripItemPresenter> tripItemPanelPresenterProvider,
      final Provider<TripEditPopupPresenter> tripEditProvider,
      final Provider<TripItemOptionsPresenter> headerOptionsProvider) {
    this.display = display;
    this.headerPresenter = headerProvider.get();
    this.tripItemPresenter = tripItemPanelPresenterProvider.get();
    this.tripEditPopupPresenter = tripEditProvider.get();
    this.headerOptions = headerOptionsProvider.get();
  }

  /**
   * Method should be preceded with the setTripItem(TripItem item) method call.
   */
  @Override
  public void bind() {
    this.headerPresenter.bind();
    this.tripItemPresenter.bind();
    this.headerOptions.bind();
    this.tripEditPopupPresenter.bind();
  }

  @Override
  public HandlerManager getEventBus() {
    return tripItemPresenter.getEventBus();
  }

  @Override
  public void release() {
    this.headerPresenter.release();
    this.tripItemPresenter.release();
    this.headerOptions.release();
    this.tripEditPopupPresenter.release();
  }

  @Override
  public Display getDisplay() {
    return display;
  }

  /**
   * Set trip item.
   */
  public void setTripItem(final Trip trip, final TripItem item) {
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
    headerPresenter.setSubNavigationVisible(false);
    headerPresenter.setOptions(headerOptions, true);
    tripItemPresenter.setTripItem(item);
    headerOptions.setMapScreen(false);
    headerOptions.setTripItem(item);

    populateView();
  }

  private void populateView() {
    display.setHeader(headerPresenter.getDisplay());
    display.setBody(tripItemPresenter.getDisplay());
  }
}
