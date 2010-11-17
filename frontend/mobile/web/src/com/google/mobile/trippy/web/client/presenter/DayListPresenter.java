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
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.DefaultUtils;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.event.TripItemAddedEvent;
import com.google.mobile.trippy.web.client.event.TripItemAddedEventHandler;
import com.google.mobile.trippy.web.client.event.TripItemDeletedEvent;
import com.google.mobile.trippy.web.client.event.TripItemDeletedEventHandler;
import com.google.mobile.trippy.web.client.event.TripItemUpdatedEvent;
import com.google.mobile.trippy.web.client.event.TripItemUpdatedEventHandler;
import com.google.mobile.trippy.web.client.event.TripUpdatedEvent;
import com.google.mobile.trippy.web.client.event.TripUpdatedEventHandler;
import com.google.mobile.trippy.web.client.presenter.TripItemProvider.ViewType;
import com.google.mobile.trippy.web.client.presenter.header.TripItemOptionsPresenter;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.client.widget.Toast;
import com.google.mobile.trippy.web.shared.exception.AuthenticationException;
import com.google.mobile.trippy.web.shared.exception.AuthorizationException;
import com.google.mobile.trippy.web.shared.exception.TripNotFoundException;
import com.google.mobile.trippy.web.shared.exception.TripVersionException;
import com.google.mobile.trippy.web.shared.models.IdDayDateTuple;
import com.google.mobile.trippy.web.shared.models.IdDayDateTupleList;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for Day list.
 * 
 */
public class DayListPresenter implements EventHandlerPresenter<DayListPresenter.Display> {

  /**
   * Interface for the view of this presenter
   */
  public interface Display extends View {
    void addDay(DayListItemPresenter.Display dayDisplay, int day);

    void clear();

    void addTripItem(TripItemPresenter.Display tripItemDisplay, String itemId);

    ArrayList<IdDayDateTuple> getReorderedItems();

    HasClickHandlers getSaveButton();
  }

  private final Display display;
  private final SingletonComponents singletonComponents;
  private final Provider<DayListItemPresenter> dayProvider;
  private final Provider<TripItemOptionsPresenter> tripItemOptionsPresenterProvider;
  private final List<HandlerRegistration> handlers;
  private Trip trip;
  private List<TripItem> allItems;

  public DayListPresenter(final Display display, final SingletonComponents singletonComponents,
      final Provider<DayListItemPresenter> provider,
      final Provider<TripItemOptionsPresenter> tripItemOptionsPresenterProvider) {
    this.display = display;
    this.singletonComponents = singletonComponents;
    this.dayProvider = provider;
    this.tripItemOptionsPresenterProvider = tripItemOptionsPresenterProvider; 
    this.handlers = new ArrayList<HandlerRegistration>();
  }

  /**
   * Set the trip
   */
  public void setTrip(final Trip trip) {
    this.trip = trip;
    populateView();
  }

