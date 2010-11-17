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
import com.google.common.base.Preconditions;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.mobile.trippy.web.client.base.Constants;
import com.google.mobile.trippy.web.client.base.Provider;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.base.Utils;
import com.google.mobile.trippy.web.client.i18n.Message;
import com.google.mobile.trippy.web.client.view.View;
import com.google.mobile.trippy.web.client.widget.Toast;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Presenter for one day on day list
 * 
 * 
 */
public class DayListItemPresenter implements EventHandlerPresenter<DayListItemPresenter.Display> {

  /**
   * Interface for the view of this presenter
   */
  public interface Display extends View {
    HasClickHandlers getDayFocus();
    HasClickHandlers getSearch();
    public void setDay(final String day);
    public void setItemCount(final String count);
  }

  private static final int STATUS_TIME_MILLIS = 2000;
  private final Display display;
  private final HandlerManager eventBus;
  private final List<HandlerRegistration> handlers;
  private final Toast toast;
  private final Message message;
  private final SearchBarPresenter searchPresenter;

  private Trip trip;
  private int tripDay;
  private Utils utils;
  private List<TripItem> itemList;

  public DayListItemPresenter(final Display display, 
      final SingletonComponents singletonComponents,
      final Provider<SearchBarPresenter> searchProvider) {
    this.display = display;
    this.eventBus = singletonComponents.getEventBus();
    this.utils = singletonComponents.getUtils();
    this.toast = singletonComponents.getToast();
    this.message = singletonComponents.getMessage();
    this.searchPresenter = searchProvider.get();
    this.handlers = new ArrayList<HandlerRegistration>();
  }

  public void setTripItems(final Trip trip, final List<TripItem> items, final int day) {
    Preconditions.checkNotNull(trip);
    Preconditions.checkNotNull(items);
    this.trip = trip;
    this.tripDay = day;
    this.itemList = items;
    searchPresenter.setTrip(trip, day, null);
    populateView();
  }

  /**
   * Handle user events on the view
   * 
   * Events listened: None
   */
  @Override
  public void bind() {
    searchPresenter.bind();
    //TODO : Remove search functionality from day if not required.

    handlers.add(display.getDayFocus().addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        if (itemList.size() == 0) {
          searchPresenter.showPopup();
        } else { 
          toast.showToast(message.toastMsgSearchToAddItem(),
              STATUS_TIME_MILLIS);
        }
      }
    }));
  }

  @Override
  public HandlerManager getEventBus() {
    return eventBus;
  }

  @Override
  public Display getDisplay() {
    return display;
  }

  @Override
  public void release() {
    for (HandlerRegistration handler : handlers) {
      if (handler != null) {
        handler.removeHandler();
      }
    }
    handlers.clear();
  }

  @VisibleForTesting
  void populateView() {
    Date itemdate = (Date) trip.getStartDate().clone();
    itemdate = utils.addDaysToDate(itemdate, tripDay - 1);
    display.setDay(utils.getItineraryDate(itemdate, tripDay));

    final int itemCount = itemList.size();
    display.setItemCount((tripDay == 0) ? Constants.ITEMS_STR + " ("
        + itemCount + ")" : " (" + itemCount + ")");
  }
}
