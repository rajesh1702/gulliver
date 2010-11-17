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
 * This event will be fired to show details for a search item. 
 * 
 */
public class ShowSearchItemDetailsEvent extends TripEvent<ShowSearchItemDetailsEventHandler> {

  protected static Type<ShowSearchItemDetailsEventHandler> type;
  private final String searchItemId;
  private final int day;
  
  public ShowSearchItemDetailsEvent(final String searchItemId, final Trip trip, final int day) {
    super(trip);
    this.searchItemId = searchItemId;
    this.day = day;
  }
  
  public static Type<ShowSearchItemDetailsEventHandler> getType() {
    return type != null ? type : (type = new Type<ShowSearchItemDetailsEventHandler>());
  }

  @Override
  public Type<ShowSearchItemDetailsEventHandler> getAssociatedType() {
    return getType();
  }

  @Override
  protected void dispatch(ShowSearchItemDetailsEventHandler handler) {
    handler.onShowSearchDetails(this);
  }
  
  public String getSearchItemId() {
    return searchItemId;
  }
  
  public int getDay() {
    return day;
  }
}
