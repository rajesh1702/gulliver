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
import com.google.mobile.trippy.web.client.event.ShowHomePageEvent;
import com.google.mobile.trippy.web.client.presenter.BaseHeaderPresenter;
import com.google.mobile.trippy.web.client.presenter.EventHandlerPresenter;
import com.google.mobile.trippy.web.client.presenter.TripListPresenter;
import com.google.mobile.trippy.web.client.presenter.header.TripListOptionsPresenter;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.shared.models.Trip;

import java.util.List;

/**
 * This class is responsible to show list of trips. 
 * 
 *
 */
public class TripListScreenPresenter implements 
    EventHandlerPresenter<TripListScreenPresenter.Display> {

  /**
   *  Interface to view the create trip screen.
   */
  public static interface Display extends View {
    void setHeader(final BaseHeaderPresenter.Display display);
    void setBody(final TripListPresenter.Display display);
  }

  private final BaseHeaderPresenter tripListHeaderPresenter;
  private final TripListPresenter tripListPresenter;
  private final TripListOptionsPresenter headerOptions;
  private final Display display;

  public TripListScreenPresenter(final Display display,
      final Provider<BaseHeaderPresenter> tripListHeaderProvider,
      final Provider<TripListPresenter> tripListPresenterProvider,
      final Provider<TripListOptionsPresenter> headerOptionsProvider) {
    this.display = display;
    this.tripListHeaderPresenter = tripListHeaderProvider.get();
    this.tripListPresenter = tripListPresenterProvider.get();
    this.headerOptions = headerOptionsProvider.get();
  }
  
  /**
   * Method should be preceded with the setTripItem(TripItem item) method call.
   */
  @Override
  public void bind() {
    tripListHeaderPresenter.bind();
    tripListPresenter.bind();
    headerOptions.bind();
  }

  @Override
  public HandlerManager getEventBus() {
    return tripListPresenter.getEventBus();
  }

  @Override
  public void release() {
    tripListHeaderPresenter.release();
    tripListPresenter.release();
    headerOptions.release();
  }

  @Override
  public Display getDisplay() {
    return display;
  }
  
  public void setTrips(final List<Trip> trips, final String titleStr) {
    tripListHeaderPresenter.setTitleString(titleStr, null);
    tripListHeaderPresenter.setNavigation(Constants.HOME_STR, new Runnable() {
      @Override
      public void run() {
        getEventBus().fireEvent(new ShowHomePageEvent());
      }
    });
    tripListHeaderPresenter.setSubNavigationVisible(false);
    headerOptions.addMenu();
    tripListHeaderPresenter.setOptions(headerOptions, true);
    tripListPresenter.setTrips(trips);
    populateView();
  }
  
  private void populateView() {
    display.setHeader(tripListHeaderPresenter.getDisplay());
    display.setBody(tripListPresenter.getDisplay());
  }
}
