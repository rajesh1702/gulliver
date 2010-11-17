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

package com.google.mobile.trippy.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.mobile.trippy.web.client.base.SingletonComponents;
import com.google.mobile.trippy.web.client.db.TripItemService;
import com.google.mobile.trippy.web.client.db.TripService;
import com.google.mobile.trippy.web.client.event.ShareTripEvent;
import com.google.mobile.trippy.web.client.event.ShowCreateTripEvent;
import com.google.mobile.trippy.web.client.event.ShowFilteredTripListEvent;
import com.google.mobile.trippy.web.client.event.ShowHomePageEvent;
import com.google.mobile.trippy.web.client.event.ShowSearchItemDetailsEvent;
import com.google.mobile.trippy.web.client.event.ShowSearchResultsInListEvent;
import com.google.mobile.trippy.web.client.event.ShowSearchResultsOnMapEvent;
import com.google.mobile.trippy.web.client.event.ShowTripItemCommentsEvent;
import com.google.mobile.trippy.web.client.event.ShowTripItemDetailsEvent;
import com.google.mobile.trippy.web.client.event.ShowTripListEvent;
import com.google.mobile.trippy.web.client.event.ShowTripOnMapEvent;
import com.google.mobile.trippy.web.client.event.ShowTripScheduleEvent;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.TripItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handling of history tokens to show pages.
 * 
 * 
 */
public class HistoryTokenHandler implements ValueChangeHandler<String> {

  /** Messages instance. */
  // private static final Message constants = (Message)
  // GWT.create(Message.class);
  private final HandlerManager eventBus;
  private TripService tripService;
  private TripItemService tripItemService;

  public HistoryTokenHandler(SingletonComponents singletonComponent) {
    super();
    this.eventBus = singletonComponent.getEventBus();
    this.tripService = singletonComponent.getTripService();
    this.tripItemService = singletonComponent.getTripItemService();
  }

  @Override
  public void onValueChange(final ValueChangeEvent<String> event) {
    final String token = event.getValue();
    showPageForToken(token);
  }

