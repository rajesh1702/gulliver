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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.event.ShowTripScheduleEvent;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.shared.models.Trip;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is responsible to show the top five up coming trip on the screen.
 * 
 *
 */
public class UpcomingTripPresenter implements
    EventHandlerPresenter<UpcomingTripPresenter.Display> {

  /**
   * Interface for the view of this presenter
   */
  public interface Display extends View {
    HasClickHandlers getTripHandler();
    void setName(final String tripName);
    void setDuration(final String tripDuration);
  }

  private final Display display;
  private final List<HandlerRegistration> handlers;
  /* Common container for all the singleton components of the application. */
  private final SingletonComponents provider;
  private Trip trip;

  public UpcomingTripPresenter(final Display display, final SingletonComponents provider) {
    this.display = display;
    this.provider = provider;
    handlers = new ArrayList<HandlerRegistration>();
  }

  /**
   * Method binds the handlers with event.
   * 
   * This method takes the responsibility for handling all the events and
   * firing the events.
   * 
   * Event Listened: None.
   * 
   * Events fired :
   * 1) ShowTripScheduleEvent: This event will be fired when
   * one clicks on the trip. This Event is responsible for showing the
   * schedule screen for respective trip.
   * 
   */
  @Override
  public void bind() {
    handlers.add(display.getTripHandler().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        provider.getEventBus().fireEvent(new ShowTripScheduleEvent(trip));
      }
    }));
  }

  /**
   * Set trip.
   * 
   */
  void setTrip(final Trip trip) {
    this.trip = trip;
    final Date tripStartDate = (Date) trip.getStartDate().clone();
    if (!(tripStartDate.equals(Constants.UNSCHEDULED_DATE))) {
      final Date tripEndDate = provider.getUtils()
          .addDaysToDate(tripStartDate, trip.getDuration() - 1);
      display.setDuration(provider.getUtils().getDisplayDate(tripStartDate)
          + " - " + provider.getUtils().getDisplayDate(tripEndDate));
    } else {
      display.setDuration(Constants.UNSCHEDULED_STR);
    }
    display.setName(trip.getName());
  }

  @Override
  public HandlerManager getEventBus() {
    return provider.getEventBus();
  }

  @Override
  public void release() {
    for (final HandlerRegistration handler : handlers) {
      handler.removeHandler();
    }
    handlers.clear();
  }

  @Override
  public Display getDisplay() {
    return display;
  }
}
