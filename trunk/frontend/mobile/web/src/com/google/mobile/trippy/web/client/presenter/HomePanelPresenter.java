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
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.event.ShowCreateTripEvent;
import com.google.mobile.trippy.web.client.event.ShowTripListEvent;
import com.google.mobile.trippy.web.client.event.TripAddedEvent;
import com.google.mobile.trippy.web.client.event.TripAddedEventHandler;
import com.google.mobile.trippy.web.client.event.TripDeletedEvent;
import com.google.mobile.trippy.web.client.event.TripDeletedEventHandler;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.shared.models.Trip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * This presenter is responsible to show the two tabs on the screen and perform
 * desired action on respective clicks. The first tab is used to create a new
 * trip, second tab is to view the user's own trip list and below the tabs
 * unscheduled trips and up coming trips are shown on the screen.
 * 
 */
public class HomePanelPresenter  implements
    EventHandlerPresenter<HomePanelPresenter.Display> {

  /**
   *  Interface to view the create trip screen.
   */
  public static interface Display extends View {
    HasClickHandlers getCreateNewTrip();
    HasClickHandlers getViewMyTrips();
    void addUpcomingTrip(final UpcomingTripPresenter.Display display);
    void clearTripList();
    void addUnscheduledTrip(final UpcomingTripPresenter.Display display);
    void setUnscheduledTripsTitle(final boolean visible);
    void setUpcomingTripsTitle(final boolean visible);

  }

  private static final int SHOW_MAX_TRIPS = 5;
  private final Display display;
  private final List<HandlerRegistration> handlers;
  private final Provider<UpcomingTripPresenter> upcomingTripPresenterProvider;
  private List<UpcomingTripPresenter> upcomingTripPreseters;
  private List<UpcomingTripPresenter> unScheduleTripPreseters;

  /* Common container for all the singleton components of the application. */
  private final SingletonComponents singletonComponents;
  private List<Trip> trips;
  private List<Trip> activeTrips;
  private List<Trip> unscheduleTrips;

  public HomePanelPresenter(final Display display, final SingletonComponents singletonComponents, 
      final Provider<UpcomingTripPresenter> upcomingTripPresenterProvider) {
    this.display = display;
    this.singletonComponents = singletonComponents;
    this.upcomingTripPresenterProvider = upcomingTripPresenterProvider;
    handlers = new ArrayList<HandlerRegistration>();
  }

  /**
   * Method binds the handlers with event.
   * 
   * This method takes the responsibility for handling all the events and firing
   * the events.
   * 
   * Event Listened:
   * 1) TripAddedEvent: When new trip is added to the application. 
   *
   * Events fired :
   * 1) ShowCreateTripEvent: This event will be fired when one
   * clicks on the create new trip button.  This Event is
   * responsible for showing the screen to create the new trip.
   * 
   * 2) ShowTripListEvent: This event will be fired when one
   * clicks on the my trips button. This Event is
   * responsible for showing the trip list screen.
   */
  @Override
  public void bind() {
    handlers.add(display.getCreateNewTrip().addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        singletonComponents.getEventBus().fireEvent(new ShowCreateTripEvent());
      }
    }));
    
    handlers.add(display.getViewMyTrips().addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        singletonComponents.getEventBus().fireEvent(new ShowTripListEvent());
      }
    }));
    
    handlers.add(singletonComponents.getEventBus().addHandler(
        TripAddedEvent.getType(), new TripAddedEventHandler() {

      @Override
      public void onTripAdded(TripAddedEvent event) {
        if (!hasTrip(event.getTrip())) {
          trips.add(event.getTrip());
          setTrips(trips);
        }
      }
    }));
    
    handlers.add(singletonComponents.getEventBus().addHandler(
        TripDeletedEvent.getType(), new TripDeletedEventHandler() {

        @Override
        public void onTripDeleted(TripDeletedEvent event) {
          if (hasTrip(event.getTrip())) {
            trips.remove(event.getTrip());
            setTrips(trips);
          }
        }
    }));
  }

  @Override
  public HandlerManager getEventBus() {
    return singletonComponents.getEventBus();
  }

  @Override
  public void release() {
    for (final HandlerRegistration handler : handlers) {
      handler.removeHandler();
    }
    handlers.clear();
    if (upcomingTripPreseters != null) {
      for (UpcomingTripPresenter presenter : upcomingTripPreseters) {
        presenter.release();
      }
      upcomingTripPreseters.clear();
    }
    if (unScheduleTripPreseters != null) {
      for (UpcomingTripPresenter presenter : unScheduleTripPreseters) {
        presenter.release();
      }
      unScheduleTripPreseters.clear();
    }
  }

  @Override
  public Display getDisplay() {
    return display;
  }

  /**
   * Set trip list and find the active trip list  
   * 
   */
  public void setTrips(final List<Trip> trips) {
    this.trips = trips;
    if (activeTrips == null) {
      activeTrips = new ArrayList<Trip>();
    }
    if (unscheduleTrips == null) {
      unscheduleTrips = new ArrayList<Trip>();
    }
    display.clearTripList();
    activeTrips.clear(); //In case of update.
    unscheduleTrips.clear();
    final Date currentDate = new Date();
    for (final Trip trip : trips) {
      final Date tripStartDate = trip.getStartDate();
      if (!tripStartDate.equals(Constants.UNSCHEDULED_DATE)) {
        final Date tripEndDate = 
            singletonComponents.getUtils().addDaysToDate(tripStartDate, trip.getDuration());
        if (tripEndDate.after(currentDate)) {
          activeTrips.add(trip);
        }
      } else {
        unscheduleTrips.add(trip);
      }
    }

    if (!unscheduleTrips.isEmpty()) {
      addUnsheduledTripsHandler();
      display.setUnscheduledTripsTitle(true);
    } else {
      display.setUnscheduledTripsTitle(false);
    }
    if (!activeTrips.isEmpty()) {
      addTripsHandlers();
      display.setUpcomingTripsTitle(true);
    } else {
      display.setUpcomingTripsTitle(false);
    }
  }

  /**
   * Method binds the handlers with event.
   * 
   * This method takes the responsibility firing the events.
   * 
   * 1.) ShowTripScheduleEvent: This event will be fired when one
   * clicks on one of the up coming trips.
   * 
   */
  private void addTripsHandlers() {
    Collections.sort(activeTrips, dateOrder);
    if (upcomingTripPreseters == null) {
      upcomingTripPreseters = new ArrayList<UpcomingTripPresenter>();
    }

    //Creating up coming trip. 
    for (int i = 0; i < activeTrips.size() && i < SHOW_MAX_TRIPS; i++) {
      final UpcomingTripPresenter upcomingTripPresenter = upcomingTripPresenterProvider.get();
      upcomingTripPresenter.setTrip(activeTrips.get(i));
      upcomingTripPresenter.bind();
      upcomingTripPreseters.add(upcomingTripPresenter);
      display.addUpcomingTrip(upcomingTripPresenter.getDisplay());
    }
  }

  /**
   * Method binds the handlers with event.
   * 
   * This method takes the responsibility firing the events.
   * 
   * 1.) ShowTripScheduleEvent: This event will be fired when one
   * clicks on one of the unscheduled trips.
   * 
   */
  private void addUnsheduledTripsHandler() {
    Collections.sort(unscheduleTrips, lastModifiedTripOrder);
    if (unScheduleTripPreseters == null) {
      unScheduleTripPreseters = new ArrayList<UpcomingTripPresenter>();
    }
    //Creating unscheduled trip list. 
    for (int i = 0; i < unscheduleTrips.size() && i < SHOW_MAX_TRIPS; i++) {
      final UpcomingTripPresenter unscheduleTripPresenter = upcomingTripPresenterProvider.get();
      unscheduleTripPresenter.setTrip(unscheduleTrips.get(i));
      unscheduleTripPresenter.bind();
      unScheduleTripPreseters.add(unscheduleTripPresenter);
      display.addUnscheduledTrip(unscheduleTripPresenter.getDisplay());
    }
  }

  /**
   * Comparator to sort the trip list.
   * 
   */
  private final Comparator<Trip> dateOrder = new Comparator<Trip>() {
    public int compare(Trip firstTrip, Trip secondTrip) {
      if (firstTrip.getStartDate().before(secondTrip.getStartDate())) {
        return -1; // when first trip has start date before the second trip.
      }
      if (firstTrip.getStartDate().after(secondTrip.getStartDate())) {
        return 1; // when first trip has start date after the second trip.
      }
      return 0; // when first trip has same start date as the second trip.
    }
  };

  /**
   * Comparator to sort the trip list based on last modified trip.
   * 
   */
  private final Comparator<Trip> lastModifiedTripOrder = new Comparator<Trip>() {
    public int compare(Trip firstTrip, Trip secondTrip) {
      if (firstTrip.getLastModified().before(secondTrip.getLastModified())) {
        return 1; // when first trip is modified before the second trip.
      }
      if (firstTrip.getLastModified().after(secondTrip.getLastModified())) {
        return -1; // when first trip is modified after the second trip.
      }
      return 0; // for rest cases.
    }
  };

  private boolean hasTrip(final Trip trip) {
    for (Trip t : trips) {
      if (t.getKey().equals(trip.getKey())) {
        return true;
      }
    }
    return false;
  }
}