  public void showPageForToken(final String token) {
    final Map<String, List<String>> paramMap = buildListParamMap(token);

    if (!paramMap.containsKey(Page.PAGE)) {
      eventBus.fireEvent(new ShowHomePageEvent());
      return;
    }
    try {
      switch (Integer.parseInt(paramMap.get(Page.PAGE).get(0))) {
        case Page.PAGE_HOME:
          eventBus.fireEvent(new ShowHomePageEvent(true));
          break;
        case Page.PAGE_CREATE_TRIP:
          eventBus.fireEvent(new ShowCreateTripEvent(true));
          break;
        case Page.PAGE_TRIP_LIST:
          eventBus.fireEvent(new ShowTripListEvent(true));
          break;
        case Page.PAGE_FILTERED_TRIP_LIST:
          String filterQuery = paramMap.get(Page.SEARCH_QUERY).get(0);
          eventBus.fireEvent(new ShowFilteredTripListEvent(filterQuery, true));
          break;
        case Page.PAGE_TRIP_DETAILS:
          Trip trip = tripService.getTrip(paramMap.get(Page.TRIP_ID).get(0));
          if (trip != null) {
            final ShowTripScheduleEvent event = new ShowTripScheduleEvent(trip, false);
            event.setHistoryEvent(true);
            eventBus.fireEvent(event);
          } else {
            // TODO: Show error screen that Trip no more exists and
            // give option to go to home page.
            History.back();
          }
          break;
        case Page.PAGE_TRIP_MAP:
          trip = tripService.getTrip(paramMap.get(Page.TRIP_ID).get(0));
          int tripDay = Integer.parseInt(paramMap.get(Page.TRIP_DAY).get(0));

          ShowTripOnMapEvent showTripOnMapEvent = null;
          if (paramMap.get(Page.TRIP_ITEM_ID) != null) {
            String tripItemkey = paramMap.get(Page.TRIP_ITEM_ID).get(0);
            showTripOnMapEvent = new ShowTripOnMapEvent(trip, tripDay, tripItemkey);
          } else {
            showTripOnMapEvent = new ShowTripOnMapEvent(trip, tripDay, null);
          }
          showTripOnMapEvent.setHistoryEvent(true);
          eventBus.fireEvent(showTripOnMapEvent);
          break;

        case Page.PAGE_TRIP_ITEM_DETAILS:
          TripItem tripItem = tripItemService.getTripItem(paramMap.get(Page.TRIP_ITEM_ID).get(0));
          eventBus.fireEvent(new ShowTripItemDetailsEvent(tripItem, true));
          break;

        case Page.PAGE_SEARCH_RESULT_MAP:
          trip = tripService.getTrip(paramMap.get(Page.TRIP_ID).get(0));
          int day = Integer.parseInt(paramMap.get(Page.TRIP_DAY).get(0));
          String key = paramMap.get(Page.SEARCH_RESULTS_KEY).get(0);

          ShowSearchResultsOnMapEvent showSearchResultsOnMapEvent =
              new ShowSearchResultsOnMapEvent(trip, day, key);
          showSearchResultsOnMapEvent.setHistoryEvent(true);
          eventBus.fireEvent(showSearchResultsOnMapEvent);
          break;

        case Page.PAGE_SEARCH_RESULT_LIST:
          trip = tripService.getTrip(paramMap.get(Page.TRIP_ID).get(0));
          day = Integer.parseInt(paramMap.get(Page.TRIP_DAY).get(0));
          key = paramMap.get(Page.SEARCH_RESULTS_KEY).get(0);
          ShowSearchResultsInListEvent showSearchResultsInListEvent =
              new ShowSearchResultsInListEvent(trip, day, key);
          showSearchResultsInListEvent.setHistoryEvent(true);
          eventBus.fireEvent(showSearchResultsInListEvent);
          break;

        case Page.PAGE_SEARCH_ITEM_DETAILS:
          trip = tripService.getTrip(paramMap.get(Page.TRIP_ID).get(0));
          day = Integer.parseInt(paramMap.get(Page.TRIP_DAY).get(0));
          String id = paramMap.get(Page.SEARCH_ITEM_ID).get(0);
          ShowSearchItemDetailsEvent showSearchItemDetailsEvent =
              new ShowSearchItemDetailsEvent(id, trip, day);
          showSearchItemDetailsEvent.setHistoryEvent(false);
          eventBus.fireEvent(showSearchItemDetailsEvent);
          break;

        case Page.PAGE_TRIP_ITEM_COMMENTS:
          tripItem = tripItemService.getTripItem(paramMap.get(Page.TRIP_ITEM_ID).get(0));
          eventBus.fireEvent(new ShowTripItemCommentsEvent(tripItem, true));
          break;
        case Page.PAGE_TRIP_SHARE:
          trip = tripService.getTrip(paramMap.get(Page.TRIP_ID).get(0));
          eventBus.fireEvent(new ShareTripEvent(trip, true));
          break;
        default:
          eventBus.fireEvent(new ShowHomePageEvent());
      }
    } catch (NumberFormatException e) {
      // TODO: Show error screen
      GWT.log("History token : " + e.getMessage());
      eventBus.fireEvent(new ShowHomePageEvent());
    } catch (Exception e) {
      // TODO: Show error screen
      GWT.log("History token : " + e.getMessage());
      eventBus.fireEvent(new ShowHomePageEvent());
    }
  }

  private static Map<String, List<String>> buildListParamMap(final String queryString) {
    Map<String, List<String>> out = new HashMap<String, List<String>>();
    if (queryString != null && queryString.length() > 1) {
      final String qs = queryString.substring(1);

      for (String kvPair : qs.split("&")) {
        final String[] kv = kvPair.split("=", 2);
        if (kv[0].length() == 0) {
          continue;
        }
        List<String> values = out.get(kv[0]);
        if (values == null) {
          values = new ArrayList<String>();
          out.put(kv[0], values);
        }
        values.add(kv.length > 1 ? URL.decodeComponent(kv[1]) : "");
      }
    }
    for (Map.Entry<String, List<String>> entry : out.entrySet()) {
      entry.setValue(Collections.unmodifiableList(entry.getValue()));
    }
    out = Collections.unmodifiableMap(out);
    return out;
  }
}
