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
import com.google.mobile.trippy.web.client.event.ShowCreateTripEvent;
import com.google.mobile.trippy.web.client.event.TripAddedEvent;
import com.google.mobile.trippy.web.client.event.TripAddedEventHandler;
import com.google.mobile.trippy.web.client.event.TripDeletedEvent;
import com.google.mobile.trippy.web.client.event.TripDeletedEventHandler;
import com.google.mobile.trippy.web.client.event.TripUpdatedEvent;
import com.google.mobile.trippy.web.client.event.TripUpdatedEventHandler;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.shared.models.Trip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Presenter to handle trip list 
 * 
 */
public class TripListPresenter implements EventHandlerPresenter<TripListPresenter.Display> {

  /**
   * Interface for the view of this presenter
   */
  public interface Display extends View {
    void addActiveTrip(TripListItemPresenter.Display activeTrip);

    void addPastTrip(TripListItemPresenter.Display pastTrip);

    void setNoTripsMsgVisible(boolean visible);
    
    void clear();

    HasClickHandlers getCreateTripButton();

    HasClickHandlers getRefreshButton();
  }

  private final Display display;
  private final SingletonComponents singletonComponents;
  private final List<HandlerRegistration> handlers;
  private final Provider<TripListItemPresenter> provider;
  
  @VisibleForTesting
  List<Trip> unScheduledTrips;
  @VisibleForTesting
  List<Trip> activeTrips;
  @VisibleForTesting
  List<Trip> pastTrips;

  /**
   * Public constructor
   */
  public TripListPresenter(final Display display, final SingletonComponents singletonComponents, 
      final Provider<TripListItemPresenter> provider) {
    this.display = display;
    this.singletonComponents = singletonComponents;
    this.handlers = new ArrayList<HandlerRegistration>();
    this.provider = provider; 
  }

