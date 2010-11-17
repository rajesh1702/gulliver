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

import com.google.common.annotations.VisibleForTesting;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.base.Utils;
import com.google.mobile.trippy.web.client.event.ShowTripScheduleEvent;
import com.google.mobile.trippy.web.client.event.TripUpdatedEvent;
import com.google.mobile.trippy.web.client.event.TripUpdatedEventHandler;
import com.google.mobile.trippy.web.client.presenter.header.TripOptionsPresenter;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.shared.models.Trip;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * List item on trip list
 * 
 * 
 */
public class TripListItemPresenter implements
    EventHandlerPresenter<TripListItemPresenter.Display> {

  /**
   * Interface for the view of this presenter
   */
  public interface Display extends View {
    HasClickHandlers getName();
    HasClickHandlers getOptions();

    void setName(String name);
    void setAddress(String address);
    void setDuration(String duration);
    void setCommentCount(String count);
    void setUpdate(boolean tripUpdated, boolean commentsUpdated);
  }

  private final Display display;
  private final SingletonComponents singletonComponents;
  private final List<HandlerRegistration> handlers;
  private Trip trip;
  private final TripOptionsPresenter tripOptionsPresenter;

  public TripListItemPresenter(final Display display, 
      final SingletonComponents singletonComponents,
      final Provider<TripOptionsPresenter> tripOptionsProvider) {
    this.display = display;
    this.singletonComponents = singletonComponents;
    handlers = new ArrayList<HandlerRegistration>();
    this.tripOptionsPresenter = tripOptionsProvider.get();
  }

  /**
   * Bind the presenter and the view.
   * 
   * Listen for user events on display and take action. Also listen for
   * appropriate application events and update display accordingly
   * 
   * Events fired : 
   * 1) ShowTripScheduleEvent - Show the schedule page of this trip
   * 
   * Events listened : 
   * 1) TripUpdatedEvent -Check if the current trip has been
   * updated. If the current trip has been updated then Check if any of the
   * following attributes have been updated a) Trip name b) Start date b) End
   * date If trip name has been updated then update the display name If
   * Start/End date have been updated then update the display schedule.
   */
  @Override
  public void bind() {
    tripOptionsPresenter.bind(false);  // Do not bind another HW search key.

    final HandlerManager eventBus = singletonComponents.getEventBus();
    
    handlers.add(display.getName().addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        eventBus.fireEvent(new ShowTripScheduleEvent(trip));
      }
    }));
    
    handlers.add(display.getOptions().addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        tripOptionsPresenter.getMenuPresenter().showMenu();
      }
    }));

    handlers.add(eventBus.addHandler(TripUpdatedEvent.getType(), new TripUpdatedEventHandler() {
      
      @Override
      public void onTripUpdated(TripUpdatedEvent event) {
        setTrip(event.getTrip());
      }
    }));
  }

  @Override
  public HandlerManager getEventBus() {
    return singletonComponents.getEventBus();
  }

  @Override
  public Display getDisplay() {
    return display;
  }

  @Override
  public void release() {
    for (HandlerRegistration handler : handlers) {
      handler.removeHandler();
    }
    handlers.clear();
    tripOptionsPresenter.release();
  }

  /**
   * Set the trip
   */
  public void setTrip(final Trip trip) {
    this.trip = trip;
    tripOptionsPresenter.setTrip(trip, Constants.NO_SELECTED_DAY);
    populateView();
  }
  
  @SuppressWarnings("deprecation")
  @VisibleForTesting
  String getScheduleString() {
    final Date startDate = trip.getStartDate();
    if(!startDate.equals(Constants.UNSCHEDULED_DATE)) {
      if (startDate == null) {
        return null;
      }
      final Utils utils = singletonComponents.getUtils();
      final Date endDate = utils.addDaysToDate(startDate, trip.getDuration() - 1);
      final String startString =
        startDate.getDate() + "/" + (startDate.getMonth() + 1) + 
        "/" + Integer.toString(startDate.getYear() + 1900);
      final String endString =
        endDate.getDate() + "/" + (endDate.getMonth() + 1) +
        "/" + Integer.toString(endDate.getYear() + 1900);
      return startString + " - " + endString;
    }
    //TODO: Move string to constant file.
    return "Unscheduled trip";
  }
  
  /**
   * Fetch the details of the trip and populate the display
   */
  @VisibleForTesting
  void populateView() {
    display.setName(trip.getName());
    display.setAddress(trip.getLocation());
    final Date currentDate = new Date();
    final Date startDate = trip.getStartDate();
    if (!startDate.equals(Constants.UNSCHEDULED_DATE)) {
      final Date endDate =
              singletonComponents.getUtils().addDaysToDate((Date) trip.getStartDate().clone(),
                      trip.getDuration());
      if (currentDate.after(trip.getStartDate()) && currentDate.before(endDate)) {
        display.setCommentCount(singletonComponents.getMessage().inProgress());
      }
    }
    display.setDuration(getScheduleString());
    display.setUpdate(trip.isUpdated(), trip.isCommentsUpdated());
  }
}
