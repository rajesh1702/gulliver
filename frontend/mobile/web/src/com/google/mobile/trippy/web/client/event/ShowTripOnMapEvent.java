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
 * Event to show trip on map
 * 
 */
public class ShowTripOnMapEvent extends TripEvent<ShowTripOnMapEventHandler> {

  protected static Type<ShowTripOnMapEventHandler> type;

  private int day;
  private String itemId;
  public ShowTripOnMapEvent(
      final Trip trip, final int day, final String tripItemId) {
    super(trip);
    this.day = day;
    this.itemId = tripItemId;
  }
  
  public static Type<ShowTripOnMapEventHandler> getType() {
    return type != null ? type : (type = new Type<ShowTripOnMapEventHandler>());
  }

  @Override
  public Type<ShowTripOnMapEventHandler> getAssociatedType() {
    return getType();
  }

  @Override
  protected void dispatch(ShowTripOnMapEventHandler handler) {
    handler.onShowTripOnMap(this);
  }
  
  public int getDay() {
    return day;
  }
  
  public String getTripItemId(){
    return itemId;
  }
}
