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

import com.google.mobile.trippy.web.shared.models.Trip;

/**
 * Event to show search results on map
 * 
 */
public class ShowSearchResultsOnMapEvent extends TripEvent<ShowSearchResultsOnMapEventHandler> {

  protected static Type<ShowSearchResultsOnMapEventHandler> type;
  private final int tripDay;
  private final String searchResultsKey;
  
  public ShowSearchResultsOnMapEvent(final Trip trip, final int day, 
      final String searchResultsKey) {
    super(trip);
    this.tripDay = day;
    this.searchResultsKey = searchResultsKey;
  }

  public static Type<ShowSearchResultsOnMapEventHandler> getType() {
    return type != null ? type : (type = new Type<ShowSearchResultsOnMapEventHandler>());
  }

  @Override
  public Type<ShowSearchResultsOnMapEventHandler> getAssociatedType() {
    return getType();
  }

  public int getTripDay() {
    return tripDay;
  }

  public String getSearchResultsKey() {
    return searchResultsKey;
  }

  @Override
  protected void dispatch(ShowSearchResultsOnMapEventHandler handler) {
    handler.onShowSearchResultsOnMap(this);
  }
}