  /**
   * Bind the presenter and the view.
   * 
   * Listen for user events on display and take action. Also listen for
   * appropriate application events and update display accordingly.
   * 
   * Events fired : None
   * 
   * Events listened : 1) TripUpdatedEvent 2)TripItemAddedEvent 3)
   * TripItemUpdatedEvent 4) TripItemDeletedEvent , When any of the above four
   * events are fired, the fetch the trip item and verify if the trip is same as
   * the current trip. If so, then recreate the day groups.
   */
  @Override
  public void bind() {
    final HandlerManager eventBus = getEventBus();

    handlers.add(display.getSaveButton().addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        ArrayList<IdDayDateTuple> itemIds = display.getReorderedItems();

        final Toast toast = singletonComponents.getToast();
          toast.showLoading(singletonComponents.getMessage().updating(Constants.TRIP_STR));
          trip.setTripItemIds(new IdDayDateTupleList(itemIds));
          try {
            singletonComponents.getTripService().updateTripItemsTuple(trip,
                new AsyncCallback<Trip>() {
                  @Override
                  public void onFailure(final Throwable caught) {
                    if (caught instanceof AuthorizationException) {
                      toast.hideLoading();
                      toast.showToast(singletonComponents.getMessage().
                          unauthorizedEdit(Constants.TRIP_STR));
                    }
                    if (caught instanceof AuthenticationException) {
                      toast.hideLoading();
                      toast.showToast(singletonComponents.getMessage().
                          unauthorizedEdit(Constants.TRIP_STR));
                      DefaultUtils.getInstance().redirect(
                        DefaultUtils.getInstance().getLoginUrl(Location.getHref()));
                    }
                    if (caught instanceof TripNotFoundException) {
                      toast.hideLoading();
                      toast.showToast(singletonComponents.getMessage().tripNotFound());
                      DefaultUtils.getInstance().redirect(
                        DefaultUtils.getInstance().getLoginUrl(Location.getHref()));
                    }
                    if (caught instanceof TripVersionException) {
                      toast.hideLoading();
                      toast.showToast(singletonComponents.getMessage().tripNotSynced());
                    }
                  }

                  @Override
                  public void onSuccess(final Trip resultTrip) {
                    toast.hideLoading();
                    if (resultTrip.getKey().equals(trip.getKey())) {
                      setTrip(resultTrip);
                    }
                  }
                });
          } catch (AuthorizationException e) {
            toast.hideLoading();
            toast.showToast(singletonComponents.getMessage().
                unauthorizedEdit(Constants.TRIP_STR));
          }
      }
    }));

    handlers.add(eventBus.addHandler(TripUpdatedEvent.getType(), new TripUpdatedEventHandler() {
      @Override
      public void onTripUpdated(TripUpdatedEvent event) {
        if (event.getTrip().getKey().equals(trip.getKey())) {
          setTrip(event.getTrip());
        }
      }
    }));

    handlers.add(eventBus.addHandler(TripItemAddedEvent.getType(), new TripItemAddedEventHandler() {
      @Override
      public void onTripItemAdded(TripItemAddedEvent event) {
        if (event.getTripItem().getTripId().equals(trip.getKey())) {
          populateView();
        }
      }
    }));

    handlers.add(eventBus.addHandler(TripItemUpdatedEvent.getType(),
        new TripItemUpdatedEventHandler() {
          @Override
          public void onTripItemUpdated(TripItemUpdatedEvent event) {
            if (event.getTripItem().getTripId().equals(trip.getKey())) {
              populateView();
            }
          }
        }));

    handlers.add(eventBus.addHandler(TripItemDeletedEvent.getType(),
        new TripItemDeletedEventHandler() {
          @Override
          public void onTripItemDeleted(TripItemDeletedEvent event) {
            if (event.getTripItem().getTripId().equals(trip.getKey())) {
              populateView();
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
      if (handler != null) {
        handler.removeHandler();
      }
    }
    handlers.clear();
  }

  @Override
  public Display getDisplay() {
    return display;
  }

  /**
   * Fetch the trip items of the trip and group them according to the trip day.
   * Create presenters and display for each group and add the group's display
   * into the list display.
   */
  @SuppressWarnings("unchecked")
  @VisibleForTesting
  void populateView() {
    display.clear();
    // Fetch the trip items of the trip.
    allItems = singletonComponents.getTripItemService().getTripItems(trip.getKey());

    // Initialize List of DayList of trip items.
    final int tripDuration = trip.getDuration();
    final List<TripItem> dayItemsList[] = new ArrayList[tripDuration + 1];
    for (int i = 0; i <= tripDuration; i++) {
      dayItemsList[i] = new ArrayList<TripItem>();
    }

    // Group Trip items according to the trip day.
    for (int j = 0; j < allItems.size(); j++) {
      final TripItem item = allItems.get(j);
      if (item.getStartDay() > tripDuration) {
        dayItemsList[Constants.UNSCHEDULED_DAY].add(item);
      } else {
        dayItemsList[item.getStartDay()].add(item);
      }
    }

    // Create presenters and display for each group and add the group's display
    // into the list display.
    for (int i = 0; i <= tripDuration; i++) {
      final DayListItemPresenter dayPresenter = dayProvider.get();
      dayPresenter.setTripItems(trip, dayItemsList[i], i);
      dayPresenter.bind();
      display.addDay(dayPresenter.getDisplay(), i);

      final Provider<TripItemPresenter> tripItemProvider =
          new TripItemProvider(ViewType.LIST, singletonComponents,
              tripItemOptionsPresenterProvider);
      for (final TripItem item : dayItemsList[i]) {
        final TripItemPresenter presenter = tripItemProvider.get();
        presenter.setTripItem(item, true);
        presenter.bind();
        display.addTripItem(presenter.getDisplay(), item.getKey());
      }
    }
  }
}
