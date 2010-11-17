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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.event.ShowTripScheduleEvent;
import com.google.mobile.trippy.web.client.event.TripUpdatedEvent;
import com.google.mobile.trippy.web.client.event.TripUpdatedEventHandler;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.client.widget.Toast;
import com.google.mobile.trippy.web.shared.exception.AuthorizationException;
import com.google.mobile.trippy.web.shared.models.Trip;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is responsible to create a trip edit popup.
 * 
 * 
 */
public class TripEditPopupPresenter implements 
    EventHandlerPresenter<TripEditPopupPresenter.Display> {

  /**
   * Interface for the view of this presenter
   */
  public interface Display extends View {
    public static final String MONTHS[] =
        {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static final int START_YEAR = 2010;
    public static final int MAX_YEAR = 2020;
    public static final int START_DAY = 1;
    public static final int LAST_DAY = 31;
    public static final int LAST_MONTH = 11; // starting with index 0;

    HasClickHandlers getSaveDate();
    HasClickHandlers getTripUnschedule();
    HasClickHandlers getTripSchedule();
    HasClickHandlers getCancel();
    void setPopupVisible(final boolean visible);
    // Creating lists for date(day, month, year).
    void setDefaultValues();
    void setDate(final int date);
    void setMonth(final int month);
    void setYear(final int year);
    void setDuration(final String duration);
    void setTripName(final String name);
    void showErrorMsg(final String msg);
    void setEnabledDate(final boolean enabled);
    void clearErrorMsg();
    void setTripUnschedule(final boolean checked);
    void setTripSchedule(final boolean checked);
    void clearDate();
    String getTripName();
    String getDuration();
    int getMonth();
    int getYear();
    int getDate();
    boolean isTripUnscheduleChecked();
    boolean isTripScheduleChecked();
  }

  private final SingletonComponents singletonComponents;
  private final Display display;
  private Trip trip;
  private static final int YEAR_BASE = 1900;
  private final List<HandlerRegistration> handlers;

  public TripEditPopupPresenter(final Display display, 
      final SingletonComponents singletonComponents) {
    this.singletonComponents = singletonComponents;
    this.display = display;
    handlers = new ArrayList<HandlerRegistration>();
  }

  /**
   * Bind the presenter and the view.
   * 
   * Listen for user events on display and take action. Also listen for
   * appropriate application events and update display accordingly
   * 
   * Events fired : None
   * 
   * Events listened : 1) TripUpdatedEvent: When the above is fired, update the
   * trip detail accordingly.
   * 2) ShowEditTripEvent: When new trip is created, shows the edit popup.
   */
  @Override
  public void bind() {
    handlers.add(singletonComponents.getEventBus().addHandler(TripUpdatedEvent.getType(),
        new TripUpdatedEventHandler() {

          @Override
          public void onTripUpdated(TripUpdatedEvent event) {
            setTrip(event.getTrip());
          }
    }));

    handlers.add(display.getTripUnschedule().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        display.setEnabledDate(false);
      }
    }));

    handlers.add(display.getTripSchedule().addClickHandler(new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
          display.setEnabledDate(true);
        }
    }));

    handlers.add(display.getSaveDate().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        display.setPopupVisible(false);
        if (display.getTripName().trim().isEmpty()) {
          display.showErrorMsg(singletonComponents.getMessage().errorMsgLocationEmpty());
          display.setPopupVisible(true);
        } else {
          display.clearErrorMsg();
          try {
            final int duration = Integer.parseInt(display.getDuration());
            if (duration > 0)
              updateTrip(duration);
            else {
            display.showErrorMsg(singletonComponents.getMessage().errorMsgNoDuration());
            display.setPopupVisible(true);
            }
          } catch(NumberFormatException e) {
            display.showErrorMsg(singletonComponents.getMessage().errorMsgNoDuration());
            display.setPopupVisible(true);
            return;
          }
        }
      }
    }));

    handlers.add(display.getCancel().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        display.setPopupVisible(false);
      }
    }));
  }

  public void showPopup() {
    display.setPopupVisible(true);
  }
  
  public void hidePopup() {
    display.setPopupVisible(false);
  }

  /**
   * Set the trip detail header.
   * 
   */
  public void setTrip(final Trip trip) {
    this.trip = trip;
    populateView();
  }

  @Override
  public HandlerManager getEventBus() {
    return singletonComponents.getEventBus();
  }

  @Override
  public void release() {
    for (HandlerRegistration handler : handlers) {
      handler.removeHandler();
    }
    handlers.clear();
  }

  @Override
  public Display getDisplay() {
    return display;
  }

  @SuppressWarnings("deprecation")
  @VisibleForTesting
  void populateView() {
    display.clearErrorMsg();
    display.clearDate();
    display.setDefaultValues();
    display.setPopupVisible(false);
    display.setTripName(trip.getName());
    Date startDate = trip.getStartDate();
    if (startDate.equals(Constants.UNSCHEDULED_DATE)) {
      startDate = new Date();
      display.setEnabledDate(false);
      display.setTripUnschedule(true);
      display.setTripSchedule(false);
    } else {
      display.setEnabledDate(true);
      display.setTripUnschedule(false);
      display.setTripSchedule(true);
    }
    display.setDuration(String.valueOf(trip.getDuration()));
    display.setDate(startDate.getDate());
    display.setMonth(startDate.getMonth());
    display.setYear(startDate.getYear() + YEAR_BASE);
  }

  @SuppressWarnings("deprecation")
  void updateTrip(final int duration) {
    if (display.isTripScheduleChecked()) {
      final Date newDate = new Date();
      newDate.setMonth(display.getMonth());
      newDate.setYear(display.getYear() - YEAR_BASE);
      newDate.setDate( display.getDate());
      trip.setStartDate(newDate);
    } else {
      if (display.isTripUnscheduleChecked()) {
        trip.setStartDate(Constants.UNSCHEDULED_DATE);
      }
    }
    trip.setDuration(duration);
    trip.setName(display.getTripName());
    final Toast toast = singletonComponents.getToast();
    try {
      toast.showLoading(singletonComponents.getMessage().updating("trip"));
      singletonComponents.getTripService().updateTrip(trip, new AsyncCallback<Trip>() {

        @Override
        public void onSuccess(Trip result) {
          toast.hideLoading();
          toast.showToast(singletonComponents.getMessage().tripModifiedMsg());
          singletonComponents.getEventBus().fireEvent(new ShowTripScheduleEvent(result));
        }

        @Override
        public void onFailure(Throwable caught) {
          toast.hideLoading();
          toast.showToast(singletonComponents.getMessage().tripSaveFailed());
        }
      });
    } catch (AuthorizationException e) {
      toast.hideLoading();
      toast.showToast(singletonComponents.getMessage().tripSaveFailed());
    }
  }
}
