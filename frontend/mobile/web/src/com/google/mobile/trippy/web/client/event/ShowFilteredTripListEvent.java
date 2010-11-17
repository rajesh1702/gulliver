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

/**
 * Event to show the filtered trip list screen.
 * 
 *
 */
public class ShowFilteredTripListEvent extends BaseEvent<ShowFilteredTripListEventHandler> {

  protected static Type<ShowFilteredTripListEventHandler> type;
  
  private final String filterTripsQuery;

  public ShowFilteredTripListEvent(final String searchTripQuery) {
    this(searchTripQuery, false);
  }
  
  public ShowFilteredTripListEvent(final String searchTripQuery, final boolean isHistoryEvent) {
    super(isHistoryEvent);
    this.filterTripsQuery = searchTripQuery;
  }

  public static Type<ShowFilteredTripListEventHandler> getType() {
    return type != null ? type : (type = new Type<ShowFilteredTripListEventHandler>());
  }

  @Override
  public Type<ShowFilteredTripListEventHandler> getAssociatedType() {
    return getType();
  }

  public String getSearchTripQuery() {
    return filterTripsQuery;
  }

  @Override
  protected void dispatch(ShowFilteredTripListEventHandler handler) {
    handler.onShowFilteredTripList(this);
  }
}
