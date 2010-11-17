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

package com.google.mobile.trippy.web.client.event;

import com.google.gwt.maps.client.base.HasLatLngBounds;
import com.google.mobile.trippy.web.shared.models.Trip;
import com.google.mobile.trippy.web.shared.models.SearchItem.SearchType;

/**
 * Event to notify that a Google or Lonely planet search request is raised.
 * 
 */
public class SearchQueryEvent extends TripEvent<SearchQueryEventHandler> {

  protected static Type<SearchQueryEventHandler> type;
  private int tripDay;
  private String query;
  private HasLatLngBounds searchBounds;
  private SearchType searchType;
  
  public SearchQueryEvent(Trip trip, int day, String query, HasLatLngBounds searchBounds, SearchType searchType) {
    super(trip);
    this.tripDay = day;
    this.query = query;
    this.searchBounds = searchBounds;
    this.searchType = searchType;
  }
  
  public static Type<SearchQueryEventHandler> getType() {
    return type != null ? type : (type = new Type<SearchQueryEventHandler>());
  }

  @Override
  public Type<SearchQueryEventHandler> getAssociatedType() {
    return getType();
  }

  public int getTripDay() {
    return tripDay;
  }
  
  public String getQuery() {
    return query;
  }
  
  public HasLatLngBounds getSearchBounds() {
    return searchBounds;
  }
  
  public SearchType getSearchType() {
    return searchType;
  }
  
  @Override
  protected void dispatch(SearchQueryEventHandler handler) {
    handler.onSearchRequest(this);
  }
}