  /**
   * Bind the presenter and the view.
   * 
   * Listen for user events on display and take action. Also listen for
   * appropriate application events and update display accordingly
   * 
   * Events fired : None
   * 
   * Events listened : 
   * 1) TripAddedEvent - Add the trip to the list and call the populateView(). 
   * 2) TripUpdatedEvent - Identify the updated trip and its current location.
   * Check if start or end date has been modified. If start or end date has 
   * been modified then resort the trip lists, if the location of the updated 
   * trip is different the delete the display from view and 
   * insert it at the correct location in the correct trip list 
   * 3) TripDeletedEvent -  Identify the trip that needs to be deleted. 
   * Identify the trip list and the location, then delete that trip view 
   * from the display.
   */
  @Override
  public void bind() {
    final HandlerManager eventBus = singletonComponents.getEventBus();

    handlers.add(display.getCreateTripButton().addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        singletonComponents.getEventBus().fireEvent(new ShowCreateTripEvent());
      }
    }));
    
    handlers.add(display.getRefreshButton().addClickHandler(new ClickHandler() {
      
      @Override
      public void onClick(ClickEvent event) {
        singletonComponents.getUtils().reload();
      }
    }));

    handlers.add(eventBus.addHandler(TripAddedEvent.getType(), new TripAddedEventHandler() {
      
      @Override
      public void onTripAdded(TripAddedEvent event) {
        if (!hasTrip(event.getTrip())) {
          addTrip(event.getTrip());
        }
      }
    }));

    handlers.add(eventBus.addHandler(TripUpdatedEvent.getType(), new TripUpdatedEventHandler() {
      
      @Override
      public void onTripUpdated(TripUpdatedEvent event) {
        if (hasTrip(event.getTrip())) {
          updateTrip(event.getTrip());
        } else {
          addTrip(event.getTrip());
        }
      }
    }));

    handlers.add(eventBus.addHandler(TripDeletedEvent.getType(), new TripDeletedEventHandler() {
      
      @Override
      public void onTripDeleted(TripDeletedEvent event) {
        if (hasTrip(event.getTrip())) {
          deleteTrip(event.getTrip());
        }
      }
    }));
  }

  @Override
  public Display getDisplay() {
    return display;
  }

  @Override
  public HandlerManager getEventBus() {
    return singletonComponents.getEventBus();
  }

  @Override
  public void release() {
    for (final HandlerRegistration handler : handlers) {
      if (handler != null) {
        handler.removeHandler();
      }
    }
    handlers.clear();
  }

  /**
   * Sort the trips by their start dates and divide them into two groups
   * "Active trips" and "Past trips" depending upon their finish date. 
   */
  public void setTrips(final List<Trip> trips) {
    activeTrips = new ArrayList<Trip>();
    pastTrips = new ArrayList<Trip>();
    unScheduledTrips = new ArrayList<Trip>();
    for (final Trip trip : trips) {
      addTripToList(trip);
    }
    populateView();
  }
  
  @VisibleForTesting
  void addTrip(final Trip trip) {
    addTripToList(trip);
    populateView();
  }

  /**
   * Find out whether the newly added trip is a active trip or
   * past trip or unscheduled trip. Add the trip to the appropriate trip list.
   */
  @VisibleForTesting
  void addTripToList(final Trip trip) {
    final Date currentDate = new Date();
    final Date tripStartDate = trip.getStartDate();
    if (!tripStartDate.equals(Constants.UNSCHEDULED_DATE)) {
      final Date tripEndDate = 
          singletonComponents.getUtils().addDaysToDate(tripStartDate, trip.getDuration());
      if (tripEndDate.after(currentDate)) {
        activeTrips.add(trip);
      } else {
        pastTrips.add(trip);
      }
    } else {
      unScheduledTrips.add(trip);
    }
  }

  /**
   * Delete from the trip list and from the trip list display.
   * 
   * @param trip
   */
  @VisibleForTesting
  void deleteTrip(final Trip trip) {
    deleteTripFromList(trip);
    populateView();
  }

  /**
   * Find out whether the newly added trip is a active trip or
   * past trip or unscheduled trip. Delete the trip to the appropriate trip list.
   */
  @VisibleForTesting
  void deleteTripFromList(final Trip trip) {
    final Date tripStartDate = trip.getStartDate();
    if (!tripStartDate.equals(Constants.UNSCHEDULED_DATE)) {
      //Check if the item added is a active trip or past trip
      if (activeTrips.contains(trip)) {
        activeTrips.remove(trip);
      } else {
        pastTrips.remove(trip);
      }
    } else {
      unScheduledTrips.remove(trip);
    }
  }

  /**
   * Find out current position of the updated trip.Check if the start date or
   * duration was updated.If start date or duration was updated then remove 
   * from its current position.
   */
  @VisibleForTesting
  void updateTrip(final Trip trip) {
    //TODO: check for update event because of trip name.
    final Date tripStartDate = trip.getStartDate();
    if (!tripStartDate.equals(Constants.UNSCHEDULED_DATE)) {
      Trip currentTrip = null;
      int currentPos = activeTrips.indexOf(trip);
      int pastPos = pastTrips.indexOf(trip);
      int unscheduledPos = unScheduledTrips.indexOf(trip); 
      if (currentPos != -1) { //trip is in active trips
        currentTrip = activeTrips.get(currentPos);
      } else if (pastPos != -1) { //trip is in past trips
        currentTrip = pastTrips.get(pastTrips.indexOf(trip));
      } else {
        currentTrip = unScheduledTrips.get(unscheduledPos);
      }

      //Check if the start date or duration has changed, thus requiring
      //rearranging of active and past list items
      if (trip.getStartDate() == currentTrip.getStartDate() 
          && trip.getDuration() == currentTrip.getDuration()) {
        return; //trip schedule has not changed
      }
      
      deleteTripFromList(trip);
      addTripToList(trip);
      populateView();
    }
  }

  /**
   * Create presenters and display for each trip and show them as 
   * "Active trips" list and "Past trips" list
   */
  @VisibleForTesting
  void populateView() {
    display.clear();
    for (Trip unscheduledTrip : unScheduledTrips) {
      final TripListItemPresenter unscheduledTripPresenter = provider.get();
      unscheduledTripPresenter.setTrip(unscheduledTrip);
      unscheduledTripPresenter.bind();
      display.addActiveTrip(unscheduledTripPresenter.getDisplay());
    }
    if (activeTrips.size() > 0) {
      Collections.sort(activeTrips, dateOrder);
      for (Trip activeTrip : activeTrips) {
        final TripListItemPresenter activeTripPresenter = provider.get();
        activeTripPresenter.setTrip(activeTrip);
        activeTripPresenter.bind();
        display.addActiveTrip(activeTripPresenter.getDisplay());
      }
    }
    if (pastTrips.size() > 0) {
      final Comparator<Trip> dateOrderReverse = Collections.reverseOrder(dateOrder);
      Collections.sort(pastTrips, dateOrderReverse);
      for (Trip pastTrip : pastTrips) {
        final TripListItemPresenter pastTripPresenter = provider.get();
        pastTripPresenter.setTrip(pastTrip);
        pastTripPresenter.bind();
        display.addPastTrip(pastTripPresenter.getDisplay());
      }
    }
    display.setNoTripsMsgVisible((activeTrips.size() + pastTrips.size() +
        unScheduledTrips.size()) == 0);
  }



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

  private boolean hasTrip(final Trip trip) {
    for (Trip t : unScheduledTrips) {
      if (t.getKey().equals(trip.getKey())) {
        return true;
      }
    }
    for (Trip t : activeTrips) {
      if (t.getKey().equals(trip.getKey())) {
        return true;
      }
    }
    for (Trip t : pastTrips) {
      if (t.getKey().equals(trip.getKey())) {
        return true;
      }
    }
    return false;
  }
}
