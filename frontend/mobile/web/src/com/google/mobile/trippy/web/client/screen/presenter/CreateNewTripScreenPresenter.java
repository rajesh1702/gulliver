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
import com.google.mobile.trippy.web.client.presenter.CreateNewTripPresenter;
import com.google.mobile.trippy.web.client.presenter.EventHandlerPresenter;
import com.google.mobile.trippy.web.client.presenter.header.LogOutOptionsPresenter;
import com.google.mobile.trippy.web.client.view.View;

/**
 * This class is responsible to create a screen presenter for create a new trip.
 * 
 *
 */
public class CreateNewTripScreenPresenter implements 
    EventHandlerPresenter<CreateNewTripScreenPresenter.Display> {

  /**
   *  Interface to view the create trip screen.
   */
  public static interface Display extends View {
    void setHeader(final BaseHeaderPresenter.Display display);
    void setBody(final CreateNewTripPresenter.Display display);
  }

  private final Display display;
  private final CreateNewTripPresenter createNewTripProvider;
  private final BaseHeaderPresenter tripListHeaderPresenter;
  private final LogOutOptionsPresenter headerOptions;
  
  public CreateNewTripScreenPresenter(final Display display,
      final Provider<BaseHeaderPresenter> tripListHeaderProvider,
      final Provider<CreateNewTripPresenter> createNewTripProvider,
      final Provider<LogOutOptionsPresenter> headerOptionsProvider) {
    this.display = display;
    this.tripListHeaderPresenter = tripListHeaderProvider.get();
    this.createNewTripProvider = createNewTripProvider.get();
    this.headerOptions = headerOptionsProvider.get();
  }

  @Override
  public void bind() {
    tripListHeaderPresenter.bind();
    createNewTripProvider.bind();
    headerOptions.bind();
  }

  public void populateView() {
    display.setHeader(tripListHeaderPresenter.getDisplay());
    tripListHeaderPresenter.setTitleString(Constants.NEW_TRIP_STR, null);
    tripListHeaderPresenter.setNavigation(Constants.HOME_STR, new Runnable() {
      @Override
      public void run() {
        getEventBus().fireEvent(new ShowHomePageEvent());
      }
    });
    tripListHeaderPresenter.setSubNavigationVisible(false);
    tripListHeaderPresenter.setOptions(headerOptions, true);
    createNewTripProvider.getDisplay().clearErrorMsg();
    createNewTripProvider.getDisplay().setLocationBoxEmpty();
    display.setBody(createNewTripProvider.getDisplay());
  }

  
  @Override
  public HandlerManager getEventBus() {
    return createNewTripProvider.getEventBus();
  }

  @Override
  public void release() {
    tripListHeaderPresenter.release();
    createNewTripProvider.release();
    headerOptions.release();
  }

  @Override
  public Display getDisplay() {
    return display;
  }

}
